package app.mvp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.mvporgsfiles.MvpOrgsFilesResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material

//import kotlinx.android.synthetic.main.activity_base.*
//import kotlinx.android.synthetic.main.activity_mvp_orgs_folders.*
//import kotlinx.android.synthetic.main.app_bar_base.*

class MvpOrgsFoldersActivity : BaseActivity(), View.OnClickListener {
  
  private val mvpOrgsFolders: ArrayList<Material> = ArrayList<Material>()
  private lateinit var gv: GridView
  private val tag = this::class.java.simpleName
  lateinit var mvp_orgs_files_pb: ProgressBar
  lateinit var mvp_orgs_files_back: Button
  lateinit var mvp_orgs_folders_create: Button
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_folders, contentFrameLayout)
    mvp_orgs_files_pb = findViewById(R.id.mvp_orgs_files_pb)
    mvp_orgs_files_back = findViewById(R.id.mvp_orgs_files_back)
    mvp_orgs_folders_create = findViewById(R.id.mvp_orgs_folders_create)
    myHelper.setTag(tag)
    myHelper.setProgressBar(mvp_orgs_files_pb)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      getOrgsFiles(myData.project_id)
      toolbar_title.text = myData.mvp_orgs_project_name
    }
    
    gv = findViewById<GridView>(R.id.mvp_orgs_files_gridview)
    
    gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
      
      when {
        myData.isForLoadResult -> {
          val intent = intent
          myData.mvp_orgs_files_id = mvpOrgsFolders[position].id
          myData.mvp_orgs_files_name = mvpOrgsFolders[position].number
          myData.aws_path = mvpOrgsFolders[position].aws_path
          myData.relative_path = mvpOrgsFolders[position].relative_path
          intent.putExtra("myData", myData)
          setResult(Activity.RESULT_OK, intent)
          finish()
        }
        else -> {
          myHelper.log(mvpOrgsFolders[position].toString())
          myData.mvp_orgs_files_id = mvpOrgsFolders[position].id
          myData.mvp_orgs_files_name = mvpOrgsFolders[position].number
          myData.aws_path = mvpOrgsFolders[position].aws_path
          myData.relative_path = mvpOrgsFolders[position].relative_path
          myHelper.setLastJourney(myData)
          val intent = Intent(this, MvpStartDataCollectionActivity::class.java)
          startActivity(intent)
        }
      }
    }
    
    mvp_orgs_files_back.setOnClickListener(this)
    mvp_orgs_folders_create.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
//    when {
//      myHelper.isOnline() -> {
//        mvp_orgs_folders_create.setBackgroundColor(resources.getColor(R.color.colorPrimary)); }
//      else -> {
//        mvp_orgs_folders_create.setBackgroundColor(resources.getColor(R.color.gray_dark));
//      }
//    }
  }
  
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_orgs_files_back -> {
        finish()
      }
      R.id.mvp_orgs_folders_create -> {
        when {
          myHelper.isOnline() -> {
            val intent = Intent(this, MvpOrgsCreateFolderActivity::class.java)
            intent.putExtra("myData", myData)
            startActivity(intent)
          }
          else -> {
            myHelper.showErrorDialog(resources.getString(R.string.no_internet_connection), "Please connect to Internet to create new task.")
          }
        }
      }
    }
  }
  
  
  internal fun getOrgsFiles(mvpOrgsProjectId: Int) {
    myHelper.showProgressBar()
    myHelper.log("n_days_tasks:${myHelper.getAppSettings().n_days_tasks}")
    val prefix = "taputapu/${myHelper.getCurrentYear()}/${myHelper.getCurrentMonth()}/${myHelper.getCurrentDay()}/"
//    val prefix = "taputapu/${myHelper.getCurrentYear()}/${myHelper.getCurrentMonth()}/"
    myHelper.log("prefix:$prefix")
    val call = this.retrofitAPI.getMvpOrgsFiles(
      mvpOrgsProjectId, prefix, myHelper.getLoginAPI().role, false, myHelper.getLoginAPI().auth_token, 2, myHelper.getAppSettings().n_days_tasks
    )
    call.enqueue(object : retrofit2.Callback<MvpOrgsFilesResponse> {
      override fun onResponse(
        call: retrofit2.Call<MvpOrgsFilesResponse>, response: retrofit2.Response<MvpOrgsFilesResponse>
      ) {
        myHelper.hideProgressBar()
        try {
          val responseBody = response.body()
          if (responseBody!!.success) {
            myHelper.log("responseBodyTapu: $responseBody")
            if (responseBody.separateFolders?.isEmpty() == true) {
              myHelper.showErrorDialogOnUi("No Tasks in ${myHelper.getAppSettings().n_days_tasks} days!", "Go to Settings and change Days Last Tasks \nOR\nCreate new Task.")
            } else {
              responseBody.separateFolders?.forEach {
                val material = Material()
                material.id = it.id!!
                material.aws_path = it.awsPath
                material.relative_path = it.relativePath
                material.number = it.fileFolder?.name!!.dropLast(1)
                material.created_at = it.createdAt.toString()
                material.updated_at = it.updatedAt.toString()
                mvpOrgsFolders.add(material)
              }
              val adapter = CustomGridLMachine(this@MvpOrgsFoldersActivity, mvpOrgsFolders, 4)
              gv.adapter = adapter
            }
            
          } else {
            myHelper.toastOnUi(responseBody.message)
          }
        }
        catch (e: Exception) {
          myHelper.log("getServerSync:${e.localizedMessage}")
          myHelper.toastOnUi("getServerSync:${e.localizedMessage}")
        }
      }
      
      override fun onFailure(call: retrofit2.Call<MvpOrgsFilesResponse>, t: Throwable) {
        myHelper.hideProgressBar()
        myHelper.log("Failure" + t.message)
        myHelper.toastOnUi("Failure" + t.message)
      }
    })
  }
}
