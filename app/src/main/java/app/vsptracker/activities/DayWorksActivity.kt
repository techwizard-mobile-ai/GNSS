package app.vsptracker.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_day_works.*

class DayWorksActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  private var isDailyModeStart = false
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_day_works, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(4).isChecked = true
    
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
