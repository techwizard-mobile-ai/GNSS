package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_unload_task.*

class UnloadTaskActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

//    private var myData = MyData()

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

        when(myHelper.getMachineType()){
            2-> ultask_off_site.visibility = View.INVISIBLE
            else -> ultask_off_site.visibility = View.VISIBLE
        }
        ultask_fill.setOnClickListener(this)
        ultask_off_site.setOnClickListener(this)
        ultask_respread.setOnClickListener(this)
        ultask_stockpile.setOnClickListener(this)
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
                    myData.unloading_task_id = 2
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Fill"
                    myData.unloading_task_id = 2
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Fill"
                    myData.unloading_task_id = 2
                        }
                        3 -> {
                            myData.unloadingTask = "Fill"
                    myData.unloading_task_id = 2
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
                    myData.unloading_task_id = 4
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Off site"
                    myData.unloading_task_id = 4
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Off site"
                    myData.unloading_task_id = 4
                        }
                        3 -> {
                            myData.unloadingTask = "Off site"
                    myData.unloading_task_id = 4
                        }
                    }
                    val intent = Intent(this, Location1Activity::class.java)
                    myData.unloadingTask = "Off site"
                    myData.unloading_task_id = 4
                    intent.putExtra("myData", myData)
                    startActivity(intent)
                }

            }
            R.id.ultask_respread -> {

                myHelper.toast("Selected Task: Respread")
                if (myData.isForUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Respread"
                    myData.unloading_task_id = 3
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Respread"
                    myData.unloading_task_id = 3
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Respread"
                    myData.unloading_task_id = 3
                        }
                        3 -> {
                            myData.unloadingTask = "Respread"
                    myData.unloading_task_id = 3
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
                    myData.unloading_task_id = 1
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    myData.unloadingTask = "Stockpile"
                    myData.unloading_task_id = 1
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    when (myData.nextAction) {
                        1 -> {
                            myData.unloadingTask = "Stockpile"
                    myData.unloading_task_id = 1
                        }
                        3 -> {
                            myData.unloadingTask = "Stockpile"
                    myData.unloading_task_id = 1
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
