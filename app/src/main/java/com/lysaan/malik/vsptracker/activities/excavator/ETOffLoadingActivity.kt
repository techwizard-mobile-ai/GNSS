package com.lysaan.malik.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.adapters.EOffLoadingAdapter
import com.lysaan.malik.vsptracker.apis.delay.EWork
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_etoff_loading.*

class ETOffLoadingActivity : BaseActivity(), View.OnClickListener {
    private val eWork = EWork()
    private val TAG = this::class.java.simpleName
    private lateinit var workTitle: String
    private var isWorking = false
    private var eWorkID = 0
    private var startTime = 0L
    private val REQUEST_MATERIAL = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_etoff_loading, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
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

    override fun onBackPressed() {

        if (isWorking) {
            myHelper.showStopMessage(startTime)
        } else {
            super.onBackPressed()
            finish()
        }
    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.et_offload_material ->{
                if (isWorking) {
                    myHelper.showStopMessage(startTime)
                } else {
                    val intent = Intent(this, Material1Activity::class.java)
                    myData.isForLoadResult = true
                    intent.putExtra("myData", myData)
                    startActivityForResult(intent, REQUEST_MATERIAL)
                }

            }
            R.id.ework_offload_action -> {
                stopDelay()
                if (isWorking) {


                    eWork.ID = eWorkID
                    eWork.startTime = startTime
                    eWork.unloadingGPSLocation = gpsLocation

                    val currentTime = System.currentTimeMillis()

                    eWork.stopTime = currentTime
                    eWork.totalTime = currentTime - eWork.startTime

                    if(myHelper.isOnline()){
                        pushSideCasting(eWork)
                    }

                    val updatedID = db.updateEWork(eWork)
                    eWorkID = updatedID
                    myHelper.log("updatedID :$updatedID ")
//
                    if (updatedID > 0) {
                        myHelper.toast(
                            "$workTitle is Stopped.\n" +
                                    "MyData Saved Successfully.\n" +
                                    "Work Duration : ${myHelper.getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                                    "Work Duration : ${myHelper.getTotalTimeMintues(startTime)} (Minutes)"
                        )
                        ework_action_text.text = "Start"
                        chronometer1.stop()
                        isWorking = false
                        eWorkID = 0
                    } else {
                        myHelper.toast("MyData Not Saved.")
                        isWorking = false
                    }


                } else {
                    startTime = System.currentTimeMillis()
                    myHelper.toast("$workTitle is Started.")
                    ework_action_text.text = "Stop"
                    chronometer1.setBase(SystemClock.elapsedRealtime())
                    chronometer1.start()
                    isWorking = true


                    eWork.startTime = startTime
                    eWork.stopTime = System.currentTimeMillis()
                    eWork.totalTime = System.currentTimeMillis() - startTime
                    eWork.workType = myData.eWorkType
                    eWork.workActionType = 2
                    eWork.loadingGPSLocation = gpsLocation

                    val insertID = db.insertEWork(eWork)
                    eWorkID = insertID.toInt()
                    myHelper.log("insertID:$insertID")
                    myHelper.log("eWorkID:$eWorkID")
                }

            }

            R.id.ework_offload_load -> {
                if (isWorking) {
                    if (eWorkID < 1) {
                        myHelper.toast("Please Restart Timer.")
                    } else {
                        stopDelay()
                        val eWork = EWork()
                        eWork.eWorkID = eWorkID
                        eWork.loadingGPSLocation = gpsLocation
                        val insertedID = db.insertEWorkOffLoad(eWork)
                        if (insertedID > 0) {
                            myHelper.toast("Load Saved Successfully.")

                            val offLoads = db.getEWorksOffLoads(eWorkID)
                            if (offLoads.size > 0) {
                                eoff_rv.visibility = View.VISIBLE
                                val aa = EOffLoadingAdapter(this@ETOffLoadingActivity, offLoads)
                                val layoutManager1 =
                                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                eoff_rv.layoutManager = layoutManager1
                                eoff_rv!!.setAdapter(aa)
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
                    myHelper.showStopMessage(startTime)
                } else {
                    myHelper.startHomeActivityByType(myData)
                }
            }
            R.id.ework_offload_finish -> {
                if (isWorking) {
                    myHelper.showStopMessage(startTime)
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
            var bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                myData = bundle!!.getSerializable("myData") as MyData
                myHelper.log("myData:$myData")

                et_offload_material.text = myData.loadingMaterial
                eWork.materialID = myData.loading_material_id

            }
        }
    }
}
