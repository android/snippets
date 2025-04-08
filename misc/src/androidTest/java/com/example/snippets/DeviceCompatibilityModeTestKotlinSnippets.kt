package com.example.snippets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class DeviceCompatibilityModeTestKotlinSnippets: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    // [START android_device_compatibility_mode_assert_isLetterboxed_kotlin]
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun activity_launched_notLetterBoxed() {
        activityRule.scenario.onActivity {
            assertFalse(it.isLetterboxed())
        }
    }
    // [END android_device_compatibility_mode_assert_isLetterboxed_kotlin]


    // Classes used by snippets.

    class MainActivity: AppCompatActivity() {

        fun isLetterboxed(): Boolean {
            return true
        }
    }
}