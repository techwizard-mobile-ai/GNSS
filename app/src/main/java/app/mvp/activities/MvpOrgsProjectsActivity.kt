package app.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.mvporgsfiles.MvpOrgsProjectsResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_orgs_projects.*

class MvpOrgsProjectsActivity : BaseActivity(), View.OnClickListener {
  
  private val mvpOrgsProjects: ArrayList<Material> = ArrayList<Material>()
  private lateinit var gv: GridView
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_orgs_projects, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
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
      myData.mvp_orgs_project_id = mvpOrgsProjects[position].id
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
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    when {
      myHelper.isOnline() -> {
        mvp_orgs_projects_create.setBackgroundColor(resources.getColor(R.color.colorPrimary)); }
      else -> {
        mvp_orgs_projects_create.setBackgroundColor(resources.getColor(R.color.gray_dark));
      }
    }
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
    val call = this.retrofitAPI.listMvpProjects(
      myHelper.getOrgID(),
      0,
      myHelper.getLoginAPI().auth_token,
    )
    call.enqueue(object : retrofit2.Callback<MvpOrgsProjectsResponse> {
      override fun onResponse(
        call: retrofit2.Call<MvpOrgsProjectsResponse>,
        response: retrofit2.Response<MvpOrgsProjectsResponse>
      ) {
        myHelper.hideProgressBar()
        try {
          val responseBody = response.body()
          if (responseBody!!.success) {
//            myHelper.log("responseBodyTapu: $responseBody")
            responseBody.data?.forEach {
              val material = Material()
              material.id = it.id!!
              material.number = it.name.toString()
//              myHelper.log("material: $material")
              mvpOrgsProjects.add(material)
            }
//            myHelper.log("mvpOrgsProjects: $mvpOrgsProjects")
            val adapter = CustomGridLMachine(this@MvpOrgsProjectsActivity, mvpOrgsProjects)
            gv.adapter = adapter
            
          } else {
            myHelper.showErrorDialogOnUi(responseBody.message)
          }
        }
        catch (e: Exception) {
          myHelper.log("getServerSync:${e.localizedMessage}")
          myHelper.hideProgressBar()
        }
      }
      
      override fun onFailure(call: retrofit2.Call<MvpOrgsProjectsResponse>, t: Throwable) {
        myHelper.hideProgressBar()
        myHelper.log("Failure" + t.message)
      }
    })
  }
  
}
