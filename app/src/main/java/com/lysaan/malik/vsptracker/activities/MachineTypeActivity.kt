package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.SelectMachineNumberAdapter
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.classes.MyData
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.activity_machine_type.*

class MachineTypeActivity : AppCompatActivity(), View.OnClickListener {

    private var selectedMachineType = Material(0, "Select Machine Type")
    private var selectedMachineLocation = Material(0, "Select Machine Location")
    private var selectedMachineNumber = Material(0, "Select Machine Number")
    protected lateinit var db: DatabaseAdapter
    private lateinit var myHelper: MyHelper
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_type)

        myHelper = MyHelper(TAG, this)
        db = DatabaseAdapter(this)

        selectMachineType()
        selectMachineLocation()
        selectMachineNumber()

        machine_number1.isEnabled = false

        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type_main_layout)
            false
        })

        machine_type.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type)
            false
        })

        machine_location.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_location)
            false
        })

        machine_number1.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_number1)
            false
        })

        machine_save.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.machine_save -> {

                val machineNumber = machine_number.text.toString()
                if (selectedMachineType.id == 0) {
                    myHelper.toast("Please Select Machine Type")
                }
//                else if (machineNumber.isNullOrBlank()) {
//                    myHelper.toast("Please enter a valid Machine Number")
//                }
                else if (selectedMachineLocation.id == 0) {
                    myHelper.toast("Please Select Machine Location")
                } else if (selectedMachineNumber.id == 0) {
                    myHelper.toast("Please Select Machine Number")
                } else {
                    myHelper.setMachineType(selectedMachineType.id)
                    myHelper.setMachineNumber(machineNumber)

                    myHelper.startHomeActivityByType(MyData())
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }


    private fun selectMachineType() {
        var machineTypes = myHelper.getMachineTypes()
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
                    selectMachineNumber()
                    machine_number1.isEnabled = true
                } else {
                    machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                    selectMachineNumber()
                    machine_number1.isEnabled = false
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun selectMachineLocation() {
        var machineTypes = myHelper.getMachineLocations()
        machineTypes = db.getLocations()
        machineTypes.add(0, Material(0, "Select Machine Location"))
//        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        val selectMaterialAdapter = SelectStateAdapter(this, machineTypes)
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


    private fun selectMachineNumber() {
//        var machineTypes = myHelper.getMachineLocations()
        val machineTypes = db.getMachines(selectedMachineType.id)
        val machine = Material(0, "")
        machine.number = "Select Machine Number"
        machineTypes.add(0, machine)
//        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        val selectMaterialAdapter = SelectMachineNumberAdapter(this@MachineTypeActivity, machineTypes)
        machine_number1!!.setAdapter(selectMaterialAdapter)
        machine_number1.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_number1.setSelection(0, false)
        machine_number1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineNumber = machineTypes.get(position)
                if (machineTypes.get(position).id != 0) {
                    machine_number1.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_number1.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

}
