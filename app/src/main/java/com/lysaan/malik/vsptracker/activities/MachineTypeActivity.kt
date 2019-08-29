package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.activity_machine_type.*

class MachineTypeActivity : AppCompatActivity(), View.OnClickListener {

    private var selectedMachineType = Material(0, "Select Machine Type")
    private var selectedMachineLocation = Material(0, "Select Machine Location")

    private lateinit var helper: Helper
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_type)


//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        helper = Helper(TAG, this)

        selectMachineType()
        selectMachineLocation()

        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
            helper.hideKeyboard(machine_type_main_layout)
            false
        })

        machine_type.setOnTouchListener(View.OnTouchListener { v, event ->
            helper.hideKeyboard(machine_type)
            false
        })

        machine_location.setOnTouchListener(View.OnTouchListener { v, event ->
            helper.hideKeyboard(machine_location)
            false
        })

        machine_save.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.machine_save -> {

                val machineNumber = machine_number.text.toString()
                if (selectedMachineType.id == 0) {
                    helper.toast("Please Select Machine Type")
                } else if (machineNumber.isNullOrBlank()) {
                    helper.toast("Please enter a valid Machine Number")
                } else if (selectedMachineLocation.id == 0) {
                    helper.toast("Please Select Machine Location")
                } else {
                    helper.setMachineType(selectedMachineType.id)
                    helper.setMachineNumber(machineNumber)

//                    helper.toast("Data Saved Successfully.")
                    helper.stopDailyMode()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }


    private fun selectMachineType() {
        var machineTypes = helper.getMachineTypes()
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        machine_type!!.setAdapter(selectMaterialAdapter)
        machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_type.setSelection(0, false)
        machine_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineType = machineTypes.get(position)
                if (machineTypes.get(position).id != 0) {
                    machine_type.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun selectMachineLocation() {
        var machineTypes = helper.getMachineLocations()
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        machine_location!!.setAdapter(selectMaterialAdapter)
        machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_location.setSelection(0, false)
        machine_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineLocation = machineTypes.get(position)
                if (machineTypes.get(position).id != 0) {
                    machine_location.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

}
