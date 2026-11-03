import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.compose.snippets.designsystems.ColorScheme
import com.example.compose.snippets.designsystems.SettingsMock

@Preview(
    name = "SettingsMock — Night",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 360,
    heightDp = 720
)
@Composable
fun PreviewSettingsMock_Night() {
    // Force darkTheme = true so the preview uses the dark color scheme
    ColorScheme.ReplyTheme(darkTheme = true) {
        SettingsMock(
            itemsList = listOf(
                "Display",
                "Battery",
                "Wallpaper and style",
                "Themes",
                "Home screen",
                "Lock screen",
                "Security and privacy",
                "Location",
                "Safety and emergency"
            )
        )
    }
}
