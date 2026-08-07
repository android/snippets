package com.example.aiglasses.camera

import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.ProjectedDisplayController
import androidx.xr.projected.ProjectedDisplayController.PresentationMode.Companion.VISUALS_ON
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Base activity for AI Glasses projected samples (`CameraAction`, `MotionGesture`, `BackGesture`).
 *
 * Encapsulates non-sample-specific hardware boilerplate and state management:
 * - Initializes [TextToSpeech] using [ProjectedDeviceContext] (`USAGE_ASSISTANT`) for glasses speaker routing.
 * - Observes [ProjectedDisplayController] presentation modes (`VISUALS_ON`) into [isDisplayOn].
 * - Holds common UI state ([notificationText], [gestureSourceText]) so samples don't need to declare them.
 * - Provides [triggerFeedback] for unified visual chips, [Toast], and audio [TextToSpeech] notifications.
 */
abstract class BaseProjectedActivity(val sampleTitle: String) : ComponentActivity() {

    companion object {
        private const val BASE_TAG = "BaseProjectedActivity"
    }

    /** Observable Compose state indicating whether the projected display currently has visuals active. */
    val isDisplayOn = mutableStateOf(true)

    /** Observable notification message string for Glimmer UI rendering. */
    val notificationText = mutableStateOf<String?>(null)

    /** Observable source description (e.g., "Touchpad Tap", "ACTION_DOWN") for Glimmer UI rendering. */
    val gestureSourceText = mutableStateOf<String?>(null)

    /** Counter incremented on every triggerFeedback invocation so UI reacts even for repeated identical messages. */
    val feedbackEventCount = mutableStateOf(0)

    private var tts: TextToSpeech? = null
    private var displayController: ProjectedDisplayController? = null
    private var lastSpokenTimestamp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeTextToSpeech()
        observeDisplayState()
    }

    private fun initializeTextToSpeech() {
        val ttsContext = try {
            ProjectedContext.createProjectedDeviceContext(this)
        } catch (e: Exception) {
            Log.w(BASE_TAG, "Could not create ProjectedDeviceContext for TTS, falling back to activity context: ${e.message}")
            this
        }
        tts = TextToSpeech(ttsContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(attributes)
                onTextToSpeechReady()
            } else {
                Log.e(BASE_TAG, "TextToSpeech initialization failed.")
            }
        }
    }

    protected open fun onTextToSpeechReady() {}

    private fun observeDisplayState() {
        lifecycleScope.launch {
            try {
                val controller = ProjectedDisplayController.create(this@BaseProjectedActivity)
                displayController = controller
                controller.addPresentationModeChangedListener { flags ->
                    val visualsOn = flags.hasPresentationMode(VISUALS_ON)
                    isDisplayOn.value = visualsOn
                    Log.d(BASE_TAG, "Display state changed. Visuals On: $visualsOn")
                }
            } catch (e: Exception) {
                Log.w(BASE_TAG, "ProjectedDisplayController not available: ${e.message}")
            }
        }
    }

    /**
     * Updates observable UI states ([notificationText], [gestureSourceText]) and triggers
     * a spoken and visual [Toast] notification on the AI Glasses.
     *
     * @param message The text string to speak and display.
     * @param source Description of the trigger source (e.g., "System Back", "Touchpad Swipe").
     * @param utteranceId Identifier for the TTS utterance.
     * @param throttleMs Minimum milliseconds between consecutive speeches.
     */
    fun triggerFeedback(
        message: String,
        source: String,
        utteranceId: String = "projected_utterance",
        throttleMs: Long = 0L,
        speakAudio: Boolean = true
    ) {
        val now = System.currentTimeMillis()
        if (throttleMs > 0L && now - lastSpokenTimestamp < throttleMs) {
            return
        }
        lastSpokenTimestamp = now

        notificationText.value = message
        gestureSourceText.value = source
        feedbackEventCount.value = feedbackEventCount.value + 1

        Toast.makeText(this, "$message ($source)", Toast.LENGTH_SHORT).show()
        if (speakAudio) {
            tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        displayController?.close()
        tts?.stop()
        tts?.shutdown()
    }
}
