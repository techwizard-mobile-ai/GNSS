package app.mvp.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.others.MyEnum
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch


class MvpSurveysLabelsSettingsActivity : BaseActivity(), View.OnClickListener {
  private lateinit var adapter_labels: CustomGridLMachine
  private lateinit var adapter_labels_favorite: CustomGridLMachine
  private val tag = this::class.java.simpleName
  private lateinit var gv: GridView
  private lateinit var gv_favorite: GridView
  private var admin_file_type_id = 0;
  private var selected_color_alpha = 255
  private var selected_color_r = 16
  private var selected_color_g = 109
  private var selected_color_b = 20
  
  lateinit var mvp_survey_label_name: EditText
  lateinit var mvp_survey_label_search: EditText
  lateinit var settings_title: TextView
  lateinit var settings_back: Button
  lateinit var settings_save: Button
  lateinit var survey_label_hex_color: ImageView
  lateinit var mvp_survey_label_search_clear: ImageView
  lateinit var survey_label_type: RadioGroup
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(app.vsptracker.R.id.base_content_frame)
    layoutInflater.inflate(app.vsptracker.R.layout.activity_mvp_surveys_labels_settings, contentFrameLayout)
    
    mvp_survey_label_name = findViewById(R.id.mvp_survey_label_name)
    settings_title = findViewById(R.id.settings_title)
    settings_back = findViewById(R.id.settings_back)
    settings_save = findViewById(R.id.settings_save)
    survey_label_hex_color = findViewById(R.id.survey_label_hex_color)
    mvp_survey_label_search_clear = findViewById(R.id.mvp_survey_label_search_clear)
    survey_label_type = findViewById(R.id.survey_label_type)
    mvp_survey_label_search = findViewById(R.id.mvp_survey_label_search)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      settings_title.text = myData.name
    }
    
    myHelper.log("labels:${db.getAdminMvpSurveysLabels()}")
    myHelper.log("favorite labels:${db.getAdminMvpSurveysLabels(2)}")
    
    gv = findViewById<GridView>(app.vsptracker.R.id.survey_labels_gridview)
    gv_favorite = findViewById<GridView>(app.vsptracker.R.id.survey_labels_gridview_favorite)

