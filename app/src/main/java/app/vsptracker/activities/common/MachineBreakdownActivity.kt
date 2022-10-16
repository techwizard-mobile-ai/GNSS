package app.vsptracker.activities.common

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_machine_breakdown.*

class MachineBreakdownActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  private var startReading = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_machine_breakdown, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(3).isChecked = true
    myHelper.setTag(tag)
    
    if (!myHelper.getIsMachineStopped()) {
      machine_breakdown_title.text = getString(R.string.stop_machine_due_to_breakdown)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
      machine_breakdown_action_text.text = getString(R.string.stop_machine)
      
    } else {
      machine_breakdown_title.text = getString(R.string.machine_stopped_due_to_breakdown)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
      machine_breakdown_action_text.text = getString(R.string.start_machine)
    }
    
    myData = MyData()
    
    val meter = myHelper.getMeter()
    if (meter.isMachineStartTimeCustom)
      myData.isStartHoursCustom = 1
    myData.startHours = myHelper.getMeterTimeForFinish()
    myData.startTime = meter.machineStartTime
    myData.loadingGPSLocation = meter.hourStartGPSLocation
    startReading = myHelper.getMeterTimeForFinish()
    sfinish_reading.setText(startReading)
    
    machine_breakdown_back.setOnClickListener(this)
    machine_breakdown_action.setOnClickListener(this)
    sfinish_minus.setOnClickListener(this)
    sfinish_plus.setOnClickListener(this)
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.sfinish_minus -> {
        val value = sfinish_reading.text.toString().toFloat()
        if (value > 0) {
          val newValue = value - 0.1
          sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
        }
      }
      
      R.id.sfinish_plus -> {
        val value = sfinish_reading.text.toString().toFloat()
        val newValue = value + 0.1
        sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
      }
      R.id.machine_breakdown_back -> {
        finish()
      }
      /**
       * Here Machine will be stopped due to Breakdown and Following action will be performed.
       * 1. Now Machine Total Hours till now should be updated in Machine hours Table in App and in Portal.
       * 2. Also Machine status should be updated in Machines Table alongside Machine Hours DB ID.
       * 3. Machine Stopped Time should be started and this time will be calculated when machine is Started again.
       */
      R.id.machine_breakdown_action -> {
        val data = MyData()
        val stoppedReasons = db.getStopReasons()
        // First Reason for Machine Stops is Breakdown for All Companies
        val material = stoppedReasons[0]
        data.machine_stop_reason_id = material.id
        data.siteId = myHelper.getMachineSettings().siteId
        data.unloadingGPSLocation = gpsLocation
//                data.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)
        
        if (!startReading.equals(sfinish_reading.text.toString(), true)) {
          data.isTotalHoursCustom = 1
          myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${sfinish_reading.text}")
        } else {
          data.isTotalHoursCustom = 0
          myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${sfinish_reading.text}")
        }
        
        data.totalHours = sfinish_reading.text.toString()
        // If waiting is started then stop waiting
        if (myHelper.isDelayStarted())
          stopDelay()
        // If daily mode is started the stop daily mode
        if (myHelper.isDailyModeStarted())
          myDataPushSave.insertOperatorHour(gpsLocation)
        myDataPushSave.pushInsertMachineHour(data)
        // Location which is Machine Hour Stop GPS is same location which is Machine Stop Start GPS
        data.loadingGPSLocation = gpsLocation
        if (myDataPushSave.insertMachineStop(data, material, true) > 0) {
          myHelper.logout(this)
          finishAffinity()
        } else {
          myHelper.toast("Machine Not Stopped. Please try again.")
        }
        
      }
    }
  }
}
