package com.lysaan.malik.vsptracker.activities.common

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import kotlinx.android.synthetic.main.activity_machine_status1.*

class MachineStatus1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
//    private var data = Data()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_machine_status1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }

        if(helper.getIsMachineStopped()){
            machine_status_title.text = "Machine Stopped Reason"
            machine_start_layout.visibility = View.VISIBLE
            machinestatus_gridview.visibility = View.GONE
            machine_stopped_reason.text = helper.getMachineStoppedReason()
        }else{
            machine_status_title.text = "Select Machine Stop Reason"
            machine_start_layout.visibility = View.GONE
            machinestatus_gridview.visibility = View.VISIBLE
        }

        val gv = findViewById(R.id.machinestatus_gridview) as GridView
        val stoppedReasons = helper.getMachineStopReasons()
        stoppedReasons.removeAt(0)
        val adapter = CustomGrid(this@MachineStatus1Activity, stoppedReasons)


        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            helper.toast("Machine Stopped due to : " + stoppedReasons.get(position).name)
            helper.setIsMachineStopped(true , stoppedReasons.get(position).name)
            helper.stopMachine()
            helper.startHomeActivityByType(data)
        })

        machine_status_start.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.machine_status_start -> {
                helper.toast("Machine Started Successfully")
                helper.setIsMachineStopped(false , "")
                helper.startMachine()
                helper.startHomeActivityByType(data)
            }
        }
    }
}
