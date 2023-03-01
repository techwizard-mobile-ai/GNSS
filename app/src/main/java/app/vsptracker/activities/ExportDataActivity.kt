package app.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum.Companion.ALL_DATA
import app.vsptracker.others.MyEnum.Companion.UNSYNC_DATA

class ExportDataActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  lateinit var activity_title: TextView
  lateinit var export_all_data_text: TextView
  lateinit var export_unsync_data_text: TextView
  lateinit var export_all_data: Button
  lateinit var export_unsync_data: Button
  lateinit var logout: Button
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_export_data, contentFrameLayout)
    
    activity_title = findViewById(R.id.activity_title)
    export_all_data_text = findViewById(R.id.export_all_data_text)
    export_unsync_data_text = findViewById(R.id.export_unsync_data_text)
    export_all_data = findViewById(R.id.export_all_data)
    export_unsync_data = findViewById(R.id.export_unsync_data)
    logout = findViewById(R.id.logout)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      activity_title.text = myData.name
    }
    
    export_all_data_text.text = "Total Entries : ${db.getTrips(ALL_DATA).size}"
    export_unsync_data_text.text = "Total Entries : ${db.getTrips(UNSYNC_DATA).size}"
    
    export_all_data.setOnClickListener(this)
    export_unsync_data.setOnClickListener(this)
    logout.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(14))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.export_all_data -> {
        db.exportVsptDb(ALL_DATA)
      }
      R.id.export_unsync_data -> {
        db.exportVsptDb(UNSYNC_DATA)
      }
      R.id.logout -> {
        myHelper.logout(this@ExportDataActivity)
      }
    }
  }
}