package com.sameh.alarmclock.utilsAndExtensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun toDay(day: Int): String {
    return when (day) {
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        else -> throw Exception("Could not locate day")
    }
}

fun getDateTime(): Pair<String, String> {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val currentDate = dateFormat.format(Date()).uppercase(Locale.getDefault())
    val currentTime = timeFormat.format(Date())
    return Pair(currentDate, currentTime)
}

fun getDate(timestampInMillis: Long): String {
    val pattern = "dd-MM-yyyy HH:mm:ss" // Example pattern, change it to your desired format
    // Create a SimpleDateFormat instance with the desired pattern and locale
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    // Convert the timestamp to a Date object
    val date = Date(timestampInMillis)
    // Format the Date object to a string
    return sdf.format(date)
}