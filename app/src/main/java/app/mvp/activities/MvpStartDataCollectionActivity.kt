package app.mvp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_start_data_collection.*

private const val REQUEST_MACHINE = 1
private const val REQUEST_MATERIAL = 2
private const val REQUEST_LOCATION = 3
private const val REQUEST_WEIGHT = 4

class MvpStartDataCollectionActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      // There are no request codes
      val intent: Intent? = result.data
      myHelper.log("resultLauncher")
      
      val bundle: Bundle? = intent!!.extras
      if (bundle != null) {
        myData = bundle.getSerializable("myData") as MyData
        myHelper.log("onActivityResult----:$myData")
        mvp_load_project.text = myData.mvp_orgs_project_name
        mvp_load_folder.text = myData.mvp_orgs_files_name
        myData.isForLoadResult = false
        myHelper.setLastJourney(myData)
      }
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_start_data_collection, contentFrameLayout)
    
    myHelper.setTag(tag)
    
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    
    mvp_load_title.text = getString(R.string.data_collection_details)
    
    mvp_load_project.text = myData.mvp_orgs_project_name
    mvp_load_folder.text = myData.mvp_orgs_files_name
    
    mvp_load_project.setOnClickListener(this)
    mvp_load_folder.setOnClickListener(this)
    mvp_load_home.setOnClickListener(this)
    mvp_load_load.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(5))
  }
  
  override fun onClick(view: View?) {
    myData.trip0ID = System.currentTimeMillis().toString()
    
    when (view!!.id) {
      
      R.id.mvp_load_project -> {
        val intent = Intent(this, MvpOrgsProjectsActivity::class.java)
        myData.isForLoadResult = true
        intent.putExtra("myData", myData)
//                startActivityForResult(intent, REQUEST_MACHINE)
        resultLauncher.launch(intent)
      }
      
      R.id.mvp_load_folder -> {
        val intent = Intent(this, MvpOrgsFoldersActivity::class.java)
        myData.isForLoadResult = true
        intent.putExtra("myData", myData)
//                startActivityForResult(intent, REQUEST_MACHINE)
        resultLauncher.launch(intent)
      }
      
      R.id.mvp_load_home -> {
        myData = MyData()
        myHelper.setLastJourney(myData)
        val intent = Intent(this, MvpHomeActivity::class.java)
        startActivity(intent)
      }
      
      R.id.mvp_load_load -> {
        val intent = Intent(this, MvpSurveyHomeActivity::class.java)
        startActivity(intent)
      }
      
    }
  }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
//        super.onActivityResult(requestCode, resultCode, intent)
//
//        if (resultCode == Activity.RESULT_OK) {
//            val bundle: Bundle? = intent!!.extras
//            if (bundle != null) {
//                myData = bundle.getSerializable("myData") as MyData
//                myHelper.log("onActivityResult:$myData")
//                mvp_load_project.text = myData.mvp_orgs_project_name
//                mvp_load_folder.text = myData.mvp_orgs_folder_name
//                myData.isForLoadResult = false
//                myHelper.setLastJourney(myData)
//            }
//
//        } else {
//            myHelper.toast("Request can not be completed.")
//        }
//    }

}