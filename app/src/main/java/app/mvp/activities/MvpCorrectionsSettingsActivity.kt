package app.mvp.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.others.Utils.tag

class MvpCorrectionsSettingsActivity : BaseActivity(), View.OnClickListener {
  lateinit var connect: Button
  var mBound = false
  var m_handler: Handler? = null
  val MESSAGE_PARAMS_MAP = 0
  val MESSAGE_SETTINGS_MAP = 1
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_corrections_settings, contentFrameLayout)
    connect = findViewById(R.id.connect)
    myHelper.setTag(tag)
    
    connect.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(6))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.connect -> {
        myHelper.log("mvp_corrections_bluetooth_settings")
      }
    }
  }
  
}