/*
 * Copyright 2025 The Android Open Source Project
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

package insets

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.insets.GradientProtection
import androidx.core.view.insets.ProtectionLayout
import com.example.example.snippet.views.R

class SystemBarProtectionSnippet : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.system_bar_protection)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.item_list)
        ) { v: View, insets: WindowInsetsCompat ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(
                innerPadding.left,
                innerPadding.top,
                innerPadding.right,
                innerPadding.bottom
            )
            insets
        }

        val red = 52
        val green = 168
        val blue = 83
        val paneBackgroundColor = Color.rgb(red, green, blue)
        // [START android_system_bar_protection_kotlin]
        findViewById<ProtectionLayout>(R.id.list_protection)
            .setProtections(
                listOf(
                    GradientProtection(
                        WindowInsetsCompat.Side.TOP,
                        // Ideally, this is the pane's background color
                        paneBackgroundColor
                    )
                )
            )
        // [END android_system_bar_protection_kotlin]
    }
}
