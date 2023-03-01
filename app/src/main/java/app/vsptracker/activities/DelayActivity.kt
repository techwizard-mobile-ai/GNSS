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
import app.vsptracker.apis.delay.EWork
import app.vsptracker.classes.GPSLocation

class DelayActivity : BaseActivity(),
                      View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  private lateinit var eWork: EWork
  lateinit var day_works_chronometer: Chronometer
  lateinit var day_work_title: TextView
  lateinit var day_works_action_text: TextView
  lateinit var day_works_action: FrameLayout
  
  lateinit var day_works_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_delay, contentFrameLayout)
    
    day_works_chronometer = findViewById(R.id.day_works_chronometer)
    day_work_title = findViewById(R.id.day_work_title)
    day_works_action_text = findViewById(R.id.day_works_action_text)
    day_works_button = findViewById(R.id.day_works_button)
    day_works_action = findViewById(R.id.day_works_action)
    
    myHelper.setTag(tag)
    eWork = EWork()
//        startGPS()
    
    var startLocation = gpsLocation
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      startLocation = bundle.getSerializable("gpsLocation") as GPSLocation
    }
    
    if (!myHelper.isDelayStarted()) {
      
      day_works_chronometer.base = SystemClock.elapsedRealtime()
      day_works_chronometer.start()
      day_work_title.text = getString(R.string.waiting_for_load_started)
      day_works_action_text.text = getString(R.string.stop)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
      myHelper.startDelay(startLocation)
      
    } else {
      
      val currentTime = System.currentTimeMillis()
      val meter = myHelper.getMeter()
      val startTime = meter.delayStartTime
      val totalTime = (currentTime - startTime)
      day_works_chronometer.base = SystemClock.elapsedRealtime() - totalTime
      day_works_chronometer.start()
      day_works_action_text.text = getString(R.string.stop)
      day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
    }
    
    day_works_action.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(1))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      
      R.id.day_works_action -> {
        if (myHelper.isDelayStarted()) {
          stopDelay()
//                    if(dbID>0){
          day_work_title.text = getString(R.string.wait_for_load)
          day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
          day_works_action_text.text = getString(R.string.start)
          day_works_chronometer.stop()
//                        myHelper.startHomeActivityByType(MyData())
//                        finish()
//                        onBackPressed();
//                    }
        
        } else {
          
          day_works_chronometer.base = SystemClock.elapsedRealtime()
          day_works_chronometer.start()
          
          day_work_title.text = getString(R.string.waiting_for_load_started)
          day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
          day_works_action_text.text = getString(R.string.stop)
          val gpslocation = gpsLocation
          myHelper.startDelay(gpslocation)
          
        }
      }
    }
    
  }
  
}
