package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.activity_rload.*
import kotlinx.android.synthetic.main.activity_runload.*

class RUnloadActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private val REQUEST_MACHINE= 1
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3
    private val REQUEST_WEIGHT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_runload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        helper.log("lastJourney:${helper.getLastJourney()}")

        val lastJourney = helper.getLastJourney()
//        var lastJourney = Data()
//        if(lastJourneyList.size > 0){
//            lastJourney = lastJourneyList.get(0)
//        }

        trul_task.text = "1. " +lastJourney.unloadingTask
        trul_material.text = "2. " +lastJourney.unloadingMaterial
        trul_location.text = "3. " +lastJourney.unloadingLocation
        trul_weight.text = "4. Tons ("+lastJourney.loadedWeight +")"

        trunload_unload.setOnClickListener(this)
        trul_task.setOnClickListener(this)
        trul_material.setOnClickListener(this)
        trul_location.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!.id){

            R.id.trunload_unload -> {
                val intent = Intent(this, RLoadActivity::class.java)
                intent.putExtra("data", helper.getLastJourney())
                startActivity(intent)
                finish()
            }
            R.id.trul_task -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MATERIAL)
            }
            R.id.trul_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MATERIAL)
            }
            R.id.trul_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForUnloadResult= true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_LOCATION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            var bundle :Bundle ?=intent!!.extras
            if(bundle != null){
                data = bundle!!.getSerializable("data") as Data
                helper.log("data:$data")

                trul_task.text = "1. " +data.unloadingTask
                trul_material.text = "2. " +data.unloadingMaterial
                trul_location.text = "3. " +data.unloadingLocation
                data.isForUnloadResult = false
                data.isForLoadResult = false
                helper.setLastJourney(data)

            }
        }else{
            helper.toast("Request can not be completed.")
        }

        helper.log("requestCode:$requestCode")
        helper.log("Result Code:$requestCode")
    }


}
