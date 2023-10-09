package com.sameh.alarmclock.broadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sameh.alarmclock.R
import com.sameh.alarmclock.app.const.Constants
import com.sameh.alarmclock.app.const.Constants.CHANNEL_ID
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.service.RescheduleAlarmService
import com.sameh.alarmclock.ui.activities.ring.RingActivity
import com.sameh.alarmclock.utilsAndExtensions.parcelable
import com.sameh.alarmclock.utilsAndExtensions.toLogD
import com.sameh.alarmclock.utilsAndExtensions.toLogE
import java.util.Calendar

class AlarmBroadcastReceiver : BroadcastReceiver() {

    object MediaManager {
        var mediaPlayer: MediaPlayer? = null
        var vibrator: Vibrator? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // if phone is restarted
                val toastText = "Alarm Reboot"
                "onReceive: $toastText".toLogD()
                startRescheduleAlarmsService(context)
            }

            else -> {
                val toastText = "Alarm Received"
                "onReceive: $toastText".toLogD()

                val alarm = intent.parcelable<Alarm>(Constants.ALARM)

                if (alarm != null) {
                    if (alarm.recurring) {
                        // if alarm RECURRING
                        if (alarmIsToday(alarm)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                startAlarmNotification(context, alarm)
                            }
                        }
                    } else {
                        // if alarm not RECURRING
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            startAlarmNotification(context, alarm)
                        }
                    }
                }
            }
        }
    }

    private fun alarmIsToday(alarm: Alarm): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> alarm.saturday
            Calendar.SUNDAY -> alarm.sunday
            Calendar.MONDAY -> alarm.monday
            Calendar.TUESDAY -> alarm.tuesday
            Calendar.WEDNESDAY -> alarm.wednesday
            Calendar.THURSDAY -> alarm.thursday
            Calendar.FRIDAY -> alarm.friday
            else -> false
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startAlarmNotification(context: Context, alarm: Alarm) {
        try {
            val intent = Intent(context, RingActivity::class.java).apply {
                putExtra(Constants.ALARM, alarm)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )

            val customLayout = RemoteViews(context.packageName, R.layout.notification_layout)
            customLayout.setTextViewText(
                R.id.tv_notification_title,
                context.getString(R.string.app_name)
            )
            customLayout.setTextViewText(R.id.tv_notification_des, "${alarm.title.trim()} is now !")
            customLayout.setImageViewResource(R.id.iv_notification_ic, R.drawable.ic_alarm_clock)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Clock",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("${alarm.title.trim()} is now !")
                    .setSmallIcon(R.drawable.ic_alarm_clock)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setCustomContentView(customLayout)
                    .setContentIntent(pendingIntent)

            startAlarmTone(context, alarm.alarmToneUri)

            notificationManager.notify(1, notificationBuilder.build())

        } catch (e: Exception) {
            e.toString().toLogE()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startAlarmTone(context: Context, alarmToneUri: Uri?) {
        alarmToneUri.toString().toLogD()
        try {
            // Initialize media player and vibrator
            val mediaPlayer = MediaPlayer()

            // Check if alarmToneUri is not null
            if (alarmToneUri != null) {
                mediaPlayer.setDataSource(context, alarmToneUri)
            } else {
                mediaPlayer.setDataSource(context, Settings.System.DEFAULT_RINGTONE_URI)
            }

            mediaPlayer.isLooping = true

            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator

            // Start media player and vibrator
            mediaPlayer.prepare()
            mediaPlayer.start()

            val pattern = longArrayOf(0, 100, 1000)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))

            // Store the media player and vibrator in the MediaManager
            MediaManager.mediaPlayer = mediaPlayer
            MediaManager.vibrator = vibrator

        } catch (e: Exception) {
            e.toString().toLogE()
        }
    }
}