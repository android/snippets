/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

repositories {
    google()
    mavenCentral()
}

// Both preview renderers draw text with the same variable Roboto Flex font so the live WASM
// preview matches the Roborazzi screenshots. The WASM runner fetches it from `fonts/`.
val copyPreviewFonts by tasks.registering(Sync::class) {
    from("${rootProject.projectDir}/compose/snippets/src/main/res/font") {
        include("robotoflex_variable.ttf")
        into("fonts")
    }
    into(layout.buildDirectory.dir("generated/previewResources/wasmJsMain"))
}

val generatedSnippetsDir = layout.buildDirectory.dir("generated/sources/composeSnippets/wasmJsMain/kotlin")
val generatedSnippetResDir = layout.buildDirectory.dir("generated/snippetResources/composeResources")
val generatedSnippetResKotlinDir = layout.buildDirectory.dir("generated/snippetResources/kotlin")

// Copies the snippet files registered in SnippetRegistry into the wasm build, so these files must
// compile for Kotlin/Wasm (no Android-only APIs such as android.*, java.net or java.text).
// R.drawable/R.string and painterResource/stringResource are supported (see
// generateSnippetResources). Add files here as more snippets get interactive previews.
val pullSnippets by tasks.registering(Sync::class) {
    val snippetsRoot = "${rootProject.projectDir}/compose/snippets/src/main/java/com/example/compose/snippets"
    from("$snippetsRoot/components") {
        include("Button.kt")
        into("com/example/compose/snippets/components")
    }
    from("$snippetsRoot/animations") {
        include("AnimationQuickGuide.kt")
        into("com/example/compose/snippets/animations")
    }
    // Sample image URLs used by AnimationQuickGuide.kt.
    from("$snippetsRoot/util") {
        include("ImageLoadingSampleUtils.kt")
        into("com/example/compose/snippets/util")
    }
    into(generatedSnippetsDir)
}

