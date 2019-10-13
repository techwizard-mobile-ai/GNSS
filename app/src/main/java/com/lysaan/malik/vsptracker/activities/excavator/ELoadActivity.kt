package com.lysaan.malik.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.Location1Activity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.adapters.ELoadingAdapter
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_eload.*

class ELoadActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    //    private lateinit var myData: MyData
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            myData = bundle!!.getSerializable("myData") as MyData
//            myHelper.log("myData:$myData")
//        }
//        myHelper.setLastJourney(myData)

        myData = myHelper.getLastJourney()
        eload_material.text = myData.loadingMaterial
        eload_location.text = myData.loadingLocation


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




    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.load_truck_load -> {
                stopDelay()
                myData.loadingMachine = myHelper.getMachineNumber().toString()
                myData.loadedMachine = "Load"

                myData.loadingGPSLocation = gpsLocation
                myData.loadTypeId = 1
                myData.orgId = myHelper.getLoginAPI().org_id
                myData.operatorId = myHelper.getOperatorAPI().id
                if(myHelper.isOnline()){
                    pushLoad(myData)
                }
                val insertID = db.insertELoad(myData)
                if (insertID > 0) {
                    myHelper.toast("Loading Successful.\nLoaded Truck Number # $insertID")

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
                    myHelper.toast("Error while Saving Record.")
                }

            }
            R.id.eload_back -> {
                myHelper.startHomeActivityByType(MyData())
            }
            R.id.eload_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
            R.id.eload_material -> {
                val intent = Intent(this, Material1Activity::class.java)
//                val data1 = myHelper.getLastJourney()
                myData.isForLoadResult = true
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.eload_location -> {
                val intent = Intent(this, Location1Activity::class.java)
//                val data1 = myHelper.getLastJourney()
                myData.isForLoadResult = true
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            var bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                myData = bundle!!.getSerializable("myData") as MyData
                myHelper.log("myData:$myData")


                eload_material.text = myData.loadingMaterial
                eload_location.text = myData.loadingLocation

                myData.isForUnloadResult = false
                myData.isForLoadResult = false
                myHelper.setLastJourney(myData)

            }

        } else {
            myHelper.toast("Request can not be completed.")
        }
    }
}
