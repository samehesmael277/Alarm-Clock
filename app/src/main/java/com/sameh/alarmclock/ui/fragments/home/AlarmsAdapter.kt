package com.sameh.alarmclock.ui.fragments.home

import android.annotation.SuppressLint
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sameh.alarmclock.R
import com.sameh.alarmclock.data.model.Alarm
import com.sameh.alarmclock.databinding.ItemAlarmBinding
import javax.inject.Inject

class AlarmsAdapter @Inject constructor(
    private val app: Application
) : RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {

    private var alarmToggleButtonClickListener: ((Alarm) -> Unit)? = null

    fun onAlarmToggleButtonClickListener(clickListener: (Alarm) -> Unit) {
        alarmToggleButtonClickListener = clickListener
    }

    private var deleteAlarmClickListener: ((Alarm) -> Unit)? = null

    fun onDeleteAlarmClickListener(clickListener: (Alarm) -> Unit) {
        deleteAlarmClickListener = clickListener
    }

    inner class ViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(alarm: Alarm) {
            binding.apply {
                toggleButtonAlarm.isChecked = alarm.started
                tvAlarmOclock.text = "${alarm.hour}:${alarm.minute}"
                alarmTitle.text = alarm.title.ifEmpty { "Alarm" }
                if (alarm.recurring) {
                    ivRecurringOption.setImageResource(R.drawable.ic_arrow_repeat)
                    tvDaysRecurring.visibility = View.VISIBLE
                    tvDaysRecurring.text = alarm.getRecurringDaysText()
                } else {
                    ivRecurringOption.setImageResource(R.drawable.ic_repeat_one)
                    tvDaysRecurring.visibility = View.GONE
                }

                toggleButtonAlarm.setOnClickListener {
                    alarmToggleButtonClickListener?.invoke(alarm)
                }
                btnDeleteAlarm.setOnClickListener {
                    deleteAlarmClickListener?.invoke(alarm)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.alarmId == newItem.alarmId
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(app),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.bind(currentItem)
    }
}