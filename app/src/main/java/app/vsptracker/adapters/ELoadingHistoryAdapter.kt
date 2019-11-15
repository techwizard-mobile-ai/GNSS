package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.list_row_eload_history.view.*

class ELoadingHistoryAdapter(
    val context: Activity,
    private val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<ELoadingHistoryAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_eload_history, parent, false)
        myHelper = MyHelper(tag, context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myData = myDataList[position]

        if (myData.recordID < 1) {
            holder.itemView.lhr_record_number_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_record_number.setText(":  "+myData.recordID)
            holder.itemView.lhr_record_number.text = ":  " + (myDataList.size - position)
        } else {
            holder.itemView.lhr_record_number_layout.visibility = View.GONE
        }

        if (!myData.loadingMachine.isBlank()) {
            holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_machine.text = ":  " + myData.loadingMachine
        } else {
            holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
        }

//        if(!myData.loadedMachine.isNullOrBlank()){
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_loaded_machine.setText(":  "+myData.loadedMachine)
//        }else{
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.GONE
//        }

        if (!myData.loadingMaterial.isBlank()) {
            holder.itemView.lhr_loaded_material_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loaded_material.text = ":  " + myData.loadingMaterial
        } else {
            holder.itemView.lhr_loaded_material_layout.visibility = View.GONE
        }

        if (!myData.loadingLocation.isBlank()) {
            holder.itemView.lhr_loading_location_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_location.text = ":  " + myData.loadingLocation
        } else {
            holder.itemView.lhr_loading_location_layout.visibility = View.GONE
        }

        if (myData.unloadingWeight != 0.0) {
            holder.itemView.lhr_loading_weight_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_weight.text = ":  " + myData.unloadingWeight
        } else {
            holder.itemView.lhr_loading_weight_layout.visibility = View.GONE
        }

        if (!myData.date.isBlank()) {
            holder.itemView.lhr_time_layout.visibility = View.VISIBLE
            holder.itemView.lhr_time.text = ": " + myData.date + " Hrs"
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


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

