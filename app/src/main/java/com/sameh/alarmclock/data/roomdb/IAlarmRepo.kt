package com.sameh.alarmclock.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.sameh.alarmclock.data.model.Alarm
import kotlinx.coroutines.flow.Flow

interface IAlarmRepo {

    suspend fun insertUpdateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun deleteAlarmById(alarmId: Int)

    suspend fun setAlarmOFF(alarmId: Int)

    suspend fun setAlarmOn(alarmId: Int)

    fun getAllAlarms(): Flow<List<Alarm>>
}