package app.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.common.MaterialActivity
import app.vsptracker.activities.common.UnloadTaskActivity
import app.vsptracker.activities.excavator.ESideCastingActivity
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_shome.*

class SHomeActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_shome, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
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
