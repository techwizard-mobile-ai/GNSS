package com.lysaan.malik.vsptracker.activities.common


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.activity_location1.*

class Location1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_location1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        // nextAction 0 = Do Loading
        // nextAction 1 = Do Unloading
        // nextAction 2 = Do Back Loading
        // nextAction 3 = Do Back Unloading

        when(myData.nextAction){
            0-> location_title.text = "Select Loading Location"
            1-> location_title.text = "Select Unloading Location"
            2-> location_title.text = "Select Back Loading Location"
            3-> location_title.text = "Select Back Unloading Location"
        }


        val gv = findViewById(R.id.l_gridview) as GridView
//        val locations = myHelper.getLocations()
        val locations = db.getLocations()
//        locations.removeAt(0)
        val adapter = CustomGrid(this@Location1Activity, locations)


        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("Selected Location: " + locations.get(position).name)

            if (myData.isForLoadResult) {
                val intent = intent
                myData.loadingLocation = locations.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForUnloadResult) {
                val intent = intent
                myData.unloadingLocation = locations.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackLoadResult) {
                val intent = intent
                myData.backLoadingLocation = locations.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackUnloadResult) {
                val intent = intent
                myData.backUnloadingLocation = locations.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                //    type = 1 excavator
                //    type = 2 scrapper
                //    type = 3 truck
                when (myHelper.getMachineType()) {
                    1 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        myData.loadingLocation = locations.get(position).name
                        intent.putExtra("myData", myData)
                        startActivity(intent)

                    }
                    2, 3 -> {
                        when (myData.nextAction) {
                            0 -> {
                                myData.loadingLocation = locations.get(position).name
                                myData.unloadingMaterial = myData.loadingMaterial
                                myHelper.setLastJourney(myData)

                                val intent = Intent(this, RLoadActivity::class.java)
                                startActivity(intent)
                            }
                            1 -> {
                                myData.unloadingLocation = locations.get(position).name
                                myHelper.setLastJourney(myData)

                                val intent = Intent(this, RUnloadActivity::class.java)
                                startActivity(intent)
                            }
                            2 -> {
                                myData.backLoadingLocation = locations.get(position).name
                                myData.backUnloadingMaterial = myData.backLoadingMaterial
                                myHelper.setLastJourney(myData)

                                val intent = Intent(this, RLoadActivity::class.java)
                                startActivity(intent)
                            }
                            3 -> {
                                myData.backUnloadingLocation = locations.get(position).name
                                myData.backUnloadingMaterial = myData.backLoadingMaterial
                                myHelper.setLastJourney(myData)

                                val intent = Intent(this, RUnloadActivity::class.java)
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

