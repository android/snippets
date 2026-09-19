package java.text

import java.util.Date
import java.util.Locale

class SimpleDateFormat(val pattern: String, val locale: Locale) {
    fun format(date: Date): String = "01/01/2024"
}
