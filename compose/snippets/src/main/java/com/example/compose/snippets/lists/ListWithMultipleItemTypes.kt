package com.example.compose.snippets.lists

import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.w3c.dom.Text

@Composable
fun ListWithMultipleItems(messages: List<Any>) {
    LazyColumn {
        items(
            messages.size,
            contentType = { it }
        ) {
            for (message in messages)
                when (message) {
                    is MediaStore.Audio -> AudioMessage(message)
                    is Text -> TextMessage(message)
                }
        }
    }
}

@Composable
fun AudioMessage(message: MediaStore.Audio) {
    TODO("Not yet implemented")
}

@Composable
fun TextMessage(message: Text) {
    TODO("Not yet implemented")
}

data class SampleMessage (val text:String, val content:Any)
{

}
