package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_settings.*

class MvpSettingsActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  private var mInterval: Int = 1
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_settings, contentFrameLayout)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      settings_title.text = myData.name
    }
    
    timer.text = myHelper.getAppSettings().n_days_tasks.toString()
    mInterval = myHelper.getAppSettings().n_days_tasks
    
    settings_save.setOnClickListener(this@MvpSettingsActivity)
    settings_back.setOnClickListener(this@MvpSettingsActivity)
    timer_minus.setOnClickListener(this@MvpSettingsActivity)
    timer_plus.setOnClickListener(this@MvpSettingsActivity)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(13))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.settings_save -> {
        val appSettingsActivity = myHelper.getAppSettings()
        myHelper.log("mInterval:$mInterval")
        appSettingsActivity.n_days_tasks = mInterval
        myHelper.log("n_days_tasks:$appSettingsActivity.n_days_tasks")
        
        myHelper.setAppSettings(appSettingsActivity)
        myHelper.toast("Settings updated successfully.")
        val intent = Intent(this, MvpHomeActivity::class.java)
        startActivity(intent)
      }
      R.id.settings_back -> {
        val intent = Intent(this, MvpHomeActivity::class.java)
        startActivity(intent)
      }
      R.id.timer_minus -> {
        
        val str: String = timer.text.toString()
        var timer_value = 1
        try {
          timer_value = str.toInt()
        }
        catch (e: java.lang.Exception) {
          myHelper.log("Exception Timer: " + e.message)
        }
        myHelper.log("timer_value:$timer_value")
        when {
          timer_value in 2..30 -> {
            mInterval--
            timer.text = (timer_value - 1).toString()
//            myHelper.log("mInterval:$mInterval")
          }
          timer_value > 30 -> {
            mInterval--
            timer.text = (timer_value - 1).toString()
//            myHelper.log("mInterval:$mInterval")
//            myHelper.log("showWarning:$mInterval")
          }
        }
        myHelper.log("mInterval:$mInterval")
      }
      
      R.id.timer_plus -> {
        
        val str: String = timer.text.toString()
        var timer_value = 1
        try {
          timer_value = str.toInt()
        }
        catch (e: java.lang.Exception) {
          myHelper.log("Exception Timer: " + e.message)
        }
        myHelper.log("mInterval:$mInterval")
        when {
          timer_value > 30 -> {
            mInterval++
            timer.text = (timer_value + 1).toString()
//            myHelper.log("mInterval:$mInterval")
//            myHelper.log("showWarning:$mInterval")
          }
          timer_value in 1..30 -> {
            mInterval++
            timer.text = (timer_value + 1).toString()
//            myHelper.log("mInterval:$mInterval")
          }
        }
        myHelper.log("mInterval:$mInterval")
      }
    }
  }
}