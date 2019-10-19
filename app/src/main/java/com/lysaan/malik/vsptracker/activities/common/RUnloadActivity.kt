package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.MyDataResponse
import kotlinx.android.synthetic.main.activity_runload.*

class RUnloadActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private val REQUEST_MACHINE = 1
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3
    private val REQUEST_WEIGHT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_runload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")


        when (myData.nextAction) {
            1 -> {
                trul_task.text = myData.unloadingTask
                trul_material.text = myData.unloadingMaterial
                trul_location.text = myData.unloadingLocation
                trul_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
                trunload_unload.text = "Unload"
            }
            3 -> {
                trul_task.text = myData.backUnloadingTask
                trul_material.text = myData.backUnloadingMaterial
                trul_location.text = myData.backUnloadingLocation
                trul_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
                trunload_unload.text = "Back Unload"
            }
        }

        runload_home.setOnClickListener(this)
        runload_finish.setOnClickListener(this)

        trunload_unload.setOnClickListener(this)
        trul_task.setOnClickListener(this)
        trul_material.setOnClickListener(this)
        trul_location.setOnClickListener(this)
        trul_weight.setOnClickListener(this)
    }




    override fun onClick(view: View?) {
        when (view!!.id) {


            R.id.runload_home -> {
                val data = MyData()
//                everything should be reset when clicked on Finish
//                data.nextAction = 1
                myHelper.setLastJourney(data)
                myHelper.startHomeActivityByType(data)
            }
            R.id.runload_finish -> {
                myHelper.logout(this)
            }
            R.id.trunload_unload -> {

                myData.unloadingGPSLocation = gpsLocation
                myData.orgId = myHelper.getLoginAPI().org_id
                myData.siteId = myHelper.getMachineSettings().siteId
                myData.operatorId = myHelper.getOperatorAPI().id
                myData.machineTypeId = myHelper.getMachineTypeID()
                myData.machineId = myHelper.getMachineID()

                myData.backUnloadingMaterial = myData.backLoadingMaterial
                myData.back_unloading_material_id = myData.back_loading_material_id
                stopDelay()
                val currentTime = System.currentTimeMillis()
                myData.stopTime = currentTime
                myData.totalTime = myData.stopTime - myData.startTime
                if(myHelper.isDailyModeStarted()){
                    myData.isDaysWork = 1
                }else {
                    myData.isDaysWork = 0
                }
                myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
                myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)

                saveTrip(myData)

                val datum = db.getTrip(myData.recordID)
                myHelper.log("getTrip:$datum")

                if(myData.tripType == 1) {

                    myData.loadingMachine = myData.backLoadingMachine
                    myData.loading_machine_id = myData.back_loading_machine_id

                    myData.loadingMaterial = myData.backLoadingMaterial
                    myData.loading_material_id = myData.back_loading_material_id

                    myData.loadingLocation = myData.backLoadingLocation
                    myData.loading_location_id = myData.back_loading_location_id

                    myData.unloadingTask = myData.backUnloadingTask
                    myData.unloading_task_id = myData.back_unloading_task_id

                    myData.unloadingMaterial = myData.backUnloadingMaterial
                    myData.unloading_material_id = myData.back_unloading_material_id

                    myData.unloadingLocation = myData.backUnloadingLocation
                    myData.unloading_location_id= myData.back_unloading_location_id

                    myData.loadingGPSLocation = myData.backLoadingGPSLocation
                    myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)

                    myData.unloadingGPSLocation = myData.backUnloadingGPSLocation
                    myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
                }



                if(myHelper.isOnline()){
                    pushTrip(myData)
                }else{
                    myHelper.toast("No Internet Connection.\nDelay Not Uploaded to Server.")
                    db.updateTrip(myData)
                }
            }
            R.id.trul_task -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trul_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_WEIGHT)
            }
        }
    }

    fun pushTrip(myData: MyData){
        myHelper.log("pushDelay:$myData")

        val call = this.retrofitAPI.pushTrip(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val response = response.body()
                myHelper.log("EWorkResponse:$response")
                if (response!!.success && response.data != null) {
                    myData.isSync = 1
                    db.updateTrip(myData)

                } else {
                    db.updateTrip(myData)
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
//                myHelper.hideDialog()
//                saveTrip(myData)
                db.updateTrip(myData)
                myHelper.log("Failure" + t.message)
            }
        })
    }

    fun saveTrip(myData: MyData){
        when (myData.repeatJourney) {
            0 -> {
                when (myData.nextAction) {
                    1 -> {
                        myData.nextAction = 0
                    }
                    2 -> {
                        myData.nextAction = 3
                    }
                }

                myData.unloadingWeight = 0.0;
                myHelper.setLastJourney(myData)
                myHelper.startLoadAfterActivityByType(myData)
            }
            1 -> {
                when (myData.nextAction) {
                    1 -> {
                        myData.nextAction = 0
                    }
                }
                val intent = Intent(this, RLoadActivity::class.java)
                myData.unloadingWeight = 0.0;
                myHelper.setLastJourney(myData)
                startActivity(intent)
                finish()
            }
            2 -> {
                when (myData.nextAction) {
                    1 -> {
                        myData.nextAction = 2
                    }
                    3 -> {
                        myData.nextAction = 0
                    }
                }
                myData.unloadingWeight = 0.0;
                myHelper.setLastJourney(myData)
                val intent = Intent(this, RLoadActivity::class.java)
                startActivity(intent)
                finish()
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



                when(myData.nextAction ) {
                    1 ->{
                        trul_task.text = myData.unloadingTask
                        trul_material.text = myData.unloadingMaterial
                        trul_location.text = myData.unloadingLocation
                    }
                    3 ->{
                        trul_task.text = myData.backUnloadingTask
                        trul_material.text = myData.backUnloadingMaterial
                        trul_location.text = myData.backUnloadingLocation
                    }

                }

                trul_weight.text = myData.unloadingWeight.toString()
                myData.isForUnloadResult = false
                myData.isForLoadResult = false
                myHelper.setLastJourney(myData)

            }
        } else {
            myHelper.toast("Request can not be completed.")
        }

    }


}
