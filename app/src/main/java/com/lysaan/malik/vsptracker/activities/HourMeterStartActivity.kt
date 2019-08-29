package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_start.*

class HourMeterStartActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_hour_meter_start, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        helper.setTag(TAG)
        data = Data()
        helper.hideKeybaordOnClick(hour_meter_main_layout)

        ms_minus.setOnClickListener(this)
        ms_plus.setOnClickListener(this)
        ms_continue.setOnClickListener(this)
//        ms_reading.setText(helper.getRoundedDecimal(helper.getMachineTotalTime()/60.0).toString())
        ms_reading.setText(helper.getMeterTimeForStart())

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.ms_minus ->{
                val value = ms_reading.text.toString().toFloat()
                if(value >0){
                    val newValue = value - 0.1
                    ms_reading.setText(helper.getRoundedDecimal(newValue).toString())
                }
            }

            R.id.ms_plus -> {
                val value = ms_reading.text.toString().toFloat()
                val newValue = value + 0.1
                ms_reading.setText(helper.getRoundedDecimal(newValue).toString())
            }

            R.id.ms_continue -> {

                if(!helper.getMeterTimeForStart().equals(ms_reading.text.toString(), true)){
                    val meter = helper.getMeter()
                    meter.isMachineTimeCustom = true
                    helper.setMeter(meter)
                    helper.log("Custom Time : True, Original reading:${helper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                }else{
                    val meter = helper.getMeter()
                    meter.isMachineTimeCustom = false
                    helper.setMeter(meter)
                    helper.log("Custom Time : False, Original reading:${helper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                }

                val value = ms_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = helper.getRoundedInt(minutes)
                helper.log("Minutes: $newMinutes")
                helper.setMachineTotalTime(newMinutes)
                helper.startHomeActivityByType(data)
                helper.startMachine()
                finishAffinity()
            }
        }
    }
}
