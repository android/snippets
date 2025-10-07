import org.codehaus.groovy.runtime.DefaultGroovyMethods.step
import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.target

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.spotless) apply false
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")

            val disabledRules = arrayOf(
                // These rules were introduced in ktlint 0.46.0 and should not be
                // enabled without further discussion. They are disabled for now.
                // See: https://github.com/pinterest/ktlint/releases/tag/0.46.0
                "filename",
                "annotation",
                "annotation-spacing",
                "argument-list-wrapping",
                "double-colon-spacing",
                "enum-entry-name-case",
                "multiline-if-else",
                "no-empty-first-line-in-method-block",
                "package-name",
                "trailing-comma",
                "spacing-around-angle-brackets",
                "spacing-between-declarations-with-annotations",
                "spacing-between-declarations-with-comments",
                "unary-op-spacing",
                "no-trailing-spaces",
                "max-line-length",
                // Disabled rules that were introduced or changed between 0.46.0 ~ 1.50.0
                "class-signature",
                "trailing-comma-on-call-site",
                "trailing-comma-on-declaration-site",
                "comment-wrapping",
                "function-literal",
                "function-signature",
                "function-expression-body",
                "function-start-of-body-spacing",
                "multiline-expression-wrapping",
            )

            ktlint(libs.versions.ktlint.get()).editorConfigOverride(
                mapOf(
                    "android" to "true",
                    "ktlint_code_style" to "android_studio",
                    "ij_kotlin_allow_trailing_comma" to "true",
                ) + disabledRules.map { Pair("ktlint_standard_$it", "disabled") }
            )

            // ktlint 7.0.0 introduces lints, which existing snippets do not satisfy
            val kotlinSuppressLints = arrayOf(
                "standard:function-naming",
                "standard:property-naming",
                "standard:class-naming",
                "standard:max-line-length",
                "standard:comment-wrapping",
                "standard:import-ordering",
                "standard:filename",
                "standard:backing-property-naming",
            )
            for (lint in kotlinSuppressLints) {
                suppressLintsFor {
                    step = "ktlint"
                    shortCode = lint
                }
            }

            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            // Look for the first line that doesn't have a block comment (assumed to be the license)
            licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            // Look for the root tag or a tag that is a snippet
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[a-zA-Z])|(<!--\\s+(//\\s*)?\\[START)").skipLinesMatching(".*START.*")
        }
    }
}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")

