package app.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyEnum

private const val REQUEST_ACCESS_FINE_LOCATION = 1

class HourMeterStartActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  private var startReading = ""
  lateinit var hour_meter_main_layout: RelativeLayout
  lateinit var ms_reading: EditText
  lateinit var ms_minus: ImageView
  lateinit var ms_plus: ImageView
  lateinit var ms_continue: Button
  lateinit var ms_change_machine: Button
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_hour_meter_start, contentFrameLayout)
    
    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    
    hour_meter_main_layout = findViewById(R.id.hour_meter_main_layout)
    ms_reading = findViewById(R.id.ms_reading)
    ms_minus = findViewById(R.id.ms_minus)
    ms_plus = findViewById(R.id.ms_plus)
    ms_continue = findViewById(R.id.ms_continue)
    ms_change_machine = findViewById(R.id.ms_change_machine)
    
    myHelper.TAG = tag
    
    db = DatabaseAdapter(this)
    myData = MyData()
    myHelper.hideKeyboardOnClick(hour_meter_main_layout)
    
    var reading = db.getMachineHours().totalHours
    if (reading.isEmpty()) {
      reading = "0.0"
    }
    startReading = reading
    ms_reading.setText(myHelper.getRoundedDecimal(reading.toDouble()).toString())
    myHelper.log("Machine_HourMeter--reading--:${reading}")

//        gpsLocation = GPSLocation()
//        startGPS()
    if (myHelper.isOnline())
      fetchMachineMaxHours()
    ms_minus.setOnClickListener(this)
    ms_plus.setOnClickListener(this)
    ms_continue.setOnClickListener(this)
    ms_change_machine.setOnClickListener(this)
    // Disable map if Hour meter is not set
    myHelper.setIsNavEnabled(false)
//        ms_reading.setText(myHelper.getRoundedDecimal(myHelper.getMachineTotalTime()/60.0).toString())
//        ms_reading.setText(myHelper.getMeterTimeForStart())
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(7))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.ms_minus -> {
        
        val value = myHelper.getMeterValidValue(ms_reading.text.toString()).toFloat()
        if (value > 0) {
          val newValue = value - 0.1
          ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
        } else {
          myHelper.toast("Please Enter Valid Meter Value.")
          ms_reading.setText(myHelper.getRoundedDecimal(value.toDouble()).toString())
        }
      }
      
      R.id.ms_plus -> {
        val value = myHelper.getMeterValidValue(ms_reading.text.toString()).toFloat()
        val newValue = value + 0.1
        ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
      }
      
      R.id.ms_continue -> {
        val meter = myHelper.getMeter()
        meter.hourStartGPSLocation = gpsLocation
        val currentTime = System.currentTimeMillis()
        meter.hourStartTime = currentTime
        meter.machineStartTime = currentTime
        meter.startHours = myHelper.getMeterValidValue(ms_reading.text.toString())
        
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
        myHelper.setIsNavEnabled(true)
        myHelper.awsFileDownload(db.getCurrentOrgsMap())
        val dueCheckForms = db.getAdminCheckFormsDue()
        myHelper.startMachine()
        // Either Due checkforms OR Home activity will be started
        myHelper.checkDueCheckForms(dueCheckForms)
        myDataPushSave.checkUpdateServerSyncData(MyEnum.SERVER_SYNC_UPDATE_MACHINE_STATUS)
        finishAffinity()
      }
      R.id.ms_change_machine -> {
        val intent = Intent(this@HourMeterStartActivity, MachineTypeActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
    }
  }
  
  private fun fetchMachineMaxHours() {
    myHelper.showDialog()
    val call = this.retrofitAPI.getMachineMaxHour(
      myHelper.getMachineID(),
      myHelper.getLoginAPI().org_id,
      myHelper.getLoginAPI().auth_token
    )
    call.enqueue(object : retrofit2.Callback<MyDataListResponse> {
      override fun onResponse(
        call: retrofit2.Call<MyDataListResponse>,
        response: retrofit2.Response<MyDataListResponse>
      ) {
        myHelper.hideDialog()
        myHelper.log("Response:$response")
        val responseBody = response.body()
        myHelper.log("responseBody:$responseBody")
        if (responseBody!!.success && responseBody.data != null) {
          try {
            if (responseBody.data!![0].totalHours.isNotBlank()) {
              startReading = responseBody.data!![0].totalHours
              myHelper.log("startReading:$startReading")
              val appMeterReading = ms_reading.text.toString().toDouble()
              val portalMeterReading = startReading.toDouble()
              // If App meter reading is greater than Portal reading then don't set Meter Reading value
              // of Portal, this may happen in the case if App meter reading has greater value but
              // it was not uploaded to Server due to Internet connection problem. Now when Operator
              // try to login from same device his app value is greater than Portal value as previous
              // app meter reading was not uploaded to server, so in that case don't set meter reading from portal
              if (portalMeterReading > appMeterReading)
                ms_reading.setText(startReading)
            }
            
          }
          catch (e: Exception) {
            myHelper.log(e.message.toString())
          }
          
        } else {
          myHelper.hideProgressBar()
          myHelper.toast(responseBody.message)
        }
      }
      
      override fun onFailure(call: retrofit2.Call<MyDataListResponse>, t: Throwable) {
        myHelper.hideDialog()
        myHelper.hideProgressBar()
        myHelper.log("Failure" + t.message)
      }
    })
  }
  
}
