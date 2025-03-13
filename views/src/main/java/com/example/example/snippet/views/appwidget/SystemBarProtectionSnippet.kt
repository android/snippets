package com.example.example.snippet.views.appwidget

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

        // [START system-bar-protection_kotlin]
        findViewById<ProtectionLayout>(R.id.list_protection)
            .setProtections(
                listOf(
                    GradientProtection(
                        WindowInsetsCompat.Side.TOP
                    )
                )
            )
        // [END system-bar-protection_kotlin]
    }
}
