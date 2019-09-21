package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_hour_meter_start.*

class HourMeterStartActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var myData: MyData
        private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_hour_meter_start)

//        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
//        layoutInflater.inflate(R.layout.activity_hour_meter_start, contentFrameLayout)
//        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
//        navigationView.menu.getItem(0).isChecked = true
//
//        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        myHelper = MyHelper(TAG, this)
//        myHelper.setTag(TAG)
        myData = MyData()
        myHelper.hideKeybaordOnClick(hour_meter_main_layout)

        ms_minus.setOnClickListener(this)
        ms_plus.setOnClickListener(this)
        ms_continue.setOnClickListener(this)
//        ms_reading.setText(myHelper.getRoundedDecimal(myHelper.getMachineTotalTime()/60.0).toString())
        ms_reading.setText(myHelper.getMeterTimeForStart())

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ms_minus -> {
                val value = ms_reading.text.toString().toFloat()
                if (value > 0) {
                    val newValue = value - 0.1
                    ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
                }
            }

            R.id.ms_plus -> {
                val value = ms_reading.text.toString().toFloat()
                val newValue = value + 0.1
                ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
            }

            R.id.ms_continue -> {

                if (!myHelper.getMeterTimeForStart().equals(ms_reading.text.toString(), true)) {
                    val meter = myHelper.getMeter()
                    meter.isMachineTimeCustom = true
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                } else {
                    val meter = myHelper.getMeter()
                    meter.isMachineTimeCustom = false
                    myHelper.setMeter(meter)
                    myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                }

                val value = ms_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = myHelper.getRoundedInt(minutes)
                myHelper.log("Minutes: $newMinutes")
                myHelper.setMachineTotalTime(newMinutes)
                myHelper.startHomeActivityByType(myData)

                myHelper.startMachine()
                finishAffinity()
            }
        }
    }
}
