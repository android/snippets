package com.example.snippets

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator

class DeviceCompatibilityModeKotlinSnippets: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
      super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    // [START android_device_compatibility_mode_isLetterboxed_kotlin]
    fun isLetterboxed(activity: AppCompatActivity) : Boolean {
        if (isInMultiWindowMode) return false

        val wmc = WindowMetricsCalculator.getOrCreate()
        val currentBounds = wmc.computeCurrentWindowMetrics(this).bounds
        val maxBounds = wmc.computeMaximumWindowMetrics(this).bounds

        val isScreenPortrait = maxBounds.height() > maxBounds.width()

        return if (isScreenPortrait) {
            currentBounds.height() < maxBounds.height()
        } else {
            currentBounds.width() < maxBounds.width()
        }
    }
    // [END android_device_compatibility_mode_isLetterboxed_kotlin]

}