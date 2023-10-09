package com.sameh.alarmclock.ui.fragments.scheduleAlarm

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sameh.alarmclock.R
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.databinding.FragmentScheduleAlarmBinding
import com.sameh.alarmclock.utilsAndExtensions.TimePickerUtil
import com.sameh.alarmclock.utilsAndExtensions.toLogD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleAlarmFragment : Fragment() {

    private lateinit var binding: FragmentScheduleAlarmBinding

    private val scheduleAlarmVM: ScheduleAlarmVM by viewModels()

    private var selectedAlarmTone: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        viewModelObserver()
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            scheduleAlarmVM.state.collect {
                when (it) {
                    is ScheduleAlarmState.Idle -> {}
                    is ScheduleAlarmState.Loading -> {
                        onLoading(it.loading)
                        showLoader(it.loading)
                    }

                    is ScheduleAlarmState.InsertScheduleAlarm -> {
                        onLoading(false)
                        navigateBackToHomeFragment()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setActions() {
        binding.apply {
            ivSave.setOnClickListener {
                scheduleAlarm()
            }
            ivBack.setOnClickListener {
                navigateBackToHomeFragment()
            }
            tvChooseAlarmTone.setOnClickListener {
                chooseAlarmTone()
            }
            cbRecurring.setOnCheckedChangeListener { _, b ->
                showRecurringOptions(b)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleAlarm() {
        val alarmId = (0..Int.MAX_VALUE).random()
        val timeHour = TimePickerUtil.getTimePickerHour(binding.timePicker)
        val timeMinute = TimePickerUtil.getTimePickerMinute(binding.timePicker)
        val alarmTitle = binding.etAlarmTitle.text.toString()
        val currentTimeMillis = System.currentTimeMillis()

        if (binding.cbRecurring.isChecked) {
            scheduleAlarmVM.insertScheduleAlarm(
                Alarm(
                    alarmId,
                    timeHour,
                    timeMinute,
                    alarmTitle,
                    currentTimeMillis,
                    true,
                    binding.cbRecurring.isChecked,
                    binding.cbSaturday.isChecked,
                    binding.cbSunday.isChecked,
                    binding.cbMonday.isChecked,
                    binding.cbTuesday.isChecked,
                    binding.cbWednesday.isChecked,
                    binding.cbThursday.isChecked,
                    binding.cbFriday.isChecked,
                    selectedAlarmTone
                )
            )
        } else {
            scheduleAlarmVM.insertScheduleAlarm(
                Alarm(
                    alarmId,
                    timeHour,
                    timeMinute,
                    alarmTitle,
                    currentTimeMillis,
                    true,
                    recurring = false,
                    saturday = false,
                    sunday = false,
                    monday = false,
                    tuesday = false,
                    wednesday = false,
                    thursday = false,
                    friday = false,
                    alarmToneUri = selectedAlarmTone
                )
            )
        }
    }

    private fun showRecurringOptions(show: Boolean) {
        if (show) {
            binding.linearRecurringOptions.visibility = View.VISIBLE
            binding.linearRecurringOptions2.visibility = View.VISIBLE
        } else {
            binding.linearRecurringOptions.visibility = View.GONE
            binding.linearRecurringOptions2.visibility = View.GONE
        }
    }

    private fun navigateBackToHomeFragment() {
        val id = findNavController().currentDestination?.id

        if (id == R.id.scheduleAlarmFragment) {
            findNavController().popBackStack()
        }
    }

    private fun chooseAlarmTone() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
        resultLauncherOfSelectRing.launch(intent)
    }

    private var resultLauncherOfSelectRing =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedSongUri: Uri? = data.data
                    if (selectedSongUri != null) {
                        selectedAlarmTone = selectedSongUri
                        "selectedSongUri: $selectedSongUri".toLogD()
                    }
                }
            }
        }

    private fun onLoading(loading: Boolean = false) {
        when (loading) {
            true -> closeTouche()
            false -> openTouche()
        }
    }

    private fun closeTouche() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun openTouche() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showLoader(show: Boolean) {
        if (show) {
            binding.mkLoader.visibility = View.VISIBLE
        } else {
            binding.mkLoader.visibility = View.GONE
        }
    }
}