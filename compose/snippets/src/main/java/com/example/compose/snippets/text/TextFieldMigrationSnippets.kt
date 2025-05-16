package com.example.compose.snippets.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SecureTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.snippets.touchinput.Button
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

// [START android_compose_text_textfield_migration_old_simple]
@Composable
fun OldSimpleTextField() {
  var state by rememberSaveable { mutableStateOf("") }
  TextField(
    value = state,
    onValueChange = { state = it },
    singleLine = true,
  )
}
// [END android_compose_text_textfield_migration_old_simple]

// [START android_compose_text_textfield_migration_new_simple]
@Composable
fun NewSimpleTextField() {
  TextField(
    state = rememberTextFieldState(),
    lineLimits = TextFieldLineLimits.SingleLine
  )
}
// [END android_compose_text_textfield_migration_new_simple]

// [START android_compose_text_textfield_migration_old_filtering]
@Composable
fun OldNoLeadingZeroes() {
  var input by rememberSaveable { mutableStateOf("") }
  TextField(
    value = input,
    onValueChange = { newText ->
      input = newText.trimStart { it == '0' }
    }
  )
}
// [END android_compose_text_textfield_migration_old_filtering]

// [START android_compose_text_textfield_migration_new_filtering]

@Preview
@Composable
fun NewNoLeadingZeros() {
  TextField(
    state = rememberTextFieldState(),
    inputTransformation = InputTransformation {
      while (length > 0 && charAt(0) == '0') delete(0, 1)
    }
  )
}
// [END android_compose_text_textfield_migration_new_filtering]

// [START android_compose_text_textfield_migration_old_credit_card_formatter]
@Composable
fun OldTextFieldCreditCardFormatter() {
  var state by remember { mutableStateOf("") }
  TextField(
    value = state,
    onValueChange = { if(it.length <= 16) state = it },
    visualTransformation = VisualTransformation { text ->
      // Making XXXX-XXXX-XXXX-XXXX string.
      var out = ""
      for (i in text.indices) {
        out += text[i]
        if (i % 4 == 3 && i != 15) out += "-"
      }


      TransformedText(
        text = AnnotatedString(out),
        offsetMapping = object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset + 1
            if (offset <= 11) return offset + 2
            if (offset <= 16) return offset + 3
            return 19
          }


          override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 1
            if (offset <= 14) return offset - 2
            if (offset <= 19) return offset - 3
            return 16
          }
        })
    }
  )
}
// [END android_compose_text_textfield_migration_old_credit_card_formatter]

// [START android_compose_text_textfield_migration_new_credit_card_formatter]
@Composable
fun NewTextFieldCreditCardFormatter() {
  val state = rememberTextFieldState()
  TextField(
    state = state,
    inputTransformation = InputTransformation.maxLength(16),
    outputTransformation = OutputTransformation {
      if (length > 4) insert(4, "-")
      if (length > 9) insert(9, "-")
      if (length > 14) insert(14, "-")
    },
  )
}
// [END android_compose_text_textfield_migration_new_credit_card_formatter]

private object StateUpdateSimpleSnippet {
  object UserRepository {
    suspend fun fetchUsername(): String = TODO()
  }
  // [START android_compose_text_textfield_migration_old_update_state_simple]
  @Composable
  fun OldTextFieldStateUpdate(userRepository: UserRepository) {
    var username by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
      username = userRepository.fetchUsername()
    }
    TextField(
      value = username,
      onValueChange = { username = it }
    )
  }
  // [END android_compose_text_textfield_migration_old_update_state_simple]

  // [START android_compose_text_textfield_migration_new_update_state_simple]
  @Composable
  fun NewTextFieldStateUpdate(userRepository: UserRepository) {
    val usernameState = rememberTextFieldState()
    LaunchedEffect(Unit) {
      usernameState.setTextAndPlaceCursorAtEnd(userRepository.fetchUsername())
    }
    TextField(state = usernameState)
  }
  // [END android_compose_text_textfield_migration_new_update_state_simple]
}

