package app.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class MachineStatusAdapter(
  private val myContext: Activity,
  private val dataList: ArrayList<Material>,
  private val startReading: String
) : RecyclerView.Adapter<MachineStatusAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  private lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_row_machine_status, parent, false)
    myHelper = MyHelper(tag, myContext)
    db = DatabaseAdapter(myContext)
    return ViewHolder(v)
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    val machine_status_rv_title = v.findViewById<TextView>(R.id.machine_status_rv_title)
    
    val material = dataList[position]
    
    machine_status_rv_title.text = material.name
    machine_status_rv_title.setOnClickListener {
//            myHelper.toast("Machine Stopped due to : " + material.name)
      
      val data = MyData()
      data.machine_stop_reason_id = material.id
      data.totalHours = (myContext as MachineStatusActivity).sfinish_reading.text.toString()
      
      if (!startReading.equals((myContext as MachineStatusActivity).sfinish_reading.text.toString(), true)) {
        data.isTotalHoursCustom = 1
        myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${(myContext as MachineStatusActivity).sfinish_reading.text}")
      } else {
        data.isTotalHoursCustom = 0
        myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${(myContext as MachineStatusActivity).sfinish_reading.text}")
      }
      
      myHelper.log("totalHours:${data.totalHours}")
      myHelper.log("data:$data")
      data.unloadingGPSLocation = (myContext as MachineStatusActivity).gpsLocation
      // If waiting is started then stop waiting
      if (myHelper.isDelayStarted())
        myContext.stopDelay()
      // If daily mode is started the stop daily mode
      if (myHelper.isDailyModeStarted())
        myContext.myDataPushSave.insertOperatorHour(myContext.gpsLocation)
      myContext.myDataPushSave.pushInsertMachineHour(data, true)
      data.loadingGPSLocation = (myContext as MachineStatusActivity).gpsLocation
      myContext.myDataPushSave.insertMachineStop(data, material)
      myHelper.logout(myContext)
      myContext.finishAffinity()
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

