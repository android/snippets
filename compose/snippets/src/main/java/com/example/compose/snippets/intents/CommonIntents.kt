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

package com.example.compose.snippets.intents

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.MediaStore
import android.provider.Settings
import com.google.android.gms.actions.NoteIntents
import com.google.android.gms.actions.ReserveIntents

private class CommonIntentsActivity : Activity() {
    // [START android_intents_common_create_alarm]
    fun createAlarm(message: String, hour: Int, minutes: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_create_alarm]

    // [START android_intents_common_start_timer]
    fun startTimer(message: String, seconds: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_start_timer]

    // [START android_intents_common_add_event]
    fun addEvent(title: String, location: String, begin: Long, end: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Events.CONTENT_URI
            putExtra(Events.TITLE, title)
            putExtra(Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_add_event]

    // [START android_intents_common_image_capture_dispatch]
    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Display error state to the user.
        }
    }
    // [END android_intents_common_image_capture_dispatch]

    // [START android_intents_common_camera_video]
    fun capturePhoto() {
        val intent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }
    // [END android_intents_common_camera_video]

    // [START android_intents_common_view_contact]
    fun viewContact(contactUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, contactUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_view_contact]

    // [START android_intents_common_edit_contact]
    fun editContact(contactUri: Uri, email: String) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            data = contactUri
            putExtra(ContactsContract.Intents.Insert.EMAIL, email)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_edit_contact]

    // [START android_intents_common_insert_contact]
    fun insertContact(name: String, email: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            putExtra(ContactsContract.Intents.Insert.EMAIL, email)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_insert_contact]

    // [START android_intents_common_compose_email_attachment]
    fun composeEmail(addresses: Array<String>, subject: String, attachment: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_STREAM, attachment)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_compose_email_attachment]

    // [START android_intents_common_compose_email_sendto]
    fun composeEmail(addresses: Array<String>, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps handle this.
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_compose_email_sendto]

    // [START android_intents_common_call_car]
    fun callCar() {
        val intent = Intent(ReserveIntents.ACTION_RESERVE_TAXI_RESERVATION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_call_car]

    // [START android_intents_common_show_map]
    fun showMap(geoLocation: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_show_map]

    // [START android_intents_common_play_media]
    fun playMedia(file: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = file
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_play_media]

    // [START android_intents_common_play_search_artist]
    fun playSearchArtist(artist: String) {
        val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist)
            putExtra(SearchManager.QUERY, artist)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_play_search_artist]

    // [START android_intents_common_handle_play_search]
    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        if (intent.action?.compareTo(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) == 0) {

            val mediaFocus: String? = intent.getStringExtra(MediaStore.EXTRA_MEDIA_FOCUS)
            val query: String? = intent.getStringExtra(SearchManager.QUERY)

            // Some of these extras might not be available depending on the search mode.
            val album: String? = intent.getStringExtra(MediaStore.EXTRA_MEDIA_ALBUM)
            val artist: String? = intent.getStringExtra(MediaStore.EXTRA_MEDIA_ARTIST)
            val genre: String? = intent.getStringExtra("android.intent.extra.genre")
            val playlist: String? = intent.getStringExtra("android.intent.extra.playlist")
            val title: String? = intent.getStringExtra(MediaStore.EXTRA_MEDIA_TITLE)

            // Determine the search mode and use the corresponding extras.
            when {
                mediaFocus == null -> {
                    // 'Unstructured' search mode (backward compatible).
                    playUnstructuredSearch(query)
                }
                mediaFocus.compareTo("vnd.android.cursor.item/*") == 0 -> {
                    if (query?.isNotEmpty() == true) {
                        // 'Unstructured' search mode.
                        playUnstructuredSearch(query)
                    } else {
                        // 'Any' search mode.
                        playResumeLastPlaylist()
                    }
                }
                mediaFocus.compareTo(MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE) == 0 -> {
                    // 'Genre' search mode.
                    playGenre(genre)
                }
                mediaFocus.compareTo(MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE) == 0 -> {
                    // 'Artist' search mode.
                    playArtist(artist, genre)
                }
                mediaFocus.compareTo(MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE) == 0 -> {
                    // 'Album' search mode.
                    playAlbum(album, artist)
                }
                mediaFocus.compareTo("vnd.android.cursor.item/audio") == 0 -> {
                    // 'Song' search mode.
                    playSong(album, artist, genre, title)
                }
                mediaFocus.compareTo(MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE) == 0 -> {
                    // 'Playlist' search mode.
                    playPlaylist(album, artist, genre, playlist, title)
                }
            }
        }
    }
    // [END android_intents_common_handle_play_search]

    // [START android_intents_common_create_note]
    fun createNote(subject: String, text: String) {
        val intent = Intent(NoteIntents.ACTION_CREATE_NOTE).apply {
            putExtra(NoteIntents.EXTRA_NAME, subject)
            putExtra(NoteIntents.EXTRA_TEXT, text)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_create_note]

    // [START android_intents_common_dial_phone]
    fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_dial_phone]

    // [START android_intents_common_search_web]
    fun searchWeb(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_search_web]

    // [START android_intents_common_open_wifi_settings]
    fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_open_wifi_settings]

    // [START android_intents_common_compose_mms]
    fun composeMmsMessage(message: String, attachment: Uri) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            putExtra("sms_body", message)
            putExtra(Intent.EXTRA_STREAM, attachment)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_compose_mms]

    // [START android_intents_common_open_web_page]
    fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_open_web_page]

    private fun playUnstructuredSearch(query: String?) {}
    private fun playResumeLastPlaylist() {}
    private fun playGenre(genre: String?) {}
    private fun playArtist(artist: String?, genre: String?) {}
    private fun playAlbum(album: String?, artist: String?) {}
    private fun playSong(album: String?, artist: String?, genre: String?, title: String?) {}
    private fun playPlaylist(
        album: String?,
        artist: String?,
        genre: String?,
        playlist: String?,
        title: String?
    ) {}
}

