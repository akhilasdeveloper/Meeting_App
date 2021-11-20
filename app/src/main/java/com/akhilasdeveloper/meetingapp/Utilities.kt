package com.akhilasdeveloper.meetingapp

import java.text.SimpleDateFormat
import java.util.*

object Utilities {

    val DAY_MILLIS: Long = (1000 * 60 * 60 * 24).toLong()

    fun formatMillisDays(millis: Long): String {
        val seconds: Long = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val daysString = if (days<=0) "" else "$days Day(s), "
        val hoursString = if ((hours % 24)<=0) "" else (hours % 24).toString() + " hour(s), "
        val and = if (daysString.isEmpty() and hoursString.isEmpty()) "" else "and "
        val minutesString = if ((minutes % 60)<=0) "" else and + (minutes % 60).toString() + " minute(s)"

        return daysString + hoursString + minutesString
    }

    fun formatMillis(millis: Long): String {
        val pattern = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatMillisTime(millis: Long): String {
        val pattern = "hh:mm a"
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatDateToMillis(date: String):Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        return parsedDate?.time
    }

    private fun formatTimeToMillis(time: String):Long? {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val parsedDate = dateFormat.parse(time)
        return parsedDate?.time
    }

    fun stripTime(time:Long) = formatTimeToMillis(formatMillisTime(time))

}