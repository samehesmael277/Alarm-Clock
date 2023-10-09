package com.sameh.alarmclock.data.model

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sameh.alarmclock.app.const.Constants
import com.sameh.alarmclock.broadcastReceiver.AlarmBroadcastReceiver
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Entity(tableName = Constants.ALARM_TABLE)
@Parcelize
data class Alarm(
    @PrimaryKey
    var alarmId: Int,
    var hour: Int,
    var minute: Int,
    var title: String,
    var created: Long,
    var started: Boolean,
    var recurring: Boolean,
    var saturday: Boolean,
    var sunday: Boolean,
    var monday: Boolean,
    var tuesday: Boolean,
    var wednesday: Boolean,
    var thursday: Boolean,
    var friday: Boolean,
    var alarmToneUri: Uri?,
    var isSnooze: Boolean = false
) : Parcelable {

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(Constants.ALARM, this@Alarm)
        }

        val alarmPendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If alarm time has already passed, increment day by 1
            if (timeInMillis <= System.currentTimeMillis()) {
                set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
            }
        }

        if (!recurring) alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmPendingIntent
        )
        else
            alarmManager.setRepeating(
                AlarmManager.RTC,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent
            )

        started = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent =
            PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(alarmPendingIntent)
        started = false
    }

    fun getRecurringDaysText(): String? {
        if (!recurring) {
            return null
        }

        val days = StringBuilder()
        if (saturday) {
            days.append("Sat ")
        }
        if (sunday) {
            days.append("Sun ")
        }
        if (monday) {
            days.append("Mon ")
        }
        if (tuesday) {
            days.append("Tus ")
        }
        if (wednesday) {
            days.append("Wed ")
        }
        if (thursday) {
            days.append("Thu ")
        }
        if (friday) {
            days.append("Fri ")
        }

        return days.toString().trim()
    }
}
