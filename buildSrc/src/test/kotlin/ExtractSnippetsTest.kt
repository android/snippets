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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite for SnippetExtractor, verifying region tag parsing,
 * exclusion blocks, delimiter stripping, and indentation normalization.
 */
class ExtractSnippetsTest {

    @Test
    fun testRegionTagRegex() {
        val validTags = listOf("foo", "foo1BAR", "foo_bar", "foo-bar", "android_compose_sample")
        val invalidTags = listOf("foo@bar", "foo bar", "foo#bar", "foo:bar")

        val tagRegex = Regex("""^[\w_\-]+$""")
        for (tag in validTags) {
            assertTrue("Expected '$tag' to be valid", tagRegex.matches(tag))
        }
        for (tag in invalidTags) {
            assertTrue("Expected '$tag' to be invalid", !tagRegex.matches(tag))
        }
    }

    @Test
    fun testRegionTagStartPatterns() {
        val samples = listOf(
            "// [START foo]" to "foo",
            "/* [START foo_bar] */" to "foo_bar",
            "<!-- [START xml_tag] -->" to "xml_tag",
            "# [START python_tag]" to "python_tag",
            "  // [START nested-tag]  " to "nested-tag"
        )

        for ((input, expectedTag) in samples) {
            val match = SnippetExtractor.START_TAG_REGEX.find(input)
            assertNotNull("Expected START match for: $input", match)
            assertEquals(expectedTag, match!!.groupValues[1])
        }
    }

    @Test
    fun testRegionTagEndPatterns() {
        val samples = listOf(
            "// [END foo]" to "foo",
            "/* [END foo_bar] */" to "foo_bar",
            "<!-- [END xml_tag] -->" to "xml_tag",
            "# [END python_tag]" to "python_tag",
            "  // [END nested-tag]  " to "nested-tag"
        )

        for ((input, expectedTag) in samples) {
            val match = SnippetExtractor.END_TAG_REGEX.find(input)
            assertNotNull("Expected END match for: $input", match)
            assertEquals(expectedTag, match!!.groupValues[1])
        }
    }

