package app.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
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

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        when (myData.eWorkType) {
            1 -> {
                workTitle = "General Digging (Side Casting)"
            }
            2 -> {
                workTitle = "Trenching (Side Casting)"
            }
            3 ->{
                workTitle = "Scraper Trimming"
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
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
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
            R.id.ework_action -> {
                stopDelay()
                if (isWorking) {
                    eWork.workType = myData.eWorkType
                    eWork.workActionType = 1
                    eWork.orgId = myHelper.getLoginAPI().org_id
                    eWork.operatorId = myHelper.getOperatorAPI().id
                    eWork.unloadingGPSLocation = gpsLocation

                    val time = System.currentTimeMillis()
                    eWork.time = time.toString()
                    eWork.stopTime = System.currentTimeMillis()
                    eWork.totalTime = eWork.stopTime - eWork.startTime

                    if(myHelper.isDailyModeStarted()){
                        eWork.isDaysWork = 1
                    }else {
                        eWork.isDaysWork = 0
                    }

                    myHelper.log("BeforePushSideCasting:$eWork")
                    if(myHelper.isOnline()){
                        pushSideCasting(eWork)
                    }

                    val insertID = db.insertEWork(eWork)
                    myHelper.log("insertID:$insertID")

                    if (insertID > 0) {
                        myHelper.toast(
                                "$workTitle is Stopped.\n" +
                                        "MyData Saved Successfully.\n" +
                                        "Work Duration : ${myHelper.getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                                        "Work Duration : ${myHelper.getTotalTimeMintues(startTime)} (Minutes)"
                        )
                        ework_action_text.text = "Start"
                        chronometer1.stop()
                        isWorking = false
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
                    eWork.startTime = startTime
                    eWork.loadingGPSLocation = gpsLocation
                    eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
                    isWorking = true
                    myHelper.log("$workTitle is Started.")
                    myHelper.log(eWork.toString())
                }

            }
            R.id.ework_home -> {
                if (isWorking) {
                    myHelper.showStopMessage(startTime)
                } else {
                    myHelper.startHomeActivityByType(myData)
                }
            }
            R.id.ework_finish -> {
                if (isWorking) {
                    myHelper.showStopMessage(startTime)
                } else {
                    val intent = Intent(this, HourMeterStopActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}
