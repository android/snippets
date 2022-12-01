package com.example.compose.snippets.interop

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import com.example.compose.snippets.R
import com.example.compose.snippets.databinding.ActivityExampleBinding

// [START android_compose_interop_existing_ui_shared]
@Composable
fun CallToActionButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondary
    ),
    onClick = onClick,
    modifier = modifier,
  ) {
    Text(text)
  }
}

class CallToActionViewButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

  var text by mutableStateOf("")
  var onClick by mutableStateOf({})

  @Composable
  override fun Content() {
    YourAppTheme {
      CallToActionButton(text, onClick)
    }
  }
}
// [START_EXCLUDE silent]
@Composable
fun YourAppTheme(content: @Composable () -> Unit) {
}
// [END_EXCLUDE]
// [END android_compose_interop_existing_ui_shared]

// [START android_compose_interop_existing_ui_shared_view_binding]
class ViewBindingActivity : ComponentActivity() {

  private lateinit var binding: ActivityExampleBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityExampleBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.callToAction.apply {
      text = getString(R.string.greeting)
      onClick = { /* Do something */ }
    }
  }
}
// [END android_compose_interop_existing_ui_shared_view_binding]
