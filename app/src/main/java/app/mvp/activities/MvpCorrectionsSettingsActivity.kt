package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.others.Utils.tag
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_mvp_corrections_settings.*

class MvpCorrectionsSettingsActivity : BaseActivity(), View.OnClickListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_corrections_settings, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(5).isChecked = true
    myHelper.setTag(tag)
    
    mvp_corrections_bluetooth_settings.setOnClickListener(this);
    mvp_corrections_receiver_settings.setOnClickListener(this);
    mvp_corrections_ntrip_settings.setOnClickListener(this);
    
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_corrections_bluetooth_settings -> {
        myHelper.log("mvp_corrections_bluetooth_settings")
        val intent = Intent(this, MvpBluetoothSettingsActivity::class.java)
        startActivity(intent)
      }
      R.id.mvp_corrections_receiver_settings -> {
        myHelper.log("mvp_corrections_receiver_settings")
      }
      R.id.mvp_corrections_ntrip_settings -> {
        myHelper.log("mvp_corrections_ntrip_settings")
      }
    }
  }
}