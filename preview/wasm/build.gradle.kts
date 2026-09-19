/*
 * Copyright 2024 The Android Open Source Project
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

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "snippets-wasm-preview.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val generatedSnippetsDir = layout.buildDirectory.dir("generated/sources/composeSnippets/wasmJsMain/kotlin")

        val wasmJsMain by getting {
            kotlin.srcDir(generatedSnippetsDir)
            dependencies {
                implementation("org.jetbrains.compose.runtime:runtime:1.12.0")
                implementation("org.jetbrains.compose.foundation:foundation:1.12.0")
                implementation("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation("org.jetbrains.compose.ui:ui:1.12.0")
                implementation("org.jetbrains.compose.components:components-resources:1.12.0")
            }
        }
    }
}

val pullSnippets by tasks.registering(Sync::class) {
    from("${rootProject.projectDir}/compose/snippets/src/main/java/com/example/compose/snippets/components") {
        include("*.kt")
        exclude("ComponentsScreen.kt")
        into("com/example/compose/snippets/components")
        filter { line ->
            line.replace("LocalLocale.current.platformLocale", "Locale.getDefault()")
        }
    }
    into(layout.buildDirectory.dir("generated/sources/composeSnippets/wasmJsMain/kotlin"))
    doLast {
        val navDrawer = file("${layout.buildDirectory.get()}/generated/sources/composeSnippets/wasmJsMain/kotlin/com/example/compose/snippets/components/NavigationDrawer.kt")
        if (navDrawer.exists()) {
            val text = navDrawer.readText()
            if (!text.contains("package com.example.compose.snippets.components")) {
                navDrawer.writeText("package com.example.compose.snippets.components\n\n" + text)
            }
        }
    }
}

tasks.named("compileKotlinWasmJs") {
    dependsOn(pullSnippets)
}

tasks.matching { it.name.contains("KotlinWasmJsOptimize") }.configureEach {
    enabled = false
}

tasks.register<Copy>("packageStaticSite") {
    group = "distribution"
    description = "Packages the complete static website (HTML, WASM, JS, and screenshots) for GitHub Pages"
    dependsOn("wasmJsDevelopmentExecutableCompileSync", "wasmJsProcessResources")

    into(layout.buildDirectory.dir("dist/site"))

    // Copy static website HTML & assets
    from("src/wasmJsMain/resources") {
        include("**/*")
    }

    // Copy compiled WASM and JS from compileSync
    from(layout.buildDirectory.dir("compileSync/wasmJs/main/developmentExecutable/kotlin")) {
        include("*.wasm")
        include("*.mjs")
        include("*.js")
        include("*.map")
    }

    // Copy skiko runtime (skiko.wasm, skiko.mjs)
    from(layout.buildDirectory.dir("compose/skiko-runtime-processed-wasmjs")) {
        include("skiko.wasm")
        include("skiko.mjs")
    }
}
