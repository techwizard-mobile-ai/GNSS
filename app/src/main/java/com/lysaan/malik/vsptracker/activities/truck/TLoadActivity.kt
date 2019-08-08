package com.lysaan.malik.vsptracker.activities.truck

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.SelectLocationAdapter
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.classes.Location
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.activity_tload.*

class TLoadActivity : BaseActivity(), View.OnClickListener {
    private var isRepeatJourney = false

    private var selectedLocation = Location(0, "Select Location")
    private var selectedMaterial = Material(0, "Select Material")
    private var selectedMachine = Material(0, "Select Machine")

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_tload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            isRepeatJourney = bundle!!.getBoolean("repeat")
            myHelper.log("isRepeat:$isRepeatJourney")
        }

        selectMachine()
        selectMaterial()
        selectLocation()

        myHelper.hideKeybaordOnClick(tload_machine_spinner)
        myHelper.hideKeybaordOnClick(tload_main_layout)
        myHelper.hideKeybaordOnClick(tload_material_spinner)
        myHelper.hideKeybaordOnClick(tload_location_spinner)

//        tload_machine_spinner.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(tload_machine_spinner)
//            false
//        })

//        tload_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(tload_main_layout)
//            false
//        })
//
//        tload_material_spinner.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(tload_material_spinner)
//            false
//        })
//
//        tload_location_spinner.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(tload_location_spinner)
//            false
//        })

        tload_next.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.tload_next -> {
                val materialWeight = tload_weight.text.toString()

                if(selectedMachine.id == 0){
                    myHelper.toast("Please Select Machine")
                }else if(selectedMaterial.id == 0){
                    myHelper.toast("Please Select Material")
                }else if (selectedLocation.id == 0){
                    myHelper.toast("Please Select Location")
                }
//                else if (materialWeight.isNullOrBlank()){
//                    myHelper.toast("Please Enter Material Weight")
//                }
                else {
                    val intent = Intent (this, THomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }



    private fun selectMachine() {
        var materials = myHelper.getMachines()
        val selectMaterialAdapter = SelectStateAdapter(this@TLoadActivity, materials)

        tload_machine_spinner!!.setAdapter(selectMaterialAdapter)

        if(isRepeatJourney)
        { tload_machine_spinner.setSelection(1)}
        else{
            tload_machine_spinner.setSelection(0, false)
        }

        tload_machine_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        tload_machine_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {

                selectedMachine = materials.get(position)
                if (materials.get(position).id != 0) {
                    tload_machine_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    tload_machine_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, materials.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }


    private fun selectMaterial() {
        var materials = myHelper.getMaterials()
        val selectMaterialAdapter = SelectStateAdapter(this@TLoadActivity, materials)

        tload_material_spinner!!.setAdapter(selectMaterialAdapter)

        if(isRepeatJourney)
        { tload_material_spinner.setSelection(1)}
        else{
            tload_material_spinner.setSelection(0, false)
        }

        tload_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        tload_material_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {

                selectedMaterial = materials.get(position)
                if (materials.get(position).id != 0) {
                    tload_material_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    tload_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, materials.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
    private fun selectLocation() {
        var locations = myHelper.getLocations()
        val locationAdapter = SelectLocationAdapter(this@TLoadActivity, locations)
        tload_location_spinner!!.setAdapter(locationAdapter)

        if(isRepeatJourney){
            myHelper.log("inSelectLocation")
            tload_location_spinner.setSelection(1)
        }else{
            tload_location_spinner.setSelection(0, false)
        }

        tload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))

        tload_location_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {

                selectedLocation = locations.get(position)
                if (locations.get(position).id != 0) {
                    tload_location_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    tload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, locations.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
}
