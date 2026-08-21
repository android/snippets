/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

object SnippetExtractor {
    val START_TAG_REGEX = Regex("""\[START\s+([\w_\-]+)\]""")
    val END_TAG_REGEX = Regex("""\[END\s+([\w_\-]+)\]""")
    val START_EXCLUDE_REGEX = Regex("""\[START_EXCLUDE(?:\s+(silent))?\]""")
    val END_EXCLUDE_REGEX = Regex("""\[END_EXCLUDE\]""")
    val ANY_DELIMITER_REGEX = Regex("""\[(?:START|END)(?:_EXCLUDE)?(?:\s+[\w_\-]+)?\]""")

    /**
     * Creates a language-appropriate ellipses comment, preserving leading whitespace and comment prefix.
     */
    fun createEllipsesComment(line: String): String {
        val startPhrase = "[START_EXCLUDE]"
        val phraseInd = line.indexOf(startPhrase)
        if (phraseInd < 0) return ""
        val prefix = line.substring(0, phraseInd)
        return when {
            line.contains("-->") -> prefix + "... -->"
            line.contains("*/") -> prefix + "... */"
            else -> prefix + "..."
        }
    }

    /**
     * Extracts and transforms regions from lines of code, matching DevSite transformations.
     *
     * @param lines Source lines.
     * @param targetTag Optional tag name to filter for a single region tag.
     * @param onWarning Callback for diagnostic warnings (unclosed tags, mismatched excludes).
     * @return Map of region tag name to dedented snippet content.
     */
    fun extractRegions(
        lines: List<String>,
        targetTag: String? = null,
        onWarning: (String) -> Unit = {}
    ): Map<String, String> {
        val activeRegions = mutableMapOf<String, MutableList<String>>()
        val completedRegions = mutableMapOf<String, String>()
        var inExclude = false
        var silentExclude = false

        lines.forEachIndexed { index, line ->
            val lineNumber = index + 1

            // 1. Detect START region tags
            START_TAG_REGEX.findAll(line).forEach { match ->
                val tag = match.groupValues[1]
                if (targetTag == null || targetTag == tag) {
                    activeRegions[tag] = mutableListOf()
                }
            }

            // 2. Detect EXCLUDE blocks
            START_EXCLUDE_REGEX.find(line)?.let { match ->
                if (inExclude) {
                    onWarning("Line $lineNumber: Nested or duplicate [START_EXCLUDE] found")
                }
                inExclude = true
                silentExclude = match.groupValues[1] == "silent"
                if (!silentExclude) {
                    val ellipsis = createEllipsesComment(line)
                    activeRegions.values.forEach { it.add(ellipsis) }
                }
            }

            if (END_EXCLUDE_REGEX.containsMatchIn(line)) {
                if (!inExclude) {
                    onWarning("Line $lineNumber: [END_EXCLUDE] found without matching [START_EXCLUDE]")
                }
                inExclude = false
                return@forEachIndexed
            }

            // 3. Collect active line content (ignore lines that contain region tag delimiters)
            if (!inExclude && !ANY_DELIMITER_REGEX.containsMatchIn(line)) {
                activeRegions.values.forEach { it.add(line) }
            }

            // 4. Detect END region tags and store completed snippets
            END_TAG_REGEX.findAll(line).forEach { match ->
                val tag = match.groupValues[1]
                val snippetLines = activeRegions.remove(tag)
                if (snippetLines != null) {
                    val dedentedSnippet = snippetLines.joinToString("\n").trimIndent()
                    completedRegions[tag] = dedentedSnippet
                } else if (targetTag == null || targetTag == tag) {
                    onWarning("Line $lineNumber: [END $tag] found without matching [START $tag]")
                }
            }
        }

        if (inExclude) {
            onWarning("End of file reached with unclosed [START_EXCLUDE] block")
        }
        activeRegions.keys.forEach { unclosedTag ->
            onWarning("End of file reached with unclosed region tag '[START $unclosedTag]'")
        }

        return completedRegions
    }

    /**
     * Helper to extract a single region from text.
     */
    fun parseRegion(
        text: String,
        regionTag: String,
        onWarning: (String) -> Unit = {}
    ): String? {
        val extracted = extractRegions(text.lines(), targetTag = regionTag, onWarning = onWarning)
        return extracted[regionTag]
    }
}

abstract class ExtractSnippetsTask : DefaultTask() {

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    @get:Optional
    val targetTag: Property<String> = project.objects.property(String::class.java).convention(
        project.providers.gradleProperty("tag")
    )

    @get:Input
    @get:Optional
    val targetPackage: Property<String> = project.objects.property(String::class.java).convention(
        project.providers.gradleProperty("package")
    )

    @TaskAction
    fun extract() {
        val outDir = outputDir.get().asFile
        outDir.mkdirs()
        outDir.listFiles()?.forEach { it.delete() }

        val filterTag = targetTag.orNull?.trim()?.takeIf { it.isNotEmpty() }
        val filterPkg = targetPackage.orNull?.trim()?.takeIf { it.isNotEmpty() }

        val filesToScan = sourceFiles.files.filter { file ->
            filterPkg == null || file.invariantSeparatorsPath.contains(filterPkg)
        }

        var extractedCount = 0

        filesToScan.forEach { file ->
            val lines = file.readLines(Charsets.UTF_8)
            val snippets = SnippetExtractor.extractRegions(lines, filterTag) { warning ->
                logger.warn("${file.path}: $warning")
            }
            snippets.forEach { (tag, content) ->
                val extensionSuffix = if (file.extension.isNotEmpty()) ".${file.extension}" else ""
                val snippetFile = File(outDir, "$tag$extensionSuffix")
                if (snippetFile.exists()) {
                    logger.warn("Duplicate snippet tag '$tag' found in ${file.path}; overwriting previous snippet file")
                }
                snippetFile.writeText(content, Charsets.UTF_8)
                extractedCount++
            }
        }

        if (filterTag != null && extractedCount == 0) {
            logger.info("No snippet matching tag '$filterTag' found in ${project.name}")
        }
    }
}
