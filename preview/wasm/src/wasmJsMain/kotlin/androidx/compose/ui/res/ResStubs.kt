package androidx.compose.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.example.compose.snippets.R
import com.example.compose.snippets.components.AppIcons

@Composable
fun painterResource(id: Int): Painter {
    val icon = when (id) {
        R.drawable.favorite -> AppIcons.Favorite
        R.drawable.favorite_filled -> AppIcons.FavoriteFilled
        R.drawable.fast_forward -> AppIcons.FastForward
        R.drawable.fast_forward_filled -> AppIcons.FastForwardFilled
        R.drawable.fast_rewind -> AppIcons.FastRewind
        R.drawable.fast_rewind_filled -> AppIcons.FastRewindFilled
        else -> null
    }
    return if (icon != null) {
        rememberVectorPainter(icon)
    } else {
        ColorPainter(Color(0xFF006D3B))
    }
}

@Composable
fun stringResource(id: Int): String = "Sample Text"
