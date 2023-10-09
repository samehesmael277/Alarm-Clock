package com.sameh.alarmclock.ui.fragments.home

sealed class HomeState {

    data object Idle : HomeState()

    data class Loading(val loading: Boolean) : HomeState()
}
