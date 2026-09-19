package androidx.compose.ui.tooling.preview

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Preview(
    val showBackground: Boolean = false,
    val name: String = "",
    val group: String = "",
    val apiLevel: Int = -1,
    val widthDp: Int = -1,
    val heightDp: Int = -1,
    val locale: String = "",
    val fontScale: Float = 1f,
    val showSystemUi: Boolean = false
)
