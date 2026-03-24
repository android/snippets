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

package com.example.contacts

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsPickerSessionContract.ACTION_PICK_CONTACTS
import android.provider.ContactsPickerSessionContract.EXTRA_PICK_CONTACTS_MATCH_ALL_DATA_FIELDS
import android.provider.ContactsPickerSessionContract.EXTRA_PICK_CONTACTS_REQUESTED_DATA_FIELDS
import android.provider.ContactsPickerSessionContract.EXTRA_PICK_CONTACTS_SELECTION_LIMIT
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.contacts.ui.theme.SnippetsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactPickerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SnippetsTheme {
                ContactPickerScreen(Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPickerScreen(modifier: Modifier) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State to hold the list of selected contacts
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

    // [START android_contact_picker_intent_handler]
    // Launcher for the Contact Picker intent
    val pickContact = rememberLauncherForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val resultUri = it.data?.data ?: return@rememberLauncherForActivityResult

            // Process the result URI in a background thread to fetch all selected contacts
            coroutine.launch {
                contacts = processContactPickerResultUri(resultUri, context)
            }
        }
    }
    // [END android_contact_picker_intent_handler]

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Contact Picker") }) },
        bottomBar = {
            BottomAppBar(
                actions = {
                    TextButton(onClick = {
                        try {
                            // [START android_contact_picker_single_contact_selection_intent]
                            // Define the specific contact data fields you need
                            val requestedFields = arrayListOf(
                                Email.CONTENT_ITEM_TYPE,
                                Phone.CONTENT_ITEM_TYPE,
                            )
    
                            // Set up the intent for the Contact Picker
                            val pickContactIntent = Intent(ACTION_PICK_CONTACTS).apply {
                                putStringArrayListExtra(
                                    EXTRA_PICK_CONTACTS_REQUESTED_DATA_FIELDS,
                                    requestedFields
                                )
                            }
    
                            // Launch the picker
                            pickContact.launch(pickContactIntent)
                            // [END android_contact_picker_single_contact_selection_intent]
                        } catch (_: ActivityNotFoundException) {
                            coroutine.launch {
                                snackbarHostState.showSnackbar("The contact picker isn't available on your version of Android")
                            }
                        }
                    }) {
                        Text("Single contact")
                    }
                    TextButton(onClick = {
                        try {
                            // [START android_contact_picker_multiple_contacts_selection_intent]
                            val requestedFields = arrayListOf(
                                Email.CONTENT_ITEM_TYPE,
                                Phone.CONTENT_ITEM_TYPE,
                            )
    
                            // Set up the intent for the Contact Picker
                            val pickContactIntent = Intent(ACTION_PICK_CONTACTS).apply {
                                // Enable multi-select
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                // Set limit of selectable contacts
                                putExtra(EXTRA_PICK_CONTACTS_SELECTION_LIMIT, 5)
                                // Define the specific contact data fields you need
                                putStringArrayListExtra(
                                    EXTRA_PICK_CONTACTS_REQUESTED_DATA_FIELDS,
                                    requestedFields
                                )
                                // Enable this option to only filter contacts that have all the requested data fields
                                putExtra(EXTRA_PICK_CONTACTS_MATCH_ALL_DATA_FIELDS, false)
                            }
    
                            // Launch the picker
                            pickContact.launch(pickContactIntent)
                            // [END android_contact_picker_multiple_contacts_selection_intent]
                        } catch (_: ActivityNotFoundException) {
                            coroutine.launch {
                                snackbarHostState.showSnackbar("The contact picker isn't available on your version of Android")
                            }
                        }
                    }) {
                        Text("Multiple contacts")
                    }
                    TextButton(onClick = {
                        try {
                            // [START android_contact_picker_single_phone_selection_intent]

                            // When the Contact Picker is launched with only one requested data field
                            // (e.g. only Phone.CONTENT_ITEM_TYPE), its UI allows for the selection
                            // of specific items (like a specific phone number or email) separately,
                            // rather than selecting the whole contact.
                            val pickContactIntent = Intent(ACTION_PICK_CONTACTS).apply {
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                putStringArrayListExtra(
                                    EXTRA_PICK_CONTACTS_REQUESTED_DATA_FIELDS,
                                    arrayListOf(Phone.CONTENT_ITEM_TYPE)
                                )
                            }

                            // Launch the picker
                            pickContact.launch(pickContactIntent)
                            // [END android_contact_picker_single_phone_selection_intent]
                        } catch (_: ActivityNotFoundException) {
                            coroutine.launch {
                                snackbarHostState.showSnackbar("The contact picker isn't available on your version of Android")
                            }
                        }
                    }) {
                        Text("Single phone only")
                    }
                },
            )
        },
    ) { innerPadding ->
        // Display the selected contact details
        LazyColumn(Modifier.padding(innerPadding)) {
            items(contacts) { contact ->
                ListItem(
                    headlineContent = { Text(contact.name) },
                    supportingContent = {
                        Column {
                            if (contact.emails.isNotEmpty())
                                Text("Emails: ${contact.emails.joinToString(", ")}")
                            if (contact.phones.isNotEmpty())
                                Text("Phones: ${contact.phones.joinToString(", ")}")
                        }
                    },
                )
            }
        }
    }
}

