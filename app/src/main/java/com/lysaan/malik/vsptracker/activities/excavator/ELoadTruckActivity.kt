package com.lysaan.malik.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.adapters.ELoadingHistoryAdapter
import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.activity_eload_truck.*

class ELoadTruckActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
//    private lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eload_truck, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

//        toolbar_title.text = "Load Truck"

        helper = Helper(TAG, this)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        load_truck_load.setOnClickListener(this)
        load_truck_back.setOnClickListener(this)
        load_truck_finish.setOnClickListener(this)

        helper.log("Loads:${db.getLoadHistroy()}")

        val aa = ELoadingHistoryAdapter(this@ELoadTruckActivity ,db.getLoadHistroy())
        val layoutManager1 = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        elh_rv.layoutManager = layoutManager1
        elh_rv!!.setAdapter(aa)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.load_truck_load -> {
                data.loadingMachine = helper.getMachineNumber()
                data.loadedMachine = "Truck"

                val insertID = db.insertLoad(data)
                if(insertID > 0){
                    helper.toast("Loading Successful.\nLoaded Truck Number # $insertID")
                    val aa = ELoadingHistoryAdapter(this@ELoadTruckActivity ,db.getLoadHistroy())
                    val layoutManager1 = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                    elh_rv.layoutManager = layoutManager1
                    elh_rv!!.setAdapter(aa)
                }else{
                    helper.toast("Error while Saving Record.")
                }

            }
            R.id.load_truck_back -> {finish()}
            R.id.load_truck_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
