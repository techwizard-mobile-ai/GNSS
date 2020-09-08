package app.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.database.DatabaseAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_start.*

private const val REQUEST_ACCESS_FINE_LOCATION = 1

class HourMeterStartActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName
    private var startReading = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.onActivityCreateSetTheme(this)
//        setContentView(R.layout.activity_hour_meter_start)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_hour_meter_start, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(7).isChecked = true

//        if(myHelper.getIsMachineStopped()){
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }
//        myHelper = MyHelper(tag, this)
        myHelper.TAG = tag


//        myHelper.setTag(tag)
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
        // Disable map if Hour meter is not set
        myHelper.setIsNavEnabled(false)
//        ms_reading.setText(myHelper.getRoundedDecimal(myHelper.getMachineTotalTime()/60.0).toString())
//        ms_reading.setText(myHelper.getMeterTimeForStart())
    
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ms_minus -> {
    
                val value = myHelper.getMeterValidValue(ms_reading.text.toString()).toFloat()
                if (value > 0) {
                    val newValue = value - 0.1
                    ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
                }else{
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
