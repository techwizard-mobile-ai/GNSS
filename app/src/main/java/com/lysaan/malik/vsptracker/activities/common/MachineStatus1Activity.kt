package com.lysaan.malik.vsptracker.activities.common



import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStartActivity
import com.lysaan.malik.vsptracker.adapters.MachineStatusAdapter
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_machine_status1.*

class MachineStatus1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_machine_status1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(2).isChecked = true

        myHelper.setTag(TAG)


        if (myHelper.getIsMachineStopped()) {
            machine_status_title.text = "Machine Stopped Reason"
            machine_start_layout.visibility = View.VISIBLE
            machine_status_rv.visibility = View.GONE
            machine_stopped_reason.text = myHelper.getMachineStoppedReason()
        } else {
            machine_status_title.text = "Select Machine Stop Reason"
            machine_start_layout.visibility = View.GONE
            machine_status_rv.visibility = View.VISIBLE
        }

        if(myHelper.getIsMachineStopped()){
            machine_status_logout.visibility = View.VISIBLE
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }else{
            machine_status_logout.visibility = View.GONE
        }

        val stoppedReasons = myHelper.getMachineStopReasons()
        stoppedReasons.removeAt(0)

        val mAdapter = MachineStatusAdapter(this, stoppedReasons)
        machine_status_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        machine_status_rv.setAdapter(mAdapter)


//        val gv = findViewById(R.id.machinestatus_gridview) as GridView
//        val stoppedReasons = myHelper.getMachineStopReasons()
//        stoppedReasons.removeAt(0)
//        val adapter = CustomGrid(this@MachineStatus1Activity, stoppedReasons)
//        gv.setAdapter(adapter)
//
//        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
//            myHelper.toast("Machine Stopped due to : " + stoppedReasons.get(position).name)
//            myHelper.setIsMachineStopped(true , stoppedReasons.get(position).name)
//            myHelper.stopMachine()
//            myHelper.startHomeActivityByType(MyData())
//        })

        machine_status_start.setOnClickListener(this)
        machine_status_logout.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.machine_status_logout -> {
                myHelper.logout(this)
            }

            R.id.machine_status_start -> {

                val machineData = MyData()
                machineData.recordID = myHelper.getMeter().machineDbID
                machineData.unloadingGPSLocation = gpsLocation
                val updateID = db.updateMachineStatus(machineData)
//                if (updateID > 0) {
                    myHelper.toast("Machine Started Successfully")
                    myHelper.setIsMachineStopped(false, "")
                    if(myHelper.getIsMachineStopped()){
                        val intent = Intent(this, HourMeterStartActivity::class.java)
                        startActivity(intent)
                    }else{
                        myHelper.startHomeActivityByType(MyData())
                    }
//                } else {
//                    myHelper.toast("Machine Not Started. Due to App Deleted Cache.")
//                }

            }
        }
    }
}
