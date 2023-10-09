package com.sameh.alarmclock.di

import com.sameh.alarmclock.data.roomdb.AlarmRepo
import com.sameh.alarmclock.data.roomdb.AlarmsDao
import com.sameh.alarmclock.data.roomdb.IAlarmRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModel {

    @Singleton
    @Provides
    fun provideAlarmRepo(alarmsDao: AlarmsDao): IAlarmRepo = AlarmRepo(alarmsDao)
}