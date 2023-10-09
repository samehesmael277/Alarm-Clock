package com.sameh.alarmclock.data.roomdb

import com.sameh.alarmclock.data.model.Alarm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepo @Inject constructor(
    private val alarmDao: AlarmsDao
) : IAlarmRepo {

    override suspend fun insertUpdateAlarm(alarm: Alarm) = alarmDao.insertAlarm(alarm)

    override suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    override suspend fun deleteAlarmById(alarmId: Int) = alarmDao.deleteAlarmById(alarmId)

    override suspend fun setAlarmOFF(alarmId: Int) = alarmDao.setAlarmOFF(alarmId)

    override suspend fun setAlarmOn(alarmId: Int) = alarmDao.setAlarmOn(alarmId)

    override fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAlarms()
}