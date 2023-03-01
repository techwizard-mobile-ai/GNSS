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


class LoadingHistoryAdapter(
  val context: Activity,
  private val myDataList: ArrayList<MyData>
) : RecyclerView.Adapter<LoadingHistoryAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  private lateinit var myHelper: MyHelper
  
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_loading_history, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val v = holder.itemView
    val lhr_trip_number = v.findViewById<TextView>(R.id.lhr_trip_number)
    val lhr_trip_type = v.findViewById<TextView>(R.id.lhr_trip_type)
    val lhr_trip0_id = v.findViewById<TextView>(R.id.lhr_trip0_id)
    val lhr_operator = v.findViewById<TextView>(R.id.lhr_operator)
    val lhr_sync = v.findViewById<TextView>(R.id.lhr_sync)
    val lhr_gps_unloading = v.findViewById<TextView>(R.id.lhr_gps_unloading)
    val lhr_site = v.findViewById<TextView>(R.id.lhr_site)
    val lhr_machine_type = v.findViewById<TextView>(R.id.lhr_machine_type)
    val lhr_machine_number = v.findViewById<TextView>(R.id.lhr_machine_number)
    val lhr_loading_machine = v.findViewById<TextView>(R.id.lhr_loading_machine)
    val lhr_loaded_material = v.findViewById<TextView>(R.id.lhr_loaded_material)
    val lhr_loading_location = v.findViewById<TextView>(R.id.lhr_loading_location)
    val lhr_loading_weight = v.findViewById<TextView>(R.id.lhr_loading_weight)
    val lhr_task = v.findViewById<TextView>(R.id.lhr_task)
    val lhr_time = v.findViewById<TextView>(R.id.lhr_time)
    val lhr_duration = v.findViewById<TextView>(R.id.lhr_duration)
    val lhr_workmode = v.findViewById<TextView>(R.id.lhr_workmode)
    val lhr_gps_loading = v.findViewById<TextView>(R.id.lhr_gps_loading)
    
    val lhr_loading_machine_layout = v.findViewById<LinearLayout>(R.id.lhr_loading_machine_layout)
    val lhr_task_layout = v.findViewById<LinearLayout>(R.id.lhr_task_layout)
    val lhr_gps_unloading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_unloading_layout)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    
    
    val myData = myDataList[position]
    
    when (myData.tripType) {
      0 -> {
        lhr_trip_number.text = ":  ${myData.id}"
        lhr_trip_type.text = ": Simple Load"
      }
      1 -> {
        lhr_trip_number.text = ":  ${myData.id}"
        lhr_trip_type.text = ": Back Load"
      }
    }
    
    
    lhr_trip0_id.text = ": ${myData.trip0ID}"
    val operatorName = db.getOperatorByID(myData.operatorId).name
    lhr_operator.text = ": $operatorName"
    lhr_sync.text = if (myData.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    when (myData.machineTypeId) {
      2 -> {
        lhr_loading_machine_layout.visibility = View.GONE
        lhr_task_layout.visibility = View.GONE
      }
      3 -> {
        lhr_loading_machine_layout.visibility = View.VISIBLE
        lhr_task_layout.visibility = View.VISIBLE
      }
    }
    lhr_site.text = ":  " + db.getSiteByID(myData.siteId).name
    lhr_machine_type.text = ":  " + db.getMachineTypeByID(myData.machineTypeId).name
    val machineNumber = db.getMachineByID(myData.machineId).number
    lhr_machine_number.text = ":  $machineNumber"
    
    lhr_loading_machine.text = ":  ${db.getMachineByID(myData.loading_machine_id).number}"
    
    if (myData.loading_material_id != myData.unloading_material_id) {
      lhr_loaded_material.text =
        ":  ${db.getMaterialByID(myData.loading_material_id).name} / ${db.getMaterialByID(myData.unloading_material_id).name}"
    } else {
      lhr_loaded_material.text = ":  ${db.getMaterialByID(myData.loading_material_id).name}"
    }
    
    val loadingLocation = db.getLocationByID(myData.loading_location_id).name
    val unloadingLocation = db.getLocationByID(myData.unloading_location_id).name
    lhr_loading_location.text = ":  $loadingLocation / $unloadingLocation"
    lhr_loading_weight.text = ":  " + myData.unloadingWeight
    lhr_task.text = ":  ${db.getTaskByID(myData.unloading_task_id).name}"
    
    lhr_time.text = ": ${myHelper.getTime(myData.startTime)} / ${
      myHelper.getTime(
        myData.stopTime
      )
    } Hrs"
    
    lhr_duration.text = ": ${myHelper.getFormattedTime(myData.totalTime)} Hrs"
    
    when (myData.isDayWorks) {
      1 -> lhr_workmode.text = context.getString(R.string.day_works_text)
      else -> lhr_workmode.text = context.getString(R.string.standard_mode_text)
    }
    lhr_gps_loading.text =
      ": ${myHelper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          myData.loadingGPSLocation.longitude
        )
      } "
    lhr_gps_unloading.text =
      ": ${myHelper.getRoundedDecimal(myData.unloadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          myData.unloadingGPSLocation.longitude
        )
      } "
    
    lhr_gps_loading_layout.setOnClickListener {
      when (myData.tripType) {
        0 -> {
          myHelper.showOnMap(myData.loadingGPSLocation, "$machineNumber, $operatorName, Loading Location ($loadingLocation)")
        }
        1 -> {
          myHelper.showOnMap(myData.loadingGPSLocation, "$machineNumber, $operatorName, Back Loading Location ($unloadingLocation)")
        }
      }
      
    }
    lhr_gps_unloading_layout.setOnClickListener {
      when (myData.tripType) {
        0 -> {
          myHelper.showOnMap(myData.unloadingGPSLocation, "$machineNumber, $operatorName, Unloading Location ($loadingLocation)")
        }
        1 -> {
          myHelper.showOnMap(myData.unloadingGPSLocation, "$machineNumber, $operatorName, Back Unloading Location ($unloadingLocation)")
        }
      }
      
    }
    
    
  }
  
  
  override fun getItemCount(): Int {
    return myDataList.size
  }
  
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

