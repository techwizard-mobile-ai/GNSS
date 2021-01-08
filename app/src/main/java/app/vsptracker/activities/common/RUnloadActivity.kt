package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
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
                trul_task.text = db.getTaskByID(myData.unloading_task_id).name
                trul_material.text = db.getMaterialByID(myData.unloading_material_id).name
                trul_location.text = db.getLocationByID(myData.unloading_location_id).name
                trul_weight.text = "0.0"
                trunload_unload.text = getString(R.string.unload)
            }
            3 -> {
                trul_task.text = db.getTaskByID(myData.back_unloading_task_id).name
                trul_material.text = db.getMaterialByID(myData.back_unloading_material_id).name
                trul_location.text = db.getLocationByID(myData.back_unloading_location_id).name
                trul_weight.text = "0.0"
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
        updateWeightBackground()
    }
    
    fun updateWeightBackground() {
        if (trul_weight.text.toString().toDouble() > 0) {
            trul_weight.background = ContextCompat.getDrawable(applicationContext, R.drawable.rounded_corner_unload)
        } else {
            trul_weight.background = ContextCompat.getDrawable(applicationContext, R.drawable.rounded_corner_yellow)
        }
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
    
            R.id.runload_home -> {
                unload()
//                everything should be reset when clicked on Finish
                val data = MyData()
                myHelper.setLastJourney(data)
                myHelper.startHomeActivityByType(data)
            }
            R.id.runload_finish -> {
                unload()
//                everything should be reset when clicked on Finish
                val data = MyData()
                myHelper.setLastJourney(data)
                myHelper.logout(this)
            }
            R.id.trunload_unload -> {
                unload()
                // Reset unload weight as in Repeat Journey OR Loop weight should be reset
                myData.unloadingWeight = 0.0
                afterUnloadAction(myData)
            }
            R.id.trul_task -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                if (myData.nextAction == 1) {
                    myData.isForUnloadResult = true
                } else {
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_material -> {
                val intent = Intent(this, MaterialActivity::class.java)
                if (myData.nextAction == 1) {
                    myData.isForUnloadResult = true
                } else {
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, LocationActivity::class.java)
                if (myData.nextAction == 1) {
                    myData.isForUnloadResult = true
                } else {
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trul_weight -> {
                val intent = Intent(this, WeightActivity::class.java)
                if (myData.nextAction == 1) {
                    myData.isForUnloadResult = true
                } else {
                    myData.isForBackUnloadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_WEIGHT)
            }
        }
    }
    
    private fun unload() {
        myData.unloadingGPSLocation = gpsLocation
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()
        
        val currentTime = System.currentTimeMillis()
        myData.stopTime = currentTime
        myData.totalTime = myData.stopTime - myData.startTime
        if (myHelper.isDailyModeStarted()) {
            myData.isDayWorks = 1
        } else {
            myData.isDayWorks = 0
        }
        val datum = db.getTrip(myData.recordID)
        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(datum.loadingGPSLocation)
        myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        
        myData.unloadingWeight = trul_weight.text.toString().toDouble()
        myDataPushSave.updateTrip(myData)
        stopDelay()
    }
    
    private fun afterUnloadAction(myData: MyData) {
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
                
                
                
                when (myData.nextAction) {
                    1 -> {
                        trul_task.text = db.getTaskByID(myData.unloading_task_id).name
                        trul_material.text = db.getMaterialByID(myData.unloading_material_id).name
                        trul_location.text = db.getLocationByID(myData.unloading_location_id).name
                    }
                    3 -> {
                        trul_task.text = db.getTaskByID(myData.back_unloading_task_id).name
                        trul_material.text = db.getMaterialByID(myData.back_unloading_material_id).name
                        trul_location.text = db.getLocationByID(myData.back_unloading_location_id).name
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
