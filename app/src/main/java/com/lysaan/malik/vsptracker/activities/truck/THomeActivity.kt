package com.lysaan.malik.vsptracker.activities.truck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.LMachine1Activity
import com.lysaan.malik.vsptracker.activities.common.UnloadTaskActivity
import com.lysaan.malik.vsptracker.adapters.LoadingHistoryAdapter
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.activity_thome.*
import kotlinx.android.synthetic.main.fragment_loading_history.*

class THomeActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_thome, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            data = bundle!!.getSerializable("data") as Data
//            helper.log("data:$data")
//        }

        data = helper.getLastJourney()
        helper.log("data:$data")

        when (helper.getNextAction()) {
            0, 2 -> {
                helper.setToDoLayout(thome_load_button)
            }
            1, 3 -> {
                helper.setToDoLayout(thome_unload_button)
            }
        }

        thome_logout.setOnClickListener(this)
        thome_load.setOnClickListener(this)
        thome_unload.setOnClickListener(this)
    }

//    override fun onResume() {
//        super.onResume()
//        startGPS()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopGPS()
//    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.thome_load -> {
                val intent = Intent(this, LMachine1Activity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.thome_unload -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.thome_logout -> {

//                helper.log("LocationLat:${latitude}")
//                helper.log("LocationLongg:${longitude}")
//                helper.log("Loads:${db.getTrips()}")

                helper.logout(this)
            }
        }

    }

}
