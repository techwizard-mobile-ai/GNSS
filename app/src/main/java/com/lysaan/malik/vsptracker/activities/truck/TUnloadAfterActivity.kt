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
import com.lysaan.malik.vsptracker.others.Data
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

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }



        tul_after_new.setOnClickListener(this)
        tul_after_repeat.setOnClickListener(this)
        tul_after_finish.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.tul_after_new -> {
                val intent = Intent (this, LMachine1Activity::class.java)
//                intent.putExtra("repeat", false)
                startActivity(intent)
            }
            R.id.tul_after_repeat -> {
                val intent = Intent (this, RLoadActivity::class.java)
//                intent.putExtra("repeat", true)
                startActivity(intent)
            }
            R.id.tul_after_finish -> {
                val intent = Intent (this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
