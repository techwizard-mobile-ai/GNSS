package com.lysaan.malik.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.LMachine1Activity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.activities.common.RLoadActivity
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_sunload_after.*

class SUnloadAfterActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_sunload_after, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        sul_after_new.setOnClickListener(this)
        sul_after_repeat.setOnClickListener(this)
        sul_after_finish.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.sul_after_new -> {

//                val intent = Intent(this, Material1Activity::class.java)
//                val myData = Data()
//                intent.putExtra("data", myData)
//                startActivity(intent)

                val intent = Intent (this, Material1Activity::class.java)
                val data = Data()
                helper.setLastJourney(data)
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.sul_after_repeat -> {

//                val intent = Intent(this, RLoadActivity::class.java)
//                data.isRepeatJourney = true
//                intent.putExtra("data", data)
//                startActivity(intent)

//                when(data.nextAction){
//                    1 ->{
//                        data.repeatJourney = 1
//                    }
//                }
                data.repeatJourney = 1
                data.nextAction = 0
                helper.setLastJourney(data)
                val intent = Intent (this, RLoadActivity::class.java)
                startActivity(intent)

            }
            R.id.sul_after_finish -> {
                val intent = Intent (this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
