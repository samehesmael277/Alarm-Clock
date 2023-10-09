package com.sameh.alarmclock.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sameh.alarmclock.data.model.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@TypeConverters(AlarmTypeConverter::class)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmsDao(): AlarmsDao
}