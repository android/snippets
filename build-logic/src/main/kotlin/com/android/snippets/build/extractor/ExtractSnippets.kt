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

package com.android.snippets.build.extractor

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal object SnippetExtractor {
    val START_TAG_REGEX = Regex("""\[START\s+([\w_\-]+)\]""")
    val END_TAG_REGEX = Regex("""\[END\s+([\w_\-]+)\]""")
    val START_EXCLUDE_REGEX = Regex("""\[START_EXCLUDE(?:\s+(silent))?\]""")
    val END_EXCLUDE_REGEX = Regex("""\[END_EXCLUDE\]""")
    val ANY_DELIMITER_REGEX =
        Regex("""\[(?:(?:START|END)\s+[\w_\-]+|(?:START_EXCLUDE(?:\s+silent)?|END_EXCLUDE))\]""")

    private class RegionState {
        val lines = mutableListOf<String>()
        var inExclude = false
    }

    /**
     * Creates a language-appropriate ellipses comment, matching documentation site output
     * (`line[:phrase_ind] + "..."`).
     */
    fun createEllipsesComment(line: String): String {
        val startPhrase = "[START_EXCLUDE]"
        val phraseInd = line.indexOf(startPhrase)
        if (phraseInd < 0) return ""
        return line.substring(0, phraseInd) + "..."
    }

    /**
     * Extracts and transforms regions from lines of code, matching documentation site transformations.
     *
     * @param lines Source lines.
     * @param targetTag Optional tag name to filter for a single region tag.
     * @param onWarning Callback for diagnostic warnings (unclosed tags, mismatched excludes).
     * @return Map of region tag name to dedented snippet content.
     */
    fun extractRegions(
        lines: List<String>,
        targetTag: String? = null,
        onWarning: (String) -> Unit = {},
    ): Map<String, String> {
        val activeRegions = mutableMapOf<String, RegionState>()
        val completedRegions = mutableMapOf<String, String>()

        lines.forEachIndexed { index, line ->
            val lineNumber = index + 1

            // 1. Detect START region tags
            START_TAG_REGEX.findAll(line).forEach { match ->
                val tag = match.groupValues[1]
                if (targetTag == null || targetTag == tag) {
                    activeRegions[tag] = RegionState()
                }
            }

            // 2. Detect EXCLUDE blocks within active regions
            if (activeRegions.isNotEmpty()) {
                START_EXCLUDE_REGEX.find(line)?.let { match ->
                    if (activeRegions.values.any { it.inExclude }) {
                        onWarning("Line $lineNumber: Nested or duplicate [START_EXCLUDE] found")
                    }
                    activeRegions.values.forEach { it.inExclude = true }
                    val isSilent = match.groupValues[1] == "silent"
                    if (!isSilent) {
                        val startPhrase = "[START_EXCLUDE]"
                        val phraseInd = line.indexOf(startPhrase)
                        if (phraseInd >= 0) {
                            val trimmedPrefix = line.substring(0, phraseInd).trimEnd()
                            val suffix = line.substring(phraseInd + startPhrase.length)
                            if (
                                trimmedPrefix.endsWith("<!--") ||
                                trimmedPrefix.endsWith("/*") ||
                                suffix.contains("-->") ||
                                suffix.contains("*/")
                            ) {
                                onWarning(
                                    "Line $lineNumber: Non-silent [START_EXCLUDE] inside block or XML comment " +
                                        "produces an unclosed comment; consider [START_EXCLUDE silent]"
                                )
                            }
                        }
                        val ellipsis = createEllipsesComment(line)
                        activeRegions.values.forEach { it.lines.add(ellipsis) }
                    }
                }

                if (END_EXCLUDE_REGEX.containsMatchIn(line)) {
                    if (activeRegions.values.none { it.inExclude }) {
                        onWarning("Line $lineNumber: [END_EXCLUDE] found without matching [START_EXCLUDE]")
                    }
                    activeRegions.values.forEach { it.inExclude = false }
                }
            }

            // 3. Collect active line content (ignore lines that contain region tag delimiters)
            if (!ANY_DELIMITER_REGEX.containsMatchIn(line)) {
                activeRegions.values.forEach { region ->
                    if (!region.inExclude) {
                        region.lines.add(line)
                    }
                }
            }

            // 4. Detect END region tags and store completed snippets
            END_TAG_REGEX.findAll(line).forEach { match ->
                val tag = match.groupValues[1]
                val regionState = activeRegions.remove(tag)
                if (regionState != null) {
                    if (regionState.inExclude) {
                        onWarning("Line $lineNumber: Region '$tag' closed with unclosed [START_EXCLUDE] block")
                    }
                    completedRegions[tag] = regionState.lines.joinToString("\n").trimIndent()
                } else if (targetTag == null || targetTag == tag) {
                    onWarning("Line $lineNumber: [END $tag] found without matching [START $tag]")
                }
            }
        }

        activeRegions.forEach { (unclosedTag, state) ->
            if (state.inExclude) {
                onWarning("End of file reached with unclosed [START_EXCLUDE] block in region '$unclosedTag'")
            }
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
        onWarning: (String) -> Unit = {},
    ): String? {
        val extracted = extractRegions(text.lines(), targetTag = regionTag, onWarning = onWarning)
        return extracted[regionTag]
    }
}

internal abstract class ExtractSnippetsTask : DefaultTask() {

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFiles: ConfigurableFileCollection

    @get:Internal
    abstract val projectRootDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    @get:Optional
    @get:Option(option = "tag", description = "Filter extraction to a specific snippet tag")
    abstract val targetTag: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "package", description = "Filter extraction to file paths containing this substring")
    abstract val targetPackage: Property<String>

    @TaskAction
    fun extract() {
        val outDir = outputDir.get().asFile
        outDir.mkdirs()
        outDir.listFiles()?.forEach { it.delete() }

        val filterTag = targetTag.orNull?.trim()?.takeIf { it.isNotEmpty() }
        val filterPkg = targetPackage.orNull?.trim()?.takeIf { it.isNotEmpty() }
        val rootDir = projectRootDir.orNull?.asFile

        val filesToScan =
            sourceFiles.files
                .map { file ->
                    val relPath =
                        if (rootDir != null) {
                            file.relativeToOrSelf(rootDir).invariantSeparatorsPath
                        } else {
                            file.invariantSeparatorsPath
                        }
                    file to relPath
                }
                .filter { (_, relPath) -> filterPkg == null || relPath.contains(filterPkg) }
                .sortedBy { (_, relPath) -> relPath }

        var extractedCount = 0
        val writtenByOutputName = mutableMapOf<String, MutableList<String>>()

        filesToScan.forEach { (file, relPath) ->
            if (isLikelyBinary(file)) return@forEach

            val lines = file.readLines(Charsets.UTF_8)
            val snippets =
                SnippetExtractor.extractRegions(lines, filterTag) { warning ->
                    logger.warn("$relPath: $warning")
                }
            snippets.forEach { (tag, content) ->
                val extensionSuffix = if (file.extension.isNotEmpty()) ".${file.extension}" else ""
                val outputFileName = "$tag$extensionSuffix"
                val snippetFile = File(outDir, outputFileName)
                val previousSources = writtenByOutputName.getOrPut(outputFileName) { mutableListOf() }
                if (previousSources.isNotEmpty() && filterTag == null) {
                    logger.warn(
                        "Duplicate snippet tag '$tag' found in $relPath (previously in ${previousSources.last()}); " +
                            "overwriting $outputFileName. Pass --package=<substring> to disambiguate."
                    )
                }
                previousSources.add(relPath)
                snippetFile.writeText(content, Charsets.UTF_8)
                extractedCount++
            }
        }

        if (filterTag != null) {
            val collisions = writtenByOutputName.filterValues { it.size > 1 }
            if (collisions.isNotEmpty()) {
                collisions.keys.forEach { File(outDir, it).delete() }
                val details =
                    collisions.entries.joinToString("\n") { (outName, paths) ->
                        "  $outName matched in:\n" + paths.joinToString("\n") { "    - $it" }
                    }
                throw GradleException(
                    "Ambiguous snippet tag '$filterTag' in $path matched multiple files:\n$details\n" +
                        "Pass --package=<path-substring> to disambiguate."
                )
            }
            if (extractedCount == 0) {
                logger.info("No snippet matching tag '$filterTag' found in $path")
            }
        }
    }

    private fun isLikelyBinary(file: File): Boolean {
        if (!file.isFile || file.length() == 0L) return false
        val buffer = ByteArray(4096)
        val bytesRead = file.inputStream().use { it.read(buffer) }
        for (i in 0 until bytesRead) {
            if (buffer[i] == 0.toByte()) return true
        }
        return false
    }
}
