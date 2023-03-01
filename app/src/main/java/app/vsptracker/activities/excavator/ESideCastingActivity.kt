package app.vsptracker.activities.excavator

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData

class ESideCastingActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  private lateinit var workTitle: String
  private var isWorking = false
  private var startTime1 = 0L
  private lateinit var eWork: EWork
  lateinit var ework_title: TextView
  lateinit var ework_action_text: TextView
  lateinit var ework_action: FrameLayout
  lateinit var ework_home: Button
  lateinit var ework_finish: Button
  lateinit var chronometer1: Chronometer
  lateinit var ework_action_fab: com.google.android.material.floatingactionbutton.FloatingActionButton
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_eside_casting, contentFrameLayout)
    
    ework_title = findViewById(R.id.ework_title)
    ework_action = findViewById(R.id.ework_action)
    ework_home = findViewById(R.id.ework_home)
    ework_finish = findViewById(R.id.ework_finish)
    ework_action_fab = findViewById(R.id.ework_action_fab)
    ework_action_text = findViewById(R.id.ework_action_text)
    chronometer1 = findViewById(R.id.chronometer1)
    
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
      myHelper.showStopMessage(startTime1)
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
                    "Work Duration : ${myHelper.getTotalTimeVSP(startTime1)} (VSP Meter).\n" +
                    "Work Duration : ${myHelper.getTotalTimeMinutes(startTime1)} (Minutes)"
          )
          ework_action_fab.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorPrimary)
          )
          ework_action_text.text = getString(R.string.start)
          chronometer1.stop()
          isWorking = false
          
        } else {
          startTime1 = System.currentTimeMillis()
          myHelper.toast("$workTitle is Started.")
          ework_action_fab.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.black)
          )
          ework_action_text.text = getString(R.string.stop)
          chronometer1.base = SystemClock.elapsedRealtime()
          chronometer1.start()
          eWork.startTime = startTime1
          eWork.loadingGPSLocation = gpsLocation
          eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
          isWorking = true
          myHelper.log("$workTitle is Started.")
          myHelper.log(eWork.toString())
        }
        
      }
      R.id.ework_home -> {
        if (isWorking) {
          myHelper.showStopMessage(startTime1)
        } else {
          myHelper.startHomeActivityByType(myData)
        }
      }
      R.id.ework_finish -> {
        if (isWorking) {
          myHelper.showStopMessage(startTime1)
        } else {
          val intent = Intent(this, HourMeterStopActivity::class.java)
          startActivity(intent)
        }
      }
    }
  }
  
}
