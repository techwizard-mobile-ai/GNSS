package com.lysaan.malik.vsptracker.activities.excavator
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.adapters.EOffLoadingAdapter

import com.lysaan.malik.vsptracker.others.Data
import com.lysaan.malik.vsptracker.others.EWork
import kotlinx.android.synthetic.main.activity_eoff_loading.*

class EOffLoadingActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private lateinit var workTitle :String
    private var isWorking  = false
    private var eWorkID = 0
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eoff_loading, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        when(data.eWorkType){
            1 ->{ workTitle = "General Digging (Loading)"}
            2 ->{ workTitle = "Trenching (Loading)"}
        }

        ework_title.text = workTitle

        ework_offload_action.setOnClickListener(this)
        ework_offload_load.setOnClickListener(this)
        ework_offload_home.setOnClickListener(this)
        ework_offload_finish.setOnClickListener(this)

    }
    override fun onBackPressed() {

        if(isWorking){
            helper.showStopMessage(startTime)
        }else{
            super.onBackPressed()
            finish()
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){

            R.id.ework_offload_action-> {
                if(isWorking){

                    val eWork = EWork()
                    eWork.ID = eWorkID
                    eWork.startTime = startTime

                    val updatedID = db.updateEWork(eWork)
                    eWorkID = updatedID
                    helper.log("updatedID :$updatedID ")
//
                    if(updatedID > 0){
                        helper.toast("$workTitle is Stopped.\n" +
                                "Data Saved Successfully.\n" +
                                "Work Duration : ${helper.getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                                "Work Duration : ${helper.getTotalTimeMintues(startTime)} (Minutes)")
                        ework_action_text.text = "Start"
                        chronometer1.stop()
                        isWorking = false
                        eWorkID = 0
                    }else{
                        helper.toast("Data Not Saved.")
                        isWorking = false
                    }


                }else{
                    startTime = System.currentTimeMillis()
                    helper.toast("$workTitle is Started.")
                    ework_action_text.text = "Stop"
                    chronometer1.setBase(SystemClock.elapsedRealtime())
                    chronometer1.start()
                    isWorking = true

                    val eWork = EWork()
                    eWork.startTime = startTime
                    eWork.stopTime = System.currentTimeMillis()
                    eWork.totalTime = System.currentTimeMillis() - startTime
                    eWork.workType = data.eWorkType
                    eWork.workActionType = 2

                    val insertID = db.insertEWork(eWork)
                    eWorkID = insertID.toInt()
                    helper.log("insertID:$insertID")
                    helper.log("eWorkID:$eWorkID")
                }

            }

            R.id.ework_offload_load ->{
                if(isWorking){
                    if(eWorkID <1){
                        helper.toast("Please Restart Timer.")
                    }else{
                        val eWork = EWork()
                        eWork.eWorkID = eWorkID
                        val insertedID = db.insertEWorkOffLoad(eWork)
                        if(insertedID > 0){
                            helper.toast("Load Saved Successfully.")

                            val offLoads = db.getEWorksOffLoads(eWorkID)
                            if(offLoads.size > 0){
                                eoff_rv.visibility = View.VISIBLE
                                val aa = EOffLoadingAdapter(this@EOffLoadingActivity,offLoads)
                                val layoutManager1 = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                                eoff_rv.layoutManager = layoutManager1
                                eoff_rv!!.setAdapter(aa)
                            }else{
                                eoff_rv.visibility = View.INVISIBLE
                            }


                        }else{
                            helper.toast("Load Not Saved. Please Try again.")

                        }
                    }
                }else{
                    helper.toast("Timer is Stopped.\nPlease start Timer First.")}
            }
            R.id.ework_offload_home-> {

                helper.log("Loads:${db.getEWorksOffLoads(eWorkID)}")

                if(isWorking){
                    helper.showStopMessage(startTime)
                }else{
                    helper.startHomeActivityByType(data)
                }
            }
            R.id.ework_offload_finish-> {
                if(isWorking){
                    helper.showStopMessage(startTime)
                }else{
                    val intent = Intent(this, HourMeterStopActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }
}
