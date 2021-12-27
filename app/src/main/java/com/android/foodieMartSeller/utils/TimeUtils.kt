package com.android.foodieMartSeller.utils


import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    private const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    private const val DATE_FORMAT_YY_MM_DD = "yyyy-MM-dd"
//    private const val DATE_FORMAT_API = "E MMM dd yyyy HH:mm:ss 'GMT'XXX (z)"


    fun getUTCDate(calender: Calendar): String {
        calender.timeZone = TimeZone.getTimeZone("UTC")
        val time: Date = calender.time
        val outputFmt = SimpleDateFormat(DATE_FORMAT_API, Locale.getDefault())
        return outputFmt.format(time)
    }

    fun getDayMonthYearFormat(input: String?): String {
        return input?.let {
            val localDateTime =
                LocalDateTime.parse(input, DateTimeFormat.forPattern(DATE_FORMAT_API))
            SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(localDateTime.toDate().time)
        } ?: run {
            ""
        }
    }

    fun getDayMonthFormat(input: String?): String {
        return input?.let {
            val localDateTime =
                LocalDateTime.parse(input, DateTimeFormat.forPattern(DATE_FORMAT_API))
            SimpleDateFormat("dd MMM", Locale.getDefault())
                .format(localDateTime.toDate().time)
        } ?: run {
            ""
        }
    }

    fun getMonth(input: String?): Int {
        val localDateTime = LocalDateTime.parse(input, DateTimeFormat.forPattern(DATE_FORMAT_API))
        return Integer.parseInt(
            SimpleDateFormat(
                "MM",
                Locale.getDefault()
            ).format(localDateTime.toDate().time)
        )
    }

    fun getDaysAgoDateTime(daysAgo: Int): String {
        return DateTime.now().minusDays(daysAgo).toString(DATE_FORMAT_YY_MM_DD)
    }

    fun getMonthAgoDateTime(monthsAgo: Int): String {
        return DateTime.now().minusMonths(monthsAgo).toString(DATE_FORMAT_YY_MM_DD)
    }

    fun getCurrentDateTime(): String {
        return DateTime.now().toString(DATE_FORMAT_YY_MM_DD)
    }

    fun getLastWeekDaysNamesListAsShort(daysAgo: Int): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until daysAgo) {
            list.add(DateTime.now().minusDays(i).dayOfWeek().asShortText)
        }
        return list.asReversed()
    }


    fun getLastMonthsNamesListAsShort(monthAgo: Int): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until monthAgo) {
            list.add(DateTime.now().minusMonths(i).monthOfYear().asShortText)
        }
        return list.asReversed()
    }

    fun getLastWeekDaysAsYYMMDD(daysAgo: Int): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until daysAgo) {
            list.add(DateTime.now().minusDays(i).toString(DATE_FORMAT_API))
        }
        return list.asReversed()
    }

    fun getLastMonthsFullNamesAsYYMMDD(monthAgo: Int): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in 0 until monthAgo) {
            list.add(DateTime.now().minusMonths(i).toString(DATE_FORMAT_YY_MM_DD))
        }
        return list.asReversed()
    }


    fun getWeekDayAsShort(date: String?): String {
        return date?.let {
            val dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT_API)
            dateFormatter.parseDateTime(date).dayOfWeek().asShortText
        } ?: ""
    }

    fun getMonthDayAsShort(date: String?): String {
        return date?.let {
            val dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT_YY_MM_DD)
            dateFormatter.parseDateTime(date).dayOfMonth().asShortText
        } ?: ""
    }

    fun getHoursBetween(start: DateTime, end: DateTime): Float {
        return Seconds.secondsBetween(
            start,
            end
        ).seconds / 3600.toFloat()
    }

    fun getDateTimeForString(date: String?): DateTime {
        val dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT_API)
        return dateFormatter.parseDateTime(date)
    }
}
