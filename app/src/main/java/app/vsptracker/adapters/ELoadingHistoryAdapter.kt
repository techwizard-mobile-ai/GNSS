package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_eload_history.view.*

class ELoadingHistoryAdapter(
    val context: Activity,
    private val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<ELoadingHistoryAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_eload_history, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myData = myDataList[position]

        myHelper.log("$myData")
//        if (myData.recordID < 1) {
//            holder.itemView.lhr_record_number_layout.visibility = View.VISIBLE
////            holder.itemView.lhr_record_number.setText(":  "+myData.recordID)
//            holder.itemView.lhr_record_number.text = ":  " + (myDataList.size - position)
//        } else {
//            holder.itemView.lhr_record_number_layout.visibility = View.GONE
//        }

        holder.itemView.lhr_record_number.text = ":  ${myData.id}"
        holder.itemView.lhr_site.text = ":  ${db.getSiteByID(myData.siteId).name}"
        val operatorName = db.getOperatorByID(myData.operatorId).name
        holder.itemView.lhr_operator.text = ":  $operatorName"
        when (myData.isSync) {
            1 -> holder.itemView.lhr_is_sync.text = ":  Yes"
            else -> holder.itemView.lhr_is_sync.text = ":  No"
        }

        when (myData.isDayWorks) {
            1 -> holder.itemView.lhr_workmode.text = ":  Day Works"
            else -> holder.itemView.lhr_workmode.text = ":  Standard Mode"
        }


        if (myData.machineId > 0) {
            holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_machine.text = ":  ${db.getMachineByID(myData.machineId).number}"
        } else {
            holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
        }

//        if(!myData.loadedMachine.isNullOrBlank()){
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_loaded_machine.setText(":  "+myData.loadedMachine)
//        }else{
//            holder.itemView.lhr_loaded_machine_layout.visibility = View.GONE
//        }

        if (myData.loading_material_id > 0) {
            holder.itemView.lhr_loaded_material_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loaded_material.text = ":  ${db.getMaterialByID(myData.loading_material_id).name}"
        } else {
            holder.itemView.lhr_loaded_material_layout.visibility = View.GONE
        }

        if (myData.loading_location_id > 0) {
            holder.itemView.lhr_loading_location_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_location.text = ":  ${db.getLocationByID(myData.loading_location_id).name}"
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

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(myData.loadingGPSLocation, "Excavator Loading (${myData.loadingLocation})")
        }
    }


    override fun getItemCount(): Int {
        return myDataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