// The shared snippets use Android `R.drawable.*` / `R.string.*` resources. This finds the ones
// they reference, copies those files into Compose Multiplatform resources, and generates an `R`
// whose IDs index into tables of the matching `Res` accessors (see ResStubs.kt).
val generateSnippetResources by tasks.registering {
    dependsOn(pullSnippets)
    val snippetsDir = generatedSnippetsDir
    val resDir = file("${rootProject.projectDir}/compose/snippets/src/main/res")
    val resOut = generatedSnippetResDir
    val kotlinOut = generatedSnippetResKotlinDir
    inputs.dir(snippetsDir)
    inputs.dir(resDir)
    outputs.dirs(resOut, kotlinOut)

    doLast {
        val refs = snippetsDir.get().asFile.walk().filter { it.extension == "kt" }
            .flatMap { Regex("""(?<!android\.)\bR\.(drawable|string)\.(\w+)""").findAll(it.readText()) }
            .map { it.groupValues[1] to it.groupValues[2] }
            .toSet()
        val drawables = refs.filter { it.first == "drawable" }.map { it.second }.sorted()
        val strings = refs.filter { it.first == "string" }.map { it.second }.sorted()

        val resRoot = resOut.get().asFile.apply { deleteRecursively() }
        val drawableOut = File(resRoot, "drawable").apply { mkdirs() }
        drawables.forEach { name ->
            val source = listOf("drawable", "drawable-nodpi", "drawable-v24")
                .flatMap { File(resDir, it).listFiles().orEmpty().toList() }
                .firstOrNull { it.nameWithoutExtension == name && it.extension in setOf("png", "jpg", "jpeg", "webp", "xml") }
                ?: error("No drawable resource found for R.drawable.$name")
            source.copyTo(File(drawableOut, source.name))
        }
        if (strings.isNotEmpty()) {
            val stringsXml = File(resDir, "values/strings.xml").readText()
            val entries = strings.map { name ->
                Regex("""<string name="$name"[^>]*>.*?</string>""", RegexOption.DOT_MATCHES_ALL)
                    .find(stringsXml)?.value ?: error("No string resource found for R.string.$name")
            }
            File(resRoot, "values").mkdirs()
            File(resRoot, "values/strings.xml")
                .writeText("<resources>\n${entries.joinToString("\n") { "    $it" }}\n</resources>\n")
        }

        // IDs start at 1 because 0 means "no resource" on Android; ResStubs.kt indexes with id - 1.
        fun ids(names: List<String>) = names.withIndex().joinToString("\n") { (i, n) -> "        const val $n = ${i + 1}" }
        fun accessors(type: String, names: List<String>) = names.joinToString { "Res.$type.$it" }
        val kotlinRoot = kotlinOut.get().asFile.apply { deleteRecursively() }
        File(kotlinRoot, "com/example/compose/snippets").apply { mkdirs() }.resolve("R.kt").writeText(
            """
            |// Generated by :preview:wasm:generateSnippetResources. Do not edit.
            |package com.example.compose.snippets
            |
            |import com.example.compose.preview.wasm.generated.resources.*
            |import org.jetbrains.compose.resources.DrawableResource
            |import org.jetbrains.compose.resources.StringResource
            |
            |object R {
            |    object drawable {
            |${ids(drawables)}
            |    }
            |    object string {
            |${ids(strings)}
            |    }
            |}
            |
            |internal val SnippetDrawables: List<DrawableResource> = listOf(${accessors("drawable", drawables)})
            |internal val SnippetStrings: List<StringResource> = listOf(${accessors("string", strings)})
            |""".trimMargin()
        )
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.example.compose.preview.wasm.generated.resources"
    generateResClass = always
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = generateSnippetResources.map { generatedSnippetResDir.get() },
    )
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val wasmJsMain by getting {
            kotlin.srcDir(files(generatedSnippetsDir).builtBy(pullSnippets))
            kotlin.srcDir(files(generatedSnippetResKotlinDir).builtBy(generateSnippetResources))
            resources.srcDir(copyPreviewFonts)
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.jetbrains.compose.components.resources)
                implementation(libs.jetbrains.navigation.compose)
                // AnimationQuickGuide.kt loads network images with Coil 3.
                implementation(libs.coil3.compose)
                implementation(libs.coil3.network.ktor3)
                implementation(libs.ktor.client.js)
            }
        }
    }
}

// Binaryen's wasm-opt shrinks snippets-preview-wasm.wasm by about 3.5x (for example 23 MB to
// 6.6 MB, or 5 MB to 1.8 MB gzipped) but adds a minute or two. Only the GitHub Pages bundle
// (packageStaticSite) needs it, so it stays off for everything else, including `./gradlew build`
// in CI. Development and test builds never run it.
val optimizeTaskName = "compileProductionExecutableKotlinWasmJsOptimize"
tasks.matching { it.name.contains("KotlinWasmJsOptimize") }.configureEach {
    enabled = false
}
gradle.taskGraph.whenReady {
    if (hasTask("${project.path}:packageStaticSite")) {
        tasks.named(optimizeTaskName).get().enabled = true
    }
}

// packageStaticSite and packageDevelopmentSite copy directly from compileSync without Webpack,
// so skip Webpack bundling and Karma browser test tasks during `./gradlew build`.
tasks.matching {
    it.name == "wasmJsBrowserDistribution" ||
        it.name == "wasmJsBrowserProductionWebpack" ||
        it.name == "wasmJsBrowserDevelopmentWebpack" ||
        it.name == "wasmJsBrowserTest"
}.configureEach {
    enabled = false
    setDependsOn(emptyList<Any>())
}

tasks.named("wasmJsProcessResources") {
    mustRunAfter(":preview:generator:recordRoborazziDebug")
}

