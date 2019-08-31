package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
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

        helper.setTag(TAG)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }
//        helper.log("LastJourney:${helper.getLastJourney()}")

        data = helper.getLastJourney()
        helper.log("data:$data")

//        if(data.isForBackLoad){
//            trload_load.text = "Back LOAD"
//        }else{
//            trload_load.text = "LOAD"
//        }
        when (data.nextAction) {
            0 -> {
                trload_load.text = "LOAD"
            }
            1 -> {
            }
            2 -> {
                trload_load.text = "Back LOAD"
            }
            3 -> {
            }
        }

        trload_weight.visibility = View.GONE

        if (helper.getMachineType() == 2) {
            trload_machine.visibility = View.GONE
//            trload_weight.visibility = View.GONE

            trload_material.text = data.loadingMaterial
            trload_location.text = data.loadingLocation
//            trload_weight.text = "Tonnes  (" +lastJourney.unloadingWeight +")"
        } else {

            trload_machine.visibility = View.VISIBLE
//            trload_weight.visibility = View.VISIBLE

            when (data.nextAction) {
                0 -> {
                    trload_machine.text = data.loadingMachine
                    trload_material.text = data.loadingMaterial
                    trload_location.text = data.loadingLocation
//                    trload_weight.text = "Tonnes (" +data.unloadingWeight +")"
                }
                1 -> {
                }
                2 -> {

                    trload_machine.text = data.backLoadingMachine
                    trload_material.text = data.backLoadingMaterial
                    trload_location.text = data.backLoadingLocation
//                    trload_weight.text = "Tonnes (" +data.backUnloadedWeight +")"
                }
                3 -> {
                }
            }
//            if(lastJourney.isForBackLoad){
//                trload_machine.text = lastJourney.backLoadingMachine
//                trload_material.text = lastJourney.backLoadingMaterial
//                trload_location.text = lastJourney.backLoadingLocation
//                trload_weight.text = "Tonnes (" +lastJourney.backUnloadedWeight +")"
//            }else{
//                trload_machine.text = lastJourney.loadingMachine
//                trload_material.text = lastJourney.loadingMaterial
//                trload_location.text = lastJourney.loadingLocation
//                trload_weight.text = "Tonnes (" +lastJourney.unloadingWeight +")"
//            }

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
        startGPS()
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.trload_load -> {

//                val insertID = db.insertELoad(helper.getLastJourney())
//                if(insertID > 0){
//                    helper.toast("Loading Successful.\nLoaded Number # $insertID")

                data.loadingGPSLocation = gpsLocation
                stopDelay()
                when (data.repeatJourney) {
                    0 -> {
                        var insertID: Long = 0L
                        when (data.nextAction) {
                            0 -> {
                                data.nextAction = 1
                                insertID = db.insertTrip(data)
                            }
                            2 -> {
                                data.tripType = 1
                                data.trip0ID = data.recordID.toInt()
                                insertID = db.insertTrip(data)
                                data.nextAction = 3
                            }
                        }

                        if (insertID > 0) {
                            helper.toast("Load Saved Successfully.")
                            data.recordID = insertID
                            helper.setLastJourney(data)
                            helper.startHomeActivityByType(data)
                        } else {
                            helper.toast("Error while Saving Load.")
                        }

                    }
                    1 -> {
                        when (data.nextAction) {
                            0 -> {
                                data.nextAction = 1
                            }
                        }
                        val intent = Intent(this, RUnloadActivity::class.java)
                        val insertID = db.insertTrip(data)
                        if (insertID > 0) {
                            helper.toast("Load Saved Successfully.")
                            data.recordID = insertID
                            helper.setLastJourney(data)
                            startActivity(intent)
                            finish()
                        } else {
                            helper.toast("Error while Saving Load.")
                        }

                    }
                    2 -> {
                        when (data.nextAction) {
                            0 -> {
                                data.tripType = 0
                                data.trip0ID = 0
                                data.recordID = db.insertTrip(data)

                                data.nextAction = 1
                            }
                            2 -> {
                                data.tripType = 1
                                data.trip0ID = data.recordID.toInt()
                                data.recordID = db.insertTrip(data)

                                data.nextAction = 3
                            }
                        }


//                            data.recordID = insertID
                        if (data.recordID > 0) {
                            helper.toast("Load Saved Successfully.")
                            helper.setLastJourney(data)
                            val intent = Intent(this, RUnloadActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            helper.toast("Error while Saving Load.")
                        }

                    }
                }
//                    if(data.isRepeatJourney){
//                        val intent = Intent(this, RUnloadActivity::class.java)
//                        intent.putExtra("data", data)
//                        startActivity(intent)
//                        finish()
//                    }else{
//                        helper.startHomeActivityByType(data)
//                    }

//                }else{
//                    helper.toast("Error while Saving Record.")
//                }
            }
            R.id.trload_machine -> {
                val intent = Intent(this, LMachine1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent, REQUEST_MACHINE)
            }
            R.id.trload_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trload_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trload_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent, REQUEST_WEIGHT)
            }

            R.id.rload_home -> {
//                when(data.nextAction){
//                    1 ->{data.nextAction = 0}
//                    3 ->{data.nextAction = 2}
//                    else ->{data.nextAction = 0}
//                }
                data = Data()
                helper.setLastJourney(data)
                helper.startHomeActivityByType(data)
            }
            R.id.rload_finish -> {
                helper.logout(this)
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

                if (helper.getMachineType() == 2) {
                    trload_machine.visibility = View.GONE
                    trload_material.text = data.loadingMaterial
                    trload_location.text = data.loadingLocation
                    trload_weight.text = "Tonnes (" + data.unloadingWeight + ")"
                } else {

                    trload_machine.visibility = View.VISIBLE
                    when (data.nextAction) {
                        0 -> {
                            trload_machine.text = data.loadingMachine
                            trload_material.text = data.loadingMaterial
                            trload_location.text = data.loadingLocation
//                            trload_weight.text = "Tonnes (" +data.unloadingWeight +")"
                        }
                        1 -> {

                        }
                        2 -> {
                            trload_machine.text = data.backLoadingMachine
                            trload_material.text = data.backLoadingMaterial
                            trload_location.text = data.backLoadingLocation
//                            trload_weight.text = "Tonnes (" +data.backUnloadedWeight +")"
                        }
                        3 -> {

                        }
                    }
//                    if(data.isForBackLoad){
//                        trload_machine.text = data.backLoadingMachine
//                        trload_material.text = data.backLoadingMaterial
//                        trload_location.text = data.backLoadingLocation
//                        trload_weight.text = "Tonnes (" +data.backUnloadedWeight +")"
//                    }else{
//                        trload_machine.text = data.loadingMachine
//                        trload_material.text = data.loadingMaterial
//                        trload_location.text = data.loadingLocation
//                        trload_weight.text = "Tonnes (" +data.unloadingWeight +")"
//                    }

                }


                data.isForUnloadResult = false
                data.isForLoadResult = false
                data.isForBackUnloadResult = false
                data.isForBackLoadResult = false
                helper.setLastJourney(data)

            }

        } else {
            helper.toast("Request can not be completed.")
        }
    }

}
