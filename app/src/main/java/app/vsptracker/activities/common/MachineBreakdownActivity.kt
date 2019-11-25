package app.vsptracker.activities.common

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_machine_breakdown.*

class MachineBreakdownActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_machine_breakdown, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(3).isChecked = true
        myHelper.setTag(tag)

        if (!myHelper.getIsMachineStopped()) {
            machine_breakdown_title.text = getString(R.string.stop_machine_due_to_breakdown)
            day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            machine_breakdown_action_text.text = getString(R.string.stop_machine)

        } else {
            machine_breakdown_title.text = getString(R.string.machine_stopped_due_to_breakdown)
            day_works_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            machine_breakdown_action_text.text = getString(R.string.start_machine)
        }

        myData = MyData()

        val meter = myHelper.getMeter()
        if(meter.isMachineStartTimeCustom)
            myData.isStartHoursCustom = 1
        myData.startHours = myHelper.getMeterTimeForFinish()
        myData.startTime = meter.machineStartTime
        myData.loadingGPSLocation = meter.hourStartGPSLocation
        sfinish_reading.setText(myHelper.getMeterTimeForFinish())

        machine_breakdown_back.setOnClickListener(this)
        machine_breakdown_action.setOnClickListener(this)
        sfinish_minus.setOnClickListener(this)
        sfinish_plus.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.sfinish_minus -> {
                val value = sfinish_reading.text.toString().toFloat()
                if (value > 0) {
                    val newValue = value - 0.1
                    sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
                }
            }

            R.id.sfinish_plus -> {
                val value = sfinish_reading.text.toString().toFloat()
                val newValue = value + 0.1
                sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
            }
            R.id.machine_breakdown_back ->{ finish()}
            /**
             * Here Machine will be stopped due to Breakdown and Following action will be performed.
             * 1. Now Machine Total Hours till now should be updated in Machine hours Table in App and in Portal.
             * 2. Also Machine status should be updated in Machines Table alongside Machine Hours DB ID.
             * 3. Machine Stopped Time should be started and this time will be calculated when machine is Started again.
             */
            R.id.machine_breakdown_action ->{
                val data = MyData()
                val stoppedReasons = db.getStopReasons()
                // First Reason for Machine Stops is Breakdown for All Companies
                val material = stoppedReasons[0]
                data.machineStoppedReason = material.name
                data.loadingGPSLocation = gpsLocation
                data.siteId = myHelper.getMachineSettings().siteId

                myDataPushSave.insertMachineStop(data, material)
//                if (insertID > 0) {
//                    myHelper.toast("Record Saved in Database Successfully.")
//                    myHelper.stopMachine(insertID, material)
                    myHelper.logout(this)
                    finishAffinity()
//                } else {
//                    myHelper.toast("Machine Not Stopped. Please try again.")
//                }

            }
        }
    }
}
