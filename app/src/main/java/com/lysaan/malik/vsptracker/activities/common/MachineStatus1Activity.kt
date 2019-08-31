package com.lysaan.malik.vsptracker.activities.common

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.MachineStatusAdapter
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_machine_status1.*

class MachineStatus1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
//    private var data = Data()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_machine_status1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(2).isChecked = true

        helper.setTag(TAG)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }

        if (helper.getIsMachineStopped()) {
            machine_status_title.text = "Machine Stopped Reason"
            machine_start_layout.visibility = View.VISIBLE
            machine_status_rv.visibility = View.GONE
            machine_stopped_reason.text = helper.getMachineStoppedReason()
        } else {
            machine_status_title.text = "Select Machine Stop Reason"
            machine_start_layout.visibility = View.GONE
            machine_status_rv.visibility = View.VISIBLE
        }

        val stoppedReasons = helper.getMachineStopReasons()
        stoppedReasons.removeAt(0)

        val mAdapter = MachineStatusAdapter(this, stoppedReasons)
        machine_status_rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        machine_status_rv.setAdapter(mAdapter)


//        val gv = findViewById(R.id.machinestatus_gridview) as GridView
//        val stoppedReasons = helper.getMachineStopReasons()
//        stoppedReasons.removeAt(0)
//        val adapter = CustomGrid(this@MachineStatus1Activity, stoppedReasons)
//        gv.setAdapter(adapter)
//
//        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
//            helper.toast("Machine Stopped due to : " + stoppedReasons.get(position).name)
//            helper.setIsMachineStopped(true , stoppedReasons.get(position).name)
//            helper.stopMachine()
//            helper.startHomeActivityByType(Data())
//        })

        machine_status_start.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        startGPS()
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.machine_status_start -> {

                val machineData = Data()
                machineData.recordID = helper.getMeter().machineDbID
                machineData.unloadingGPSLocation = gpsLocation
                val updateID = db.updateMachineStatus(machineData)
                if (updateID > 0) {
                    helper.toast("Machine Started Successfully")
                    helper.setIsMachineStopped(false, "")
                    helper.startMachine()
                    helper.startHomeActivityByType(Data())
                } else {
                    helper.toast("Machine Not Started. Due to App Deleted Cache.")
                }

            }
        }
    }
}
