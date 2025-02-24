package app.vsptracker.activities.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStartActivity
import app.vsptracker.adapters.MachineStatusAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum

class MachineStatusActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  private var startReading = ""
  lateinit var machine_status_title: TextView
  lateinit var machine_stopped_reason: TextView
  lateinit var machine_start_layout: LinearLayout
  lateinit var machine_status_logout: Button
  lateinit var machine_status_back: Button
  lateinit var machine_status_start: Button
  lateinit var sfinish_reading: EditText
  lateinit var sfinish_minus: ImageView
  lateinit var sfinish_plus: ImageView
  lateinit var machine_status_rv: androidx.recyclerview.widget.RecyclerView
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_machine_status, contentFrameLayout)
    
    machine_status_title = findViewById(R.id.machine_status_title)
    machine_start_layout = findViewById(R.id.machine_start_layout)
    machine_status_rv = findViewById(R.id.machine_status_rv)
    machine_stopped_reason = findViewById(R.id.machine_stopped_reason)
    machine_status_logout = findViewById(R.id.machine_status_logout)
    machine_status_back = findViewById(R.id.machine_status_back)
    sfinish_reading = findViewById(R.id.sfinish_reading)
    machine_status_start = findViewById(R.id.machine_status_start)
    sfinish_minus = findViewById(R.id.sfinish_minus)
    sfinish_plus = findViewById(R.id.sfinish_plus)
    
    myHelper.setTag(tag)
    
    if (myHelper.getIsMachineStopped()) {
      machine_status_title.text = getString(R.string.machine_stopped_reason)
      machine_start_layout.visibility = View.VISIBLE
      machine_status_rv.visibility = View.GONE
      machine_stopped_reason.text = myHelper.getMachineStoppedReason()
    } else {
      machine_status_title.text = getString(R.string.select_machine_stop_reason)
      machine_start_layout.visibility = View.GONE
      machine_status_rv.visibility = View.VISIBLE
    }
    
    myData = MyData()
    if (myHelper.getIsMachineStopped()) {
      machine_status_logout.visibility = View.VISIBLE
      drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
      machine_status_back.visibility = View.GONE
      startReading = db.getMachineHours().totalHours
//            sfinish_reading.setText()
    
    } else {
      machine_status_logout.visibility = View.GONE
      machine_status_back.visibility = View.VISIBLE
      
      val meter = myHelper.getMeter()
      if (meter.isMachineStartTimeCustom)
        myData.isStartHoursCustom = 1
      myData.startHours = myHelper.getMeterTimeForFinish()
      myData.startTime = meter.machineStartTime
      myData.loadingGPSLocation = meter.hourStartGPSLocation
      startReading = myHelper.getMeterTimeForFinish()
    }
    sfinish_reading.setText(startReading)
    
    val stoppedReasons = db.getStopReasons()
    myHelper.log("MachineStops:$stoppedReasons")
    stoppedReasons.removeAt(0)
    
    val mAdapter = MachineStatusAdapter(this, stoppedReasons, startReading)
    machine_status_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    machine_status_rv.adapter = mAdapter
    
    
    machine_status_start.setOnClickListener(this)
    machine_status_logout.setOnClickListener(this)
    machine_status_back.setOnClickListener(this)
    sfinish_minus.setOnClickListener(this)
    sfinish_plus.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(2))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.sfinish_minus -> {
        
        val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
        if (value > 0) {
          val newValue = value - 0.1
          sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
        } else {
          myHelper.toast("Please Enter Valid Meter Value.")
          sfinish_reading.setText(myHelper.getRoundedDecimal(value.toDouble()).toString())
        }
      }
      
      R.id.sfinish_plus -> {
        val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
        val newValue = value + 0.1
        sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
      }
      
      R.id.machine_status_back -> {
        finish()
      }
      R.id.machine_status_logout -> {
        logout(MyEnum.LOGOUT_TYPE_OPERATOR, gpsLocation, sfinish_reading.text.toString())
      }
      
      R.id.machine_status_start -> {
        val machineData = db.getMachineStopByID(myHelper.getMeter().machineDbID)
        val currentTime = System.currentTimeMillis()
        machineData.stopTime = currentTime
        machineData.totalTime = currentTime - machineData.startTime
        machineData.time = currentTime.toString()
        machineData.date = myHelper.getDate(currentTime)
        
        machineData.recordID = myHelper.getMeter().machineDbID
        machineData.unloadingGPSLocation = gpsLocation
        
        machineData.loadingGPSLocationString = myHelper.getGPSLocationToString(machineData.loadingGPSLocation)
        machineData.unloadingGPSLocationString = myHelper.getGPSLocationToString(machineData.unloadingGPSLocation)
        
        machineData.org_id = myHelper.getLoginAPI().org_id
        machineData.siteId = myHelper.getMachineSettings().siteId
        machineData.operatorId = myHelper.getOperatorAPI().id
        machineData.machineTypeId = myHelper.getMachineTypeID()
        machineData.machineId = myHelper.getMachineID()
        machineData.machine_stop_reason_id = myHelper.getMachineStoppedReasonID()
        
        if (myHelper.isDailyModeStarted()) {
          machineData.isDayWorks = 1
        } else {
          machineData.isDayWorks = 0
        }
        
        myDataPushSave.updateMachineStop(machineData)
        
        val meter = myHelper.getMeter()
        meter.hourStartGPSLocation = gpsLocation
        meter.hourStartTime = currentTime
        meter.machineStartTime = currentTime
        meter.startHours = myHelper.getMeterValidValue(sfinish_reading.text.toString())
        
        if (!startReading.equals(meter.startHours, true)) {
          meter.isMachineStartTimeCustom = true
          myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${meter.startHours}")
        } else {
          meter.isMachineStartTimeCustom = false
          myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${meter.startHours}")
        }
        
        myHelper.setMeter(meter)
        val value = meter.startHours.toDouble()
        val minutes = value * 60
        val newMinutes = myHelper.getRoundedInt(minutes)
        myHelper.log("Minutes: $newMinutes")
        myHelper.setMachineTotalTime(newMinutes)
        updateMachineStatus()
        // update Machine status to server
        myDataPushSave.checkUpdateServerSyncData(MyEnum.SERVER_SYNC_UPDATE_MACHINE_STATUS)
      }
    }
  }
  
  private fun updateMachineStatus() {
    myHelper.toast("Machine Started Successfully")
    myHelper.setIsMachineStopped(false, "", 0)
    myHelper.startMachine()
    if (myHelper.getIsMachineStopped()) {
      val intent = Intent(this, HourMeterStartActivity::class.java)
      startActivity(intent)
    } else {
      val lastJourney = myHelper.getLastJourney()
      myHelper.log("lastJourney:$lastJourney")
//            myHelper.startHomeActivityByType(MyData())
      
      val dueCheckForms = db.getAdminCheckFormsDue()
      myHelper.checkDueCheckForms(dueCheckForms)
      
    }
  }
}
