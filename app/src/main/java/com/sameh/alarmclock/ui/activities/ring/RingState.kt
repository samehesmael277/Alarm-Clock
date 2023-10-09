package com.sameh.alarmclock.ui.activities.ring

sealed class RingState {

    data object Idle : RingState()

    data class Loading(val loading: Boolean) : RingState()

    data object MoveToMainActivity : RingState()
}
