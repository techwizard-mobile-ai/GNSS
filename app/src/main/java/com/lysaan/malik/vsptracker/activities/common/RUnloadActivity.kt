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
import kotlinx.android.synthetic.main.activity_runload.*

class RUnloadActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private val REQUEST_MACHINE= 1
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3
    private val REQUEST_WEIGHT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_runload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }

//        helper.log("lastJourney:${helper.getLastJourney()}")

        data = helper.getLastJourney()
        helper.log("data:$data")

        if(helper.getMachineType() == 2){
            trul_task.visibility = View.GONE
//            trul_weight.visibility = View.GONE
        }else{
            trul_task.visibility = View.VISIBLE
            trul_weight.visibility = View.VISIBLE
        }

        when(data.nextAction){
            3 ->{
                trul_task.text = data.backUnloadingTask
                trul_material.text = data.backUnloadingMaterial
                trul_location.text = data.backUnloadingLocation
                trul_weight.text = "Tonnes ("+data.backUnloadedWeight +")"
                trunload_unload.text = "Back Unload"
            }
            1 ->{
                trul_task.text = data.unloadingTask
                trul_material.text = data.unloadingMaterial
                trul_location.text = data.unloadingLocation
                trul_weight.text = "Tonnes ("+data.unloadingWeight +")"
                trunload_unload.text = "Unload"
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
        startGPS()
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }

    override fun onClick(view: View?) {
        when(view!!.id){


            R.id.runload_home -> {
//                when(data.nextAction){
//                    0 ->{data.nextAction = 1}
//                    2 ->{data.nextAction = 3}
//                    else ->{data.nextAction = 1}
//                }
                val data = Data()
                data.nextAction = 1
                helper.setLastJourney(data)
                helper.startHomeActivityByType(data)
            }
            R.id.runload_finish -> {
                helper.logout(this)

            }
            R.id.trunload_unload -> {

                data.unloadingGPSLocation = gpsLocation


                when(data.repeatJourney){
                    0 -> {
                        when(data.nextAction){
                            1 ->{data.nextAction = 0}
                            2 ->{data.nextAction = 3}

                        }


                        db.updateTrip(data)
                        helper.setLastJourney(data)
                        helper.startLoadAfterActivityByType(data)
                    }
                    1 -> {
                        when(data.nextAction){
                            1 ->{data.nextAction = 0}
                        }
                        val intent = Intent(this, RLoadActivity::class.java)
//                        intent.putExtra("data", data)
                        db.updateTrip(data)
                        helper.setLastJourney(data)
                        startActivity(intent)
                        finish()
                    }
                    2 -> {
                        when(data.nextAction){
                            1 ->{data.nextAction = 2}
                            3 ->{data.nextAction = 0}
                        }
                        db.updateTrip(data)
                        helper.setLastJourney(data)
                        val intent = Intent(this, RLoadActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
//                if(data.isRepeatJourney){
//                    val intent = Intent(this, RLoadActivity::class.java)
//                    intent.putExtra("data", data)
//                    startActivity(intent)
//                    finish()
//                }else{
//                    helper.startLoadAfterActivityByType(data)
//                }

            }
            R.id.trul_task -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MATERIAL)
            }
            R.id.trul_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_LOCATION)
            }
            R.id.trul_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_WEIGHT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            var bundle :Bundle ?=intent!!.extras
            if(bundle != null){
                data = bundle!!.getSerializable("data") as Data
                helper.log("data:$data")

                trul_task.text = data.unloadingTask
                trul_material.text = data.unloadingMaterial
                trul_location.text = data.unloadingLocation
                trul_weight.text = data.unloadingWeight.toString()
                data.isForUnloadResult = false
                data.isForLoadResult = false
                helper.setLastJourney(data)

            }
        }else{
            helper.toast("Request can not be completed.")
        }

    }


}
