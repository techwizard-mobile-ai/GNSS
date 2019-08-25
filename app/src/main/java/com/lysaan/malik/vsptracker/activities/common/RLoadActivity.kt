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

class RLoadActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private val REQUEST_MACHINE= 1
    private val REQUEST_MATERIAL = 2
    private val REQUEST_LOCATION = 3
    private val REQUEST_WEIGHT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_rload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }


        helper.log("LastJourney:${helper.getLastJourney()}")

        val lastJourney = helper.getLastJourney()



        if(helper.getMachineType() == 2){
            trload_machine.visibility = View.GONE

            trload_material.text = lastJourney.loadingMaterial
            trload_location.text = lastJourney.loadingLocation
            trload_weight.text = "Tonnes  (" +lastJourney.loadedWeight +")"
        }else{
            trload_machine.visibility = View.VISIBLE
            trload_machine.text = lastJourney.loadingMachine
            trload_material.text = lastJourney.loadingMaterial
            trload_location.text = lastJourney.loadingLocation
            trload_weight.text = "Tonnes (" +lastJourney.loadedWeight +")"
        }

        rload_home.setOnClickListener(this)
        rload_finish.setOnClickListener(this)

        trload_load.setOnClickListener(this)
        trload_machine.setOnClickListener(this)
        trload_material.setOnClickListener(this)
        trload_location.setOnClickListener(this)
        trload_weight.setOnClickListener(this)

    }
    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.trload_load -> {

                val insertID = db.insertLoad(helper.getLastJourney())
                if(insertID > 0){
                    helper.toast("Loading Successful.\nLoaded Number # $insertID")
                    if(data.isRepeatJourney){
                        val intent = Intent(this, RUnloadActivity::class.java)
                        intent.putExtra("data", data)
                        startActivity(intent)
                        finish()
                    }else{
                        helper.startHomeActivityByType(data)
                    }

                }else{
                    helper.toast("Error while Saving Record.")
                }
            }
            R.id.trload_machine -> {
                val intent = Intent(this, LMachine1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MACHINE)
            }
            R.id.trload_material -> {
                val intent = Intent(this, Material1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_MATERIAL)
            }
            R.id.trload_location -> {
                val intent = Intent(this, Location1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_LOCATION)
            }
            R.id.trload_weight -> {
                val intent = Intent(this, Weight1Activity::class.java)
                val data1 = helper.getLastJourney()
                data1.isForLoadResult = true
                intent.putExtra("data", data1)
                startActivityForResult(intent,REQUEST_WEIGHT)
            }

            R.id.rload_home -> {
                helper.startHomeActivityByType(data)
            }
            R.id.rload_finish -> {
                helper.logout(this)
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

                if(helper.getMachineType() == 2){
                    trload_machine.visibility = View.GONE

                    trload_material.text = data.loadingMaterial
                    trload_location.text = data.loadingLocation
                    trload_weight.text = "Tonnes (" +data.loadedWeight +")"
                }else{
                    trload_machine.visibility = View.VISIBLE
                    trload_machine.text = data.loadingMachine
                    trload_material.text = data.loadingMaterial
                    trload_location.text = data.loadingLocation
                    trload_weight.text = "Tonnes (" +data.loadedWeight +")"
                }


                data.isForUnloadResult = false
                data.isForLoadResult = false
                helper.setLastJourney(data)

            }

        }else{
            helper.toast("Request can not be completed.")
        }
    }

}
