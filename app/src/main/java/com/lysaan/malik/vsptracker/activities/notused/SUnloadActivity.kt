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
import com.lysaan.malik.vsptracker.activities.scrapper.SUnloadAfterActivity
import com.lysaan.malik.vsptracker.adapters.SelectLocationAdapter
import com.lysaan.malik.vsptracker.classes.Location
import kotlinx.android.synthetic.main.activity_sunload.*

class SUnloadActivity : BaseActivity(), View.OnClickListener {
    private var selectedLocation = Location(0, "Select Location")

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_sunload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        selectLocation()

        sunload_next.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.sunload_next -> {
                if (selectedLocation.id == 0) {
                    helper.toast("Please Select Location")
                } else {
                    val intent = Intent(this, SUnloadAfterActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

    private fun selectLocation() {
        var locations = helper.getLocations()
        val locationAdapter = SelectLocationAdapter(this@SUnloadActivity, locations)
        sunload_location_spinner!!.setAdapter(locationAdapter)
        sunload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        sunload_location_spinner.setSelection(0, false)
        sunload_location_spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                            arg0: AdapterView<*>, arg1: View,
                            position: Int, arg3: Long
                    ) {

                        selectedLocation = locations.get(position)
                        if (locations.get(position).id != 0) {
                            sunload_location_spinner.setBackground(resources.getDrawable(R.drawable.spinner_border))
                        } else {
                            sunload_location_spinner.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                        }
                        Log.e(TAG, locations.get(position).toString())
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {

                    }
                }
    }
}