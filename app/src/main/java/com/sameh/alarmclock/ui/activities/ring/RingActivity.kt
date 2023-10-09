package com.sameh.alarmclock.ui.activities.ring

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sameh.alarmclock.app.const.Constants
import com.sameh.alarmclock.broadcastReceiver.AlarmBroadcastReceiver
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.databinding.ActivityRingBinding
import com.sameh.alarmclock.ui.activities.main.MainActivity
import com.sameh.alarmclock.utilsAndExtensions.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RingActivity : AppCompatActivity() {

    private var _binding: ActivityRingBinding? = null
    private val binding get() = _binding!!

    private val ringVM: RingVM by viewModels()

    private var alarm: Alarm? = null

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarm = intent.parcelable(Constants.ALARM)

        if (alarm != null) {
            binding.tvAlarmTitle.text = "${alarm!!.title.trim()} Alarm Now !"
            animateClock()
            setActions()
            viewModelObserver()
        } else {
            stopMediaPlayerAndVibrator()
            moveToMainActivity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMediaPlayerAndVibrator()
        ringVM.dismissAlarm(alarm!!)
        _binding = null
    }

    private fun setActions() {
        onBackPressedDispatcher.addCallback(this) {
            stopMediaPlayerAndVibrator()
            ringVM.dismissAlarm(alarm!!)
        }
        binding.apply {
            btnDismiss.setOnClickListener {
                stopMediaPlayerAndVibrator()
                ringVM.dismissAlarm(alarm!!)
            }
            btnSnooze.setOnClickListener {
                stopMediaPlayerAndVibrator()
                ringVM.snoozeAlarm(alarm!!)
            }
        }
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            ringVM.state.collect {
                when (it) {
                    is RingState.Idle -> {}
                    is RingState.Loading -> onLoading(it.loading)
                    is RingState.MoveToMainActivity -> moveToMainActivity()
                }
            }
        }
    }

    private fun stopMediaPlayerAndVibrator() {
        AlarmBroadcastReceiver.MediaManager.mediaPlayer?.stop()
        AlarmBroadcastReceiver.MediaManager.mediaPlayer?.release()
        AlarmBroadcastReceiver.MediaManager.mediaPlayer = null

        AlarmBroadcastReceiver.MediaManager.vibrator?.cancel()
        AlarmBroadcastReceiver.MediaManager.vibrator = null
    }

    private fun animateClock() {
        val rotateAnimation =
            ObjectAnimator.ofFloat(binding.ivAlarmClock, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }

    private fun onLoading(loading: Boolean = false) {
        when (loading) {
            true -> closeTouche()
            false -> openTouche()
        }
    }

    private fun closeTouche() {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun openTouche() {
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}