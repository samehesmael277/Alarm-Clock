package com.sameh.alarmclock.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sameh.alarmclock.R
import com.sameh.alarmclock.databinding.FragmentHomeBinding
import com.sameh.alarmclock.utilsAndExtensions.getDateTime
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeVM: HomeVM by viewModels()

    private var updateTimeJob: Job? = null

    @Inject
    lateinit var alarmsAdapter: AlarmsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        alarmsObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAlarmsAdapter()
        viewModelObserver()
        setActions()
    }

    override fun onResume() {
        super.onResume()
        startGetDateTime()
    }

    override fun onPause() {
        super.onPause()

        updateTimeJob?.cancel()
    }

    private fun startGetDateTime() {
        updateTimeJob = lifecycleScope.launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    updateDateAndTime(getDateTime())
                    delay(500)
                }
            }
        }
    }

    private fun setupAlarmsAdapter() {
        binding.apply {
            rvAlarms.adapter = alarmsAdapter
            rvAlarms.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvAlarms.itemAnimator = SlideInUpAnimator().apply {
                addDuration = 200
            }
        }
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            homeVM.state.collect {
                when (it) {
                    is HomeState.Idle -> {}
                    is HomeState.Loading -> {
                        onLoading(it.loading)
                        showLoader(it.loading)
                    }
                }
            }
        }
    }

    private fun alarmsObserver() {
        lifecycleScope.launch {
            homeVM.alarms.collect { alarms ->
                showEmptyViews(alarms.isEmpty())
                alarmsAdapter.differ.submitList(alarms)
            }
        }
    }

    private fun updateDateAndTime(dateTime: Pair<String, String>) {
        binding.currentDate.text = dateTime.first
        binding.tvCurrentTime.text = dateTime.second
    }

    private fun setActions() {
        binding.fabScheduleAlarm.setOnClickListener {
            navigateToScheduleAlarmFragment()
        }
        alarmsAdapter.onAlarmToggleButtonClickListener {
            homeVM.updateScheduleCancelAlarm(it)
        }
        alarmsAdapter.onDeleteAlarmClickListener {
            homeVM.cancelDeleteAlarm(it)
        }
    }

    private fun navigateToScheduleAlarmFragment() {
        val id = findNavController().currentDestination?.id

        if (id == R.id.homeFragment) {
            val action = HomeFragmentDirections.actionHomeFragmentToScheduleAlarmFragment()
            findNavController().navigate(action)
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

    private fun showEmptyViews(show: Boolean) {
        if (show) {
            binding.ivEmpty.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.ivEmpty.visibility = View.GONE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        updateTimeJob = null
    }
}