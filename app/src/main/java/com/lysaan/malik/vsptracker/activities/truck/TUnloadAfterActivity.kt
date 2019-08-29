package com.lysaan.malik.vsptracker.activities.truck

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.LMachine1Activity
import com.lysaan.malik.vsptracker.activities.common.RLoadActivity
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_tunload_after.*

class TUnloadAfterActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_tunload_after, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }

        data = helper.getLastJourney()

        helper.log("data:$data")

        when (data.nextAction) {
            3 -> {
                tul_back_load.visibility = View.GONE
            }
            else -> {
                tul_back_load.visibility = View.VISIBLE
            }
        }
        tul_after_new.setOnClickListener(this)
        tul_after_repeat.setOnClickListener(this)
        tul_after_finish.setOnClickListener(this)
        tul_back_load.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.tul_back_load -> {
                val intent = Intent(this, LMachine1Activity::class.java)
                helper.setNextAction(2)
                data = helper.getLastJourney()
//                data.isForBackLoad = true
                data.nextAction = 2
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.tul_after_new -> {
                val intent = Intent(this, LMachine1Activity::class.java)
                val data = Data()
                helper.setLastJourney(data)
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.tul_after_repeat -> {
                when (data.nextAction) {
                    0 -> {
                        data.repeatJourney = 1
                    }
                    3 -> {
                        data.repeatJourney = 2
                    }
                }
//                data.isRepeatJourney = true
//                data.repeatJourney = 1
                data.nextAction = 0
                helper.setLastJourney(data)
//                intent.putExtra("data", data)
                val intent = Intent(this, RLoadActivity::class.java)
                startActivity(intent)
            }
            R.id.tul_after_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
