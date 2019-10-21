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
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_rload.*

class RLoadActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private val REQUEST_MACHINE = 1
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3
    private val REQUEST_WEIGHT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_rload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")


        when (myData.nextAction) {
            0 -> {
                trload_load.text = "LOAD"
            }
            2 -> {
                trload_load.text = "Back LOAD"
            }
        }

        trload_weight.visibility = View.GONE

        if(myHelper.getMachineTypeID() == 2){
            trload_machine.visibility = View.GONE
        }else{
            trload_machine.visibility = View.VISIBLE
        }
        when(myHelper.getMachineTypeID()){
            2, 3 ->{
                when (myData.nextAction) {
                    0 -> {
                        trload_machine.text = myData.loadingMachine
                        trload_material.text = myData.loadingMaterial
                        trload_location.text = myData.loadingLocation
                    }
                    2 -> {
                        trload_machine.text = myData.backLoadingMachine
                        trload_material.text = myData.backLoadingMaterial
                        trload_location.text = myData.backLoadingLocation
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
                stopDelay()
                when (myData.repeatJourney) {
                    0 -> {
                        var insertID: Long = 0L
                        when (myData.nextAction) {
                            0 -> {
                                insertID = db.insertTrip(myData)
                                myData.nextAction = 1
                            }
                            2 -> {
                                val datum = db.getTrip(myData.recordID)
                                myData.trip0ID = datum.trip0ID
                                myData.tripType = 1
                                insertID = db.insertTrip(myData)
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
                        val insertID = db.insertTrip(myData)
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
                                myData.recordID = db.insertTrip(myData)

                                myData.nextAction = 1
                            }
                            2 -> {
                                myData.tripType = 1

                                val datum = db.getTrip(myData.recordID)
                                myData.trip0ID = datum.trip0ID
                                myData.recordID = db.insertTrip(myData)

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
                val intent = Intent(this, LMachine1Activity::class.java)
                if(myData.nextAction == 0){
                    myData.isForLoadResult = true
                }else{
                    myData.isForBackLoadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MACHINE)
            }
            R.id.trload_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                if(myData.nextAction == 0){
                    myData.isForLoadResult = true
                }else{
                    myData.isForBackLoadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trload_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                if(myData.nextAction == 0){
                    myData.isForLoadResult = true
                }else{
                    myData.isForBackLoadResult = true
                }
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trload_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
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
            var bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                myData = bundle!!.getSerializable("myData") as MyData
                myHelper.log("myData:$myData")

                if (myHelper.getMachineTypeID() == 2) {
                    trload_machine.visibility = View.GONE
                    trload_material.text = myData.loadingMaterial
                    trload_location.text = myData.loadingLocation
                    trload_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
                } else {

                    trload_machine.visibility = View.VISIBLE
                    when (myData.nextAction) {
                        0 -> {
                            trload_machine.text = myData.loadingMachine
                            trload_material.text = myData.loadingMaterial
                            trload_location.text = myData.loadingLocation
                        }
                        2 -> {
                            trload_machine.text = myData.backLoadingMachine
                            trload_material.text = myData.backLoadingMaterial
                            trload_location.text = myData.backLoadingLocation
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
