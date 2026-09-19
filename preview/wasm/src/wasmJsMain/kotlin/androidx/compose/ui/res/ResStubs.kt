package androidx.compose.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun painterResource(id: Int): Painter = ColorPainter(Color(0xFF006D3B))

@Composable
fun stringResource(id: Int): String = "Sample Text"
