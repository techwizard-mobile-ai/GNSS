package app.vsptracker.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData

class DayWorksActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  private var isDailyModeStart = false
  lateinit var day_work_title: TextView
  lateinit var day_works_action_text: TextView
  lateinit var day_works_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  lateinit var day_works_chronometer: Chronometer
  lateinit var day_works_action: FrameLayout
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_day_works, contentFrameLayout)
    
    day_work_title = findViewById(R.id.day_work_title)
    day_works_button = findViewById(R.id.day_works_button)
    day_works_action_text = findViewById(R.id.day_works_action_text)
    day_works_chronometer = findViewById(R.id.day_works_chronometer)
    day_works_action = findViewById(R.id.day_works_action)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    
    if (myHelper.isDailyModeStarted()) {
      day_work_title.text = getString(R.string.stop_day_works_mode)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
      day_works_action_text.text = getString(R.string.stop)
      val currentTime = System.currentTimeMillis()
      val meter = myHelper.getMeter()
      val startTime = meter.dailyModeStartTime
      val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime
      
      day_works_chronometer.base = SystemClock.elapsedRealtime() - totalTime
      day_works_chronometer.start()
    } else {
      day_work_title.text = getString(R.string.start_day_works_mode)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
      day_works_action_text.text = getString(R.string.start)

//            Previously it was counting total Day Works Time but now as entries for each mode changed
//            are saved in database. No need to Count Previous Time for Days Works
//            val meter = myHelper.getMeter()
//            day_works_chronometer.base = SystemClock.elapsedRealtime() - meter.dailyModeTotalTime
      day_works_chronometer.base = SystemClock.elapsedRealtime()
    }
    day_works_action.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(4))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.day_works_action -> {
        
        myHelper.setLastJourney(MyData())
//                Before Day Works Mode is Changed, Insert Previous Hours into Database and Portal
        myDataPushSave.insertOperatorHour(gpsLocation)
        if (myHelper.isDailyModeStarted()) {
          day_work_title.text = getString(R.string.start_day_works_mode)
          day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
          day_works_action_text.text = getString(R.string.start)
          isDailyModeStart = false
          day_works_chronometer.stop()
          myHelper.stopDailyMode()
          myHelper.startHomeActivityByType(MyData())
          
        } else {

//                    day_works_chronometer.base = SystemClock.elapsedRealtime() - myHelper.getMeter().dailyModeTotalTime
          day_works_chronometer.base = SystemClock.elapsedRealtime()
          day_works_chronometer.start()
          
          day_work_title.text = getString(R.string.stop_day_works_mode)
          day_works_button.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.black)
          )
          day_works_action_text.text = getString(R.string.stop)
          isDailyModeStart = true
          myHelper.startDailyMode()
          myHelper.startHomeActivityByType(MyData())
        }
      }
    }
  }
}