//    val adapter = CustomGridLMachine(this@MvpSurveysLabelsSettingsActivity, db.getAdminMvpSurveysLabels(), 1)
//    gv.adapter = CustomGridLMachine(this@MvpSurveysLabelsSettingsActivity, db.getAdminMvpSurveysLabels(3), 2) // get only which are not favorite
//    gv_favorite.adapter = CustomGridLMachine(this@MvpSurveysLabelsSettingsActivity, db.getAdminMvpSurveysLabels(2), 3) // get only favorite labels
    refreshRv()
    
    settings_back.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    settings_save.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    survey_label_hex_color.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    mvp_survey_label_search_clear.setOnClickListener(this@MvpSurveysLabelsSettingsActivity)
    
    survey_label_type.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.survey_label_type_point -> {
          admin_file_type_id = MyEnum.ADMIN_FILE_TYPE_TAPU_SURVEY_POINT
        }
        R.id.survey_label_type_line -> {
          admin_file_type_id = MyEnum.ADMIN_FILE_TYPE_TAPU_SURVEY_LINE
        }
      }
    })
    
    mvp_survey_label_search.addTextChangedListener(object : TextWatcher {
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Call back the Adapter with current character to Filter
        adapter_labels.filter!!.filter(s.toString())
        adapter_labels_favorite.filter!!.filter(s.toString())
      }
      
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun afterTextChanged(s: Editable) {}
    })
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(5))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.settings_back -> {
        finish()
      }
      R.id.mvp_survey_label_search_clear -> {
        mvp_survey_label_search.setText("")
        refreshRv()
      }
      R.id.survey_label_hex_color -> {
        
        MaterialColorPickerDialog
          .Builder(this@MvpSurveysLabelsSettingsActivity) // Pass Activity Instance
          .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
//          .setColorShape(ColorShape.CIRCLE) // Or ColorShape.CIRCLE
//          .setColorSwatch(ColorSwatch._300) // Default ColorSwatch._500
          .setColorSwatch(ColorSwatch.A400) // Default ColorSwatch._500
          .setDefaultColor("") // Pass Default Color
          .setColorRes(resources.getIntArray(R.array.colors_array))
//          .setColorRes(resources.getIntArray(R.array.colors_array_all))
          .setColorListener { color, colorHex ->
            selected_color_alpha = Color.alpha(color)
            selected_color_r = Color.red(color)
            selected_color_g = Color.green(color)
            selected_color_b = Color.blue(color)
            survey_label_hex_color.setBackgroundColor(color)
            myHelper.log("color:$color")
            myHelper.log("colorHex:$colorHex")
            myHelper.log("Alpha:" + Integer.toString(Color.alpha(color)))
            myHelper.log("Red:" + Integer.toString(Color.red(color)))
            myHelper.log("Green:" + Integer.toString(Color.green(color)))
            myHelper.log("Blue:" + Integer.toString(Color.blue(color)))
            myHelper.log("Pure Hex:" + Integer.toHexString(color))
            myHelper.log("#Hex no alpha:" + String.format("#%06X", 0xFFFFFF and color))
            myHelper.log("#Hex with alpha:" + String.format("#%08X", -0x1 and color))
          }
          .show()

//        val cp = ColorPicker(this@MvpSurveysLabelsSettingsActivity, selected_color_alpha, selected_color_r, selected_color_g, selected_color_b)
//        cp.show();
//        cp.enableAutoClose();
//        cp.setCallback { color ->
//          selected_color_alpha = Color.alpha(color)
//          selected_color_r = Color.red(color)
//          selected_color_g = Color.green(color)
//          selected_color_b = Color.blue(color)
//          myHelper.log("color:" + color)
//          myHelper.log("Alpha:" + Integer.toString(Color.alpha(color)))
//          myHelper.log("Red:" + Integer.toString(Color.red(color)))
//          myHelper.log("Green:" + Integer.toString(Color.green(color)))
//          myHelper.log("Blue:" + Integer.toString(Color.blue(color)))
//          myHelper.log("Pure Hex:" + Integer.toHexString(color))
//          myHelper.log("#Hex no alpha:" + String.format("#%06X", 0xFFFFFF and color))
//          myHelper.log("#Hex with alpha:" + String.format("#%08X", -0x1 and color))
//          survey_label_hex_color.setBackgroundColor(color)
//          cp.dismiss();
//        }
      }
      R.id.settings_save -> {
        val name = mvp_survey_label_name.text.toString()
        myHelper.log("save:$name - $admin_file_type_id")
        
        when {
          name.isEmpty() -> myHelper.showErrorDialog("Empty label!", "Please provide unique survey label.")
          db.getAdminMvpSurveysLabel(2, name).id > 0 -> myHelper.showErrorDialog("Duplicate entry", "Label $name is already created by Admin, please provide unique label name.")
          admin_file_type_id == 0 -> myHelper.showErrorDialog("Label type", "Please select Point or Line label type.")
          else -> {
            val list = ArrayList<MyData>()
            val datum = MyData()
            
            val color = Color.argb(selected_color_alpha, selected_color_r, selected_color_g, selected_color_b)
            val color_hex = String.format("#%06X", 0xFFFFFF and color)
//            datum.id =
            datum.org_id = myHelper.getOrgID()
            datum.user_id = myHelper.getUserID()
            datum.name = name
            datum.admin_surveys_labels_type_id = 3 //1 created by admin, 2 created by orgs, 3 created by user
            datum.admin_file_type_id = admin_file_type_id
//              datum.file_description =
            datum.color_hex = color_hex
            datum.color_rgb = "{\"r\":$selected_color_r,\"g\":$selected_color_g,\"b\":$selected_color_b,\"a\":$selected_color_alpha}"
            datum.color = "{\"hex\":\"$color_hex\",\"rgb\":{\"r\":$selected_color_r,\"g\":$selected_color_g,\"b\":$selected_color_b,\"a\":$selected_color_alpha}}"
            datum.loadingGPSLocation = gpsLocation
            datum.isSync = 1
            datum.status = 1
            datum.isDeleted = 0
//              datum.created_at =
//              datum.updated_at =
            myHelper.log("datum:$datum")
            list.add(datum)
            db.insertAdminMvpSurveysLabels(list)
            myHelper.toast("Label created successfully.")
            mvp_survey_label_name.setText("")
            refreshRv()
          }
        }
      }
    }
  }
  
  fun refreshRv() {
    gv.adapter = CustomGridLMachine(this@MvpSurveysLabelsSettingsActivity, db.getAdminMvpSurveysLabels(3), 2) // get only which are not favorite
    adapter_labels = gv.adapter as CustomGridLMachine
    gv_favorite.adapter = CustomGridLMachine(this@MvpSurveysLabelsSettingsActivity, db.getAdminMvpSurveysLabels(2), 3) // get only favorite labels
    adapter_labels_favorite = gv_favorite.adapter as CustomGridLMachine
  }
  
  fun updateFavoriteUnfavorite(datum: Material) {
    val myData1 = MyData()
    myData1.id = datum.id
    myData1.is_favorite = datum.is_favorite
    db.updateAdminMvpSurveysLabels(1, myData1)
    refreshRv()
  }
  
}