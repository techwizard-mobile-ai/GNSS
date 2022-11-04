package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.LoginActivity
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.Utils.tag
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_mvp_orgs_create_folder.back
import kotlinx.android.synthetic.main.activity_mvp_orgs_create_folder.create
import kotlinx.android.synthetic.main.activity_mvp_orgs_create_project.*
import kotlinx.android.synthetic.main.app_bar_base.*
import okhttp3.*
import okio.IOException
import org.json.JSONObject

class MvpOrgsCreateProjectActivity : BaseActivity(), View.OnClickListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_create_project, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      toolbar_title.text = myData.name
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
        
        val project_name = project_name.text.toString()
        when {
          project_name.length < 3 -> myHelper.showErrorDialog("Site not created!","Please provide minimum 3 characters site name.")
          else -> createProject(project_name)
        }
      }
    }
  }
  
  fun createProject(name: String) {
    val client = myHelper.skipSSLOkHttpClient().build()
    val formBody =
      FormBody.Builder().add("name", name).add("org_id", myHelper.getLoginAPI().id.toString()).add("details", myHelper.getGPSLocationToString(gpsLocation))
        .add("token", myHelper.getLoginAPI().auth_token).build()
    
    val request = Request.Builder().url("${getString(R.string.api_url)}mvporgsprojects/store").post(formBody).build()
    myHelper.log("request:${myHelper.requestToString(request)}.")
    
    client.newCall(request).enqueue(object : Callback {
      override fun onResponse(call: Call, response: Response) {
        
        val responseString = response.body!!.string()
        try {
          val responseJObject = JSONObject(responseString)
          myHelper.log("responseString:$responseString")
          val success = responseJObject.getBoolean("success")
          if (success) {
            val gson = GsonBuilder().create()
            
            val loginAPI = gson.fromJson(responseJObject.getString("data"), LoginAPI::class.java)
            myHelper.log("loginAPI:$loginAPI")
            
            myDataPushSave.fetchOrgData()
            val intent = Intent(this@MvpOrgsCreateProjectActivity, MvpOrgsFoldersActivity::class.java)
            myData.mvp_orgs_project_id = loginAPI.id
            myData.mvp_orgs_project_name = loginAPI.name
            myData.isForLoadResult = false
            myData.name = loginAPI.name
            intent.putExtra("myData", myData)
            myHelper.setLastJourney(myData)
            startActivity(intent)
          } else if (myHelper.isDuplicateEntry(responseJObject.getString("message"))) {
            myHelper.showErrorDialogOnUi("$name already created", "Please provide unique site name")
          } else {
            myHelper.showErrorDialogOnUi(responseJObject.getString("message"))
          }
        }
        catch (e: Exception) {
          myHelper.toastOnUi("refreshTokenException:" + e.localizedMessage)
          myHelper.log("refreshTokenException:" + e.localizedMessage)
          val intent = Intent(this@MvpOrgsCreateProjectActivity, LoginActivity::class.java)
          startActivity(intent)
        }
        
      }
      
      override fun onFailure(call: Call, e: IOException) {
        myHelper.toastOnUi("onFailure: ${e.printStackTrace()}")
        myHelper.log("onFailure: ${e.printStackTrace()}")
      }
    })
  }
  
}