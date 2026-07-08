package rain.function

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

val currentTimeMillis: Long
    inline get() = System.currentTimeMillis()

val currentTimeSecondsL: Long
    inline get() = System.currentTimeMillis()

val currentTimeSecondsI: Int
    inline get() = (currentTimeSecondsL / 1000).toInt()

@Deprecated("Use currentTimeSecondsI instead", ReplaceWith("currentTimeSecondsI"))
val currentTimeSeconds: Int
    inline get() = (currentTimeMillis / 1000).toInt()

val currentTimeHours: Long
    inline get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault())
        .truncatedTo(ChronoUnit.HOURS)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

val currentTimeDays: Long
    inline get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault())
        .truncatedTo(ChronoUnit.DAYS)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

val currentTimeMonths: Long
    inline get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault())
        .withDayOfMonth(1)
        .truncatedTo(ChronoUnit.DAYS)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

const val mSecond = 1000L
const val m2Seconds = 2L * mSecond
const val m3Seconds = 3L * mSecond
const val m5Seconds = 5L * mSecond
const val m10Seconds = 10L * mSecond
const val m15Seconds = 15L * mSecond
const val m20Seconds = 20L * mSecond
const val m30Seconds = 30L * mSecond

const val mMinute = 60L * mSecond
const val m5Minutes = 5L * mMinute
const val m10Minutes = 10L * mMinute
const val m15Minutes = 15L * mMinute
const val m20Minutes = 20L * mMinute
const val m30Minutes = 30L * mMinute

const val mHour = 60L * mMinute
const val m2Hours = 2L * mHour
const val m3Hours = 3L * mHour
const val m4Hours = 4L * mHour
const val m5Hours = 5L * mHour
const val m6Hours = 6L * mHour
const val m10Hours = 10L * mHour
const val m12Hours = 12L * mHour

const val mDay = 24L * mHour
const val m2Days = 2L * mDay
const val m3Days = 3L * mDay
const val m4Days = 4L * mDay
const val m5Days = 5L * mDay
const val m6Days = 6L * mDay
const val mWeek = 7L * mDay
const val m10Days = 10L * mDay
const val m2Weeks = 2L * mWeek
const val m15Days = 15L * mDay
const val m20Days = 20L * mDay
const val m28Days = 28L * mDay
const val m29Days = 29L * mDay
const val m30Days = 30L * mDay
const val m31Days = 31L * mDay

// 30 Days
const val mMonth = m30Days
const val m2Months = 2L * mMonth
const val m3Months = 3L * mMonth
const val m4Months = 4L * mMonth
const val m5Months = 5L * mMonth
const val m6Months = 6L * mMonth
const val m10Months = 10L * mMonth
const val m12Months = 12L * mMonth

// 365 Days
const val mYear = 365 * mDay