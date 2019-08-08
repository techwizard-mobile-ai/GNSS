package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        sfinish_minus.setOnClickListener(this)
        sfinish_plus.setOnClickListener(this)
        sfinish_logout.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.sfinish_minus ->{
                val value = sfinish_reading.text.toString().toFloat()
                myHelper.log("beforeValue:$value")
                if(value >0){
                    val newValue = value - 0.1
                val number3digits:Double = Math.round(newValue* 1000.0) / 1000.0
                val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
                val solution:Double = Math.round(number2digits * 10.0) / 10.0
                myHelper.log("afterValue:$newValue")
                    sfinish_reading.setText(solution.toString())
                }

            }
            R.id.sfinish_plus -> {
                val value = sfinish_reading.text.toString().toFloat()
                myHelper.log("value:$value")
                val newValue = value + 0.1
                val number3digits:Double = Math.round(newValue * 1000.0) / 1000.0
                val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
                val solution:Double = Math.round(number2digits * 10.0) / 10.0
                myHelper.log("afterValue:$newValue")
                sfinish_reading.setText(solution.toString())
            }
            R.id.sfinish_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}
