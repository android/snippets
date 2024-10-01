/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused", "UNUSED_VARIABLE", "PreviewMustBeTopLevelFunction")

package com.example.compose.snippets.text

import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * This file lets DevRel track changes to snippets present in
 * https://developer.android.com/jetpack/compose/text
 *
 * No action required if it's modified.
 */

private object SimpleTextSnippet {
    // [START android_compose_text_simple_text]
    @Composable
    fun SimpleText() {
        Text("Hello World")
    }
    // [END android_compose_text_simple_text]
}

private object StringResourceSnippet {
    // [START android_compose_text_resource]
    @Composable
    fun StringResourceText() {
        Text(stringResource(R.string.hello_world))
    }
    // [END android_compose_text_resource]
}

private object TextColorSnippet {
    // [START android_compose_text_color]
    @Composable
    fun BlueText() {
        Text("Hello World", color = Color.Blue)
    }
    // [END android_compose_text_color]
}

private object TextSizeSnippet {
    // [START android_compose_text_size]
    @Composable
    fun BigText() {
        Text("Hello World", fontSize = 30.sp)
    }
    // [END android_compose_text_size]
}

private object TextItalicSnippet {
    // [START android_compose_text_italic]
    @Composable
    fun ItalicText() {
        Text("Hello World", fontStyle = FontStyle.Italic)
    }
    // [END android_compose_text_italic]
}

private object TextBoldSnippet {
    // [START android_compose_text_bold]
    @Composable
    fun BoldText() {
        Text("Hello World", fontWeight = FontWeight.Bold)
    }
    // [END android_compose_text_bold]
}

private object TextAlignmentSnippet {
    // [START android_compose_text_alignment]
    @Composable
    fun CenterText() {
        Text(
            "Hello World", textAlign = TextAlign.Center, modifier = Modifier.width(150.dp)
        )
    }
    // [END android_compose_text_alignment]
}

private object TextShadowSnippet {
    // [START android_compose_text_shadow]
    @Composable
    fun TextShadow() {
        val offset = Offset(5.0f, 10.0f)
        Text(
            text = "Hello world!",
            style = TextStyle(
                fontSize = 24.sp,
                shadow = Shadow(
                    color = Color.Blue, offset = offset, blurRadius = 3f
                )
            )
        )
    }
    // [END android_compose_text_shadow]
}

private object TextMultipleFontsSnippet {
    // [START android_compose_text_multiple_fonts]
    @Composable
    fun DifferentFonts() {
        Column {
            Text("Hello World", fontFamily = FontFamily.Serif)
            Text("Hello World", fontFamily = FontFamily.SansSerif)
        }
    }
    // [END android_compose_text_multiple_fonts]
}

@Composable
private fun TextDefineFontFamilySnippet() {
    // [START android_compose_text_multiple_fonts_styles]
    val firaSansFamily = FontFamily(
        Font(R.font.firasans_light, FontWeight.Light),
        Font(R.font.firasans_regular, FontWeight.Normal),
        Font(R.font.firasans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.firasans_medium, FontWeight.Medium),
        Font(R.font.firasans_bold, FontWeight.Bold)
    )
    // [END android_compose_text_multiple_fonts_styles]
}

@Composable
private fun TextFontWeightSnippet() {
    // [START android_compose_text_font_weight]
    Column {
        Text(text = "text", fontFamily = firaSansFamily, fontWeight = FontWeight.Light)
        Text(text = "text", fontFamily = firaSansFamily, fontWeight = FontWeight.Normal)
        Text(
            text = "text",
            fontFamily = firaSansFamily,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic
        )
        Text(text = "text", fontFamily = firaSansFamily, fontWeight = FontWeight.Medium)
        Text(text = "text", fontFamily = firaSansFamily, fontWeight = FontWeight.Bold)
    }
    // [END android_compose_text_font_weight]
}

private object TextMultipleStylesSnippet {
    // [START android_compose_text_multistyle]
    @Composable
    fun MultipleStylesInText() {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append("H")
                }
                append("ello ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)) {
                    append("W")
                }
                append("orld")
            }
        )
    }
    // [END android_compose_text_multistyle]
}

private object TextParagraphStyleSnippet {
    // [START android_compose_text_paragraph_style]
    @Composable
    fun ParagraphStyle() {
        Text(
            buildAnnotatedString {
                withStyle(style = ParagraphStyle(lineHeight = 30.sp)) {
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append("Hello\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold, color = Color.Red
                        )
                    ) {
                        append("World\n")
                    }
                    append("Compose")
                }
            }
        )
    }
    // [END android_compose_text_paragraph_style]
}

