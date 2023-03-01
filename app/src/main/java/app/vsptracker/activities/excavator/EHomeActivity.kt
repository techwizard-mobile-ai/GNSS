package app.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.common.LocationActivity
import app.vsptracker.apis.trip.MyData

class EHomeActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  lateinit var ehome_logout: TextView
  lateinit var ehome_loading: TextView
  lateinit var ehome_trenching: TextView
  lateinit var ehome_digging: TextView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_ehome, contentFrameLayout)
    
    ehome_logout = findViewById(R.id.ehome_logout)
    ehome_loading = findViewById(R.id.ehome_loading)
    ehome_trenching = findViewById(R.id.ehome_trenching)
    ehome_digging = findViewById(R.id.ehome_digging)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    ehome_logout.setOnClickListener(this)
    ehome_loading.setOnClickListener(this)
    ehome_trenching.setOnClickListener(this)
    ehome_digging.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {

//            Production Digging
      R.id.ehome_loading -> {
        val intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      
      R.id.ehome_trenching -> {
//                val intent = Intent(this, ESelectWorkActivity::class.java)
//                intent.putExtra("myData", myData)
//                startActivity(intent)
        
        val intent = Intent(this, ETOffLoadingActivity::class.java)
        myData.eWorkType = 2
        myData.eWorkActionType = 2
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      
      R.id.ehome_digging -> {
        val intent = Intent(this, ESelectWorkActivity::class.java)
        myData.eWorkType = 1
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      
      R.id.ehome_logout -> {
        myHelper.logout(this)
      }
    }
  }
  
  
}
