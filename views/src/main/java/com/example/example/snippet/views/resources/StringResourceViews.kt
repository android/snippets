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

package com.example.example.snippet.views.resources

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Annotation
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.example.example.snippet.views.R

private const val TEXT_EXTRA = "text_extra"

private class StringResourceViewsActivity : ComponentActivity() {
    private lateinit var styledText: TextView

    private fun getStringExample() {
        // [START android_resources_string_views_get_string]
        val string: String = getString(R.string.hello)
        // [END android_resources_string_views_get_string]
    }

    private fun getStringArrayExample() {
        // [START android_resources_string_views_get_string_array]
        val array: Array<String> = resources.getStringArray(R.array.planets_array)
        // [END android_resources_string_views_get_string_array]
    }

    private fun getQuantityStringExample() {
        // [START android_resources_string_views_get_quantity_string]
        val count = getNumberOfSongsAvailable()
        val songsFound = resources.getQuantityString(R.plurals.numberOfSongsAvailable, count, count)
        // [END android_resources_string_views_get_quantity_string]
    }

    private fun formatStringExample(username: String, mailCount: Int) {
        // [START android_resources_string_views_format_string]
        var text = getString(R.string.welcome_messages, username, mailCount)
        // [END android_resources_string_views_format_string]
    }

    private fun fromHtmlExample(username: String, mailCount: Int) {
        // [START android_resources_string_views_from_html]
        val text: String = getString(R.string.welcome_messages, username, mailCount)
        val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_LEGACY)
        // [END android_resources_string_views_from_html]
    }

    private fun htmlEncodeExample(username: String, mailCount: Int) {
        // [START android_resources_string_views_html_encode]
        val escapedUsername: String = TextUtils.htmlEncode(username)

        val text: String = getString(R.string.welcome_messages, escapedUsername, mailCount)
        val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_LEGACY)
        // [END android_resources_string_views_html_encode]
    }

    // [START android_resources_string_views_spannable_helpers]
    /**
     * Returns a CharSequence that concatenates the specified array of CharSequence
     * objects and then applies a list of zero or more tags to the entire range.
     *
     * @param content an array of character sequences to apply a style to
     * @param tags the styled span objects to apply to the content
     *        such as android.text.style.StyleSpan
     */
    private fun apply(content: Array<out CharSequence>, vararg tags: Any): CharSequence {
        return SpannableStringBuilder().apply {
            openTags(tags)
            content.forEach { charSequence ->
                append(charSequence)
            }
            closeTags(tags)
        }
    }

    /**
     * Iterates over an array of tags and applies them to the beginning of the specified
     * Spannable object so that future text appended to the text will have the styling
     * applied to it. Do not call this method directly.
     */
    private fun Spannable.openTags(tags: Array<out Any>) {
        tags.forEach { tag ->
            setSpan(tag, 0, 0, Spannable.SPAN_MARK_MARK)
        }
    }

    /**
     * "Closes" the specified tags on a Spannable by updating the spans to be
     * endpoint-exclusive so that future text appended to the end will not take
     * on the same styling. Do not call this method directly.
     */
    private fun Spannable.closeTags(tags: Array<out Any>) {
        tags.forEach { tag ->
            if (length > 0) {
                setSpan(tag, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                removeSpan(tag)
            }
        }
    }
    // [END android_resources_string_views_spannable_helpers]

    // [START android_resources_string_views_spannable_styles]
    /**
     * Returns a CharSequence that applies boldface to the concatenation
     * of the specified CharSequence objects.
     */
    fun bold(vararg content: CharSequence): CharSequence = apply(content, StyleSpan(Typeface.BOLD))

    /**
     * Returns a CharSequence that applies italics to the concatenation
     * of the specified CharSequence objects.
     */
    fun italic(vararg content: CharSequence): CharSequence = apply(content, StyleSpan(Typeface.ITALIC))

    /**
     * Returns a CharSequence that applies a foreground color to the
     * concatenation of the specified CharSequence objects.
     */
    fun color(color: Int, vararg content: CharSequence): CharSequence =
        apply(content, ForegroundColorSpan(color))
    // [END android_resources_string_views_spannable_styles]

    private fun spannableChainExample() {
        // [START android_resources_string_views_spannable_chain]
        // Create an italic "hello, " a red "world",
        // and bold the entire sequence.
        val text: CharSequence = bold(
            italic(getString(R.string.hello)),
            color(Color.RED, getString(R.string.world))
        )
        // [END android_resources_string_views_spannable_chain]
    }

    private fun annotationTypefaceExample() {
        // [START android_resources_string_views_annotation_typeface]
        // get the text as SpannedString so we can get the spans attached to the text.
        val titleText = getText(R.string.title) as SpannedString

        // get all the annotation spans from the text.
        val annotations = titleText.getSpans(0, titleText.length, Annotation::class.java)

        // create a copy of the title text as a SpannableString.
        // the constructor copies both the text and the spans. so we can add and remove spans.
        val spannableString = SpannableString(titleText)

        // iterate through all the annotation spans.
        for (annotation in annotations) {
            // look for the span with the key font.
            if (annotation.key == "font") {
                val fontName = annotation.value
                // check the value associated to the annotation key.
                if (fontName == "title_emphasis") {
                    // create the typeface.
                    val typeface = getFontCompat(R.font.permanent_marker)
                    // set the span at the same indices as the annotation.
                    spannableString.setSpan(
                        CustomTypefaceSpan(typeface),
                        titleText.getSpanStart(annotation),
                        titleText.getSpanEnd(annotation),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        // now, the spannableString contains both the annotation spans and the CustomTypefaceSpan.
        styledText.text = spannableString
        // [END android_resources_string_views_annotation_typeface]
    }

    private fun sendAnnotatedSpannableExample() {
        // [START android_resources_string_views_annotation_parcel_send]
        val spannableString = SpannableString("My spantastic text")
        val annotation = Annotation("font", "title_emphasis")
        spannableString.setSpan(annotation, 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // start Activity with text with spans.
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(TEXT_EXTRA, spannableString)
        startActivity(intent)
        // [END android_resources_string_views_annotation_parcel_send]
    }

    private fun receiveAnnotatedSpannableExample() {
        // [START android_resources_string_views_annotation_parcel_receive]
        // read text with Spans.
        val intentCharSequence = intent.getCharSequenceExtra(TEXT_EXTRA) as SpannableString
        // [END android_resources_string_views_annotation_parcel_receive]
    }

    private fun getNumberOfSongsAvailable(): Int = 5

    private fun getFontCompat(@FontRes id: Int): Typeface =
        ResourcesCompat.getFont(this, id) ?: Typeface.DEFAULT

    private class CustomTypefaceSpan(private val customTypeface: Typeface) : MetricAffectingSpan() {
        override fun updateMeasureState(textPaint: TextPaint) {
            textPaint.typeface = customTypeface
        }

        override fun updateDrawState(tp: TextPaint) {
            tp.typeface = customTypeface
        }
    }

    private class MainActivity : ComponentActivity()
}