private object TextMaxLinesSnippet {
    // [START android_compose_text_maxlines]
    @Composable
    fun LongText() {
        Text("hello ".repeat(50), maxLines = 2)
    }
    // [END android_compose_text_maxlines]
}

private object TextOverflowSnippet {
    // [START android_compose_text_overflow]
    @Composable
    fun OverflowedText() {
        Text("Hello Compose ".repeat(50), maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
    // [END android_compose_text_overflow]
}

private object TextBrushSnippet1 {
    @Composable
    fun TextStyledBrushSnippet(text: String) {
        // [START android_compose_text_brush]
        val gradientColors = listOf(Cyan, LightBlue, Purple /*...*/)

        Text(
            text = text,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            )
        )
        // [END android_compose_text_brush]
    }
}

private object TextBrushSnippet2 {
    @Composable
    fun TextStyledBrushSnippet() {
        val rainbowColors: List<Color> = listOf()
        // [START android_compose_text_textfield_brush]
        var text by remember { mutableStateOf("") }
        val brush = remember {
            Brush.linearGradient(
                colors = rainbowColors
            )
        }
        TextField(
            value = text, onValueChange = { text = it }, textStyle = TextStyle(brush = brush)
        )
        // [END android_compose_text_textfield_brush]
    }
}

private object TextBrushSnippet3 {
    @Composable
    fun TextStyledBrushSnippet() {
        val rainbowColors: List<Color> = listOf()
        // [START android_compose_text_annotatedString_brush]
        Text(
            text = buildAnnotatedString {
                append("Do not allow people to dim your shine\n")
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(
                            colors = rainbowColors
                        )
                    )
                ) {
                    append("because they are blinded.")
                }
                append("\nTell them to put some sunglasses on.")
            }
        )
        // [END android_compose_text_annotatedString_brush]
    }
}

private object TextBrushSnippet4 {
    @Composable
    fun TextStyledBrushSnippet() {
        val rainbowColors: List<Color> = listOf()
        // [START android_compose_text_alpha_brush]
        val brush = Brush.linearGradient(colors = rainbowColors)

        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = brush, alpha = .5f
                )
            ) {
                append("Text in ")
            }
            withStyle(
                SpanStyle(
                    brush = brush, alpha = 1f
                )
            ) {
                append("Compose ❤️")
            }
        }
        // [END android_compose_text_alpha_brush]
    }
}

private object TextIncludeFontPaddingSnippet {
    @Composable
    fun AlignedText(text: String) {
        // [START android_compose_text_includeFontPadding]
        Text(
            text = text,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    lineHeight = 2.5.em,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            )
        )
        // [END android_compose_text_includeFontPadding]
    }
}

private object TextSelectableSnippet {
    // [START android_compose_text_selection]
    @Composable
    fun SelectableText() {
        SelectionContainer {
            Text("This text is selectable")
        }
    }
    // [END android_compose_text_selection]
}

private object TextPartiallySelectableSnippet {
    // [START android_compose_text_partial_selection]
    @Composable
    fun PartiallySelectableText() {
        SelectionContainer {
            Column {
                Text("This text is selectable")
                Text("This one too")
                Text("This one as well")
                DisableSelection {
                    Text("But not this one")
                    Text("Neither this one")
                }
                Text("But again, you can select this one")
                Text("And this one too")
            }
        }
    }
    // [END android_compose_text_partial_selection]
}

private object TextTextFieldSnippet {
    // [START android_compose_text_textfield_filled]
    @Composable
    fun SimpleFilledTextFieldSample() {
        var text by remember { mutableStateOf("Hello") }

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label") }
        )
    }
    // [END android_compose_text_textfield_filled]
}

private object TextOutlinedTextFieldSnippet {
    // [START android_compose_text_textfield_outlined]
    @Composable
    fun SimpleOutlinedTextFieldSample() {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label") }
        )
    }
    // [END android_compose_text_textfield_outlined]
}

private object TextStylingTextFieldSnippet {
    // [START android_compose_text_textfield_styled]
    @Composable
    fun StyledTextField() {
        var value by remember { mutableStateOf("Hello\nWorld\nInvisible") }

        TextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Enter text") },
            maxLines = 2,
            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(20.dp)
        )
    }
    // [END android_compose_text_textfield_styled]
}

private object TextFormattingTextFieldSnippet {
    // [START android_compose_text_textfield_visualtransformation]
    @Composable
    fun PasswordTextField() {
        var password by rememberSaveable { mutableStateOf("") }

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
    // [END android_compose_text_textfield_visualtransformation]
}

private object TextCleanInputSnippet {
    // [START android_compose_text_textfield_clean_input]
    @Composable
    fun NoLeadingZeroes() {
        var input by rememberSaveable { mutableStateOf("") }
        TextField(
            value = input,
            onValueChange = { newText ->
                input = newText.trimStart { it == '0' }
            }
        )
    }
    // [END android_compose_text_textfield_clean_input]
}

/** Effective State management **/

private object TextEffectiveStateManagement1 {
    // [START android_compose_text_state_viewmodel]
    class SignUpViewModel : ViewModel() {

