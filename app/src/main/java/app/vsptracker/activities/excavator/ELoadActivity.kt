package app.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.common.LocationActivity
import app.vsptracker.activities.common.MaterialActivity
import app.vsptracker.adapters.ELoadingAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyHelper

private const val REQUEST_MATERIAL = 2
private const val REQUEST_LOCATION = 3

class ELoadActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  lateinit var eload_material: TextView
  lateinit var eload_location: TextView
  lateinit var load_truck_load: FrameLayout
  lateinit var eload_back: Button
  lateinit var eload_finish: Button
  lateinit var elh_rv: androidx.recyclerview.widget.RecyclerView
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_eload, contentFrameLayout)
    
    eload_material = findViewById(R.id.eload_material)
    eload_location = findViewById(R.id.eload_location)
    load_truck_load = findViewById(R.id.load_truck_load)
    eload_back = findViewById(R.id.eload_back)
    eload_finish = findViewById(R.id.eload_finish)
    elh_rv = findViewById(R.id.elh_rv)
    
    myHelper = MyHelper(tag, this)
    
    
    myData = myHelper.getLastJourney()
    myHelper.log("$myData")
    eload_material.text = db.getMaterialByID(myData.loading_material_id).name
    eload_location.text = db.getLocationByID(myData.loading_location_id).name
    
    
    load_truck_load.setOnClickListener(this)
    eload_back.setOnClickListener(this)
    eload_finish.setOnClickListener(this)
    eload_material.setOnClickListener(this)
    eload_location.setOnClickListener(this)
    
    
    val loadHistory = db.getELoadHistory()
    if (loadHistory.size > 0) {
      elh_rv.visibility = View.VISIBLE
      val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
      val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
      elh_rv.layoutManager = layoutManager1
      elh_rv!!.adapter = aa
    } else {
      elh_rv.visibility = View.INVISIBLE
    }
    
  }
  
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.load_truck_load -> {
        stopDelay()
        
        myData.loadingGPSLocation = gpsLocation
        myData.loadTypeId = 1
        myData.org_id = myHelper.getLoginAPI().org_id
        myData.operatorId = myHelper.getOperatorAPI().id
        
        
        myData.loadingGPSLocation = gpsLocation
        myData.org_id = myHelper.getLoginAPI().org_id
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()
        
        val currentTime = System.currentTimeMillis()
        myData.startTime = currentTime
        myData.stopTime = currentTime
        myData.totalTime = myData.stopTime - myData.startTime
        if (myHelper.isDailyModeStarted()) {
          myData.isDayWorks = 1
        } else {
          myData.isDayWorks = 0
        }
        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime)
        myData.isSync = 0
        
        myData.machineTypeId = myHelper.getMachineTypeID()
        
        pushInsertELoad(myData)
        stopDelay()
//                if(myHelper.isOnline()){
//                    pushELoad(myData)
//                }
//                val insertID = db.insertELoad(myData)
//                if (insertID > 0) {
        myHelper.toast("Loading Successful.")

//                    val loadHistory = db.getELoadHistory()
//                    if (loadHistory.size > 0) {
//                        elh_rv.visibility = View.VISIBLE
//                        val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
//                        val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//                        elh_rv.layoutManager = layoutManager1
//                        elh_rv!!.adapter = aa
//                    } else {
//                        elh_rv.visibility = View.INVISIBLE
//                    }

//                } else {
//                    myHelper.toast("Error while Saving Record.")
//                }
      
      }
      R.id.eload_back -> {
        myHelper.startHomeActivityByType(MyData())
      }
      R.id.eload_finish -> {
        val intent = Intent(this, HourMeterStopActivity::class.java)
        startActivity(intent)
      }
      R.id.eload_material -> {
        val intent = Intent(this, MaterialActivity::class.java)
//                val data1 = myHelper.getLastJourney()
        myData.isForLoadResult = true
        intent.putExtra("myData", myData)
        startActivityForResult(intent, REQUEST_MATERIAL)
      }
      R.id.eload_location -> {
        val intent = Intent(this, LocationActivity::class.java)
//                val data1 = myHelper.getLastJourney()
        myData.isForLoadResult = true
        intent.putExtra("myData", myData)
        startActivityForResult(intent, REQUEST_LOCATION)
      }
    }
  }
  
  private fun pushInsertELoad(myData: MyData): Int {

//        when {
//            myHelper.isOnline() -> pushELoad1(myData)
//            else -> {
//                myHelper.toast("No Internet Connection.\nData Saved in App but Not Uploaded to Server.")
    insertELoad(myData)
//            }
//        }
    return 1
  }
  
  /**
   * Previously was using this API call to Upload Data for Excavator Production Digging
   * as after call there was adapter update with load which could not be done without this
   * Activity. Now Adapter is updated and Data is inserted in App Database but it is not
   * pushed to Server, rather on Data insertion standard method checkServerSyncData is called
   * for uploading data to Server and updating in App after call.
   */
/*    private fun pushELoad1(myData: MyData) {

//        myData.loadingGPSLocation = gpsLocation
//        myData.orgId = myHelper.getLoginAPI().org_id
//        myData.siteId = myHelper.getMachineSettings().siteId
//        myData.operatorId = myHelper.getOperatorAPI().id
//        myData.machineTypeId = myHelper.getMachineTypeID()
//        myData.machineId = myHelper.getMachineID()
//
//        stopDelay()
//        val currentTime = System.currentTimeMillis()
//        myData.startTime = currentTime
//        myData.stopTime = currentTime
//        myData.totalTime = myData.stopTime - myData.startTime
//        if (myHelper.isDailyModeStarted()) {
//            myData.isDaysWork = 1
//        } else {
//            myData.isDaysWork = 0
//        }
//        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
//        myData.time = currentTime.toString()
//        myData.date = myHelper.getDate(currentTime)

        myHelper.log("pushELoad:$myData")

        val call = this.retrofitAPI.pushLoads(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                myHelper.log(response.toString())
                val responseBody = response.body()
                myHelper.log("EWorkResponse:$responseBody")
                if (responseBody!!.success) {
                    myHelper.toast("Load Pushed to Server Successfully.")
                    myData.isSync = 1
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$responseBody")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                insertELoad(myData)
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                insertELoad(myData)
                myHelper.toast("Failure" + t.message)
            }
        })
    }*/
  
  private fun insertELoad(myData: MyData): Long {
    myHelper.log("insertELoad:$myData")
    val insertID = myDataPushSave.insertELoad(myData)
    myHelper.log("insertELoadID:$insertID")
    
    if (insertID > 0) {
      val loadHistory = db.getELoadHistory()
      if (loadHistory.size > 0) {
        elh_rv.visibility = View.VISIBLE
        val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
        val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        elh_rv.layoutManager = layoutManager1
        elh_rv!!.adapter = aa
      } else {
        elh_rv.visibility = View.INVISIBLE
      }
    }
    return insertID
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    
    if (resultCode == Activity.RESULT_OK) {
      val bundle: Bundle? = intent!!.extras
      if (bundle != null) {
        myData = bundle.getSerializable("myData") as MyData
        myHelper.log("myData:$myData")
        
        eload_material.text = db.getMaterialByID(myData.loading_material_id).name
        eload_location.text = db.getLocationByID(myData.loading_location_id).name
        
        myData.isForUnloadResult = false
        myData.isForLoadResult = false
        myHelper.setLastJourney(myData)
        
      }
      
    } else {
      myHelper.toast("Request can not be completed.")
    }
  }
}
