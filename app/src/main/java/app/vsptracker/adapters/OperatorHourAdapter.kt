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

class OperatorHourAdapter(
  val context: Activity,
  private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<OperatorHourAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_row_operator_hour, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    val oh_site = v.findViewById<TextView>(R.id.oh_site)
    val eth_sync = v.findViewById<TextView>(R.id.eth_sync)
    val eth_record_number = v.findViewById<TextView>(R.id.eth_record_number)
    val eth_operator = v.findViewById<TextView>(R.id.eth_operator)
    val eth_start_time = v.findViewById<TextView>(R.id.eth_start_time)
    val eth_end_time = v.findViewById<TextView>(R.id.eth_end_time)
    val eth_duration = v.findViewById<TextView>(R.id.eth_duration)
    val eth_date = v.findViewById<TextView>(R.id.eth_date)
    val eth_mode = v.findViewById<TextView>(R.id.eth_mode)
    val mh_gps_loading = v.findViewById<TextView>(R.id.mh_gps_loading)
    val lhr_gps_unloading = v.findViewById<TextView>(R.id.lhr_gps_unloading)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    val mh_gps_unloading_layout = v.findViewById<LinearLayout>(R.id.mh_gps_unloading_layout)
    val datum = dataList[position]
    
    val siteName = db.getSiteByID(datum.siteId).name
    
    oh_site.text = ":  $siteName"
    
    eth_sync.text = if (datum.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    
    eth_record_number.text = ":  " + (datum.id)
    val operatorName = db.getOperatorByID(datum.operatorId).name
    
    eth_operator.text = ":  $operatorName"
    
    if (datum.startTime > 0)
      
      eth_start_time.text = ":  " + myHelper.getDateTime(datum.startTime) + " Hrs"
    if (datum.stopTime > 0)
      
      eth_end_time.text = ":  " + myHelper.getDateTime(datum.stopTime) + " Hrs"
    
    eth_duration.text = ":  " + myHelper.getFormattedTime(datum.totalTime) + " Hrs"
    
    eth_date.text = ":  " + myHelper.getDateTime(datum.stopTime) + " Hrs"
    
    when {
      datum.isDayWorks > 0 -> {
        
        eth_mode.text = context.getString(R.string.day_works_text)
      }
      else ->
        eth_mode.text = context.getString(R.string.standard_mode_text)
    }
    
    
    mh_gps_loading.text =
      ": ${myHelper.getRoundedDecimal(datum.loadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          datum.loadingGPSLocation.longitude
        )
      } "
    
    lhr_gps_unloading.text =
      ": ${myHelper.getRoundedDecimal(datum.unloadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          datum.unloadingGPSLocation.longitude
        )
      } "
    
    
    lhr_gps_loading_layout.setOnClickListener {
      myHelper.showOnMap(datum.loadingGPSLocation, "$operatorName Started Work at $siteName")
    }
    
    mh_gps_unloading_layout.setOnClickListener {
      myHelper.showOnMap(datum.unloadingGPSLocation, "$operatorName Stopped Work at $siteName")
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

