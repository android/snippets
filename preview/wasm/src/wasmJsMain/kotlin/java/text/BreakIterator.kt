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

package java.text

/*
 * The java.text APIs used by AnimationSnippets.kt's typing-text snippet, which aren't available on
 * Kotlin/Wasm. Only character boundaries are supported: surrogate pairs, emoji modifiers, variation
 * selectors and zero-width-joiner sequences stay together, so emoji aren't split mid-character.
 */

interface CharacterIterator {
    val string: String
}

class StringCharacterIterator(override val string: String) : CharacterIterator

class BreakIterator private constructor() {
    private var source = ""
    private var index = 0

    var text: CharacterIterator = StringCharacterIterator("")
        set(value) {
            field = value
            source = value.string
            index = 0
        }

    fun next(): Int {
        if (index >= source.length) return DONE
        index = nextCodePoint(index)
        while (index < source.length) {
            val c = source[index]
            when {
                c == '\uFE0F' || c == '\u20E3' -> index++
                c == '\u200D' && index + 1 < source.length -> index = nextCodePoint(index + 1)
                isSkinToneModifier(index) -> index += 2
                else -> break
            }
        }
        return index
    }

    private fun nextCodePoint(i: Int): Int =
        if (source[i].isHighSurrogate() && i + 1 < source.length) i + 2 else i + 1

    private fun isSkinToneModifier(i: Int): Boolean =
        i + 1 < source.length && source[i] == '\uD83C' && source[i + 1] in '\uDFFB'..'\uDFFF'

    companion object {
        const val DONE = -1

        fun getCharacterInstance(): BreakIterator = BreakIterator()
    }
}
