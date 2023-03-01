package app.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.common.MaterialActivity
import app.vsptracker.activities.common.UnloadTaskActivity
import app.vsptracker.activities.excavator.ESideCastingActivity
import app.vsptracker.others.MyHelper

class SHomeActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  lateinit var shome_load: FrameLayout
  lateinit var shome_unload: FrameLayout
  lateinit var shome_logout: Button
  lateinit var shome_trimming: Button
  lateinit var shome_load_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  lateinit var shome_unload_button: com.google.android.material.floatingactionbutton.FloatingActionButton
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_shome, contentFrameLayout)
    
    shome_load = findViewById(R.id.shome_load)
    shome_load_button = findViewById(R.id.shome_load_button)
    shome_unload_button = findViewById(R.id.shome_unload_button)
    shome_unload = findViewById(R.id.shome_unload)
    shome_logout = findViewById(R.id.shome_logout)
    shome_trimming = findViewById(R.id.shome_trimming)
    
    myHelper = MyHelper(tag, this)
    
    myData = myHelper.getLastJourney()
//        myHelper.log("myData:$myData")
    
    when (myHelper.getNextAction()) {
      0, 2 -> {
        myHelper.setToDoLayout(shome_load_button)
        shome_load.visibility = View.VISIBLE
      }
      1, 3 -> {
        myHelper.setToDoLayout(shome_unload_button)
        shome_unload.visibility = View.VISIBLE
      }
    }
    
    shome_logout.setOnClickListener(this)
    shome_load.setOnClickListener(this)
    shome_unload.setOnClickListener(this)
    shome_trimming.setOnClickListener(this)
    
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      
      R.id.shome_trimming -> {
        val intent = Intent(this, ESideCastingActivity::class.java)
        myData.eWorkType = 3
        myData.eWorkActionType = 1
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.shome_load -> {
        val intent = Intent(this, MaterialActivity::class.java)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.shome_unload -> {
//                val intent = Intent(this, LocationActivity::class.java)
        val intent = Intent(this, UnloadTaskActivity::class.java)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.shome_logout -> {
        myHelper.logout(this)
      }
    }
    
  }
}