private class CameraStillActivity : Activity() {
    private val REQUEST_IMAGE_CAPTURE = 1

    // [START android_intents_common_camera_still]
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Display error state to the user.
        }
    }
    // [END android_intents_common_camera_still]
}

private class ComposeMmsSmstoActivity : Activity() {
    // [START android_intents_common_compose_mms_smsto]
    fun composeMmsMessage(message: String, attachment: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:") // Only SMS apps respond to this.
            putExtra("sms_body", message)
            putExtra(Intent.EXTRA_STREAM, attachment)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_intents_common_compose_mms_smsto]
}

private object ImageCaptureScope : Activity() {
    // [START android_intents_common_image_capture]
    const val REQUEST_IMAGE_CAPTURE = 1
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val locationForPhotos: Uri = ...
    // [START_EXCLUDE silent]
     */
    val locationForPhotos: Uri = Uri.EMPTY
    // [END_EXCLUDE]

    fun capturePhoto(targetFilename: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val thumbnail: Bitmap? = data?.getParcelableExtra("data")
            // Do other work with full size photo saved in locationForPhotos.
            // ...
        }
    }
    // [END android_intents_common_image_capture]
}

private object PickContactScope : Activity() {
    // [START android_intents_common_pick_contact]
    const val REQUEST_SELECT_CONTACT = 1

    fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            val contactUri: Uri? = data?.data
            // Do something with the selected contact at contactUri.
            // ...
        }
    }
    // [END android_intents_common_pick_contact]
}

private object PickContactDataScope : Activity() {
    // [START android_intents_common_pick_contact_data]
    const val REQUEST_SELECT_PHONE_NUMBER = 1

    fun selectContact() {
        // Start an activity for the user to pick a phone number from contacts.
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = CommonDataKinds.Phone.CONTENT_TYPE
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == Activity.RESULT_OK) {
            // Get the URI and query the content provider for the phone number.
            val contactUri: Uri = data?.data ?: return
            val projection: Array<String> = arrayOf(CommonDataKinds.Phone.NUMBER)
            contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
                // If the cursor returned is valid, get the phone number.
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)
                    val number = cursor.getString(numberIndex)
                    // Do something with the phone number.
                    // ...
                }
            }
        }
    }
    // [END android_intents_common_pick_contact_data]
}

private object GetFileScope : Activity() {
    // [START android_intents_common_get_file]
    const val REQUEST_IMAGE_GET = 1

    fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val thumbnail: Bitmap? = data?.getParcelableExtra("data")
            val fullPhotoUri: Uri? = data?.data
            // Do work with photo saved at fullPhotoUri.
            // ...
        }
    }
    // [END android_intents_common_get_file]
}

private object OpenFileScope : Activity() {
    // [START android_intents_common_open_file]
    const val REQUEST_IMAGE_OPEN = 1

    fun selectImage2() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_IMAGE_OPEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data
            // Do work with full size photo saved at fullPhotoUri.
            // ...
        }
    }
    // [END android_intents_common_open_file]
}
