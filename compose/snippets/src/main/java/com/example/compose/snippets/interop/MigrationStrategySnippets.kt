package com.example.compose.snippets.interop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.example.compose.snippets.R

// [START android_compose_interop_migration_strategy_fragment]
class NewFeatureFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
      setContent {
        NewFeatureScreen()
      }
    }
  }
}
// [END android_compose_interop_migration_strategy_fragment]

// [START android_compose_interop_migration_strategy_simple_screen]
@Composable
fun SimpleScreen() {
  Column(Modifier.fillMaxSize()) {
    Text(
      text = stringResource(R.string.title),
      style = MaterialTheme.typography.headlineMedium
    )
    Text(
      text = stringResource(R.string.subtitle),
      style = MaterialTheme.typography.headlineSmall
    )
    Text(
      text = stringResource(R.string.body),
      style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.weight(1f))
    Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
      Text(text = stringResource(R.string.confirm))
    }
  }
}
// [END android_compose_interop_migration_strategy_simple_screen]

@Composable
private fun NewFeatureScreen() { }
