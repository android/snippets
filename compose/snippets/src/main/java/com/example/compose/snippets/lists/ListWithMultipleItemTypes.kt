package com.example.compose.snippets.lists

import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.w3c.dom.Text

@Composable
fun ListWithMultipleItems(numberOfMessages:Int) {
    val messages = numberOfMessages // or any Int
    val message = SampleMessage("Hi there", MediaStore.Audio())

    LazyColumn {
        items(
            messages,
            contentType = { it }
        ) {
            when (message.content) {
                // Content Types and Composables are defined elsewhere.
                is MediaStore.Audio -> AudioMessage(message)
                is Text -> TextMessage(message)
            }
        }
    }
}

@Composable
fun TextMessage(message: SampleMessage) {
    TODO("Not yet implemented")
}

@Composable
fun AudioMessage(message: SampleMessage) {
    TODO("Not yet implemented")
}

data class SampleMessage (val text:String, val content:Any)
{

}