// [START android_compose_text_textfield_migration_old_state_update_complex]
@Composable
fun OldTextFieldAddMarkdownEmphasis() {
  var markdownState by remember { mutableStateOf(TextFieldValue()) }
  Button(onClick = {
    // add ** decorations around the current selection, also preserve the selection
    markdownState = with(markdownState) {
      copy(
        text = buildString {
          append(text.take(selection.min))
          append("**")
          append(text.substring(selection))
          append("**")
          append(text.drop(selection.max))
        },
        selection = TextRange(selection.min + 2, selection.max + 2)
      )
    }
  }) {
    Text("Bold")
  }
  TextField(
    value = markdownState,
    onValueChange = { markdownState = it },
    maxLines = 10
  )
}
// [END android_compose_text_textfield_migration_old_state_update_complex]

// [START android_compose_text_textfield_migration_new_state_update_complex]
@Composable
fun NewTextFieldAddMarkdownEmphasis() {
  val markdownState = rememberTextFieldState()
  LaunchedEffect(Unit) {
    // add ** decorations around the current selection
    markdownState.edit {
      insert(originalSelection.max, "**")
      insert(originalSelection.min, "**")
      selection = TextRange(originalSelection.min + 2, originalSelection.max + 2)
    }
  }
  TextField(
    state = markdownState,
    lineLimits = TextFieldLineLimits.MultiLine(1, 10)
  )
}
// [END android_compose_text_textfield_migration_new_state_update_complex]

private object ViewModelMigrationOldSnippet {
  // [START android_compose_text_textfield_migration_old_viewmodel]
  class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
      get() = _uiState.asStateFlow()

    fun updateUsername(username: String) = _uiState.update { it.copy(username = username) }

    fun updatePassword(password: String) = _uiState.update { it.copy(password = password) }
  }

  data class UiState(
    val username: String = "",
    val password: String = ""
  )

  @Composable
  fun LoginForm(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier
  ) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier) {
      TextField(
        value = uiState.username,
        onValueChange = { loginViewModel.updateUsername(it) }
      )
      TextField(
        value = uiState.password,
        onValueChange = { loginViewModel.updatePassword(it) },
        visualTransformation = PasswordVisualTransformation()
      )
    }
  }
  // [END android_compose_text_textfield_migration_old_viewmodel]
}

private object ViewModelMigrationNewSimpleSnippet {
  // [START android_compose_text_textfield_migration_new_viewmodel_simple]
  class LoginViewModel : ViewModel() {
    val usernameState = TextFieldState()
    val passwordState = TextFieldState()
  }

  @Composable
  fun LoginForm(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier
  ) {
    Column(modifier) {
      TextField(state = loginViewModel.usernameState,)
      SecureTextField(state = loginViewModel.passwordState)
    }
  }
  // [END android_compose_text_textfield_migration_new_viewmodel_simple]
}

private object ViewModelMigrationNewConformingSnippet {
  // [START android_compose_text_textfield_migration_new_viewmodel_conforming]
  class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
      get() = _uiState.asStateFlow()

    fun updateUsername(username: String) = _uiState.update { it.copy(username = username) }

    fun updatePassword(password: String) = _uiState.update { it.copy(password = password) }
  }

  data class UiState(
    val username: String = "",
    val password: String = ""
  )

  @Composable
  fun LoginForm(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier
  ) {
    val initialUiState = remember(loginViewModel) { loginViewModel.uiState.value }
    Column(modifier) {
      val usernameState = rememberTextFieldState(initialUiState.username)
      LaunchedEffect(usernameState) {
        snapshotFlow { usernameState.text.toString() }.collectLatest {
          loginViewModel.updateUsername(it)
        }
      }
      TextField(usernameState)

      val passwordState = rememberTextFieldState(initialUiState.password)
      LaunchedEffect(usernameState) {
        snapshotFlow { usernameState.text.toString() }.collectLatest {
          loginViewModel.updatePassword(it)
        }
      }
      SecureTextField(passwordState)
    }
  }
  // [END android_compose_text_textfield_migration_new_viewmodel_conforming]
}

