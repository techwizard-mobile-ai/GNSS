package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(7).isChecked = true

        helper.setTag(TAG)

        sfinish_minus.setOnClickListener(this)
        sfinish_plus.setOnClickListener(this)
        sfinish_logout.setOnClickListener(this)

        helper.log("getMachineTotalTime:${helper.getMachineTotalTime()}")
        helper.log("getMachineStartTime:${helper.getMachineStartTime()}")

//        val meterONTime = helper.getMachineTotalTime() + helper.getMachineStartTime()
//        sfinish_reading.setText(helper.getRoundedDecimal(meterONTime/60.0).toString())

        sfinish_reading.setText(helper.getMeterTimeForFinish())

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.sfinish_minus ->{
                val value = sfinish_reading.text.toString().toFloat()
                if(value >0){
                    val newValue = value - 0.1
                    sfinish_reading.setText(helper.getRoundedDecimal(newValue).toString())
                }
            }

            R.id.sfinish_plus -> {
                val value = sfinish_reading.text.toString().toFloat()
                val newValue = value + 0.1
                sfinish_reading.setText(helper.getRoundedDecimal(newValue).toString())
            }

            R.id.sfinish_logout -> {
                if(!helper.getMeterTimeForFinish().equals(sfinish_reading.text.toString(), true)){
                    val meter = helper.getMeter()
                    meter.isMachineTimeCustom = true
                    helper.setMeter(meter)
                    helper.log("Custom Time : True, Original reading:${helper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                }else{
                    val meter = helper.getMeter()
                    meter.isMachineTimeCustom = false
                    helper.setMeter(meter)
                    helper.log("Custom Time : False, Original reading:${helper.getMeterTimeForFinish()}, New Reading: ${sfinish_reading.text}")
                }
                val value = sfinish_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = helper.getRoundedInt(minutes)
                helper.log("Minutes: $newMinutes")
                helper.setMachineTotalTime(newMinutes)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}
