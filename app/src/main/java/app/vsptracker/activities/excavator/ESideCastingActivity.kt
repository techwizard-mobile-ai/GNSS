package app.vsptracker.activities.excavator

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_eside_casting.*

class ESideCastingActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName

    private lateinit var workTitle: String
    private var isWorking = false
    private var startTime = 0L
    private lateinit var eWork: EWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_eside_casting, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(tag)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
        }

        myHelper.log("$myData")
        when (myData.eWorkType) {
            1 -> {
                workTitle = getString(R.string.general_digging_side_casting)
            }
            2 -> {
                workTitle = getString(R.string.trenching_side_casting)
            }
            3 -> {
                workTitle = getString(R.string.scraper_trimming)
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

                    val currentTime = System.currentTimeMillis()
                    eWork.time = currentTime.toString()
                    eWork.stopTime = currentTime
                    eWork.totalTime = eWork.stopTime - eWork.startTime
                    eWork.date = myHelper.getDate(currentTime)
                    eWork.isSync = 0
                    
                    if (myHelper.isDailyModeStarted()) {
                        eWork.isDayWorks = 1
                    } else {
                        eWork.isDayWorks = 0
                    }

                    myHelper.log("before$workTitle:$eWork")
                    myDataPushSave.pushInsertSideCasting(eWork)

                    myHelper.toast(
                        "$workTitle is Stopped.\n" +
                                "Data Saved Successfully.\n" +
                                "Work Duration : ${myHelper.getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                                "Work Duration : ${myHelper.getTotalTimeMinutes(startTime)} (Minutes)"
                    )
                    ework_action_fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.colorPrimary)
                    )
                    ework_action_text.text = getString(R.string.start)
                    chronometer1.stop()
                    isWorking = false
                    
                } else {
                    startTime = System.currentTimeMillis()
                    myHelper.toast("$workTitle is Started.")
                    ework_action_fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.black)
                    )
                    ework_action_text.text = getString(R.string.stop)
                    chronometer1.base = SystemClock.elapsedRealtime()
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
