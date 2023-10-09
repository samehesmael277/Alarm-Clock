package com.sameh.alarmclock.ui.activities.ring

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.data.roomdb.IAlarmRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class RingVM @Inject constructor(
    private val app: Application,
    private val alarmRepo: IAlarmRepo
) : ViewModel() {

    private val _viewState = MutableStateFlow<RingState>(RingState.Idle)
    val state: StateFlow<RingState> get() = _viewState

    fun dismissAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(RingState.Loading(true))
            if (alarm.isSnooze) {
                alarm.cancelAlarm(app)
            } else {
                if (!alarm.recurring) {
                    alarm.cancelAlarm(app)
                    alarmRepo.insertUpdateAlarm(alarm)
                }
            }
            _viewState.emit(RingState.Loading(false))
            _viewState.emit(RingState.MoveToMainActivity)
        }
    }

    fun snoozeAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(RingState.Loading(true))
            if (alarm.isSnooze) {
                alarm.cancelAlarm(app)
                val snoozeAlarm = returnSnoozeAlarm(alarm.alarmToneUri)
                snoozeAlarm.schedule(app)
            } else {
                if (alarm.recurring) {
                    val snoozeAlarm = returnSnoozeAlarm(alarm.alarmToneUri)
                    snoozeAlarm.schedule(app)
                } else {
                    alarm.cancelAlarm(app)
                    alarmRepo.insertUpdateAlarm(alarm)
                    val snoozeAlarm = returnSnoozeAlarm(alarm.alarmToneUri)
                    snoozeAlarm.schedule(app)
                }
            }
            _viewState.emit(RingState.Loading(false))
            _viewState.emit(RingState.MoveToMainActivity)
        }
    }

    private fun returnSnoozeAlarm(toneUri: Uri?): Alarm {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 10)

        return Alarm(
            Random().nextInt(Int.MAX_VALUE),
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            "Snooze",
            System.currentTimeMillis(),
            true,
            recurring = false,
            saturday = false,
            sunday = false,
            monday = false,
            tuesday = false,
            wednesday = false,
            thursday = false,
            friday = false,
            alarmToneUri = toneUri,
            isSnooze = true
        )
    }
}