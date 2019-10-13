package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.operators.OperatorAPI
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(7).isChecked = true

        myHelper.setTag(TAG)

        if(myHelper.getIsMachineStopped())
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        myData = MyData()

        if(myHelper.getMeter().isMachineTimeCustom)
        myData.isStartHoursCustom = 1
        myData.startHours = myHelper.getMeterTimeForFinish()


        val meter = myHelper.getMeter()
        myData.startTime = meter.hourStartTime

        myData.loadingGPSLocation = meter.hourStartGPSLocation

        sfinish_reading.setText(myHelper.getMeterTimeForFinish())

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
                    meter.isMachineTimeCustom = true
                    myData.isTotalHoursCustom = 1
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                } else {
                    val meter = myHelper.getMeter()
                    meter.isMachineTimeCustom = false
                    myData.isTotalHoursCustom = 0
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                }
                val value = sfinish_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = myHelper.getRoundedInt(minutes)
                myHelper.log("Minutes: $newMinutes")
                myHelper.setMachineTotalTime(newMinutes)


                myData.totalHours = sfinish_reading.text.toString()

                saveMachineHour(myData)

                myHelper.stopDelay(gpsLocation)
                myHelper.stopDailyMode()
                myHelper.setOperatorAPI(OperatorAPI())

                val intent = Intent(this, OperatorLoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}
