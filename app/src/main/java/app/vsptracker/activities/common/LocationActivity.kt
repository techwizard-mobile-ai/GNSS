package app.vsptracker.activities.common


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGrid
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_location, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(tag)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        // nextAction 0 = Do Loading
        // nextAction 1 = Do Unloading
        // nextAction 2 = Do Back Loading
        // nextAction 3 = Do Back Unloading

        when(myData.nextAction){
            0-> location_title.text = getString(R.string.select_loading_location)
            1-> location_title.text = getString(R.string.select_unloading_location)
            2-> location_title.text = getString(R.string.select_back_loading_location)
            3-> location_title.text = getString(R.string.select_back_unloading_location)
        }


        val gv = findViewById<GridView>(R.id.l_gridview)
        val locations = db.getLocations()
//        myHelper.log("Locations:$locations")
        val adapter = CustomGrid(this@LocationActivity, locations)


        gv.adapter = adapter
        gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            myHelper.toast("Selected Location: " + locations[position].name)

            when {
                myData.isForLoadResult -> {
                    val intent = intent
                    myData.loadingLocation = locations[position].name
                    myData.loading_location_id = locations[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForUnloadResult -> {
                    val intent = intent
                    myData.unloadingLocation = locations[position].name
                    myData.unloading_location_id= locations[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForBackLoadResult -> {
                    val intent = intent
                    myData.backLoadingLocation = locations[position].name
                    myData.back_loading_location_id = locations[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForBackUnloadResult -> {
                    val intent = intent
                    myData.backUnloadingLocation = locations[position].name
                    myData.back_unloading_location_id= locations[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> //    machineTypeId = 1 excavator
                    //    machineTypeId = 2 scrapper
                    //    machineTypeId = 3 truck
                    when (myHelper.getMachineTypeID()) {
                        1 -> {
                            val intent = Intent(this, MaterialActivity::class.java)
                            myData.loadingLocation = locations[position].name
                            myData.loading_location_id = locations[position].id
                            intent.putExtra("myData", myData)
                            startActivity(intent)

                        }
                        2, 3 -> {
                            when (myData.nextAction) {
                                0 -> {
                                    myData.loadingLocation = locations[position].name
                                    myData.loading_location_id = locations[position].id

                                    myHelper.setLastJourney(myData)

                                    val intent = Intent(this, RLoadActivity::class.java)
                                    startActivity(intent)
                                }
                                1 -> {
                                    myData.unloadingLocation = locations[position].name
                                    myData.unloading_location_id= locations[position].id

                                    myData.unloadingMaterial = myData.loadingMaterial
                                    myData.unloading_material_id = myData.loading_material_id

                                    myHelper.setLastJourney(myData)

                                    val intent = Intent(this, RUnloadActivity::class.java)
                                    startActivity(intent)
                                }
                                2 -> {
                                    myData.backLoadingLocation = locations[position].name
                                    myData.back_loading_location_id = locations[position].id
                                    myHelper.setLastJourney(myData)

                                    val intent = Intent(this, RLoadActivity::class.java)
                                    startActivity(intent)
                                }
                                3 -> {
                                    myData.backUnloadingLocation = locations[position].name
                                    myData.back_unloading_location_id= locations[position].id
                                    myData.backUnloadingMaterial = myData.backLoadingMaterial
                                    myData.back_unloading_material_id= myData.back_loading_material_id
                                    myHelper.setLastJourney(myData)

                                    val intent = Intent(this, RUnloadActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
            }

        }

        elocation1_back.setOnClickListener(this)
    }
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.elocation1_back -> {
                finish()
            }
        }
    }
}

