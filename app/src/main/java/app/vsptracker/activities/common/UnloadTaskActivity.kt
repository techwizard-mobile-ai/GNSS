package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_unload_task.*

class UnloadTaskActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName

//    private var myData = MyData()

    private var stockPileId = 0
    private var fillId = 0
    private var respreadId = 0
    private var offSiteId = 0
    private var crushingPlantId = 0

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
            myHelper.log("myData:$myData")
        }


        when(myData.nextAction){
            1-> task_title.text = getString(R.string.select_unloading_task)
            3-> task_title.text = getString(R.string.select_back_unloading_task)
        }

        val tasks = db.getTasks()
        myHelper.log("Tasks:${tasks}")

        var isStockPile = false
        var isFill = false
        var isRespread = false
        var isOffSite = false
        var isCrushingPlant = false
        
        for (task in tasks){
//            when(task.machineTaskId){
            when(task.name){
                MyEnum.TASK_STOCKPILE -> {
                    isStockPile = true
                    stockPileId = task.id
                }
                MyEnum.TASK_FILL -> {
                    isFill = true
                    fillId = task.id
                }
                MyEnum.TASK_RESPREAD -> {
                    isRespread = true
                    respreadId = task.id
                }
                MyEnum.TASK_OFFSITE -> {
                    isOffSite = true
                    offSiteId = task.id
                }
                MyEnum.TASK_CRUSHINGPLANT -> {
                    isCrushingPlant = true
                    crushingPlantId = task.id
                }
            }
        }

        if(isStockPile){
            ultask_stockpile.visibility = View.VISIBLE
        }else{
            ultask_stockpile.visibility = View.GONE
        }
        if(isFill){
            ultask_fill.visibility = View.VISIBLE
        }else{
            ultask_fill.visibility = View.GONE
        }
        if(!isStockPile && !isFill){
            layout_stockpile_fill.visibility = View.GONE
        }
        if(isRespread){
            ultask_respread.visibility = View.VISIBLE
        }else{
            ultask_respread.visibility = View.GONE
        }

        if(isOffSite){
            ultask_off_site.visibility = View.VISIBLE
        }else{
            ultask_off_site.visibility = View.GONE
        }
    
        if(!isRespread && !isOffSite){
            layout_respread_offsite.visibility = View.GONE
        }
    
        if(!isCrushingPlant){
            layout_crushingmachine.visibility = View.GONE
        }
        
        ultask_fill.setOnClickListener(this)
        ultask_off_site.setOnClickListener(this)
        ultask_respread.setOnClickListener(this)
        ultask_stockpile.setOnClickListener(this)
        ultask_crushing_plant.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }

    //    1 fill
    //    2 offsite
    //    3 respread
    //    4 stockpile
    //    5 crushing machine
    override fun onClick(view: View?) {

        myHelper.log("MyData:$myData")
        when (view!!.id) {
            R.id.ultask_fill -> {
                taskClickedAction(fillId)
            }
            R.id.ultask_off_site -> {
                taskClickedAction(offSiteId)
            }
            R.id.ultask_respread -> {
                taskClickedAction(respreadId)
            }
            R.id.ultask_stockpile -> {
                taskClickedAction(stockPileId)
            }
            R.id.ultask_crushing_plant -> {
                taskClickedAction(crushingPlantId)
            }
        }
    }
    private fun taskClickedAction(taskId : Int){
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
