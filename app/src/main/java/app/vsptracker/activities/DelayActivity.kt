package app.vsptracker.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import kotlinx.android.synthetic.main.activity_delay.*

class DelayActivity : BaseActivity(),
    View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var eWork: EWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout =
            findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(
            R.layout.activity_delay,
            contentFrameLayout
        )
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(1).isChecked = true

        myHelper.setTag(TAG)
        eWork = EWork()
        startGPS()

        val startLocation = gpsLocation

        if (!myHelper.isDelayStarted()) {

            day_works_chronometer.setBase(SystemClock.elapsedRealtime())
            day_works_chronometer.start()
            day_work_title.text = "Waiting For Load Started"
            day_works_action_text.text = "Stop"
            day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
            myHelper.startDelay(startLocation)

        } else {

            val currentTime = System.currentTimeMillis()
            val meter = myHelper.getMeter()
            val startTime = meter.delayStartTime
            val totalTime = (currentTime - startTime)
            day_works_chronometer.setBase(SystemClock.elapsedRealtime() - totalTime)
            day_works_chronometer.start()
            day_works_action_text.text = "Stop"
            day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
        }

        day_works_action.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.day_works_action -> {
                if (myHelper.isDelayStarted()) {
                    val dbID = stopDelay()
//                    if(dbID>0){
                        day_work_title.text = "Wait For Load"
                        day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
                        day_works_action_text.text = "Start"
                        day_works_chronometer.stop()
//                        myHelper.startHomeActivityByType(MyData())
//                        finish()
//                        onBackPressed();
//                    }

                } else {

                    day_works_chronometer.setBase(SystemClock.elapsedRealtime())
                    day_works_chronometer.start()

                    day_work_title.text = "Waiting For Load Started"
                    day_works_button.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)))
                    day_works_action_text.text = "Stop"
                    val gpslocation = gpsLocation
                    myHelper.startDelay(gpslocation)

                }
            }
        }

    }

}
