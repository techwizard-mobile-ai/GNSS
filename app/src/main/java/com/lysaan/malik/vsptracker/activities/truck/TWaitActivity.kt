package com.lysaan.malik.vsptracker.activities.truck

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.Material1Activity

import com.lysaan.malik.vsptracker.others.Data
import com.lysaan.malik.vsptracker.others.EWork
import kotlinx.android.synthetic.main.activity_twait.*

class TWaitActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private var isWaiting :Boolean = false
    private var waitStartTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_twait, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(1).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        day_works_chronometer.setBase(SystemClock.elapsedRealtime())
        day_works_chronometer.start()

        day_work_title.text = "Waiting For Load Started"
        day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
        day_works_action_text.text = "Stop"
        isWaiting = true
        waitStartTime = System.currentTimeMillis()
        helper.toast("Wait Started.")


        day_works_action.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view!!.id){

            R.id.day_works_action ->{

                if(isWaiting){
                    day_work_title.text = "Wait For Load"
                    day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
                    day_works_action_text.text = "Start"
                    isWaiting = false
                    day_works_chronometer.stop()
                    val eWork = EWork()
                    eWork.startTime = waitStartTime
                    val insertID = db.insertTWait(eWork)
                    if(insertID > 0){
                        helper.toast("Wait Saved Successfully.")
                    }else{
                        helper.toast("Error while Saving Wait.")
                    }
                    helper.toast("Wait Stopped.")
                    finish()

                }else{

                    day_works_chronometer.setBase(SystemClock.elapsedRealtime())
                    day_works_chronometer.start()

                    day_work_title.text = "Waiting For Load Started"
                    day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
                    day_works_action_text.text = "Stop"
                    isWaiting = true
                    waitStartTime = System.currentTimeMillis()

                    helper.log("TWaits:${db.getTWaits()}")
                    helper.toast("Wait Started.")

                }
            }
        }

    }
}
