package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import app.vsptracker.MyHelper
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_unload_task.*

class UnloadTaskActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

//    private var myData = MyData()

    var stockPileId = 0
    var fillId = 0
    var respreadId = 0
    var offSiteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_unload_task, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }


        when(myData.nextAction){
            1-> task_title.text = "Select Unloading Task"
            3-> task_title.text = "Select Back Unloading Task"
        }

        val tasks = db.getMachinesTasks()
        myHelper.log("Tasks:${tasks}")

        var isStockPile = false
        var isFill = false
        var isRespread = false
        var isOffSite = false
        for (task in tasks){
            when(task.machineTaskId){
                1 -> {
                    isStockPile = true
                    stockPileId = task.id
                }
                2 -> {
                    isFill = true
                    fillId = task.id
                }
                3 -> {
                    isRespread = true
                    respreadId = task.id
                }
                4 -> {
                    isOffSite = true
                    offSiteId = task.id
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

        myHelper.log("isStockPile:${isStockPile}")
        myHelper.log("isFill:${isFill}")
        myHelper.log("isRespread:${isRespread}")
        myHelper.log("isOffSite:${isOffSite}")


        ultask_fill.setOnClickListener(this)
        ultask_off_site.setOnClickListener(this)
        ultask_respread.setOnClickListener(this)
        ultask_stockpile.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }

    //    1 fill
    //    2 offsite
    //    3 respread
    //    4 stockpile
    override fun onClick(view: View?) {

        myHelper.log("MyData:$myData")
        when (view!!.id) {
            R.id.ultask_fill -> {

                myHelper.toast("Selected Task: Fill")
                if (myData.isForUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Fill"
                    myData.unloading_task_id = fillId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.backUnloadingTask = "Fill"
                    myData.back_unloading_task_id = fillId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Fill"
                            myData.unloading_task_id = fillId
                        }
                        3 -> {
                            myData.backUnloadingTask = "Fill"
                            myData.back_unloading_task_id = fillId
                        }
                    }
                    val intent = Intent(this, Location1Activity::class.java)

                    intent.putExtra("myData", myData)
                    startActivity(intent)
                }

            }
            R.id.ultask_off_site -> {
                myHelper.toast("Selected Task: Off Site")
                if (myData.isForUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Off site"
                    myData.unloading_task_id = offSiteId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.backUnloadingTask = "Off site"
                    myData.back_unloading_task_id = offSiteId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Off site"
                            myData.unloading_task_id = offSiteId
                        }
                        3 -> {
                            myData.backUnloadingTask = "Off site"
                            myData.back_unloading_task_id = offSiteId
                        }
                    }
                    val intent = Intent(this, Location1Activity::class.java)
                    myData.unloadingTask = "Off site"
                    myData.unloading_task_id = offSiteId
                    intent.putExtra("myData", myData)
                    startActivity(intent)
                }

            }
            R.id.ultask_respread -> {

                myHelper.toast("Selected Task: Respread")
                if (myData.isForUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Respread"
                    myData.unloading_task_id = respreadId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.backUnloadingTask = "Respread"
                    myData.back_unloading_task_id = respreadId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Respread"
                            myData.unloading_task_id = respreadId
                        }
                        3 -> {
                            myData.backUnloadingTask = "Respread"
                            myData.back_unloading_task_id = respreadId
                        }
                    }
                    val intent = Intent(this, Location1Activity::class.java)
                    myData.unloadingTask = "Respread"
                    myData.unloading_task_id = 3
                    intent.putExtra("myData", myData)
                    startActivity(intent)
                }

            }
            R.id.ultask_stockpile -> {

                myHelper.toast("Selected Task: Stockpile")
                if (myData.isForUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Stockpile"
                    myData.unloading_task_id = stockPileId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.backUnloadingTask = "Stockpile"
                    myData.back_unloading_task_id = stockPileId
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Stockpile"
                            myData.unloading_task_id = stockPileId
                        }
                        3 -> {
                            myData.backUnloadingTask = "Stockpile"
                            myData.back_unloading_task_id = stockPileId
                        }
                    }
                    val intent = Intent(this, Location1Activity::class.java)
                    myData.unloadingTask = "Stockpile"
                    myData.unloading_task_id = 1
                    intent.putExtra("myData", myData)
                    startActivity(intent)
                }

            }
        }

    }
}
