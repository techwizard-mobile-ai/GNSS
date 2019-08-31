package com.lysaan.malik.vsptracker.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_day_works.*

class DayWorksActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    private var isDailyModeStart = false
    private var dailyModeStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_day_works, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(3).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        if (helper.isDailyModeStarted()) {
            day_work_title.text = "Stop Day Works Mode"
            day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
            day_works_action_text.text = "Stop"
            val currentTime = System.currentTimeMillis()
            val meter = helper.getMeter()
            val startTime = meter.dailyModeStartTime
            val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime

            day_works_chronometer.setBase(SystemClock.elapsedRealtime() - totalTime)
            day_works_chronometer.start()
        } else {
            day_work_title.text = "Start Day Works Mode"
            day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
            day_works_action_text.text = "Start"
            val meter = helper.getMeter()
            day_works_chronometer.setBase(SystemClock.elapsedRealtime() - meter.dailyModeTotalTime)
        }

        day_works_action.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.day_works_action -> {

                if (helper.isDailyModeStarted()) {
                    day_work_title.text = "Start Day Works Mode"
                    day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
                    day_works_action_text.text = "Start"
                    isDailyModeStart = false
                    day_works_chronometer.stop()
                    helper.stopDailyMode()

                    helper.startHomeActivityByType(Data())
//                    helper.restartActivity(intent, this)

                } else {

                    day_works_chronometer.setBase(SystemClock.elapsedRealtime() - helper.getMeter().dailyModeTotalTime)
                    day_works_chronometer.start()

                    day_work_title.text = "Stop Day Works Mode"
                    day_works_button.setBackgroundTintList(
                            ColorStateList.valueOf(
                                    resources.getColor(
                                            R.color.black
                                    )
                            )
                    )
                    day_works_action_text.text = "Stop"
                    isDailyModeStart = true
                    helper.startDailyMode()
                    helper.startHomeActivityByType(Data())
//                    helper.restartActivity(intent, this)

                }
            }
        }
    }

}
