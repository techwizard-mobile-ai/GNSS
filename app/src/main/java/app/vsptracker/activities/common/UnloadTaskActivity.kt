package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
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

//                myHelper.toast("Selected Task: Fill")
                when {
                    myData.isForUnloadResult -> {
                        val intent = intent
                        myData.unloading_task_id = fillId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    myData.isForBackUnloadResult -> {
                        val intent = intent
                        myData.back_unloading_task_id = fillId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else -> {
                        when (myData.nextAction) {
                            1 -> {
                                myData.unloading_task_id = fillId
                            }
                            3 -> {
                                myData.back_unloading_task_id = fillId
                            }
                        }
                        val intent = Intent(this, LocationActivity::class.java)

                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }

            }
            R.id.ultask_off_site -> {
//                myHelper.toast("Selected Task: Off Site")
                when {
                    myData.isForUnloadResult -> {
                        val intent = intent
                        myData.unloading_task_id = offSiteId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    myData.isForBackUnloadResult -> {
                        val intent = intent
                        myData.back_unloading_task_id = offSiteId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else -> {
                        when (myData.nextAction) {
                            1 -> {
                                myData.unloading_task_id = offSiteId
                            }
                            3 -> {
                                myData.back_unloading_task_id = offSiteId
                            }
                        }
                        val intent = Intent(this, LocationActivity::class.java)
                        myData.unloading_task_id = offSiteId
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }

            }
            R.id.ultask_respread -> {

//                myHelper.toast("Selected Task: Respread")
                when {
                    myData.isForUnloadResult -> {
                        val intent = intent
                        myData.unloading_task_id = respreadId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    myData.isForBackUnloadResult -> {
                        val intent = intent
                        myData.back_unloading_task_id = respreadId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else -> {
                        when (myData.nextAction) {
                            1 -> {
                                myData.unloading_task_id = respreadId
                            }
                            3 -> {
                                myData.back_unloading_task_id = respreadId
                            }
                        }
                        val intent = Intent(this, LocationActivity::class.java)
                        myData.unloading_task_id = 3
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }

            }
            R.id.ultask_stockpile -> {

//                myHelper.toast("Selected Task: Stockpile")
                when {
                    myData.isForUnloadResult -> {
                        val intent = intent
                        myData.unloading_task_id = stockPileId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    myData.isForBackUnloadResult -> {
                        val intent = intent
                        myData.back_unloading_task_id = stockPileId
                        intent.putExtra("myData", myData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else -> {
                        when (myData.nextAction) {
                            1 -> {
                                myData.unloading_task_id = stockPileId
                            }
                            3 -> {
                                myData.back_unloading_task_id = stockPileId
                            }
                        }
                        val intent = Intent(this, LocationActivity::class.java)
                        myData.unloading_task_id = 1
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }

            }
        }

    }
}
