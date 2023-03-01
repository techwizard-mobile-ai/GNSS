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

class ELoadingHistoryAdapter(
  val context: Activity,
  private val myDataList: ArrayList<MyData>
) : RecyclerView.Adapter<ELoadingHistoryAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  private lateinit var myHelper: MyHelper
  lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_eload_history, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val myData = myDataList[position]
    myHelper.log("$myData")
    
    val v = holder.itemView
    val lhr_record_number = v.findViewById<TextView>(R.id.lhr_record_number)
    val lhr_site = v.findViewById<TextView>(R.id.lhr_site)
    val lhr_operator = v.findViewById<TextView>(R.id.lhr_operator)
    val lhr_is_sync = v.findViewById<TextView>(R.id.lhr_is_sync)
    val lhr_workmode = v.findViewById<TextView>(R.id.lhr_workmode)
    val lhr_loading_machine_layout = v.findViewById<LinearLayout>(R.id.lhr_loading_machine_layout)
    val lhr_loaded_material_layout = v.findViewById<LinearLayout>(R.id.lhr_loaded_material_layout)
    val lhr_loading_location_layout = v.findViewById<LinearLayout>(R.id.lhr_loading_location_layout)
    val lhr_loading_weight_layout = v.findViewById<LinearLayout>(R.id.lhr_loading_weight_layout)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    val lhr_time_layout = v.findViewById<LinearLayout>(R.id.lhr_time_layout)
    val lhr_loading_machine = v.findViewById<TextView>(R.id.lhr_loading_machine)
    val lhr_loaded_material = v.findViewById<TextView>(R.id.lhr_loaded_material)
    val lhr_loading_location = v.findViewById<TextView>(R.id.lhr_loading_location)
    val lhr_loading_weight = v.findViewById<TextView>(R.id.lhr_loading_weight)
    val lhr_time = v.findViewById<TextView>(R.id.lhr_time)
    val lhr_gps_loading = v.findViewById<TextView>(R.id.lhr_gps_loading)
    
    lhr_record_number.text = ":  ${myData.id}"
    lhr_site.text = ":  ${db.getSiteByID(myData.siteId).name}"
    val operatorName = db.getOperatorByID(myData.operatorId).name
    lhr_operator.text = ":  $operatorName"
    lhr_is_sync.text = if (myData.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    
    when {
      myData.isDayWorks > 0 -> {
        lhr_workmode.text = context.getString(R.string.day_works_text)
      }
      else -> lhr_workmode.text = context.getString(R.string.standard_mode_text)
    }
    
    if (myData.machineId > 0) {
      lhr_loading_machine_layout.visibility = View.VISIBLE
      lhr_loading_machine.text = ":  ${db.getMachineByID(myData.machineId).number}"
    } else {
      lhr_loading_machine_layout.visibility = View.GONE
    }
    
    
    if (myData.loading_material_id > 0) {
      lhr_loaded_material_layout.visibility = View.VISIBLE
      lhr_loaded_material.text = ":  ${db.getMaterialByID(myData.loading_material_id).name}"
    } else {
      lhr_loaded_material_layout.visibility = View.GONE
    }
    
    if (myData.loading_location_id > 0) {
      lhr_loading_location_layout.visibility = View.VISIBLE
      lhr_loading_location.text = ":  ${db.getLocationByID(myData.loading_location_id).name}"
    } else {
      lhr_loading_location_layout.visibility = View.GONE
    }
    
    if (myData.unloadingWeight != 0.0) {
      lhr_loading_weight_layout.visibility = View.VISIBLE
      lhr_loading_weight.text = ":  " + myData.unloadingWeight
    } else {
      lhr_loading_weight_layout.visibility = View.GONE
    }
    
    if (!myData.date.isBlank()) {
      lhr_time_layout.visibility = View.VISIBLE
      lhr_time.text = ": " + myData.date + " Hrs"
    } else {
      lhr_time_layout.visibility = View.GONE
    }
    
    lhr_gps_loading.text =
      ": ${myHelper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          myData.loadingGPSLocation.longitude
        )
      } "
    
    lhr_gps_loading_layout.setOnClickListener {
      myHelper.showOnMap(myData.loadingGPSLocation, "Excavator Loading (${db.getLocationByID(myData.loading_location_id).name})")
    }
  }
  
  override fun getItemCount(): Int {
    return myDataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

