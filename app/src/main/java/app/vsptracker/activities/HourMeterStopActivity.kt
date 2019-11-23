package app.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(10).isChecked = true

        myHelper.setTag(tag)

        if(myHelper.getIsMachineStopped()){
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            hm_layout.visibility = View.GONE
        }else{
            hm_layout.visibility = View.VISIBLE
        }

        myData = MyData()

        val meter = myHelper.getMeter()
        if(meter.isMachineStartTimeCustom)
        myData.isStartHoursCustom = 1
        myData.startHours = myHelper.getMeterTimeForFinish()

        myData.startTime = meter.machineStartTime

        myData.loadingGPSLocation = meter.hourStartGPSLocation

        sfinish_reading.setText(myHelper.getMeterTimeForFinish())

        myHelper.log("onCreate:$myData")

        sfinish_minus.setOnClickListener(this)
        sfinish_plus.setOnClickListener(this)
        hm_stop_logout.setOnClickListener(this)
        hm_stop_summary.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.hm_stop_summary -> {

            }

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

            R.id.hm_stop_logout -> {
                if (!myHelper.getMeterTimeForFinish().equals(sfinish_reading.text.toString(), true)) {
                    val meter = myHelper.getMeter()
                    meter.isMachineStopTimeCustom = true
                    myData.isTotalHoursCustom = 1
                    myData.startHours = meter.startHours
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                } else {
                    val meter = myHelper.getMeter()
                    meter.isMachineStopTimeCustom = false
                    myData.isTotalHoursCustom = 0
                    myData.startHours = meter.startHours
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                }
                val value = sfinish_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = myHelper.getRoundedInt(minutes)
                myHelper.log("Minutes: $newMinutes")
                myHelper.setMachineTotalTime(newMinutes)
                myData.totalHours = sfinish_reading.text.toString()
                myHelper.log("Before saveMachineHour:$myData")

                val operatorAPI = myHelper.getOperatorAPI()
                operatorAPI.unloadingGPSLocation = gpsLocation
                operatorAPI.orgId = myHelper.getLoginAPI().org_id
                operatorAPI.siteId = myHelper.getMachineSettings().siteId
                operatorAPI.operatorId = operatorAPI.id
                when{
                    myHelper.isDailyModeStarted() -> operatorAPI.isDaysWork = 1
                    else -> operatorAPI.isDaysWork = 0
                }
//                operatorAPI.isDaysWork = myHelper.isDailyModeStarted()
                operatorAPI.stopTime = System.currentTimeMillis()
                operatorAPI.totalTime = operatorAPI.stopTime - operatorAPI.startTime
                operatorAPI.loadingGPSLocationString = myHelper.getGPSLocationToString(operatorAPI.loadingGPSLocation)
                operatorAPI.unloadingGPSLocationString = myHelper.getGPSLocationToString(operatorAPI.unloadingGPSLocation)
                myDataPushSave.pushInsertOperatorHour(operatorAPI)

                myData.machine_stop_reason_id = -1
                saveMachineHour(myData)

//                myHelper.stopDelay(gpsLocation)
//                myHelper.stopDailyMode()
//                myHelper.setOperatorAPI(OperatorAPI())
//
//                val data = MyData()
//                myHelper.setLastJourney(data)
//
//                val intent = Intent(this, OperatorLoginActivity::class.java)
//                startActivity(intent)
//                finishAffinity()
            }
        }
    }
}
