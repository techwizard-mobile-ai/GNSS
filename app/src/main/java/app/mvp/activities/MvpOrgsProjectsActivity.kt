package app.mvp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.mvporgsfiles.MvpOrgsProjectsResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material

//import kotlinx.android.synthetic.main.activity_base.*
//import kotlinx.android.synthetic.main.activity_mvp_orgs_projects.*


class MvpOrgsProjectsActivity : BaseActivity(), View.OnClickListener {
  
  private val mvpOrgsProjects: ArrayList<Material> = ArrayList<Material>()
  private lateinit var gv: GridView
  private val tag = this::class.java.simpleName
  lateinit var mvp_orgs_projects_pb: ProgressBar
  lateinit var mvp_orgs_projects_create: Button
  lateinit var mvp_orgs_projects_back: Button
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_projects, contentFrameLayout)
    mvp_orgs_projects_pb = findViewById(R.id.mvp_orgs_projects_pb)
    mvp_orgs_projects_create = findViewById(R.id.mvp_orgs_projects_create)
    mvp_orgs_projects_back = findViewById(R.id.mvp_orgs_projects_back)
    myHelper.setTag(tag)
    myHelper.setProgressBar(mvp_orgs_projects_pb)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
      listProjects()
    }
    
    gv = findViewById<GridView>(R.id.mvp_orgs_projects_gridview)
    
    gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
      
      myHelper.log(mvpOrgsProjects[position].toString())
      val intent = Intent(this, MvpOrgsFoldersActivity::class.java)
      myData.project_id = mvpOrgsProjects[position].id
      myData.mvp_orgs_project_name = mvpOrgsProjects[position].number
      myData.isForLoadResult = false
      myData.name = mvpOrgsProjects[position].number
      intent.putExtra("myData", myData)
      myHelper.setLastJourney(myData)
      startActivity(intent)
    }
    
    mvp_orgs_projects_create.setOnClickListener(this)
    mvp_orgs_projects_back.setOnClickListener(this)
  }
  
  @SuppressLint("UseCompatLoadingForColorStateLists")
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
//    when {
//      myHelper.isOnline() -> {
////        mvp_orgs_projects_create.setBackgroundColor(resources.getColor(R.color.colorPrimary));
//        mvp_orgs_projects_create.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary);
//      }
//
//      else -> {
////        mvp_orgs_projects_create.setBackgroundColor(resources.getColor(R.color.gray_dark));
//        mvp_orgs_projects_create.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_dark);
//      }
//    }
  }
  
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_orgs_projects_create -> {
        when {
          myHelper.isOnline() -> {
            myHelper.log("create project")
            val intent = Intent(this, MvpOrgsCreateProjectActivity::class.java)
            intent.putExtra("myData", myData)
            startActivity(intent)
          }
          else -> {
            myHelper.showErrorDialog(resources.getString(R.string.no_internet_connection), "Please connect to Internet to create new site.")
          }
        }
      }
      R.id.mvp_orgs_projects_back -> {
        finish()
      }
    }
  }
  
  internal fun listProjects() {
    myHelper.showProgressBar()
    myHelper.log("listProjects--")
    val call = this.retrofitAPI.listMvpProjects(
      myHelper.getOrgID(),
      0,
      myHelper.getLoginAPI().auth_token,
    )
    call.enqueue(object : retrofit2.Callback<MvpOrgsProjectsResponse> {
      override fun onResponse(
        call: retrofit2.Call<MvpOrgsProjectsResponse>, response: retrofit2.Response<MvpOrgsProjectsResponse>
      ) {
        myHelper.hideProgressBar()
        try {
          val responseBody = response.body()
          if (responseBody!!.success) {
            if (responseBody.data?.isEmpty() == true) {
              myHelper.showErrorDialogOnUi("No Site Created!", "Please create new site to continue.")
            } else {
              responseBody.data?.forEach {
                val material = Material()
                material.id = it.id!!
                material.number = it.name.toString()
                material.created_at = it.created_at
                material.updated_at = it.updated_at
                myHelper.log("myData: $it")
                mvpOrgsProjects.add(material)
              }
              val adapter = CustomGridLMachine(this@MvpOrgsProjectsActivity, mvpOrgsProjects, 4)
              gv.adapter = adapter
            }
            
          } else {
            myHelper.showErrorDialogOnUi(responseBody.message)
          }
        }
        catch (e: Exception) {
          myHelper.log("getServerSync:${e.localizedMessage}")
          myHelper.toastOnUi("getServerSync:${e.localizedMessage}")
          myHelper.hideProgressBar()
        }
      }
      
      override fun onFailure(call: retrofit2.Call<MvpOrgsProjectsResponse>, t: Throwable) {
        myHelper.hideProgressBar()
        myHelper.log("Failure" + t.message)
        myHelper.toastOnUi("Failure" + t.message)
      }
    })
  }
  
}
