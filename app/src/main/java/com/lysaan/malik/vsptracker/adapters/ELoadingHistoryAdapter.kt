package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.list_row_eload_history.view.*

class ELoadingHistoryAdapter(
        val context: Activity,
        val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<ELoadingHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ELoadingHistoryAdapter.ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_eload_history, parent, false)
        myHelper = MyHelper(TAG, context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ELoadingHistoryAdapter.ViewHolder, position: Int) {

        val myData = myDataList.get(position)

        if (myData.recordID < 1) {
            holder.itemView.lhr_record_number_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_record_number.setText(":  "+myData.recordID)
            holder.itemView.lhr_record_number.setText(":  " + (myDataList.size - position))
        } else {
            holder.itemView.lhr_record_number_layout.visibility = View.GONE
        }

        if (!myData.loadingMachine.isNullOrBlank()) {
            holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_machine.setText(":  " + myData.loadingMachine)
        } else {
            holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
        }

//        if(!myData.loadedMachine.isNullOrBlank()){
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_loaded_machine.setText(":  "+myData.loadedMachine)
//        }else{
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.GONE
//        }

        if (!myData.loadingMaterial.isNullOrBlank()) {
            holder.itemView.lhr_loaded_material_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loaded_material.setText(":  " + myData.loadingMaterial)
        } else {
            holder.itemView.lhr_loaded_material_layout.visibility = View.GONE
        }

        if (!myData.loadingLocation.isNullOrBlank()) {
            holder.itemView.lhr_loading_location_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_location.setText(":  " + myData.loadingLocation)
        } else {
            holder.itemView.lhr_loading_location_layout.visibility = View.GONE
        }

        if (myData.unloadingWeight != 0.0) {
            holder.itemView.lhr_loading_weight_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_weight.setText(":  " + myData.unloadingWeight)
        } else {
            holder.itemView.lhr_loading_weight_layout.visibility = View.GONE
        }

        if (!myData.date.isNullOrBlank()) {
            holder.itemView.lhr_time_layout.visibility = View.VISIBLE
            holder.itemView.lhr_time.setText(": " + myData.date + " Hrs")
        } else {
            holder.itemView.lhr_time_layout.visibility = View.GONE
        }

        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        myData.loadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_workmode.text = ": ${myData.workMode}"
        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(myData.loadingGPSLocation, "Excavator Loading (${myData.loadingLocation})")
        }
    }


    override fun getItemCount(): Int {
        return myDataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

