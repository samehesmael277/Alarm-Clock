package com.sameh.alarmclock.ui.fragments.scheduleAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.data.roomdb.IAlarmRepo
import com.sameh.alarmclock.utilsAndExtensions.getToastMessageForAlarmOneTime
import com.sameh.alarmclock.utilsAndExtensions.getToastMessageRecurringAlarm
import com.sameh.alarmclock.utilsAndExtensions.toLogD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleAlarmVM @Inject constructor(
    private val app: Application,
    private val alarmRepo: IAlarmRepo
) : ViewModel() {

    private val _viewState = MutableStateFlow<ScheduleAlarmState>(ScheduleAlarmState.Idle)
    val state: StateFlow<ScheduleAlarmState> get() = _viewState

    fun insertScheduleAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(ScheduleAlarmState.Loading(true))
            alarmRepo.insertUpdateAlarm(alarm)
            alarm.schedule(app)
            if (!alarm.recurring) {
                val toastText = getToastMessageForAlarmOneTime(alarm)
                "schedule: $toastText".toLogD()
            } else {
                val toastText = getToastMessageRecurringAlarm(alarm)
                "schedule: $toastText".toLogD()
            }
            _viewState.emit(ScheduleAlarmState.Loading(false))
            _viewState.emit(ScheduleAlarmState.InsertScheduleAlarm)
        }
    }
}