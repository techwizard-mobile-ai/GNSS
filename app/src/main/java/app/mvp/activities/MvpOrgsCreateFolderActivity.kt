package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_mvp_orgs_create_folder.*

class MvpOrgsCreateFolderActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_create_folder, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    back.setOnClickListener(this)
    create.setOnClickListener(this)
  }
  
  override fun onClick(view: View?) {
    
    when (view!!.id) {
      R.id.back -> {
        finish()
      }
      R.id.create -> {
        val intent = Intent(this, MvpStartDataCollectionActivity::class.java)
        myData.mvp_orgs_folder_id = 0
        myData.mvp_orgs_folder_name = folder_name.text.toString()
        myHelper.setLastJourney(myData)
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
    }
  }
}