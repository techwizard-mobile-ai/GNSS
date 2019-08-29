package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_location1.*

class Location1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_location1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        val gv = findViewById(R.id.l_gridview) as GridView
        val locations = helper.getLocations1()
        locations.removeAt(0)
        val adapter = CustomGrid(this@Location1Activity, locations)


        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            helper.toast("Selected Location: " + locations.get(position).name)

            if (data.isForLoadResult) {
                val intent = intent
                data.loadingLocation = locations.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (data.isForUnloadResult) {
                val intent = intent
                data.unloadingLocation = locations.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (data.isForBackLoadResult) {
                val intent = intent
                data.backLoadingLocation = locations.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (data.isForBackUnloadResult) {
                val intent = intent
                data.backUnloadingLocation = locations.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                //    type = 1 excavator
                //    type = 2 scrapper
                //    type = 3 truck
                when (helper.getMachineType()) {
                    1 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        data.loadingLocation = locations.get(position).name
                        intent.putExtra("data", data)
                        startActivity(intent)

                    }
//                    2 -> {
//                        when(data.nextAction){
//                            0 -> {
//                                data.loadingLocation = locations.get(position).name
//                                helper.setLastJourney(data)
//
//
//                                val intent = Intent(this, RLoadActivity::class.java)
////                                intent.putExtra("data", data)
//                                startActivity(intent)
//                            }
//                            1 -> {
//                                data.unloadingLocation = locations.get(position).name
//                                data.unloadingMaterial = data.loadingMaterial
//                                helper.setLastJourney(data)
////                                helper.toast("Journey Saved.")
//
//                                val intent = Intent(this, RUnloadActivity::class.java)
////                                intent.putExtra("data", data)
//                                startActivity(intent)
//                            }
//                        }
//                    }
                    2, 3 -> {
                        when (data.nextAction) {
                            0 -> {
                                data.loadingLocation = locations.get(position).name
                                data.unloadingMaterial = data.loadingMaterial
                                helper.setLastJourney(data)

                                val intent = Intent(this, RLoadActivity::class.java)
//                                intent.putExtra("data", data)
                                startActivity(intent)
                            }
                            1 -> {
                                data.unloadingLocation = locations.get(position).name
                                helper.setLastJourney(data)

                                val intent = Intent(this, RUnloadActivity::class.java)
//                                intent.putExtra("data", data)
                                startActivity(intent)
                            }
                            2 -> {
                                data.backLoadingLocation = locations.get(position).name
                                data.backUnloadingMaterial = data.backLoadingMaterial
                                helper.setLastJourney(data)

                                val intent = Intent(this, RLoadActivity::class.java)
//                                intent.putExtra("data", data)
                                startActivity(intent)
                            }
                            3 -> {
                                data.backUnloadingLocation = locations.get(position).name
                                data.backUnloadingMaterial = data.backLoadingMaterial
                                helper.setLastJourney(data)

                                val intent = Intent(this, RUnloadActivity::class.java)
//                                intent.putExtra("data", data)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

        })

        elocation1_back.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.elocation1_back -> {
                finish()
            }
        }
    }
}

