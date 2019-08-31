package com.lysaan.malik.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.Location1Activity
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_shome.*

class SHomeActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_shome, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

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

        shome_logout.setOnClickListener(this)
        thome_load.setOnClickListener(this)
        thome_unload.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.thome_load -> {
                val intent = Intent(this, Material1Activity::class.java)
//                    TODO Delete it
//                data.isUnload = false
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.thome_unload -> {
                val intent = Intent(this, Location1Activity::class.java)
                if (data == null) {
                    data = Data()
                }
//                TODO Delete it
//                data.isUnload = true
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.shome_logout -> {
                helper.logout(this)
            }
        }

    }
}
