package app.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData

class ESelectWorkActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  lateinit var eswork_sidecasting: TextView
  lateinit var eswork_off_loading: TextView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_eselect_work, contentFrameLayout)
    
    eswork_sidecasting = findViewById(R.id.eswork_sidecasting)
    eswork_off_loading = findViewById(R.id.eswork_off_loading)
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    
    eswork_sidecasting.setOnClickListener(this)
    eswork_off_loading.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.eswork_sidecasting -> {
        val intent = Intent(this, ESideCastingActivity::class.java)
        myData.eWorkActionType = 1
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.eswork_off_loading -> {
        val intent = Intent(this, EOffLoadingActivity::class.java)
        myData.eWorkActionType = 2
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
    }
    
  }
}
