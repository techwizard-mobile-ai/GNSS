package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.activity_machine_status.*

class MachineStatusActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    private var selectedStopReason = Material(0, "Select Stop Reason")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_machine_status, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(2).isChecked = true

        myHelper = MyHelper(TAG, this)

        if(myHelper.getIsMachineStopped()){
            machine_start_layout.visibility = View.VISIBLE
            mstatus_stopped_reason.text = myHelper.getMachineStoppedReason()
            machine_stop_layout.visibility = View.GONE
        }else{
            machine_stop_layout.visibility = View.VISIBLE
            machine_start_layout.visibility = View.GONE
        }
        selectStopReason()
        mstatus_start.setOnClickListener(this)
        mstatus_stop.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.mstatus_stop ->{
                if(selectedStopReason.id == 0){
                    myHelper.toast("Please Select Machine Stop Reason.")
                }else {
                    myHelper.setIsMachineStopped(true , selectedStopReason.name)
                    myHelper.startHomeActivityByType()
                }
            }
            R.id.mstatus_start ->{
                    myHelper.setIsMachineStopped(false , "")
                    myHelper.startHomeActivityByType()

            }
        }
    }

    private fun selectStopReason() {
        var materials = myHelper.getMachineStopReasons()
        val selectMaterialAdapter = SelectStateAdapter(this@MachineStatusActivity, materials)
        mstop_reason_spinner!!.setAdapter(selectMaterialAdapter)
        mstop_reason_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        mstop_reason_spinner.setSelection(0, false)
        mstop_reason_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {

                selectedStopReason = materials.get(position)
                if (materials.get(position).id != 0) {
                    mstop_reason_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    mstop_reason_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, materials.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
}
