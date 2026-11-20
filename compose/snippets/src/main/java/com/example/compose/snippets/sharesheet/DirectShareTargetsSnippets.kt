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

package com.example.compose.snippets.sharesheet

import android.content.Context
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat

private fun orderDynamicShortcuts(
    myContext: Context,
    shortcut1: ShortcutInfoCompat,
    shortcut2: ShortcutInfoCompat
) {
    // [START android_compose_sharing_direct_share_set_dynamic_shortcuts]
    ShortcutManagerCompat.setDynamicShortcuts(myContext, listOf(shortcut1, shortcut2))
    // [END android_compose_sharing_direct_share_set_dynamic_shortcuts]
}

private fun pushOutgoingMessageShortcut(
    myContext: Context,
    staticConversationIdentifier: String,
    firstName: String,
    fullName: String,
    matchedCategories: Set<String>
) {
    // [START android_compose_sharing_direct_share_push_send_message]
    val shortcutInfo = ShortcutInfoCompat.Builder(myContext, staticConversationIdentifier)
        // ...
        .setShortLabel(firstName)
        .setLongLabel(fullName)
        .setCategories(matchedCategories)
        .setLongLived(true)
        .addCapabilityBinding("actions.intent.SEND_MESSAGE").build()
    ShortcutManagerCompat.pushDynamicShortcut(myContext, shortcutInfo)
    // [END android_compose_sharing_direct_share_push_send_message]
}

private fun removeDeletedContactShortcut(myContext: Context) {
    // [START android_compose_sharing_direct_share_remove_long_lived]
    val deleteShortcutId = "..."
    ShortcutManagerCompat.removeLongLivedShortcuts(myContext, listOf(deleteShortcutId))
    // [END android_compose_sharing_direct_share_remove_long_lived]
}

private fun pushOutgoingGroupMessageShortcut(
    myContext: Context,
    staticConversationIdentifier: String,
    groupShortTitle: String,
    groupLongTitle: String,
    matchedCategories: Set<String>
) {
    // [START android_compose_sharing_direct_share_push_send_group]
    val shortcutInfo = ShortcutInfoCompat.Builder(myContext, staticConversationIdentifier)
        // ...
        .setShortLabel(groupShortTitle)
        .setLongLabel(groupLongTitle)
        .setCategories(matchedCategories)
        .setLongLived(true)
        .addCapabilityBinding("actions.intent.SEND_MESSAGE", "message.recipient.@type", listOf("Audience")).build()

    ShortcutManagerCompat.pushDynamicShortcut(myContext, shortcutInfo)
    // [END android_compose_sharing_direct_share_push_send_group]
}

private fun pushIncomingMessageShortcut(
    myContext: Context,
    staticConversationIdentifier: String,
    firstName: String,
    fullName: String,
    matchedCategories: Set<String>
) {
    // [START android_compose_sharing_direct_share_push_receive_message]
    val shortcutInfo = ShortcutInfoCompat.Builder(myContext, staticConversationIdentifier)
        // ...
        .setShortLabel(firstName)
        .setLongLabel(fullName)
        .setCategories(matchedCategories)
        .setLongLived(true)
        .addCapabilityBinding("actions.intent.RECEIVE_MESSAGE").build()

    ShortcutManagerCompat.pushDynamicShortcut(myContext, shortcutInfo)
    // [END android_compose_sharing_direct_share_push_receive_message]
}

private fun pushIncomingGroupMessageShortcut(
    myContext: Context,
    staticConversationIdentifier: String,
    groupShortTitle: String,
    groupLongTitle: String,
    matchedCategories: Set<String>
) {
    // [START android_compose_sharing_direct_share_push_receive_group]
    val shortcutInfo = ShortcutInfoCompat.Builder(myContext, staticConversationIdentifier)
        // ...
        .setShortLabel(groupShortTitle)
        .setLongLabel(groupLongTitle)
        .setCategories(matchedCategories)
        .setLongLived(true)
        .addCapabilityBinding("actions.intent.RECEIVE_MESSAGE", "message.sender.@type", listOf("Audience")).build()

    ShortcutManagerCompat.pushDynamicShortcut(myContext, shortcutInfo)
    // [END android_compose_sharing_direct_share_push_receive_group]
}
