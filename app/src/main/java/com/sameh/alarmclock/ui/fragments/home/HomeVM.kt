package com.sameh.alarmclock.ui.fragments.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.data.roomdb.IAlarmRepo
import com.sameh.alarmclock.utilsAndExtensions.getToastMessageForAlarmOneTime
import com.sameh.alarmclock.utilsAndExtensions.getToastMessageForCancelAlarm
import com.sameh.alarmclock.utilsAndExtensions.getToastMessageRecurringAlarm
import com.sameh.alarmclock.utilsAndExtensions.toLogD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val app: Application,
    private val alarmRepo: IAlarmRepo
) : ViewModel() {

    private val _viewState = MutableStateFlow<HomeState>(HomeState.Idle)
    val state: StateFlow<HomeState> get() = _viewState

    val alarms: Flow<List<Alarm>> = alarmRepo.getAllAlarms()

    fun updateScheduleCancelAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(HomeState.Loading(true))
            if (alarm.started) {
                alarm.cancelAlarm(app)
                alarmRepo.insertUpdateAlarm(alarm)
                val toastText = getToastMessageForCancelAlarm(alarm)
                "cancel: $toastText".toLogD()
            } else {
                alarm.schedule(app)
                alarmRepo.insertUpdateAlarm(alarm)
                if (!alarm.recurring) {
                    val toastText = getToastMessageForAlarmOneTime(alarm)
                    "schedule: $toastText".toLogD()
                } else {
                    val toastText = getToastMessageRecurringAlarm(alarm)
                    "schedule: $toastText".toLogD()
                }
            }
            _viewState.emit(HomeState.Loading(false))
        }
    }

    fun cancelDeleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            _viewState.emit(HomeState.Loading(true))
            alarm.cancelAlarm(app)
            alarmRepo.deleteAlarm(alarm)
            val toastText = getToastMessageForCancelAlarm(alarm)
            "cancel: $toastText".toLogD()
            _viewState.emit(HomeState.Loading(false))
        }
    }
}