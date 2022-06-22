package app.mvp.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_orgs_projects.*

class MvpOrgsProjectsActivity : BaseActivity(), View.OnClickListener {
    
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_mvp_orgs_projects, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper.setTag(tag)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
        
        val gv = findViewById<GridView>(R.id.mvp_orgs_projects_gridview)
        val mvpOrgsProjects = db.getMvpOrgsProjects()
        
        val adapter = CustomGridLMachine(this@MvpOrgsProjectsActivity, mvpOrgsProjects)
        
        gv.adapter = adapter
        gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            
            when {
                myData.isForLoadResult -> {
                    val intent = intent
                    myData.mvp_orgs_project_id = mvpOrgsProjects[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> {
                    myHelper.log("test")
                    myHelper.log(mvpOrgsProjects[position].toString())
//                    val intent = Intent(this, MaterialActivity::class.java)
//                    myData.mvp_orgs_project_id = machines[position].id
//                    intent.putExtra("myData", myData)
//                    startActivity(intent)
                }
            }
        }
        
        mvp_orgs_projects_back.setOnClickListener(this)
    }
    
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.mvp_orgs_projects_back -> {
                finish()
            }
        }
    }
}
