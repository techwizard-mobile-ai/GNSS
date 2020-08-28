package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGrid
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_unload_task.*

class UnloadTaskActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_unload_task, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper = MyHelper(tag, this)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
        }
        
        when (myData.nextAction) {
            3 -> task_title.text = getString(R.string.select_back_unloading_task)
            else -> task_title.text = getString(R.string.select_unloading_task)
        }
        
        val tasks = db.getTasks()
        val adapter = CustomGrid(this, tasks, true)
        tasks_gridview.adapter = adapter
        tasks_gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            taskClickedAction(tasks[position].id)
        }
        tasks_back.setOnClickListener(this)
    }
    
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.tasks_back -> {
                finish()
            }
        }
    }
    
    private fun taskClickedAction(taskId: Int) {
        when {
            myData.isForUnloadResult -> {
                val intent = intent
                myData.unloading_task_id = taskId
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            myData.isForBackUnloadResult -> {
                val intent = intent
                myData.back_unloading_task_id = taskId
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else -> {
                when (myData.nextAction) {
                    1 -> {
                        myData.unloading_task_id = taskId
                    }
                    3 -> {
                        myData.back_unloading_task_id = taskId
                    }
                }
                val intent = Intent(this, LocationActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
        }
    }
}
