package app.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.common.MaterialActivity
import app.vsptracker.adapters.EOffLoadingAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView


private const val REQUEST_MATERIAL = 2

class ETOffLoadingActivity : BaseActivity(), View.OnClickListener {
  private val eWork = EWork()
  private val tag = this::class.java.simpleName
  private lateinit var workTitle: String
  private var isWorking = false
  private var eWorkID = 0
  private var startTime1 = 0L
  lateinit var ework_title: TextView
  lateinit var ework_action_text: TextView
  lateinit var et_offload_material: TextView
  lateinit var ework_offload_action: FrameLayout
  lateinit var ework_offload_load: FrameLayout
  lateinit var ework_offload_home: Button
  lateinit var ework_offload_finish: Button
  lateinit var chronometer1: Chronometer
  lateinit var eoff_rv: androidx.recyclerview.widget.RecyclerView
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_etoff_loading, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
    ework_title = findViewById(R.id.ework_title)
    ework_offload_action = findViewById(R.id.ework_offload_action)
    ework_offload_load = findViewById(R.id.ework_offload_load)
    ework_offload_home = findViewById(R.id.ework_offload_home)
    ework_offload_finish = findViewById(R.id.ework_offload_finish)
    et_offload_material = findViewById(R.id.et_offload_material)
    ework_action_text = findViewById(R.id.ework_action_text)
    chronometer1 = findViewById(R.id.chronometer1)
    eoff_rv = findViewById(R.id.eoff_rv)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    
    when (myData.eWorkType) {
      1 -> {
        workTitle = "General Digging (Loading)"
      }
      2 -> {
        workTitle = "Trenching"
      }
    }
    
    ework_title.text = workTitle
    
    ework_offload_action.setOnClickListener(this)
    ework_offload_load.setOnClickListener(this)
    ework_offload_home.setOnClickListener(this)
    ework_offload_finish.setOnClickListener(this)
    et_offload_material.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onBackPressed() {
    
    if (isWorking) {
      myHelper.showStopMessage(startTime1)
    } else {
      super.onBackPressed()
      finish()
    }
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.et_offload_material -> {
        if (isWorking) {
          myHelper.showStopMessage(startTime1)
        } else {
          val intent = Intent(this, MaterialActivity::class.java)
          myData.isForLoadResult = true
          intent.putExtra("myData", myData)
          startActivityForResult(intent, REQUEST_MATERIAL)
        }
      }
      R.id.ework_offload_action -> {
        stopDelay()
        if (isWorking) {
          eWork.id = eWorkID
          eWork.startTime = startTime1
          eWork.unloadingGPSLocation = gpsLocation
          val currentTime = System.currentTimeMillis()
          eWork.stopTime = currentTime
          eWork.totalTime = currentTime - eWork.startTime
          eWork.time = currentTime.toString()
          eWork.date = myHelper.getDate(currentTime)
          
          eWork.totalLoads = db.getEWorksOffLoads(eWorkID).size
          if (myHelper.isDailyModeStarted()) {
            eWork.isDayWorks = 1
          } else {
            eWork.isDayWorks = 0
          }
          
          eWork.siteId = myHelper.getMachineSettings().siteId
          eWork.machineId = myHelper.getMachineID()
          eWork.orgId = myHelper.getLoginAPI().org_id
          eWork.operatorId = myHelper.getOperatorAPI().id
          eWork.machineTypeId = myHelper.getMachineTypeID()

//                    eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
          eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)


//                    if(myHelper.isOnline()){
//                        pushSideCasting(eWork)
//                    }
//                    val updatedID = db.updateEWork(eWork)
//                    eWorkID = updatedID
//                    myHelper.log("updatedID :$updatedID ")
          if (myDataPushSave.updateEWork(eWork) > 0) {
            myHelper.toast(
              "$workTitle is Stopped.\n" +
                      "Data Saved Successfully.\n" +
                      "Work Duration : ${myHelper.getTotalTimeVSP(startTime1)} (VSP Meter).\n" +
                      "Work Duration : ${myHelper.getTotalTimeMinutes(startTime1)} (Minutes)"
            )
            ework_action_text.text = getString(R.string.start)
            chronometer1.stop()
            isWorking = false
            eWorkID = 0
          } else {
            myHelper.toast("Data Not Saved.")
            isWorking = false
          }
          
          
        } else {
          startTime1 = System.currentTimeMillis()
          myHelper.toast("$workTitle is Started.")
          ework_action_text.text = getString(R.string.stop)
          chronometer1.base = SystemClock.elapsedRealtime()
          chronometer1.start()
          isWorking = true
          
          eWork.startTime = startTime1
          eWork.orgId = myHelper.getLoginAPI().org_id
          eWork.operatorId = myHelper.getOperatorAPI().id
          eWork.siteId = myHelper.getMachineSettings().siteId
          eWork.stopTime = System.currentTimeMillis()
          eWork.totalTime = System.currentTimeMillis() - startTime1
          eWork.time = startTime1.toString()
          eWork.workType = myData.eWorkType
          eWork.isSync = 0
          if (myHelper.isDailyModeStarted()) {
            eWork.isDayWorks = 1
          } else {
            eWork.isDayWorks = 0
          }
          
          eWork.workActionType = 2
          eWork.loadingGPSLocation = gpsLocation
          eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
          val insertID = myDataPushSave.insertEWork(eWork)
          eWorkID = insertID.toInt()
          myHelper.log("insertID:$insertID")
          myHelper.log("eWorkId:$eWorkID")
        }
        
      }
      
      R.id.ework_offload_load -> {
        if (isWorking) {
          if (eWorkID < 1) {
            myHelper.toast("Please Restart Timer.")
          } else {
            stopDelay()
            val eWork = EWork()
            eWork.eWorkId = eWorkID
//                        eWork.orgId = myHelper.getLoginAPI().org_id
//                        eWork.operatorId = myHelper.getOperatorAPI().id
            eWork.loadingGPSLocation = gpsLocation
            val insertedID = myDataPushSave.insertEWorkOffLoad(eWork)
            if (insertedID > 0) {
              myHelper.toast("Load Saved Successfully.")
              
              val offLoads = db.getEWorksOffLoads(eWorkID)
              if (offLoads.size > 0) {
                eoff_rv.visibility = View.VISIBLE
                val aa = EOffLoadingAdapter(this@ETOffLoadingActivity, offLoads)
                val layoutManager1 =
                  LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                eoff_rv.layoutManager = layoutManager1
                eoff_rv!!.adapter = aa
              } else {
                eoff_rv.visibility = View.INVISIBLE
              }
              
            } else {
              myHelper.toast("Load Not Saved. Please Try again.")
              
            }
          }
        } else {
          myHelper.toast("Timer is Stopped.\nPlease start Timer First.")
        }
      }
      R.id.ework_offload_home -> {
        
        myHelper.log("Loads:${db.getEWorksOffLoads(eWorkID)}")
        
        if (isWorking) {
          myHelper.showStopMessage(startTime1)
        } else {
          myHelper.startHomeActivityByType(myData)
        }
      }
      R.id.ework_offload_finish -> {
        if (isWorking) {
          myHelper.showStopMessage(startTime1)
        } else {
          val intent = Intent(this, HourMeterStopActivity::class.java)
          startActivity(intent)
        }
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
        et_offload_material.text = db.getMaterialByID(myData.loading_material_id).name
        eWork.materialId = myData.loading_material_id
        
      }
    }
  }
}
