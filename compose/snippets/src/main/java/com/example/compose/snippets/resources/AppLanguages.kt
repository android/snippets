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

package com.example.compose.snippets.resources

import android.app.LocaleConfig
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun overrideLocaleConfigSnippet(applicationContext: Context) {
    // [START android_resources_app_languages_override_locale_config]
    //For setOverrideLocaleConfig
    val localeManager = applicationContext
        .getSystemService(LocaleManager::class.java)
    localeManager.overrideLocaleConfig = LocaleConfig(
    LocaleList.forLanguageTags("en-US,ja-JP,zh-Hans-SG")
    )

    //For getOverrideLocaleConfig
    // The app calls the API to get the override LocaleConfig
    val overrideLocaleConfig = localeManager.overrideLocaleConfig
    // If the returned overrideLocaleConfig isn't equal to NULL, then the app calls the API to get the supported Locales
    val supportedLocales = overrideLocaleConfig?.supportedLocales
    // [END android_resources_app_languages_override_locale_config]
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun currentAppLocalesSnippet(applicationContext: Context, appPackageName: String) {
    // [START android_resources_app_languages_current_app_locales]
    val currentAppLocales: LocaleList = applicationContext.getSystemService(LocaleManager::class.java).getApplicationLocales(appPackageName)
    // [END android_resources_app_languages_current_app_locales]
}

// [START android_resources_app_languages_language_selector]
@Composable
fun LanguageSelector() {
    // Retrieve the currently configured app locale.
    // If no app-specific locale is set, LocaleListCompat.get(0) returns null,
    // so we safely fall back to a default (e.g., "en").
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val currentLocaleTag = appLocales.get(0)?.toLanguageTag() ?: "en"

    // Example UI: A button to toggle between English and Spanish
    Button(
        onClick = {
            val newLanguageTag = if (currentLocaleTag == "en") "es" else "en"
            val localeList = LocaleListCompat.forLanguageTags(newLanguageTag)

            // Setting the locale re-creates the Activity by default,
            // which automatically applies the new configuration to Compose.
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    ) {
        Text(
            text = if (currentLocaleTag == "en") "Switch to Spanish" else "Switch to English"
        )
    }
}
// [END android_resources_app_languages_language_selector]

private fun setAppLocaleSnippet() {
    // [START android_resources_app_languages_set_app_locale]
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("xx-YY")
    // Call this on the main thread as it may require Activity.restart()
    AppCompatDelegate.setApplicationLocales(appLocale)
    // [END android_resources_app_languages_set_app_locale]
}

// [START android_resources_app_languages_framework_set_get]
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun setAppLanguage(context: Context, languageTag: String) {
    // 1. Retrieve the system service
    val localeManager = context.getSystemService(LocaleManager::class.java)

    // 2. Create a LocaleList from the language tag (e.g., "es-ES" or "ja")
    val localeList = LocaleList(Locale.forLanguageTag(languageTag))

    // 3. Set the locale. The system automatically updates the locale and
    // restarts the app, including any necessary configuration updates.
    localeManager.applicationLocales = localeList
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getAppLanguage(context: Context): String {
    val localeManager = context.getSystemService(LocaleManager::class.java)
    val currentLocales = localeManager.applicationLocales

    // Return the primary app locale, or fall back to the system default
    return if (!currentLocales.isEmpty) {
        currentLocales.get(0).toLanguageTag()
    } else {
        Locale.getDefault().toLanguageTag()
    }
}
// [END android_resources_app_languages_framework_set_get]

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun ResetLocaleSnippet() {
    // [START android_resources_app_languages_reset_locale]
    // Use the AndroidX APIs to reset to the system locale for backward and forward compatibility
    AppCompatDelegate.setApplicationLocales(
      LocaleListCompat.getEmptyLocaleList()
    )

    // Or use the Framework APIs for Android 13 and above to reset to the system locale
    val context = LocalContext.current
    context.getSystemService(LocaleManager::class.java)
      .applicationLocales = LocaleList.getEmptyLocaleList()
    // [END android_resources_app_languages_reset_locale]
}
