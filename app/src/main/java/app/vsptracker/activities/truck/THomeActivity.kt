package app.vsptracker.activities.truck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.common.LMachineActivity
import app.vsptracker.activities.common.UnloadTaskActivity
import app.vsptracker.others.MyHelper

class THomeActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  lateinit var thome_logout: Button
  lateinit var thome_load: FrameLayout
  lateinit var thome_unload: FrameLayout
  lateinit var thome_load_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  lateinit var thome_unload_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_thome, contentFrameLayout)
    
    thome_load_button = findViewById(R.id.thome_load_button)
    thome_unload_button = findViewById(R.id.thome_unload_button)
    thome_load = findViewById(R.id.thome_load)
    thome_unload = findViewById(R.id.thome_unload)
    thome_logout = findViewById(R.id.thome_logout)
    
    myHelper = MyHelper(tag, this)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            myData = bundle!!.getSerializable("myData") as MyData
//            myHelper.log("myData:$myData")
//        }
    
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    
    when (myHelper.getNextAction()) {
      0, 2 -> {
        myHelper.setToDoLayout(thome_load_button)
        thome_load.visibility = View.VISIBLE
      }
      1, 3 -> {
        myHelper.setToDoLayout(thome_unload_button)
        thome_unload.visibility = View.VISIBLE
      }
    }

//        myHelper.log("updated_at:${db.getCurrentOrgsMap()?.updated_at}")
//        myHelper.log("timestamp:${myHelper.getTimestampFromDate(db.getCurrentOrgsMap()!!.updated_at)}")
//        myHelper.log("currentTime:${System.currentTimeMillis()}")
//        myHelper.log("duration:${myHelper.getFormattedTime(System.currentTimeMillis() - db.getCurrentOrgsMap()!!.time.toLong())}")
    thome_logout.setOnClickListener(this)
    thome_load.setOnClickListener(this)
    thome_unload.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
//
//    override fun onPause() {
//        super.onPause()
//        stopGPS()
//    }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.thome_load -> {
        val intent = Intent(this, LMachineActivity::class.java)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.thome_unload -> {
        val intent = Intent(this, UnloadTaskActivity::class.java)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.thome_logout -> {

//                myHelper.log("LocationLat:${latitude}")
//                myHelper.log("LocationLongg:${longitude}")
//                myHelper.log("Loads:${db.getTrips()}")
        
        myHelper.logout(this)
      }
    }
    
  }
  
}