    @Test
    fun testBasicRegionExtraction() {
        val code = """
            package com.example
            
            // [START basic_sample]
            fun helloWorld() {
                println("Hello, World!")
            }
            // [END basic_sample]
            
            fun otherCode() {}
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "basic_sample")
        val expected = """
            fun helloWorld() {
                println("Hello, World!")
            }
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    /**
     * Tests nested and overlapping regions with inner delimiter stripping.
     */
    @Test
    fun testCanonicalDevsiteNestedAndOverlappingRegions() {
        val text = """
            one
            abc [START foo] def
            two
            ghi [START bar] jkl
            three
            mno [END bar]
            four
            [END foo] pqr
            five
        """.trimIndent()

        val fooSnippet = SnippetExtractor.parseRegion(text, "foo")
        val barSnippet = SnippetExtractor.parseRegion(text, "bar")

        assertEquals("two\nthree\nfour", fooSnippet)
        assertEquals("three", barSnippet)
    }

    @Test
    fun testExclusionBlockWithKotlinEllipsisComment() {
        val code = """
            // [START sample_with_exclude]
            fun calculate(): Int {
                // [START_EXCLUDE]
                val helper = InternalHelper()
                helper.init()
                // [END_EXCLUDE]
                return 42
            }
            // [END sample_with_exclude]
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "sample_with_exclude")
        val expected = """
            fun calculate(): Int {
                // ...
                return 42
            }
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testExclusionBlockWithXmlEllipsisComment() {
        val code = """
            <!-- [START xml_sample] -->
            <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android">
                <!-- [START_EXCLUDE] -->
                <View android:id="@+id/internal_view" />
                <!-- [END_EXCLUDE] -->
                <TextView android:text="Hello" />
            </FrameLayout>
            <!-- [END xml_sample] -->
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "xml_sample")
        val expected = """
            <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android">
                <!-- ... -->
                <TextView android:text="Hello" />
            </FrameLayout>
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testExclusionBlockWithBlockComment() {
        val code = """
            /* [START block_comment_sample] */
            int process() {
                /* [START_EXCLUDE] */
                secret_init();
                /* [END_EXCLUDE] */
                return 1;
            }
            /* [END block_comment_sample] */
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "block_comment_sample")
        val expected = """
            int process() {
                /* ... */
                return 1;
            }
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testExclusionBlockWithPythonEllipsisComment() {
        val code = """
            # [START py_sample]
            def process():
                # [START_EXCLUDE]
                setup_environment()
                # [END_EXCLUDE]
                return True
            # [END py_sample]
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "py_sample")
        val expected = """
            def process():
                # ...
                return True
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testSilentExclusionBlock() {
        val code = """
            // [START silent_exclude_sample]
            class Service {
                // [START_EXCLUDE silent]
                private val secretKey = "12345"
                // [END_EXCLUDE]
                fun execute() {}
            }
            // [END silent_exclude_sample]
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "silent_exclude_sample")
        val expected = """
            class Service {
                fun execute() {}
            }
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testDelimiterStrippingFromEnclosingRegion() {
        val code = """
            // [START outer_tag]
            fun outerFunction() {
                // [START inner_tag]
                println("Inside inner")
                // [END inner_tag]
            }
            // [END outer_tag]
        """.trimIndent()

        val outerSnippet = SnippetExtractor.parseRegion(code, "outer_tag")
        val expectedOuter = """
            fun outerFunction() {
                println("Inside inner")
            }
        """.trimIndent()

        assertEquals(expectedOuter, outerSnippet)
    }

    /**
     * Tests indentation normalization removing common leading whitespace.
     */
    @Test
    fun testIndentationNormalizationAuto() {
        val indentedCode = """
                // [START indented_sample]
                val a = 1
                val b = 2
                if (a < b) {
                    println(a)
                }
                // [END indented_sample]
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(indentedCode, "indented_sample")
        val expected = """
            val a = 1
            val b = 2
            if (a < b) {
                println(a)
            }
        """.trimIndent()

        assertEquals(expected, snippet)
    }

    @Test
    fun testMultipleRegionsInOneFile() {
        val code = """
            package com.example
            
            // [START region_one]
            fun first() = 1
            // [END region_one]
            
            fun intermediate() = 0
            
            // [START region_two]
            fun second() = 2
            // [END region_two]
        """.trimIndent()

        val regions = SnippetExtractor.extractRegions(code.split("\n"))
        assertEquals(2, regions.size)
        assertEquals("fun first() = 1", regions["region_one"])
        assertEquals("fun second() = 2", regions["region_two"])
    }

    @Test
    fun testNonExistentTagReturnsNull() {
        val code = """
            // [START some_tag]
            val x = 10
            // [END some_tag]
        """.trimIndent()

        val snippet = SnippetExtractor.parseRegion(code, "non_existent_tag")
        assertNull(snippet)
    }

    @Test
    fun testParseRegionWithWindowsCrlfLineEndings() {
        val code = "// [START crlf_sample]\r\nfun crlf() {\r\n    println(\"CRLF\")\r\n}\r\n// [END crlf_sample]\r\n"
        val snippet = SnippetExtractor.parseRegion(code, "crlf_sample")
        val expected = "fun crlf() {\n    println(\"CRLF\")\n}"
        assertEquals(expected, snippet)
    }

    @Test
    fun testWarningReportingForUnclosedTag() {
        val code = """
            // [START unclosed_tag]
            fun something() = true
        """.trimIndent()

        val warnings = mutableListOf<String>()
        SnippetExtractor.parseRegion(code, "unclosed_tag", onWarning = { warnings.add(it) })
        assertEquals(1, warnings.size)
        assertTrue(warnings[0].contains("unclosed region tag '[START unclosed_tag]'"))
    }

    @Test
    fun testWarningReportingForUnclosedExclude() {
        val code = """
            // [START tag]
            fun calculate(): Int {
                // [START_EXCLUDE]
                return 0
            // [END tag]
        """.trimIndent()

        val warnings = mutableListOf<String>()
        SnippetExtractor.parseRegion(code, "tag", onWarning = { warnings.add(it) })
        assertEquals(1, warnings.size)
        assertTrue(warnings[0].contains("unclosed [START_EXCLUDE] block"))
    }
}
