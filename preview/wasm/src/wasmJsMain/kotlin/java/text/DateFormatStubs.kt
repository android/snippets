package java.text

object DateFormat {
    const val SHORT = 3
    fun getDateInstance(style: Int, locale: Any? = null): DateFormat = DateFormat
    fun format(date: Any? = null): String = "01/01/2024"
}
