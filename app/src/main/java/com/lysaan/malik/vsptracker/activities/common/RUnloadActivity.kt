package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.MyData
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

        if (myHelper.getMachineType() == 2) {
            trul_task.visibility = View.GONE
//            trul_weight.visibility = View.GONE
        } else {
            trul_task.visibility = View.VISIBLE
            trul_weight.visibility = View.VISIBLE
        }

        when (myData.nextAction) {
            3 -> {
                trul_task.text = myData.backUnloadingTask
                trul_material.text = myData.backUnloadingMaterial
                trul_location.text = myData.backUnloadingLocation
                trul_weight.text = "Tonnes (" + myData.backUnloadedWeight + ")"
                trunload_unload.text = "Back Unload"
            }
            1 -> {
                trul_task.text = myData.unloadingTask
                trul_material.text = myData.unloadingMaterial
                trul_location.text = myData.unloadingLocation
                trul_weight.text = "Tonnes (" + myData.unloadingWeight + ")"
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




    override fun onClick(view: View?) {
        when (view!!.id) {


            R.id.runload_home -> {
                val data = MyData()
                data.nextAction = 1
                myHelper.setLastJourney(data)
                myHelper.startHomeActivityByType(data)
            }
            R.id.runload_finish -> {
                myHelper.logout(this)

            }
            R.id.trunload_unload -> {

                myData.unloadingGPSLocation = gpsLocation
                stopDelay()
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

                        db.updateTrip(myData)
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
                        db.updateTrip(myData)
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
                        db.updateTrip(myData)
                        myHelper.setLastJourney(myData)
                        val intent = Intent(this, RLoadActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            R.id.trul_task -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                val data1 = myHelper.getLastJourney()
                data1.isForUnloadResult = true
                intent.putExtra("myData", data1)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                val data1 = myHelper.getLastJourney()
                data1.isForUnloadResult = true
                intent.putExtra("myData", data1)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                val data1 = myHelper.getLastJourney()
                data1.isForUnloadResult = true
                intent.putExtra("myData", data1)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
            R.id.trul_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
                val data1 = myHelper.getLastJourney()
                data1.isForUnloadResult = true
                intent.putExtra("myData", data1)
                startActivityForResult(intent, REQUEST_WEIGHT)
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

                trul_task.text = myData.unloadingTask
                trul_material.text = myData.unloadingMaterial
                trul_location.text = myData.unloadingLocation
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
