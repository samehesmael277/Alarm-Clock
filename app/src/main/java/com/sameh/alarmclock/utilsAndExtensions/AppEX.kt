package com.sameh.alarmclock.utilsAndExtensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sameh.alarmclock.app.const.Constants

fun String.toLogD(tag: String = Constants.APP_TAG) {
    Log.d(tag, "Data are: $this")
}

fun String.toLogE(tag: String = Constants.APP_TAG) {
    Log.e(tag, "Data are: $this")
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}