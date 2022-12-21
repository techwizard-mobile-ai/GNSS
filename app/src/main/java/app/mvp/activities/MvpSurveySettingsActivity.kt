package app.mvp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_survey_settings.*
import kotlinx.android.synthetic.main.dialog_input.view.*


class MvpSurveySettingsActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_survey_settings, contentFrameLayout)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      settings_title.text = myData.name
    }
    
    settings_back.setOnClickListener(this@MvpSurveySettingsActivity)
    survey_settings_labels.setOnClickListener(this@MvpSurveySettingsActivity)
    survey_settings_antenna_height.setOnClickListener(this@MvpSurveySettingsActivity)
    survey_settings_point_attribute.setOnClickListener(this@MvpSurveySettingsActivity)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(5))
    survey_settings_antenna_height.text = "Antenna Height: ${myHelper.roundToN(myHelper.getLastJourney().survey_antenna_height, 3)} m"
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.survey_settings_labels -> {
//        finish()
        val intent = Intent(this, MvpSurveysLabelsSettingsActivity::class.java)
        myData.name = "Survey Labels Settings"
        myData.type = MyEnum.SETTINGS_TYPE_MVP_SURVEY
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.settings_back -> {
        finish()
      }
      R.id.settings_save -> {
        intent.putExtra("myData", myData)
        setResult(Activity.RESULT_OK, intent)
        finish()
      }
      R.id.survey_settings_antenna_height -> {
        myHelper.log("last Journey:${myHelper.getLastJourney()}")
        showInputDialog(1, this)
      }
      R.id.survey_settings_point_attribute -> {
        myHelper.log("last Journey:${myHelper.getLastJourney()}")
        showInputDialog(2, this)
      }
    }
  }
  
  
  private fun showInputDialog(type: Int, context: Context) {
    
    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null)
    var title = "Antenna Height"
    var explanation = "Please enter valid antenna height in m."
    when (type) {
      1 -> {
        title = "Antenna Height"
        explanation = "Please enter valid antenna height in meters up to three decimal places."
        mDialogView.mvp_survey_dialog_input.hint = "Please enter three decimal value for antenna height"
        mDialogView.mvp_survey_dialog_input.setText(myHelper.roundToN(myHelper.getLastJourney().survey_antenna_height, 3).toString())
      }
      2 -> {
        title = "Point Attribute"
        explanation = "Please enter point attribute for next survey point."
        mDialogView.mvp_survey_dialog_input.hint = "Please enter point attribute text"
        mDialogView.mvp_survey_dialog_input.inputType = InputType.TYPE_CLASS_TEXT
        mDialogView.mvp_survey_dialog_input.setText(myHelper.getLastJourney().survey_file_description)
      }
    }
    
    mDialogView.error_title.text = title
    if (explanation.isNotBlank()) {
      mDialogView.error_explanation.text = explanation
      mDialogView.error_explanation.visibility = View.VISIBLE
    }
    
    val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
    val mAlertDialog = mBuilder.show()
    mAlertDialog.setCancelable(true)
    
    val window = mAlertDialog.window
    val wlp = window!!.attributes
    
    wlp.gravity = Gravity.CENTER
    window.attributes = wlp
    
    mDialogView.error_cancel.setOnClickListener {
      mAlertDialog.dismiss()
    }
    
    mDialogView.error_ok.setOnClickListener {
      when {
        type == 1 && myHelper.isDecimal(mDialogView.mvp_survey_dialog_input.text.toString()) -> {
          val lastJourney = myHelper.getLastJourney()
          lastJourney.survey_antenna_height = mDialogView.mvp_survey_dialog_input.text.toString().toDouble()
          myHelper.setLastJourney(lastJourney)
          survey_settings_antenna_height.text = "Antenna Height: ${myHelper.roundToN(myHelper.getLastJourney().survey_antenna_height, 3)} m"
          mAlertDialog.dismiss()
        }
        type == 2 -> {
          val lastJourney = myHelper.getLastJourney()
          lastJourney.survey_file_description = mDialogView.mvp_survey_dialog_input.text.toString()
          myHelper.setLastJourney(lastJourney)
          mAlertDialog.dismiss()
        }
        else -> {
          myHelper.toast("Please enter valid antenna height in m.")
        }
      }
    }
  }
}