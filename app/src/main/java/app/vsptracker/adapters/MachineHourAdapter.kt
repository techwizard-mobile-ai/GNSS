package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class MachineHourAdapter(
  val context: Activity,
  private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<MachineHourAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_row_machine_hour, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val v = holder.itemView
    val eth_site = v.findViewById<TextView>(R.id.eth_site)
    val eth_machine_type = v.findViewById<TextView>(R.id.eth_machine_type)
    val eth_sync = v.findViewById<TextView>(R.id.eth_sync)
    val eth_record_number = v.findViewById<TextView>(R.id.eth_record_number)
    val eth_operator = v.findViewById<TextView>(R.id.eth_operator)
    val eth_machine_number = v.findViewById<TextView>(R.id.eth_machine_number)
    val eth_start_time = v.findViewById<TextView>(R.id.eth_start_time)
    val eth_end_time = v.findViewById<TextView>(R.id.eth_end_time)
    val eth_duration = v.findViewById<TextView>(R.id.eth_duration)
    val mh_start_hours = v.findViewById<TextView>(R.id.mh_start_hours)
    val mh_total_hours = v.findViewById<TextView>(R.id.mh_total_hours)
    val eth_mode = v.findViewById<TextView>(R.id.eth_mode)
    val mh_gps_loading = v.findViewById<TextView>(R.id.mh_gps_loading)
    val lhr_gps_unloading = v.findViewById<TextView>(R.id.lhr_gps_unloading)
    val eth_remarks = v.findViewById<TextView>(R.id.eth_remarks)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    val mh_gps_unloading_layout = v.findViewById<LinearLayout>(R.id.mh_gps_unloading_layout)
    
    val eWork = dataList[position]
    
    myHelper.log(eWork.toString())
    
    eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"
    eth_machine_type.text = ":  ${db.getMachineTypeByID(eWork.machineTypeId).name}"
    eth_sync.text = if (eWork.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    eth_record_number.text = ":  " + (dataList.size - position)
    val operatorName = db.getOperatorByID(eWork.operatorId).name
    eth_operator.text = ":  $operatorName"
    
    val machineNumber = db.getMachineByID(eWork.machineId).number
    eth_machine_number.text = ":  $machineNumber"
    
    if (eWork.startTime > 0)
      eth_start_time.text = ":  " + myHelper.getDateTime(eWork.startTime) + " Hrs"
    if (eWork.stopTime > 0)
      eth_end_time.text = ":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs"
    eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
    
    when (eWork.machine_stop_reason_id) {
      -3 -> {
        eth_remarks.text = ":  Auto Logout"
      }
      -2 -> {
        eth_remarks.text = ":  Machine Changed"
      }
      -1 -> {
        eth_remarks.text = ":  Logout"
      }
      else -> {
        if (eWork.machine_stop_reason_id > 0)
          eth_remarks.text = ":  ${db.getStopReasonByID(eWork.machine_stop_reason_id).name}"
        else eth_remarks.text = ":  Unknown"
      }
    }
    
    
    mh_start_hours.text = ":  ${eWork.startHours} Hrs"
    mh_total_hours.text = ":  ${eWork.totalHours} Hrs"
    
    when {
      eWork.isDayWorks > 0 -> {
        eth_mode.text = context.getString(R.string.day_works_text)
      }
      else -> eth_mode.text = context.getString(R.string.standard_mode_text)
    }
    
    
    mh_gps_loading.text =
      ": ${myHelper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          eWork.loadingGPSLocation.longitude
        )
      } "
    lhr_gps_unloading.text =
      ": ${myHelper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          eWork.unloadingGPSLocation.longitude
        )
      } "
    
    lhr_gps_loading_layout.setOnClickListener {
      myHelper.showOnMap(eWork.loadingGPSLocation, "Delay Location")
    }
    mh_gps_unloading_layout.setOnClickListener {
      myHelper.showOnMap(eWork.unloadingGPSLocation, "Delay Location")
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

