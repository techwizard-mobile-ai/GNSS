package app.vsptracker.activities.common


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData

private const val REQUEST_MACHINE = 1
private const val REQUEST_MATERIAL = 2
private const val REQUEST_LOCATION = 3
private const val REQUEST_WEIGHT = 4

class RLoadActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  lateinit var trload_load: Button
  lateinit var rload_home: Button
  lateinit var rload_finish: Button
  lateinit var trload_weight: TextView
  lateinit var trload_machine: TextView
  lateinit var trload_material: TextView
  lateinit var trload_location: TextView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_rload, contentFrameLayout)
    
    trload_load = findViewById(R.id.trload_load)
    trload_weight = findViewById(R.id.trload_weight)
    trload_machine = findViewById(R.id.trload_machine)
    trload_material = findViewById(R.id.trload_material)
    trload_location = findViewById(R.id.trload_location)
    rload_home = findViewById(R.id.rload_home)
    rload_finish = findViewById(R.id.rload_finish)
    
    myHelper.setTag(tag)
    
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    
    when (myData.nextAction) {
      0 -> {
        trload_load.text = getString(R.string.load)
      }
      2 -> {
        trload_load.text = getString(R.string.back_load)
      }
    }
    
    trload_weight.visibility = View.GONE
    
    if (myHelper.getMachineTypeID() == 2) {
      trload_machine.visibility = View.GONE
    } else {
      trload_machine.visibility = View.VISIBLE
    }
    when (myHelper.getMachineTypeID()) {
      2, 3 -> {
        when (myData.nextAction) {
          0 -> {
            trload_machine.text = db.getMachineByID(myData.loading_machine_id).number
            trload_material.text = db.getMaterialByID(myData.loading_material_id).name
            trload_location.text = db.getLocationByID(myData.loading_location_id).name
          }
          2 -> {
            trload_machine.text = db.getMachineByID(myData.back_loading_machine_id).number
            trload_material.text = db.getMaterialByID(myData.back_loading_material_id).name
            trload_location.text = db.getLocationByID(myData.back_loading_location_id).name
          }
        }
      }
    }
    
    rload_home.setOnClickListener(this)
    rload_finish.setOnClickListener(this)
    
    trload_load.setOnClickListener(this)
    trload_machine.setOnClickListener(this)
    trload_material.setOnClickListener(this)
    trload_location.setOnClickListener(this)
    trload_weight.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    myData.trip0ID = System.currentTimeMillis().toString()
    
    when (view!!.id) {
      
      
      R.id.trload_load -> {
        
        myData.loadingGPSLocation = gpsLocation
        myData.org_id = myHelper.getLoginAPI().org_id
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()
        
        stopDelay()
        when (myData.repeatJourney) {
          0 -> {
            var insertID = 0L
            when (myData.nextAction) {
              0 -> {
                insertID = myDataPushSave.insertTrip(myData)
                myData.nextAction = 1
              }
              2 -> {
                val datum = db.getTrip(myData.recordID)
                myData.trip0ID = datum.trip0ID
                myData.tripType = 1
                insertID = myDataPushSave.insertTrip(myData)
                myData.nextAction = 3
              }
            }
            
            if (insertID > 0) {
              myHelper.toast("Load Saved Successfully.")
              myData.recordID = insertID
              val currentTime = System.currentTimeMillis()
              myData.startTime = currentTime
              myHelper.setLastJourney(myData)
              myHelper.startHomeActivityByType(myData)
            } else {
              myHelper.toast("Error while Saving Load.")
            }
            
          }
          1 -> {
            when (myData.nextAction) {
              0 -> {
                myData.nextAction = 1
              }
            }
            val intent = Intent(this, RUnloadActivity::class.java)
            val insertID = myDataPushSave.insertTrip(myData)
            if (insertID > 0) {
              myHelper.toast("Load Saved Successfully.")
              myData.recordID = insertID
              myHelper.setLastJourney(myData)
              startActivity(intent)
              finish()
            } else {
              myHelper.toast("Error while Saving Load.")
            }
            
          }
          2 -> {
            when (myData.nextAction) {
              0 -> {
                myData.tripType = 0
                myData.recordID = myDataPushSave.insertTrip(myData)
                
                myData.nextAction = 1
              }
              2 -> {
                myData.tripType = 1
                
                val datum = db.getTrip(myData.recordID)
                myData.trip0ID = datum.trip0ID
                myData.recordID = myDataPushSave.insertTrip(myData)
                
                myData.nextAction = 3
              }
            }
            
            if (myData.recordID > 0) {
              myHelper.toast("Load Saved Successfully.")
              myHelper.setLastJourney(myData)
              val intent = Intent(this, RUnloadActivity::class.java)
              startActivity(intent)
              finish()
            } else {
              myHelper.toast("Error while Saving Load.")
            }
            
          }
        }
      }
      R.id.trload_machine -> {
        val intent = Intent(this, LMachineActivity::class.java)
        if (myData.nextAction == 0) {
          myData.isForLoadResult = true
        } else {
          myData.isForBackLoadResult = true
        }
        intent.putExtra("myData", myData)
        startActivityForResult(intent, REQUEST_MACHINE)
      }
      R.id.trload_material -> {
        val intent = Intent(this, MaterialActivity::class.java)
        if (myData.nextAction == 0) {
          myData.isForLoadResult = true
        } else {
          myData.isForBackLoadResult = true
        }
        intent.putExtra("myData", myData)
        startActivityForResult(intent, REQUEST_MATERIAL)
      }
      R.id.trload_location -> {
        val intent = Intent(this, LocationActivity::class.java)
        if (myData.nextAction == 0) {
          myData.isForLoadResult = true
        } else {
          myData.isForBackLoadResult = true
        }
        intent.putExtra("myData", myData)
        startActivityForResult(intent, REQUEST_LOCATION)
      }
      R.id.trload_weight -> {
        val intent = Intent(this, WeightActivity::class.java)
        val data1 = myHelper.getLastJourney()
        data1.isForUnloadResult = true
        intent.putExtra("myData", data1)
        startActivityForResult(intent, REQUEST_WEIGHT)
      }
      
      R.id.rload_home -> {
        myData = MyData()
        myHelper.setLastJourney(myData)
        myHelper.startHomeActivityByType(myData)
      }
      R.id.rload_finish -> {
        myHelper.logout(this)
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
        
        if (myHelper.getMachineTypeID() == 2) {
          trload_machine.visibility = View.GONE
          trload_material.text = db.getMaterialByID(myData.loading_material_id).name
          trload_location.text = db.getLocationByID(myData.loading_location_id).name
//                    trload_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
          try {
            trload_weight.text = getString(R.string.weight_tonnes_message, myData.unloadingWeight)
          }
          catch (e: Exception) {
            myHelper.log(e.message.toString())
          }
        } else {
          
          trload_machine.visibility = View.VISIBLE
          when (myData.nextAction) {
            0 -> {
              trload_machine.text = db.getMachineByID(myData.loading_machine_id).number
              trload_material.text = db.getMaterialByID(myData.loading_material_id).name
              trload_location.text = db.getLocationByID(myData.loading_location_id).name
            }
            2 -> {
              trload_machine.text = db.getMachineByID(myData.back_loading_machine_id).number
              trload_material.text = db.getMaterialByID(myData.back_loading_material_id).name
              trload_location.text = db.getLocationByID(myData.back_loading_location_id).name
            }
          }
        }
        
        
        myData.isForUnloadResult = false
        myData.isForLoadResult = false
        myData.isForBackUnloadResult = false
        myData.isForBackLoadResult = false
        myHelper.setLastJourney(myData)
        
      }
      
    } else {
      myHelper.toast("Request can not be completed.")
    }
  }
  
}