val packageDevelopmentSite by tasks.registering(Copy::class) {
    group = "distribution"
    description = "Packages the interactive WASM preview and screenshots with fast-compiling development WASM binary"
    dependsOn("wasmJsDevelopmentExecutableCompileSync", "wasmJsProcessResources", "unpackSkikoRuntimeForWasmJs")
    mustRunAfter(":preview:generator:recordRoborazziDebug")

    into(layout.buildDirectory.dir("dist/site"))

    // Copy WASM runner HTML, fonts, and generated Roborazzi screenshots
    from("src/wasmJsMain/resources") {
        include("**/*")
    }
    from("src/wasmJsMain/resources/wasm.html") {
        rename("wasm.html", "index.html")
    }
    from(copyPreviewFonts)
    // Compose Multiplatform resources (snippet images and strings), served from composeResources/
    from(layout.buildDirectory.dir("processedResources/wasmJs/main")) {
        include("composeResources/**")
    }

    // Copy compiled development WASM and JS from compileSync
    from(layout.buildDirectory.dir("compileSync/wasmJs/main/developmentExecutable/kotlin")) {
        include("*.wasm")
        include("*.mjs")
        include("*.js")
        include("*.map")
    }

    // Copy skiko runtime (skiko.wasm, skiko.mjs)
    from(layout.buildDirectory.dir("compose/skiko-wasmJs-runtime")) {
        include("skiko.wasm")
        include("skiko.mjs")
    }
    // Compose Multiplatform material3 depends on kotlinx-datetime, whose wasm build imports
    // '@js-joda/core'. Point it at the bundled copy (also mapped in wasm.html's import map).
    doLast {
        val importObj = file("${layout.buildDirectory.get()}/dist/site/snippets-preview-wasm.import-object.mjs")
        if (importObj.exists()) {
            importObj.writeText(importObj.readText().replace("'@js-joda/core'", "'./js-joda-core.mjs'"))
        }
    }
}

val packageStaticSite by tasks.registering(Copy::class) {
    group = "distribution"
    description = "Packages the interactive WASM preview and screenshots with optimized production WASM binary for deployment"
    dependsOn(
        "wasmJsProductionExecutableCompileSync",
        optimizeTaskName,
        "wasmJsProcessResources",
        "unpackSkikoRuntimeForWasmJs",
    )
    mustRunAfter(":preview:generator:recordRoborazziDebug")

    into(layout.buildDirectory.dir("dist/site"))

    // Copy WASM runner HTML, fonts, and generated Roborazzi screenshots
    from("src/wasmJsMain/resources") {
        include("**/*")
    }
    from("src/wasmJsMain/resources/wasm.html") {
        rename("wasm.html", "index.html")
    }
    from(copyPreviewFonts)
    // Compose Multiplatform resources (snippet images and strings), served from composeResources/
    from(layout.buildDirectory.dir("processedResources/wasmJs/main")) {
        include("composeResources/**")
    }

    // Copy the wasm-opt optimized production WASM and JS from compileSync
    from(layout.buildDirectory.dir("compileSync/wasmJs/main/productionExecutable/optimized")) {
        include("*.wasm")
        include("*.mjs")
        include("*.js")
        include("*.map")
    }

    // Copy skiko runtime (skiko.wasm, skiko.mjs)
    from(layout.buildDirectory.dir("compose/skiko-wasmJs-runtime")) {
        include("skiko.wasm")
        include("skiko.mjs")
    }
    // Compose Multiplatform material3 depends on kotlinx-datetime, whose wasm build imports
    // '@js-joda/core'. Point it at the bundled copy (also mapped in wasm.html's import map).
    doLast {
        val importObj = file("${layout.buildDirectory.get()}/dist/site/snippets-preview-wasm.import-object.mjs")
        if (importObj.exists()) {
            importObj.writeText(importObj.readText().replace("'@js-joda/core'", "'./js-joda-core.mjs'"))
        }
    }
}

tasks.register("buildPreviewSite") {
    group = "distribution"
    description = "Alias for packageStaticSite used by CI"
    dependsOn(packageStaticSite)
}


