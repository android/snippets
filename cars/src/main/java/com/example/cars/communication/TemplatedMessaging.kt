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

package com.example.cars.communication

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.messaging.model.CarMessage
import androidx.car.app.messaging.model.ConversationCallback
import androidx.car.app.messaging.model.ConversationItem
import androidx.car.app.model.Action
import androidx.car.app.model.CarText
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import androidx.core.app.Person

class MyMessage(
    val sender: Person,
    val body: CarText,
    val receivedTimeEpochMillis: Long,
    val isRead: Boolean
)

class MyConversation(val id: String, val title: String) {
    fun getMessages(): List<MyMessage> = emptyList()
}

// [START android_cars_communication_templated_messaging_screen]
class MyMessagingScreen(carContext: CarContext) : Screen(carContext) {
    // [START_EXCLUDE silent]
    private val myConversationCallback = object : ConversationCallback {
        override fun onMarkAsRead() {
            // Implement mark as read logic
        }

        override fun onTextReply(p0: String) {
            // Implement text reply logic
        }
    }

    private fun getConversations(): List<MyConversation> = TODO("Not implemented yet")
    // [END_EXCLUDE silent]
    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()
        val conversations: List<MyConversation> = getConversations() // Retrieve conversations

        for (conversation: MyConversation in conversations) {
            val carMessages: List<CarMessage> = conversation.getMessages()
                .map { message ->
                    // CarMessage supports additional fields such as MIME type and URI,
                    // which you should set if available
                    CarMessage.Builder()
                        .setSender(message.sender)
                        .setBody(message.body)
                        .setReceivedTimeEpochMillis(message.receivedTimeEpochMillis)
                        .setRead(message.isRead)
                        .build()
                }

            itemListBuilder.addItem(
                ConversationItem.Builder(
                    conversation.id,
                    CarText.create(conversation.title),
                    Person.Builder()
                        .setName("Me")
                        .setKey("self_id")
                        .build(),
                    carMessages,
                    myConversationCallback
                )
                    .build()
            )
        }

        val header = Header.Builder()
            .setStartHeaderAction(Action.APP_ICON)
            .setTitle("Conversations")
            .build()

        return ListTemplate.Builder()
            .setHeader(header)
            .setSingleList(itemListBuilder.build())
            .build()
    }
}
// [END android_cars_communication_templated_messaging_screen]
