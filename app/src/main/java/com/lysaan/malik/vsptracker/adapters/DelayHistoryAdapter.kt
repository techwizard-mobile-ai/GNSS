package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.EWork
import kotlinx.android.synthetic.main.list_row_delay_history.view.*


class DelayHistoryAdapter(
        val context: Activity,
        val dataList: MutableList<EWork>
) : RecyclerView.Adapter<DelayHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    lateinit var helper: Helper

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): DelayHistoryAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_delay_history, parent, false)
        helper = Helper(TAG, context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DelayHistoryAdapter.ViewHolder, position: Int) {

        val eWork = dataList.get(position)

        when (eWork.machineType) {
            1 -> {
                holder.itemView.eth_machine_type.setText(":  Excavator")
            }
            2 -> {
                holder.itemView.eth_machine_type.setText(":  Scrapper")
            }
            3 -> {
                holder.itemView.eth_machine_type.setText(":  Truck")
            }
        }

        holder.itemView.eth_record_number.setText(":  " + (dataList.size - position))
        holder.itemView.eth_machine_number.setText(":  " + eWork.machineNumber)
        holder.itemView.eth_start_time.setText(":  " + helper.getTime(eWork.startTime) + " Hrs")
        holder.itemView.eth_end_time.setText(":  " + helper.getTime(eWork.stopTime) + " Hrs")
        holder.itemView.eth_duration.setText(":  " + helper.getFormatedTime(eWork.totalTime) + " Hrs")
        holder.itemView.eth_date.setText(":  " + helper.getDateTime(eWork.stopTime) + " Hrs")
        holder.itemView.eth_mode.setText(":  ${eWork.workMode}")

        holder.itemView.lhr_gps_loading.text =
                ": ${helper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${helper.getRoundedDecimal(
                        eWork.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${helper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${helper.getRoundedDecimal(
                        eWork.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            helper.showOnMap(eWork.loadingGPSLocation, "Delay Start Location")
        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            helper.showOnMap(eWork.unloadingGPSLocation, "Delay Stop Location")
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

