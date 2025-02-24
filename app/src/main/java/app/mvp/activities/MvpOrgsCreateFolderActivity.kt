package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.LoginActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum.Companion.USER_ROLE_MVP_COMPANY_STANDARD_USER
import com.google.gson.GsonBuilder
//import kotlinx.android.synthetic.main.activity_base.*
//import kotlinx.android.synthetic.main.activity_mvp_orgs_create_folder.*
//import kotlinx.android.synthetic.main.app_bar_base.*
import okhttp3.*
import okio.IOException
import org.json.JSONObject


class MvpOrgsCreateFolderActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  lateinit var back: Button
  lateinit var create: Button
  lateinit var folder_name: EditText
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_create_folder, contentFrameLayout)
    
    back = findViewById(R.id.back)
    create = findViewById(R.id.create)
    folder_name = findViewById(R.id.folder_name)
    myHelper.setTag(tag)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      toolbar_title.text = myData.mvp_orgs_project_name
    }
    back.setOnClickListener(this)
    create.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    
    when (view!!.id) {
      R.id.back -> {
        finish()
      }
      R.id.create -> {
        
        val folder_name = folder_name.text.toString()
        when {
          folder_name.length < 3 -> myHelper.showErrorDialog("Task not created!", "Please provide minimum 3 characters task name.")
          else -> {
            myHelper.log("getValidFileName:${myHelper.getValidFileName(folder_name)}")
            createFolder(folder_name)
          }
        }
      }
    }
  }
  
  fun createFolder(name: String) {
    myHelper.log("inside createProject")
    val client = myHelper.skipSSLOkHttpClient().build()
    val selectedFiles = ArrayList<MyData>()
    
    val aws_path =
      "${myHelper.getOrgID()}/mvp_project_files/${myData.project_id}/taputapu/${myHelper.getCurrentYear()}/${myHelper.getCurrentMonth()}/${myHelper.getCurrentDay()}/${myHelper.getValidFileName(name)}/"
    val relative_path = "taputapu/${myHelper.getCurrentYear()}/${myHelper.getCurrentMonth()}/${myHelper.getCurrentDay()}/${name}/"
    
    val myData1 = MyData()
    
    val currentTime = System.currentTimeMillis()
    myData1.startTime = currentTime
    myData1.stopTime = currentTime
    myData1.totalTime = 0
    myData1.time = currentTime.toString()
    myData1.date = myHelper.getDateTime(currentTime)
    myData1.org_id = myHelper.getOrgID()
    myData1.admin_file_type_id = -1
    myData1.processing_status = -1
    myData1.aws_path = aws_path
    myData1.relative_path = "/$relative_path"
    myData1.file_details = "{ \"size\": 0 }"
    myData1.file_description = ""
    myData1.upload_status = 2
    myData1.file_level = (relative_path.split("/").size - 1)
//    myData1.security_level = myHelper.getLoginAPI().role
    myData1.security_level = USER_ROLE_MVP_COMPANY_STANDARD_USER
    myData1.size = 0
    myData1.loadingGPSLocationString = myHelper.getGPSLocationToString(gpsLocation)
    myData1.unloadingGPSLocationString = myHelper.getGPSLocationToString(gpsLocation)
    
    selectedFiles.add(myData1)
    
    myHelper.log("selectedFiles:${myHelper.arrayToString(selectedFiles)}")
    
    val formBody =
      FormBody.Builder()
        .add("project_id", "${myData.project_id}")
        .add("is_folder_creation", "true")
        .add("selected_files", myHelper.arrayToString(selectedFiles))
        .add("token", myHelper.getLoginAPI().auth_token).build()
    
    val request = Request.Builder().url("${getString(R.string.api_url)}mvporgsfiles/store").post(formBody).build()
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
            
            val myDataResponse = gson.fromJson(responseJObject.getString("data"), MyData::class.java)
            myHelper.log("myDataResponse:$myDataResponse")
            myDataPushSave.fetchOrgData()
            
            val intent = Intent(this@MvpOrgsCreateFolderActivity, MvpStartDataCollectionActivity::class.java)
            myData.mvp_orgs_files_id = myDataResponse.id
            myData.mvp_orgs_files_name = name
            myData.aws_path = myDataResponse.aws_path
            myData.relative_path = myDataResponse.relative_path
            myHelper.setLastJourney(myData)
            intent.putExtra("myData", myData)
            startActivity(intent)
            
          } else if (responseJObject.getString("message").contains("Folder already exists.")) {
            myHelper.showErrorDialogOnUi("'$name' already created", "Please provide unique task name")
          } else {
            myHelper.showErrorDialogOnUi(responseJObject.getString("message"))
          }
        }
        catch (e: Exception) {
          myHelper.toastOnUi("refreshTokenException:" + e.localizedMessage)
          myHelper.log("refreshTokenException:" + e.localizedMessage)
          val intent = Intent(this@MvpOrgsCreateFolderActivity, LoginActivity::class.java)
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