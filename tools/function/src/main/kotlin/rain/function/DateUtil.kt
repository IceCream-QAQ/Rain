package rain.function

import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtil {
    private val date = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dateTimeSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    /***
     * 获取当前日期的格式化后内容（yyyy-MM-dd）
     * @return 时间
     */
    fun formatDate(): String {
        return formatDate(LocalDateTime.now())
    }

    /***
     * 获取指定日期的格式化后内容（yyyy-MM-dd）
     * @param date 指定的日期
     * @return 时间字符串
     */
    fun formatDate(date: LocalDateTime): String {
        return date.format(this.date)
    }

    fun parseDate(dateStr: String): LocalDateTime {
        return LocalDateTime.parse(dateStr, this.date)
    }

    /***
     * 获取当前日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @return 时间
     */
    fun formatDateTime(): String {
        return formatDateTime(LocalDateTime.now())
    }

    /***
     * 获取指定日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    fun formatDateTime(date: LocalDateTime): String {
        return date.format(dateTime)
    }

    fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeStr, this.dateTime)
    }

    /***
     * 获取当前日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @return 时间
     */
    fun formatDateTimeSSS(): String {
        return formatDateTimeSSS(LocalDateTime.now())
    }

    /***
     * 获取指定日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    fun formatDateTimeSSS(date: LocalDateTime): String {
        return date.format(dateTimeSSS)
    }
}