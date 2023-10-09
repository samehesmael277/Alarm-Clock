package com.sameh.alarmclock.di

import android.content.Context
import androidx.room.Room
import com.sameh.alarmclock.app.const.Constants
import com.sameh.alarmclock.data.roomdb.AlarmDatabase
import com.sameh.alarmclock.data.roomdb.AlarmsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAlarmRoomDB(
        @ApplicationContext context: Context,
    ): AlarmDatabase =
        Room.databaseBuilder(context, AlarmDatabase::class.java, Constants.ROOM_DB_NAME).build()

    @Singleton
    @Provides
    fun provideAlarmDao(db: AlarmDatabase): AlarmsDao = db.alarmsDao()
}