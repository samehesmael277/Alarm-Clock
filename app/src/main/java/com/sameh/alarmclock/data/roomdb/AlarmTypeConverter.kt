package com.sameh.alarmclock.data.roomdb

import android.net.Uri
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters
class AlarmTypeConverter {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}