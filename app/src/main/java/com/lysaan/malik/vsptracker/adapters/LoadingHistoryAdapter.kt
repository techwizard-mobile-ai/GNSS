package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.list_row_loading_history.view.*


class LoadingHistoryAdapter(
        val context: Activity,
        val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<LoadingHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LoadingHistoryAdapter.ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_loading_history, parent, false)
        myHelper = MyHelper(TAG, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LoadingHistoryAdapter.ViewHolder, position: Int) {

        val myData = myDataList.get(position)

        when (myData.tripType) {
            0 -> {
                holder.itemView.lhr_trip_number.setText(":  ${myData.id}")
                holder.itemView.lhr_trip_type.text = ": Simple Load"
            }
            1 -> {
                holder.itemView.lhr_trip_number.setText(":  ${myData.id}")
                holder.itemView.lhr_trip_type.text = ": Back Load"
            }
        }


        holder.itemView.lhr_trip0_id.text = ": ${myData.trip0ID}"
        holder.itemView.lhr_operator.text = ": ${db.getOperatorByID(myData.operatorId.toString()).name}"
        holder.itemView.lhr_sync.text = ": ${if(myData.isSync == 1) "Yes" else "No"}"

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
                ": ${myHelper.getTime(myData.startTime)} / ${myHelper.getTime(
                        myData.stopTime
                )} Hrs"
        )

        holder.itemView.lhr_duration.setText(": ${myHelper.getFormatedTime(myData.totalTime)} Hrs")
        holder.itemView.lhr_workmode.text = ": ${myData.workMode}"

        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        myData.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${myHelper.getRoundedDecimal(myData.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        myData.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            when (myData.tripType) {
                0 -> {
                    myHelper.showOnMap(myData.loadingGPSLocation, "Loading Location (${myData.loadingLocation})")
                }
                1 -> {
                    myHelper.showOnMap(myData.loadingGPSLocation, "Back Loading Location (${myData.loadingLocation})")
                }
            }

        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            when (myData.tripType) {
                0 -> {
                    myHelper.showOnMap(myData.unloadingGPSLocation, "Unloading Location (${myData.unloadingLocation})")
                }
                1 -> {
                    myHelper.showOnMap(myData.unloadingGPSLocation, "Back Unloading Location (${myData.unloadingLocation})")
                }
            }

        }


    }


    override fun getItemCount(): Int {
        return myDataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

