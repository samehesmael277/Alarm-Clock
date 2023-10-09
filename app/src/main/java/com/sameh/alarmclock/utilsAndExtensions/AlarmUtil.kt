package com.sameh.alarmclock.utilsAndExtensions

import com.sameh.alarmclock.data.model.Alarm
import java.util.Calendar

fun getToastMessageForAlarmOneTime(alarm: Alarm): String {
    val calendar = Calendar.getInstance()
    return try {
        String.format(
            "One Time Alarm %s scheduled for %s at %02d:%02d",
            alarm.title,
            toDay(calendar.get(Calendar.DAY_OF_WEEK)),
            alarm.hour,
            alarm.minute,
            alarm.alarmId
        )
    } catch (e: Exception) {
        e.printStackTrace()
        "schedule: $e".toLogE()
        ""
    }
}

fun getToastMessageRecurringAlarm(alarm: Alarm): String {
    return String.format(
        "Recurring Alarm %s scheduled for %s at %02d:%02d",
        alarm.title,
        alarm.getRecurringDaysText(),
        alarm.hour,
        alarm.minute,
        alarm.alarmId
    )
}

fun getToastMessageForCancelAlarm(alarm: Alarm): String {
    return String.format(
        "Alarm cancelled for %02d:%02d with id %d",
        alarm.hour,
        alarm.minute,
        alarm.alarmId
    )
}