        var username by mutableStateOf("")
            private set

        /* ... */
    }
    // [END android_compose_text_state_viewmodel]
}

private object TextEffectiveStateManagement2 {
    class UserRepository

    private val viewModel = SignUpViewModel(UserRepository())

    // [START android_compose_text_state_management]
    // SignUpViewModel.kt

    class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

        var username by mutableStateOf("")
            private set

        fun updateUsername(input: String) {
            username = input
        }
    }

    // SignUpScreen.kt

    @Composable
    fun SignUpScreen(/*...*/) {

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { username -> viewModel.updateUsername(username) }
            /*...*/
        )
    }
    // [END android_compose_text_state_management]
}

// [START android_compose_text_link_1]
@Composable
fun AnnotatedStringWithLinkSample() {
    // Display multiple links in the text
    Text(
        buildAnnotatedString {
            append("Go to the ")
            withLink(
                LinkAnnotation.Url(
                    "https://developer.android.com/",
                    TextLinkStyles(style = SpanStyle(color = Color.Blue))
                )
            ) {
                append("Android Developers ")
            }
            append("website, and check out the")
            withLink(
                LinkAnnotation.Url(
                    "https://developer.android.com/jetpack/compose",
                    TextLinkStyles(style = SpanStyle(color = Color.Green))
                )
            ) {
                append("Compose guidance")
            }
            append(".")
        }
    )
}
// [END android_compose_text_link_1]

// [START android_compose_text_link_2]
@Composable
fun AnnotatedStringWithListenerSample() {
    // Display a link in the text and log metrics whenever user clicks on it. In that case we handle
    // the link using openUri method of the LocalUriHandler
    val uriHandler = LocalUriHandler.current
    Text(
        buildAnnotatedString {
            append("Build better apps faster with ")
            val link =
                LinkAnnotation.Url(
                    "https://developer.android.com/jetpack/compose",
                    TextLinkStyles(SpanStyle(color = Color.Blue))
                ) {
                    val url = (it as LinkAnnotation.Url).url
                    // log some metrics
                    uriHandler.openUri(url)
                }
            withLink(link) { append("Jetpack Compose") }
        }
    )
}
// [END android_compose_text_link_2]

