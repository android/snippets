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

package com.example.tv.discovery

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.ChannelLogoUtils.storeChannelLogo
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat

private fun createChannelBuilder(uri: Uri) {
    // [START android_tv_channel_builder]
    val builder = Channel.Builder()
    // Every channel you create must have the type `TYPE_PREVIEW`.
    builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
        .setDisplayName("Channel Name")
        .setAppLinkIntentUri(uri)
    // [END android_tv_channel_builder]
}

private fun insertChannel(context: Context, builder: Channel.Builder) {
    // [START android_tv_channel_insert]
    var channelUri = context.contentResolver.insert(
        TvContractCompat.Channels.CONTENT_URI, builder.build().toContentValues()
    )
    // [END android_tv_channel_insert]
}

private fun getChannelId(channelUri: Uri) {
    // [START android_tv_channel_id]
    var channelId = ContentUris.parseId(channelUri)
    // [END android_tv_channel_id]
}

private fun addChannelLogo(context: Context, channelId: Long, logoUri: Uri, logo: Bitmap) {
    // [START android_tv_channel_logo]
    // Choose one or the other.
    storeChannelLogo(context, channelId, logoUri) // also works if logoUri is a URL.
    storeChannelLogo(context, channelId, logo)
    // [END android_tv_channel_logo]
}

private fun makeChannelDefault(context: Context, channelId: Long) {
    // [START android_tv_channel_default]
    TvContractCompat.requestChannelBrowsable(context, channelId)
    // [END android_tv_channel_default]
}

private fun updateChannel(context: Context, channelId: Long, builder: Channel.Builder) {
    // [START android_tv_channel_update]
    context.contentResolver.update(
        TvContractCompat.buildChannelUri(channelId),
        builder.build().toContentValues(),
        null,
        null
    )
    // [END android_tv_channel_update]
}

private fun deleteChannel(context: Context, channelId: Long) {
    // [START android_tv_channel_delete]
    context.contentResolver.delete(TvContractCompat.buildChannelUri(channelId), null, null)
    // [END android_tv_channel_delete]
}

// tvprovider's program builder setters are inherited from a restricted base class, so lint flags them.
@SuppressLint("RestrictedApi")
private fun createProgramBuilder(channelId: Long, uri: Uri, appProgramId: String) {
    // [START android_tv_program_builder]
    val builder = PreviewProgram.Builder()
    builder.setChannelId(channelId)
        .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
        .setTitle("Title")
        .setDescription("Program description")
        .setPosterArtUri(uri)
        .setIntentUri(uri)
        .setInternalProviderId(appProgramId)
    // [END android_tv_program_builder]
}

private fun insertProgram(context: Context, builder: PreviewProgram.Builder) {
    // [START android_tv_program_insert]
    var programUri = context.contentResolver.insert(
        TvContractCompat.PreviewPrograms.CONTENT_URI,
        builder.build().toContentValues()
    )
    // [END android_tv_program_insert]
}

private fun getProgramId(programUri: Uri) {
    // [START android_tv_program_id]
    val programId = ContentUris.parseId(programUri)
    // [END android_tv_program_id]
}

private fun updateProgram(context: Context, programId: Long, builder: PreviewProgram.Builder) {
    // [START android_tv_program_update]
    context.contentResolver.update(
        TvContractCompat.buildPreviewProgramUri(programId),
        builder.build().toContentValues(), null, null
    )
    // [END android_tv_program_update]
}

private fun deleteProgram(context: Context, programId: Long) {
    // [START android_tv_program_delete]
    context.contentResolver
        .delete(TvContractCompat.buildPreviewProgramUri(programId), null, null)
    // [END android_tv_program_delete]
}

private fun queryChannel(context: Context, channelId: Long) {
    // [START android_tv_channel_query]
    context.contentResolver
        .query(
            TvContractCompat.buildChannelUri(channelId),
            null, null, null, null
        ).use({
                cursor ->
            if (cursor != null && cursor.moveToNext()) {
                val channel = Channel.fromCursor(cursor)
                if (channel.isBrowsable()) {
                    // update channel's programs.
                }
            }
        })
    // [END android_tv_channel_query]
}
