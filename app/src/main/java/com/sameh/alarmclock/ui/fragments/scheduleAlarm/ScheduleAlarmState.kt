package com.sameh.alarmclock.ui.fragments.scheduleAlarm

sealed class ScheduleAlarmState {

    data object Idle : ScheduleAlarmState()

    data class Loading(val loading: Boolean) : ScheduleAlarmState()

    data object InsertScheduleAlarm : ScheduleAlarmState()
}
