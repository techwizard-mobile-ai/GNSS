package com.lysaan.malik.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.Location1Activity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.adapters.ELoadingAdapter
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_eload.*

class ELoadActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    //    private lateinit var data: Data
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }
//        helper.setLastJourney(data)

        data = helper.getLastJourney()
        eload_material.text = data.loadingMaterial
        eload_location.text = data.loadingLocation


        load_truck_load.setOnClickListener(this)
        eload_back.setOnClickListener(this)
        eload_finish.setOnClickListener(this)
        eload_material.setOnClickListener(this)
        eload_location.setOnClickListener(this)


        val loadHistory = db.getELoadHistroy()
        if (loadHistory.size > 0) {
            elh_rv.visibility = View.VISIBLE
            val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
            val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            elh_rv.layoutManager = layoutManager1
            elh_rv!!.setAdapter(aa)
        } else {
            elh_rv.visibility = View.INVISIBLE
        }

    }


    override fun onResume() {
        super.onResume()
        startGPS()
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.load_truck_load -> {
                stopDelay()
                data.loadingMachine = helper.getMachineNumber().toString()
                data.loadedMachine = "Load"

                data.loadingGPSLocation = gpsLocation
                val insertID = db.insertELoad(data)
                if (insertID > 0) {
                    helper.toast("Loading Successful.\nLoaded Truck Number # $insertID")

                    val loadHistory = db.getELoadHistroy()
                    if (loadHistory.size > 0) {
                        elh_rv.visibility = View.VISIBLE
                        val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
                        val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        elh_rv.layoutManager = layoutManager1
                        elh_rv!!.setAdapter(aa)
                    } else {
                        elh_rv.visibility = View.INVISIBLE
                    }

                } else {
                    helper.toast("Error while Saving Record.")
                }

            }
            R.id.eload_back -> {
                helper.startHomeActivityByType(Data())
            }
            R.id.eload_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
            R.id.eload_material -> {
                val intent = Intent(this, Material1Activity::class.java)
//                val data1 = helper.getLastJourney()
                data.isForLoadResult = true
                intent.putExtra("data", data)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.eload_location -> {
                val intent = Intent(this, Location1Activity::class.java)
//                val data1 = helper.getLastJourney()
                data.isForLoadResult = true
                intent.putExtra("data", data)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            var bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                data = bundle!!.getSerializable("data") as Data
                helper.log("data:$data")


                eload_material.text = data.loadingMaterial
                eload_location.text = data.loadingLocation

                data.isForUnloadResult = false
                data.isForLoadResult = false
                helper.setLastJourney(data)

            }

        } else {
            helper.toast("Request can not be completed.")
        }
    }
}
