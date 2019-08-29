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
import com.lysaan.malik.vsptracker.activities.excavator.ELoadActivity
import com.lysaan.malik.vsptracker.adapters.SelectLocationAdapter
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import kotlinx.android.synthetic.main.activity_ehomeold.*

class EHomeOldActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_ehomeold, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        selectMaterial()
        selectLocation()
        ehome_next.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.ehome_next -> {
                val intent = Intent(this@EHomeOldActivity, ELoadActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun selectMaterial() {
        var materials = helper.getMaterials()
        val selectMaterialAdapter = SelectStateAdapter(this@EHomeOldActivity, materials)
        ehome_material_spinner!!.setAdapter(selectMaterialAdapter)
        ehome_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        ehome_material_spinner.setSelection(0, false)
        ehome_material_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
                ) {

//                selectedState = states.get(position)
                    if (materials.get(position).id != 0) {
                        ehome_material_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                    } else {
                        ehome_material_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                    }
                    Log.e(TAG, materials.get(position).toString())
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }
    }

    private fun selectLocation() {
        var locations = helper.getLocations()
        val locationAdapter = SelectLocationAdapter(this@EHomeOldActivity, locations)
        ehome_location_spinner!!.setAdapter(locationAdapter)
        ehome_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        ehome_location_spinner.setSelection(0, false)
        ehome_location_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
                ) {
//                selectedState = states.get(position)
                    if (locations.get(position).id != 0) {
                        ehome_location_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                    } else {
                        ehome_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                    }
                    Log.e(TAG, locations.get(position).toString())
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }
    }
}
