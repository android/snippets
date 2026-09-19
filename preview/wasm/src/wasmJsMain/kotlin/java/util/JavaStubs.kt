package java.util

class Date(val time: Long = 0)

class Calendar {
    companion object {
        const val HOUR_OF_DAY = 11
        const val MINUTE = 12
        fun getInstance(): Calendar = Calendar()
    }
    var isLenient: Boolean = true
    var time: Date = Date()
    fun set(field: Int, value: Int) {}
    fun get(field: Int): Int = 0
}

class Locale {
    companion object {
        fun getDefault(): Locale = Locale()
    }
}
