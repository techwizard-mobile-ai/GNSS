package com.lysaan.malik.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.activities.common.RLoadActivity
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.activity_sunload_after.*

class SUnloadAfterActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_sunload_after, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        sul_after_new.setOnClickListener(this)
        sul_after_repeat.setOnClickListener(this)
        sul_after_finish.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.sul_after_new -> {

//                val intent = Intent(this, Material1Activity::class.java)
//                val myData = MyData()
//                intent.putExtra("myData", myData)
//                startActivity(intent)

                val intent = Intent(this, Material1Activity::class.java)
                val data = MyData()
                myHelper.setLastJourney(data)
                intent.putExtra("myData", data)
                startActivity(intent)
            }
            R.id.sul_after_repeat -> {

//                val intent = Intent(this, RLoadActivity::class.java)
//                myData.isRepeatJourney = true
//                intent.putExtra("myData", myData)
//                startActivity(intent)

//                when(myData.nextAction){
//                    1 ->{
//                        myData.repeatJourney = 1
//                    }
//                }
                myData.repeatJourney = 1
                myData.nextAction = 0
                myHelper.setLastJourney(myData)
                val intent = Intent(this, RLoadActivity::class.java)
                startActivity(intent)

            }
            R.id.sul_after_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
