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
                .replace("import android.app.Activity", "// import android.app.Activity")
                .replace("import android.content.pm.ActivityInfo", "// import android.content.pm.ActivityInfo")
                .replace("import androidx.compose.ui.platform.LocalContext", "// import androidx.compose.ui.platform.LocalContext")
                .replace("val context = LocalContext.current", "val context: Any? = null")
                .replace("ActivityInfo.SCREEN_ORIENTATION_PORTRAIT", "0")
                .replace("ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED", "-1")
                .replace("(context as? Activity)?.requestedOrientation", "// (context as? Activity)?.requestedOrientation")
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

val packageDevelopmentSite by tasks.registering(Copy::class) {
    group = "distribution"
    description = "Packages the interactive WASM preview and screenshots with fast-compiling development WASM binary"
    dependsOn("wasmJsDevelopmentExecutableCompileSync", "wasmJsProcessResources")

    into(layout.buildDirectory.dir("dist/site"))

    // Copy WASM runner HTML, fonts, and generated Roborazzi screenshots
    from("src/wasmJsMain/resources") {
        include("**/*")
    }
    from("src/wasmJsMain/resources/wasm.html") {
        rename("wasm.html", "index.html")
    }

    // Copy compiled development WASM and JS from compileSync
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

val packageStaticSite by tasks.registering(Copy::class) {
    group = "distribution"
    description = "Packages the interactive WASM preview and screenshots with optimized production WASM binary for deployment"
    dependsOn("wasmJsProductionExecutableCompileSync", "wasmJsProcessResources")

    into(layout.buildDirectory.dir("dist/site"))

    // Copy WASM runner HTML, fonts, and generated Roborazzi screenshots
    from("src/wasmJsMain/resources") {
        include("**/*")
    }
    from("src/wasmJsMain/resources/wasm.html") {
        rename("wasm.html", "index.html")
    }

    // Copy compiled production WASM and JS from compileSync
    from(layout.buildDirectory.dir("compileSync/wasmJs/main/productionExecutable/kotlin")) {
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

tasks.register("buildPreviewSite") {
    group = "distribution"
    description = "Alias for packageStaticSite used by CI"
    dependsOn(packageStaticSite)
}


