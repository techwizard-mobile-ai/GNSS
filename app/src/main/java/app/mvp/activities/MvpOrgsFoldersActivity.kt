package app.mvp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.mvporgsfiles.MvpOrgsFilesResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_orgs_folders.*
import kotlinx.android.synthetic.main.app_bar_base.*
import java.util.ArrayList

class MvpOrgsFoldersActivity : BaseActivity(), View.OnClickListener {
    
    private val mvpOrgsFolders: ArrayList<Material> = ArrayList<Material>()
    private lateinit var gv: GridView
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_mvp_orgs_folders, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper.setTag(tag)
        myHelper.setProgressBar(mvp_orgs_files_pb)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
            getOrgsFiles(myData.mvp_orgs_project_id)
            toolbar_title.text = myData.name
        }
        
        gv = findViewById<GridView>(R.id.mvp_orgs_files_gridview)
        
        gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            
            when {
                myData.isForLoadResult -> {
                    val intent = intent
                    myData.mvp_orgs_folder_id = mvpOrgsFolders[position].id
                    myData.mvp_orgs_folder_name = mvpOrgsFolders[position].number
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> {
                    myHelper.log(mvpOrgsFolders[position].toString())
                    myData.mvp_orgs_folder_id = mvpOrgsFolders[position].id
                    myData.mvp_orgs_folder_name = mvpOrgsFolders[position].number
                    myHelper.setLastJourney(myData)
                    val intent = Intent(this, MvpStartDataCollectionActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        
        mvp_orgs_files_back.setOnClickListener(this)
    }
    
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.mvp_orgs_files_back -> {
                finish()
            }
        }
    }
    
    
    internal fun getOrgsFiles(mvpOrgsProjectId: Int) {
        myHelper.showProgressBar()
        val call = this.retrofitAPI.getMvpOrgsFiles(
            mvpOrgsProjectId,
            "",
            myHelper.getLoginAPI().role,
            false,
            myHelper.getLoginAPI().auth_token,
        )
        call.enqueue(object : retrofit2.Callback<MvpOrgsFilesResponse> {
            override fun onResponse(
                call: retrofit2.Call<MvpOrgsFilesResponse>,
                response: retrofit2.Response<MvpOrgsFilesResponse>
            ) {
                myHelper.hideProgressBar()
                try {
                    val responseBody = response.body()
                    if (responseBody!!.success) {
                        
                        responseBody.separateFolders?.forEach {
                            val material = Material()
                            material.id = it.id!!
                            material.number = it.fileFolder?.name!!.dropLast(1)
                            mvpOrgsFolders.add(material)
                        }
                        val adapter = CustomGridLMachine(this@MvpOrgsFoldersActivity, mvpOrgsFolders)
                        gv.adapter = adapter
                        
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                catch (e: Exception) {
                    myHelper.log("getServerSync:${e.localizedMessage}")
                }
            }
            
            override fun onFailure(call: retrofit2.Call<MvpOrgsFilesResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }
}
