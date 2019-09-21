package com.lysaan.malik.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.activities.common.UnloadTaskActivity
import com.lysaan.malik.vsptracker.activities.excavator.ESideCastingActivity
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_shome.*

class SHomeActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_shome, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")

        when (myHelper.getNextAction()) {
            0, 2 -> {
                myHelper.setToDoLayout(thome_load_button)
            }
            1, 3 -> {
                myHelper.setToDoLayout(thome_unload_button)
            }
        }

        shome_logout.setOnClickListener(this)
        thome_load.setOnClickListener(this)
        thome_unload.setOnClickListener(this)
        shome_trimming.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.shome_trimming ->{
                val intent = Intent(this, ESideCastingActivity::class.java)
                myData.eWorkType = 3
                myData.eWorkActionType = 1
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_load -> {
                val intent = Intent(this, Material1Activity::class.java)
//                    TODO Delete it
//                myData.isUnload = false
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_unload -> {
//                val intent = Intent(this, Location1Activity::class.java)
                val intent = Intent(this, UnloadTaskActivity::class.java)
                if (myData == null) {
                    myData = MyData()
                }
//                TODO Delete it
//                myData.isUnload = true
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.shome_logout -> {
                myHelper.logout(this)
            }
        }

    }
}
