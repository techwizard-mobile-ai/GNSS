package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.activity_unload_task.*

class UnloadTaskActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

//    private var data = Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_unload_task, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
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

    helper.log("Data:$data")
        when(view!!.id){
            R.id.ultask_fill -> {

                helper.toast("Selected Task: Fill")
                if(data.isForUnloadResult){
                    val intent = intent
                    data.unloadingTask = "Fill"
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else {
                    val intent = Intent(this, Location1Activity::class.java)
                    data.unloadingTask = "Fill"
                    intent.putExtra("data", data)
                    startActivity(intent)
                }

            }
            R.id.ultask_off_site -> {
                helper.toast("Selected Task: Off Site")
                if(data.isForUnloadResult){
                    val intent = intent
                    data.unloadingTask = "Off Site"
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    val intent = Intent(this, Location1Activity::class.java)
                    data.unloadingTask = "Off Site"
                    intent.putExtra("data", data)
                    startActivity(intent)
                }

            }
            R.id.ultask_respread -> {

                helper.toast("Selected Task: Respread")
                if(data.isForUnloadResult){
                    val intent = intent
                    data.unloadingTask = "Respread"
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    val intent = Intent(this, Location1Activity::class.java)
                    data.unloadingTask = "Respread"
                    intent.putExtra("data", data)
                    startActivity(intent)
                }

            }
            R.id.ultask_stockpile -> {

                helper.toast("Selected Task: Stockpile")
                if(data.isForUnloadResult){
                    val intent = intent
                    data.unloadingTask = "Stockpile"
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    val intent = Intent(this, Location1Activity::class.java)
                    data.unloadingTask = "Stockpile"
                    intent.putExtra("data", data)
                    startActivity(intent)
                }

            }
        }

    }
}
