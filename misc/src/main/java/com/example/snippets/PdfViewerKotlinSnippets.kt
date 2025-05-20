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

package com.example.snippets

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.pdf.viewer.fragment.PdfStylingOptions
import androidx.pdf.viewer.fragment.PdfViewerFragment
import com.google.android.material.button.MaterialButton
import java.util.logging.Level.INFO
import java.util.logging.Level.SEVERE
import java.util.logging.Logger

class PdfViewerKotlinSnippets {

    @RequiresApi(Build.VERSION_CODES.R)
    fun onCreate(savedInstanceState: Bundle?) {

        // [START android_pdf_viewer_extension_version_kotlin]
        if (SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13) {
            // Load the fragment and document.
        }
        // [END android_pdf_viewer_extension_version_kotlin]
    }

    // [START android_pdf_viewer_create_compat_activity_kotlin]
    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val getContentButton: MaterialButton = findViewById(R.id.launch_button)
            val searchButton: MaterialButton = findViewById(R.id.search_button)
        }
    }
    // [END android_pdf_viewer_create_compat_activity_kotlin]

    // [START android_pdf_viewer_extend_fragment_kotlin]
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    class PdfViewerFragmentExtended : PdfViewerFragment() {
        private val myLogger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

        override fun onLoadDocumentSuccess() {
            myLogger.log(INFO, "// Log document success.")
        }

        override fun onLoadDocumentError(error: Throwable) {
            myLogger.log(SEVERE, "// Log document error.")
        }
    }
    // [END android_pdf_viewer_extend_fragment_kotlin]

    /** Enable nested classes. **/
    class ClassHolder {

        // [START android_pdf_viewer_create_fragment_kotlin]
        class MainActivity : AppCompatActivity() {
            private var pdfViewerFragment: PdfViewerFragment? = null

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                // ...

                if (pdfViewerFragment == null) {
                    pdfViewerFragment =
                        supportFragmentManager
                        .findFragmentByTag(PDF_VIEWER_FRAGMENT_TAG) as PdfViewerFragment?
                }
            }

            // Used to instantiate and commit the fragment.
            @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
            private fun initialisePdfViewerFragment() {
                // This condition can be skipped if you want to create a new fragment everytime.
                if (pdfViewerFragment == null) {
                    val fragmentManager: FragmentManager = supportFragmentManager

                    // Fragment initialization.
                    pdfViewerFragment = PdfViewerFragmentExtended()
                    val transaction: FragmentTransaction = fragmentManager.beginTransaction()

                    // Replace an existing fragment in a container with an instance of a new fragment.
                    transaction.replace(
                        R.id.fragment_container_view,
                        pdfViewerFragment!!,
                        PDF_VIEWER_FRAGMENT_TAG
                    )
                    transaction.commitAllowingStateLoss()
                    fragmentManager.executePendingTransactions()
                }
            }

            companion object {
                private const val MIME_TYPE_PDF = "application/pdf"
                private const val PDF_VIEWER_FRAGMENT_TAG = "pdf_viewer_fragment_tag"
            }
        }
        // [END android_pdf_viewer_create_fragment_kotlin]
    }

    /** Enable nested classes. **/
    class ClassHolder2 {

        // [START android_pdf_viewer_enable_document_search_kotlin]
        class MainActivity : AppCompatActivity() {
            @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                var pdfViewerFragment: PdfViewerFragment? = null
                val searchButton: MaterialButton = findViewById(R.id.search_button)
                searchButton.setOnClickListener {
                    pdfViewerFragment?.isTextSearchActive = pdfViewerFragment?.isTextSearchActive == false
                }

                // Ensure WindowInsetsCompat are passed to content views without being consumed by the decor
                // view. These insets are used to calculate the position of the search view.
                WindowCompat.setDecorFitsSystemWindows(window, false)
            }
        }
        // [END android_pdf_viewer_enable_document_search_kotlin]
    }

    /** Enable nested classes. **/
    class ClassHolder3 : AppCompatActivity() {

        private var pdfViewerFragment: PdfViewerFragment? = null
        private const val PDF_VIEWER_FRAGMENT_TAG = "pdf_viewer_fragment_tag"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // ...

            if (pdfViewerFragment == null) {
                pdfViewerFragment =
                    supportFragmentManager
                    .findFragmentByTag(PDF_VIEWER_FRAGMENT_TAG) as PdfViewerFragment?
            }
        }

        // [START android_pdf_viewer_launch_file_picker_kotlin]
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
        class MainActivity : AppCompatActivity() {
            private var pdfViewerFragment: PdfViewerFragment? = null
            private var filePicker =
                registerForActivityResult(GetContent()) { uri: Uri? ->
                    uri?.let {
                        initialisePdfViewerFragment()
                        pdfViewerFragment?.documentUri = uri
                    }
                }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                // ...
                val getContentButton: MaterialButton = findViewById(R.id.launch_button)
                getContentButton.setOnClickListener { filePicker.launch(MIME_TYPE_PDF, null) }
            }

            private fun initialisePdfViewerFragment() {
                // ...
            }

            companion object {
                private const val MIME_TYPE_PDF = "application/pdf"
                // ...
            }
        }
        // [END android_pdf_viewer_launch_file_picker_kotlin]

        // [START android_pdf_viewer_style_fragment_kotlin]
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
        private fun initialisePdfViewerFragment() {
            // This condition can be skipped if you want to create a new fragment everytime
            if (pdfViewerFragment == null) {
                val fragmentManager: FragmentManager = supportFragmentManager

                // Create styling options
                val stylingOptions = PdfStylingOptions(R.style.pdfContainerStyle)

                // Fragment initialization
                pdfViewerFragment = PdfViewerFragment.newInstance(stylingOptions)

                // .. execute fragment transaction
            }
        }
        // [END android_pdf_viewer_style_fragment_kotlin]
    }

    // [START android_pdf_viewer_style_fragment_constructor_kotlin]
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    class StyledPdfViewerFragment : PdfViewerFragment {

        constructor() : super()

        private constructor(pdfStylingOptions: PdfStylingOptions) : super(pdfStylingOptions)

        companion object {
            fun newInstance(): StyledPdfViewerFragment {
                val stylingOptions = PdfStylingOptions(R.style.pdfContainerStyle)
                return StyledPdfViewerFragment(stylingOptions)
            }
        }
    }
    // [END android_pdf_viewer_style_fragment_constructor_kotlin]

    /** Enable nested classes. **/
    class ClassHolder4 {

        // [START android_pdf_viewer_full_code_kotlin]
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
        class MainActivity : AppCompatActivity() {

            private var pdfViewerFragment: PdfViewerFragment? = null
            private var filePicker =
                registerForActivityResult(GetContent()) { uri: Uri? ->
                    uri?.let {
                        initialisePdfViewerFragment()
                        pdfViewerFragment?.documentUri = uri
                    }
                }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                if (pdfViewerFragment == null) {
                    pdfViewerFragment =
                        supportFragmentManager
                        .findFragmentByTag(PDF_VIEWER_FRAGMENT_TAG) as PdfViewerFragment?
                }

                val getContentButton: MaterialButton = findViewById(R.id.launch_button)
                val searchButton: MaterialButton = findViewById(R.id.search_button)

                getContentButton.setOnClickListener { filePicker.launch(MIME_TYPE_PDF) }
                searchButton.setOnClickListener {
                    pdfViewerFragment?.isTextSearchActive = pdfViewerFragment?.isTextSearchActive == false
                }
            }

            private fun initialisePdfViewerFragment() {
                // This condition can be skipped if you want to create a new fragment everytime
                if (pdfViewerFragment == null) {
                    val fragmentManager: FragmentManager = supportFragmentManager

                    // Create styling options
                    // val stylingOptions = PdfStylingOptions(R.style.pdfContainerStyle)

                    // Fragment initialization
                    // For customization
                    // pdfViewerFragment = PdfViewerFragment.newInstance(stylingOptions)
                    pdfViewerFragment = PdfViewerFragmentExtended()
                    val transaction: FragmentTransaction = fragmentManager.beginTransaction()

                    // Replace an existing fragment in a container with an instance of a new fragment
                    transaction.replace(
                        R.id.fragment_container_view,
                        pdfViewerFragment!!,
                        PDF_VIEWER_FRAGMENT_TAG
                    )
                    transaction.commitAllowingStateLoss()
                    fragmentManager.executePendingTransactions()
                }
            }

            companion object {
                private const val MIME_TYPE_PDF = "application/pdf"
                private const val PDF_VIEWER_FRAGMENT_TAG = "pdf_viewer_fragment_tag"
            }
        }
        // [END android_pdf_viewer_full_code_kotlin]
    }
}
