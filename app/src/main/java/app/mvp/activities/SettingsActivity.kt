package app.mvp.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum.Companion.SETTINGS_TYPE_MVP_SCAN
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  private var mInterval: Long = 1000 // 1 seconds by default, can be changed later
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_settings, contentFrameLayout)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      settings_title.text = myData.name
      timer.text = (myData.timer_interval.toDouble() / 1000).toString()
      mInterval = myData.timer_interval
    }
    
    when (myData.type) {
      SETTINGS_TYPE_MVP_SCAN -> {
        settings_timer_layout.visibility = View.VISIBLE
        settings_timer_title.visibility = View.VISIBLE
      }
    }
//        Glide.with(this@SettingsActivity).load(ContextCompat.getDrawable(this@SettingsActivity, R.drawable.widget_logo)).into(mvp_main_taputapu)
//        Glide.with(this@SettingsActivity).load(ContextCompat.getDrawable(this@SettingsActivity, R.drawable.hub_logo_complete)).into(mvp_main_portal)
    
    timer_minus.setOnClickListener(this@SettingsActivity)
    timer_plus.setOnClickListener(this@SettingsActivity)
    settings_back.setOnClickListener(this@SettingsActivity)
    settings_save.setOnClickListener(this@SettingsActivity)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(5))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      
      R.id.timer_minus -> {
        
        val str: String = timer.text.toString()
        var timer_value = 0.5
        try {
          timer_value = str.toDouble()
        }
        catch (e: java.lang.Exception) {
          myHelper.log("Exception Timer: " + e.message)
        }
        if (timer_value > 0.9) {
          mInterval -= 500
          timer.text = (timer_value - 0.5).toString()
          myHelper.log("mInterval:$mInterval")
        } else {
          timer.text = timer_value.toString()
          myHelper.log("mInterval:$mInterval")
        }
      }
      
      R.id.timer_plus -> {
        
        val str: String = timer.text.toString()
        var timer_value = 0.5
        try {
          timer_value = str.toDouble()
        }
        catch (e: java.lang.Exception) {
          myHelper.log("Exception Timer: " + e.message)
        }
        if (timer_value < 5) {
          timer.text = (timer_value + 0.5).toString()
          mInterval += 500
          myHelper.log("mInterval:$mInterval")
        } else {
          timer.text = timer_value.toString()
          myHelper.log("mInterval:$mInterval")
        }
      }
      
      R.id.settings_back -> {
        finish()
      }
      R.id.settings_save -> {
        
        myData.timer_interval = mInterval
        intent.putExtra("myData", myData)
        setResult(Activity.RESULT_OK, intent)
        finish()
      }
      
    }
  }
}