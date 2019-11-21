package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataResponse
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_runload.*

private const val REQUEST_MATERIAL = 2
private const val REQUEST_LOCATION = 3
private const val REQUEST_WEIGHT = 4
class RUnloadActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_runload, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(tag)
        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")

        when (myData.nextAction) {
            1 -> {
                trul_task.text = myData.unloadingTask
                trul_material.text = myData.unloadingMaterial
                trul_location.text = myData.unloadingLocation
//                trul_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
                trul_weight.text = "0.0"
//                trunload_unload.text = "Unload"
                trunload_unload.text = getString(R.string.unload)
            }
            3 -> {
                trul_task.text = myData.backUnloadingTask
                trul_material.text = myData.backUnloadingMaterial
                trul_location.text = myData.backUnloadingLocation
//                trul_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
                trul_weight.text = "0.0"
//                trunload_unload.text = "Back Unload"
                trunload_unload.text = getString(R.string.back_unload)
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

    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
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
                val datum = db.getTrip(myData.recordID)
                myData.loadingGPSLocationString = myHelper.getGPSLocationToString(datum.loadingGPSLocation)
                myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)

                saveTrip(myData)

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

//                    myData.loadingGPSLocation = myData.backLoadingGPSLocation
//                    myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
//
//                    myData.unloadingGPSLocation = myData.backUnloadingGPSLocation
//                    myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
                }


                myData.unloadingWeight = trul_weight.text.toString().toDouble()
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
                val intent = Intent(this, MaterialActivity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, LocationActivity::class.java)
                if(myData.nextAction == 1){
                    myData.isForUnloadResult = true
                }else{
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trul_weight -> {
                val intent = Intent(this, WeightActivity::class.java)
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

    private fun pushTrip(myData: MyData){
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
                val responseBody = response.body()
                myHelper.log("EWorkResponse:$responseBody")
                if (responseBody!!.success) {
                    myData.isSync = 1
                    db.updateTrip(myData)

                } else {
                    db.updateTrip(myData)
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
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

    private fun saveTrip(myData: MyData){
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

//                myData.unloadingWeight = 0.0;
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
//                myData.unloadingWeight = 0.0;
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
//                myData.unloadingWeight = 0.0;
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
            val bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                myData = bundle.getSerializable("myData") as MyData
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
