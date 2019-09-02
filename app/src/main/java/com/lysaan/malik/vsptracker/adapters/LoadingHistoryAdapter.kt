package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.list_row_loading_history.view.*


class LoadingHistoryAdapter(
        val context: Activity,
        val dataList: MutableList<Data>
) : RecyclerView.Adapter<LoadingHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    private lateinit var helper: Helper


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LoadingHistoryAdapter.ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_loading_history, parent, false)
        helper = Helper(TAG, context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LoadingHistoryAdapter.ViewHolder, position: Int) {

        val myData = dataList.get(position)

        holder.itemView.lhr_trip0_layout.visibility = View.GONE
        holder.itemView.lhr_trip_type_layout.visibility = View.GONE


        when (myData.tripType) {
            0 -> {
                holder.itemView.lhr_trip_number.setText(":  ${myData.recordID}")
//                holder.itemView.lhr_trip_type.text = ": Load Trip"
//                holder.itemView.lhr_trip0_layout.visibility = View.GONE
            }
            1 -> {
                holder.itemView.lhr_trip_number.setText(":  ${myData.trip0ID}b")
//                holder.itemView.lhr_trip_type.text = ": Back Load Trip"
//                holder.itemView.lhr_trip0_layout.visibility = View.VISIBLE
//                holder.itemView.lhr_trip0_id.text = ": Load Trip Number # ${myData.trip0ID}"
            }
        }

        when (myData.loadedMachineType) {
            2 -> {
                holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
                holder.itemView.lhr_task_layout.visibility = View.GONE
            }
            3 -> {
                holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
                holder.itemView.lhr_task_layout.visibility = View.VISIBLE
            }
        }
        holder.itemView.lhr_loading_machine.setText(":  " + myData.loadingMachine)

        holder.itemView.lhr_loaded_material.setText(":  ${myData.loadingMaterial} / ${myData.unloadingMaterial}")
        holder.itemView.lhr_loading_location.setText(":  ${myData.loadingLocation} / ${myData.unloadingLocation}")
        holder.itemView.lhr_loading_weight.setText(":  " + myData.unloadingWeight)
        holder.itemView.lhr_task.setText(":  " + myData.unloadingTask)

        holder.itemView.lhr_time.setText(
                ": ${helper.getTime(myData.startTime)} / ${helper.getTime(
                        myData.stopTime
                )} Hrs"
        )

        holder.itemView.lhr_duration.setText(": ${helper.getFormatedTime(myData.totalTime)} Hrs")
        holder.itemView.lhr_workmode.text = ": ${myData.workMode}"

        holder.itemView.lhr_gps_loading.text =
                ": ${helper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${helper.getRoundedDecimal(
                        myData.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${helper.getRoundedDecimal(myData.unloadingGPSLocation.latitude)} / ${helper.getRoundedDecimal(
                        myData.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            helper.showOnMap(myData.loadingGPSLocation, myData.loadingLocation)
        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            helper.showOnMap(myData.unloadingGPSLocation, myData.unloadingLocation)
        }


    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