@Composable
private fun TextSample(samples: Map<String, @Composable () -> Unit>) {
    MaterialTheme {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                samples.forEach { (title, content) ->
                    Row(Modifier.fillMaxWidth()) {
                        content()
                        Text(
                            text = title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

private const val SAMPLE_LONG_TEXT =
    "Jetpack Compose is Android’s modern toolkit for building native UI. " +
        "It simplifies and accelerates UI development on Android bringing your apps " +
        "to life with less code, powerful tools, and intuitive Kotlin APIs. " +
        "It makes building Android UI faster and easier."

@Composable
@Preview
fun LineBreakSample() {
    // [START android_compose_text_line_break]
    TextSample(
        samples = mapOf(
            "Simple" to {
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(130.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default.copy(
                        lineBreak = LineBreak.Simple
                    )
                )
            },
            "Paragraph" to {
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(130.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default.copy(
                        lineBreak = LineBreak.Paragraph
                    )
                )
            }
        )
    )
    // [END android_compose_text_line_break]
}

@Preview
@Composable
fun SmallScreenTextSnippet() {
    // [START android_compose_text_paragraph]
    TextSample(
        samples = mapOf(
            "Balanced" to {
                val smallScreenAdaptedParagraph =
                    LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Balanced)
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(200.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default.copy(
                        lineBreak = smallScreenAdaptedParagraph
                    )
                )
            },
            "Default" to {
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(200.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default
                )
            }
        )
    )
    // [END android_compose_text_paragraph]
}

private object CJKTextSnippet {
    @Composable
    fun CJKSample() {
        // [START android_compose_text_cjk]
        val customTitleLineBreak = LineBreak(
            strategy = LineBreak.Strategy.HighQuality,
            strictness = LineBreak.Strictness.Strict,
            wordBreak = LineBreak.WordBreak.Phrase
        )
        Text(
            text = "あなたに寄り添う最先端のテクノロジー。",
            modifier = Modifier.width(250.dp),
            fontSize = 14.sp,
            style = TextStyle.Default.copy(
                lineBreak = customTitleLineBreak
            )
        )
        // [END android_compose_text_cjk]
    }
}

@Preview
@Composable
fun HyphenateTextSnippet() {
    // [START android_compose_text_hyphen]
    TextSample(
        samples = mapOf(
            "Hyphens - None" to {
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(130.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default.copy(
                        lineBreak = LineBreak.Paragraph,
                        hyphens = Hyphens.None
                    )
                )
            },
            "Hyphens - Auto" to {
                Text(
                    text = SAMPLE_LONG_TEXT,
                    modifier = Modifier
                        .width(130.dp)
                        .border(BorderStroke(1.dp, Color.Gray)),
                    fontSize = 14.sp,
                    style = TextStyle.Default.copy(
                        lineBreak = LineBreak.Paragraph,
                        hyphens = Hyphens.Auto
                    )
                )
            }
        )
    )
    // [END android_compose_text_hyphen]
}

@Preview(showBackground = true)
// [START android_compose_text_marquee]
@Composable
fun BasicMarqueeSample() {
    // Marquee only animates when the content doesn't fit in the max width.
    Column(Modifier.width(400.dp)) {
        Text(
            "Learn about why it's great to use Jetpack Compose",
            modifier = Modifier.basicMarquee(),
            fontSize = 50.sp
        )
    }
}
// [END android_compose_text_marquee]

// [START android_compose_text_auto_format_phone_number_textfieldconfig]
@Composable
fun PhoneNumber() {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    val numericRegex = Regex("[^0-9]")
    TextField(
        value = phoneNumber,
        onValueChange = {
            // Remove non-numeric characters.
            val stripped = numericRegex.replace(it, "")
            phoneNumber = if (stripped.length >= 10) {
                stripped.substring(0..9)
            } else {
                stripped
            }
        },
        label = { Text("Enter Phone Number") },
        visualTransformation = NanpVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
// [END android_compose_text_auto_format_phone_number_textfieldconfig]

// [START android_compose_text_auto_format_phone_number_transformtext]
class NanpVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text

        var out = if (trimmed.isNotEmpty()) "(" else ""

        for (i in trimmed.indices) {
            if (i == 3) out += ") "
            if (i == 6) out += "-"
            out += trimmed[i]
        }
        return TransformedText(AnnotatedString(out), phoneNumberOffsetTranslator)
    }

    private val phoneNumberOffsetTranslator = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int =
            when (offset) {
                0 -> offset
                // Add 1 for opening parenthesis.
                in 1..3 -> offset + 1
                // Add 3 for both parentheses and a space.
                in 4..6 -> offset + 3
                // Add 4 for both parentheses, space, and hyphen.
                else -> offset + 4
            }

        override fun transformedToOriginal(offset: Int): Int =
            when (offset) {
                0 -> offset
                // Subtract 1 for opening parenthesis.
                in 1..5 -> offset - 1
                // Subtract 3 for both parentheses and a space.
                in 6..10 -> offset - 3
                // Subtract 4 for both parentheses, space, and hyphen.
                else -> offset - 4
            }
    }
}
// [END android_compose_text_auto_format_phone_number_transformtext]

private val firaSansFamily = FontFamily(typeface = Typeface.DEFAULT)

val LightBlue = Color(0xFF0066FF)
val Purple = Color(0xFF800080)

// [START android_compose_text_showhidepassword]
@Composable
fun PasswordTextField() {
    val state = remember { TextFieldState() }
    var showPassword by remember { mutableStateOf(false) }
    BasicSecureTextField(
        state = state,
        textObfuscationMode =
        if (showPassword) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
            .padding(6.dp),
        decorator = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 48.dp)
                ) {
                    innerTextField()
                }
                Icon(
                    if (showPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .requiredSize(48.dp).padding(16.dp)
                        .clickable { showPassword = !showPassword }
                )
            }
        }
    )
}
// [END android_compose_text_showhidepassword]

// [START android_compose_text_auto_format_phone_number_validatetext]
class EmailViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set

    val emailHasErrors by derivedStateOf {
        if (email.isNotEmpty()) {
            // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    fun updateEmail(input: String) {
        email = input
    }
}

@Composable
fun ValidatingInputTextField(
    email: String,
    updateState: (String) -> Unit,
    validatorHasErrors: Boolean
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = email,
        onValueChange = updateState,
        label = { Text("Email") },
        isError = validatorHasErrors,
        supportingText = {
            if (validatorHasErrors) {
                Text("Incorrect email format.")
            }
        }
    )
}

@Preview
@Composable
fun ValidateInput() {
    val emailViewModel: EmailViewModel = viewModel<EmailViewModel>()
    ValidatingInputTextField(
        email = emailViewModel.email,
        updateState = { input -> emailViewModel.updateEmail(input) },
        validatorHasErrors = emailViewModel.emailHasErrors
    )
}
// [END android_compose_text_auto_format_phone_number_validatetext]
