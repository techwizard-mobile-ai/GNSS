package app.mvp.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_mvp_surveys_labels_settings.*

class MvpSurveysLabelsSettingsActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_surveys_labels_settings, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      settings_title.text = myData.name
    }
    
    settings_back.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    settings_save.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.survey_settings_labels -> {
        finish()
      }
      R.id.settings_back -> {
        finish()
      }
      R.id.settings_save -> {
        intent.putExtra("myData", myData)
        setResult(Activity.RESULT_OK, intent)
        finish()
      }
    }
  }
  
}