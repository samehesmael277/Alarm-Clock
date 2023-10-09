package com.sameh.alarmclock.data.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sameh.alarmclock.data.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("DELETE FROM alarm_table WHERE alarmId == :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    @Query("UPDATE alarm_table SET started = 0 WHERE alarmId = :alarmId")
    suspend fun setAlarmOFF(alarmId: Int)

    @Query("UPDATE alarm_table SET started = 1 WHERE alarmId = :alarmId")
    suspend fun setAlarmOn(alarmId: Int)

    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    fun getAlarms(): Flow<List<Alarm>>
}