// [START android_contact_picker_result_uri_processing]
// Data class representing a parsed Contact with selected details.
data class Contact(
    val lookupKey: String,
    val name: String,
    val emails: List<String>,
    val phones: List<String>
)

// Helper function to query the content resolver with the URI returned by the Contact Picker.
// Parses the cursor to extract contact details such as name, email, and phone number.
private suspend fun processContactPickerResultUri(
    sessionUri: Uri,
    context: Context
): List<Contact> = withContext(Dispatchers.IO) {
    // Define the columns we want to retrieve from the ContactPicker ContentProvider
    val projection = arrayOf(
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Data.MIMETYPE, // Type of data (e.g., email or phone)
        ContactsContract.Data.DATA1, // The actual data (Phone number / Email string)
    )

    // We use `LOOKUP_KEY` as a unique ID to aggregate all contact info related to a same person
    val contactsMap = mutableMapOf<String, Contact>()

    // Note: The Contact Picker Session Uri doesn't support custom selection & selectionArgs.
    // We query the URI directly to get the results chosen by the user.
    context.contentResolver.query(sessionUri, projection, null, null, null)?.use { cursor ->
        // Get the column indices for our requested projection
        val lookupKeyIdx = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)
        val mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)
        val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val data1Idx = cursor.getColumnIndex(ContactsContract.Data.DATA1)

        while (cursor.moveToNext()) {
            val lookupKey = cursor.getString(lookupKeyIdx)
            val mimeType = cursor.getString(mimeTypeIdx)
            val name = cursor.getString(nameIdx) ?: ""
            val data1 = cursor.getString(data1Idx) ?: ""

            val email = if (mimeType == Email.CONTENT_ITEM_TYPE) data1 else null
            val phone = if (mimeType == Phone.CONTENT_ITEM_TYPE) data1 else null

            val existingContact = contactsMap[lookupKey]
            if (existingContact != null) {
                contactsMap[lookupKey] = existingContact.copy(
                    emails = if (email != null) existingContact.emails + email else existingContact.emails,
                    phones = if (phone != null) existingContact.phones + phone else existingContact.phones
                )
            } else {
                contactsMap[lookupKey] = Contact(
                    lookupKey = lookupKey,
                    name = name,
                    emails = if (email != null) listOf(email) else emptyList(),
                    phones = if (phone != null) listOf(phone) else emptyList()
                )
            }
        }
    }

    return@withContext contactsMap.values.toList()
}
// [END android_contact_picker_result_uri_processing]