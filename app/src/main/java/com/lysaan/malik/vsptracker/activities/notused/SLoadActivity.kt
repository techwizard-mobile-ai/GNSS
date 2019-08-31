package com.lysaan.malik.vsptracker.activities.notused

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.scrapper.SHomeActivity
import com.lysaan.malik.vsptracker.adapters.SelectLocationAdapter
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.classes.Location
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.activity_sload.*


class SLoadActivity : BaseActivity(), View.OnClickListener {
    private var isRepeatJourney = false

    private var selectedLocation = Location(0, "Select Location")
    private var selectedMaterial = Material(0, "Select Material")
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_sload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        var bundle: Bundle? = intent.extras

        if (bundle != null) {
            isRepeatJourney = bundle!!.getBoolean("repeat")
            helper.log("isRepeat:$isRepeatJourney")
        }
        selectMaterial()
        selectLocation()

        helper.hideKeybaordOnClick(sload_main_layout)
        helper.hideKeybaordOnClick(sload_material_spinner)
        helper.hideKeybaordOnClick(sload_location_spinner)

//        sload_main_layout.setOnTouchListener(OnTouchListener { v, event ->
//            helper.hideKeyboard(sload_main_layout)
//            false
//        })
//
//        sload_material_spinner.setOnTouchListener(OnTouchListener { v, event ->
//            helper.hideKeyboard(sload_material_spinner)
//            false
//        })
//
//        sload_location_spinner.setOnTouchListener(OnTouchListener { v, event ->
//            helper.hideKeyboard(sload_location_spinner)
//            false
//        })

        sload_next.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.sload_next -> {
                val materialWeight = sload_weight.text.toString()

                if (selectedMaterial.id == 0) {
                    helper.toast("Please Select Material")
                } else if (selectedLocation.id == 0) {
                    helper.toast("Please Select Location")
                }
//                else if (materialWeight.isNullOrBlank()){
//                    helper.toast("Please Enter Material Weight")
//                }
                else {
                    val intent = Intent(this, SHomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }


    private fun selectMaterial() {
        var materials = helper.getMaterials()
        val selectMaterialAdapter = SelectStateAdapter(this@SLoadActivity, materials)

        sload_material_spinner!!.setAdapter(selectMaterialAdapter)

        if (isRepeatJourney) {
            sload_material_spinner.setSelection(1)
        } else {
            sload_material_spinner.setSelection(0, false)
        }

        sload_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        sload_material_spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                            arg0: AdapterView<*>, arg1: View,
                            position: Int, arg3: Long
                    ) {

                        selectedMaterial = materials.get(position)
                        if (materials.get(position).id != 0) {
                            sload_material_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                        } else {
                            sload_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                        }
                        Log.e(TAG, materials.get(position).toString())
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
    }

    private fun selectLocation() {
        var locations = helper.getLocations()
        val locationAdapter = SelectLocationAdapter(this@SLoadActivity, locations)
        sload_location_spinner!!.setAdapter(locationAdapter)

        if (isRepeatJourney) {
            helper.log("inSelectLocation")
            sload_location_spinner.setSelection(1)
        } else {
            sload_location_spinner.setSelection(0, false)
        }

        sload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))

        sload_location_spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                            arg0: AdapterView<*>, arg1: View,
                            position: Int, arg3: Long
                    ) {

                        selectedLocation = locations.get(position)
                        if (locations.get(position).id != 0) {
                            sload_location_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                        } else {
                            sload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                        }
                        Log.e(TAG, locations.get(position).toString())
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
    }
}
