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
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.activity_machine_type.*
import java.util.*

class MachineTypeActivity : AppCompatActivity(), View.OnClickListener {

    private var machineLocations =  ArrayList<Material>()
    private var selectedMachineSite = Material(0, "Select Machine Site")
    private var selectedMachineType = Material(0, "Select Machine Type")
    private var selectedMachineLocation = Material(0, "Select Machine Location")
    private var selectedMachineNumber = Material(0, "Select Machine Number")
    protected lateinit var db: DatabaseAdapter
    private lateinit var myHelper: MyHelper
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_type)

        myHelper = MyHelper(TAG, this@MachineTypeActivity)
        db = DatabaseAdapter(this)

//        var machineTypes = myHelper.getMachineLocations()

        selectMachineSite()
        selectMachineType()
//        selectMachineLocation()
        selectMachineNumber()

        machine_number1.isEnabled = enableList()
        machine_location.isEnabled = enableList()
        myHelper.log("enableList${enableList()}")

        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type_main_layout)
            false
        })

        mt_site.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type)
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
                if (selectedMachineSite.id == 0) {
                    myHelper.toast("Please Select Site")
                }else if (selectedMachineType.id == 0) {
                    myHelper.toast("Please Select Machine Type")
                }
//                else if (selectedMachineLocation.id == 0) {
//                    myHelper.toast("Please Select Machine Location")
//                }
                else if (selectedMachineNumber.id == 0) {
                    myHelper.toast("Please Select Machine Number")
                } else {
                    myHelper.setMachineTypeID(selectedMachineType.id)
                    myHelper.setMachineNumber(selectedMachineNumber.number)
                    myHelper.setMachineID(selectedMachineNumber.id)

                    myHelper.startHomeActivityByType(MyData())
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }

    private fun selectMachineSite() {
        var machineTypes = db.getSites()
        machineTypes.add(0, Material(0, "Select Site"))
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        mt_site!!.setAdapter(selectMaterialAdapter)
        mt_site.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        mt_site.setSelection(0, false)
        mt_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineSite = machineTypes.get(position)
                val machineSettings = myHelper.getMachineSettings()
                machineSettings.siteId = selectedMachineSite.id
                myHelper.setMachineSettings(machineSettings)
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                selectMachineNumber()
//                selectMachineLocation()

                if (machineTypes.get(position).id != 0) {
                    mt_site.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    mt_site.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))

                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun enableList() = (selectedMachineSite.id >0 && selectedMachineType.id > 0)
    private fun selectMachineType() {
        var machineTypes = db.getMachinesTypes()
        machineTypes.add(0, Material(0, "Select Machine Type"))
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
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                if (machineTypes.get(position).id != 0) {
                    machine_type.setBackground(resources.getDrawable(R.drawable.spinner_border))
                    selectMachineNumber()
                } else {
                    machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                    selectMachineNumber()
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
    private fun selectMachineLocation() {

        machineLocations = db.getLocations()
        myHelper.log("machineLocations:$machineLocations")
        machineLocations.add(0, Material(0, "Select Machine Location"))
//        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        val selectMaterialAdapter = SelectStateAdapter(this, machineLocations)
        machine_location!!.setAdapter(selectMaterialAdapter)
        machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_location.setSelection(0, false)
        machine_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineLocation = machineLocations.get(position)
                if (machineLocations.get(position).id != 0) {
                    machine_location.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineLocations.get(position).toString())
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
