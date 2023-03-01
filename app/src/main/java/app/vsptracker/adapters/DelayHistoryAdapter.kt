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
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class DelayHistoryAdapter(
  val context: Activity,
  private val dataList: ArrayList<EWork>
) : RecyclerView.Adapter<DelayHistoryAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_delay_history, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val v = holder.itemView
    val eth_machine_type = v.findViewById<TextView>(R.id.eth_machine_type)
    val eth_sync = v.findViewById<TextView>(R.id.eth_sync)
    val eth_record_number = v.findViewById<TextView>(R.id.eth_record_number)
    val eth_site = v.findViewById<TextView>(R.id.eth_site)
    val eth_operator = v.findViewById<TextView>(R.id.eth_operator)
    val eth_machine_number = v.findViewById<TextView>(R.id.eth_machine_number)
    val eth_start_time = v.findViewById<TextView>(R.id.eth_start_time)
    val eth_end_time = v.findViewById<TextView>(R.id.eth_end_time)
    val eth_duration = v.findViewById<TextView>(R.id.eth_duration)
    val eth_date = v.findViewById<TextView>(R.id.eth_date)
    val eth_mode = v.findViewById<TextView>(R.id.eth_mode)
    val lhr_gps_loading = v.findViewById<TextView>(R.id.lhr_gps_loading)
    val lhr_gps_unloading = v.findViewById<TextView>(R.id.lhr_gps_unloading)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    val lhr_gps_unloading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_unloading_layout)
    
    val eWork = dataList[position]
    myHelper.log("waiting:$eWork")
    
    eth_machine_type.text = ":  ${db.getMachineTypeByID(eWork.machineTypeId).name}"
    
    eth_sync.text = if (eWork.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    
    eth_record_number.text = ":  " + (eWork.id)
    eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"
    
    val operatorName = (db.getOperatorByID(eWork.operatorId)).name
    eth_operator.text = ":  $operatorName"
    
    val machineNumber = db.getMachineByID(eWork.machineId).number
    eth_machine_number.text = ":  $machineNumber"
    eth_start_time.text = ":  " + myHelper.getTime(eWork.startTime) + " Hrs"
    eth_end_time.text = ":  " + myHelper.getTime(eWork.stopTime) + " Hrs"
    eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
    val date = myHelper.getDateTime(eWork.stopTime)
    eth_date.text = ":  $date Hrs"
    
    when {
      eWork.isDayWorks > 0 -> {
        eth_mode.text = context.getString(R.string.day_works_text)
      }
      else -> eth_mode.text = context.getString(R.string.standard_mode_text)
    }
    lhr_gps_loading.text =
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
      myHelper.showOnMap(eWork.loadingGPSLocation, "$operatorName using Machine $machineNumber Started Waiting")
    }
    lhr_gps_unloading_layout.setOnClickListener {
      myHelper.showOnMap(eWork.unloadingGPSLocation, "$operatorName using Machine $machineNumber Stopped Waiting")
    }
    
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

