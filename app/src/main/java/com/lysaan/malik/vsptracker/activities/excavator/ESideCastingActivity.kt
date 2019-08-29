package com.lysaan.malik.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.classes.EWork
import kotlinx.android.synthetic.main.activity_eside_casting.*

class ESideCastingActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    private lateinit var workTitle: String
    private var isWorking = false
    private var startTime = 0L
    private lateinit var eWork: EWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eside_casting, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        when (data.eWorkType) {
            1 -> {
                workTitle = "General Digging (Side Casting)"
            }
            2 -> {
                workTitle = "Trenching (Side Casting)"
            }
        }

        eWork = EWork()
        ework_title.text = workTitle

        ework_action.setOnClickListener(this)
        ework_home.setOnClickListener(this)
        ework_finish.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        startGPS()
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }


    override fun onBackPressed() {

        if (isWorking) {
            helper.showStopMessage(startTime)
        } else {
            super.onBackPressed()
            finish()
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ework_action -> {
                if (isWorking) {


                    eWork.workType = data.eWorkType
                    eWork.workActionType = 1

                    eWork.unloadingGPSLocation = gpsLocation

                    val insertID = db.insertEWork(eWork)
                    helper.log("insertID:$insertID")

                    if (insertID > 0) {
                        helper.toast(
                            "$workTitle is Stopped.\n" +
                                    "Data Saved Successfully.\n" +
                                    "Work Duration : ${helper.getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                                    "Work Duration : ${helper.getTotalTimeMintues(startTime)} (Minutes)"
                        )
                        ework_action_text.text = "Start"

                        chronometer1.stop()

                        isWorking = false
                    } else {
                        helper.toast("Data Not Saved.")
                        isWorking = false
                    }


                } else {
                    startTime = System.currentTimeMillis()
                    helper.toast("$workTitle is Started.")
                    ework_action_text.text = "Stop"
                    chronometer1.setBase(SystemClock.elapsedRealtime())
                    chronometer1.start()
                    eWork.startTime = startTime
                    eWork.loadingGPSLocation = gpsLocation
                    isWorking = true
                }

            }
            R.id.ework_home -> {
                if (isWorking) {
                    helper.showStopMessage(startTime)
                } else {
                    helper.startHomeActivityByType(data)
                }
            }
            R.id.ework_finish -> {
                if (isWorking) {
                    helper.showStopMessage(startTime)
                } else {
                    val intent = Intent(this, HourMeterStopActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}
