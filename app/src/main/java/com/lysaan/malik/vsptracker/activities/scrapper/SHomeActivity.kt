package com.lysaan.malik.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.Material1Activity
import com.lysaan.malik.vsptracker.activities.common.UnloadTaskActivity
import com.lysaan.malik.vsptracker.others.Data
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

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        shome_load.setOnClickListener(this)
        shome_unload.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.shome_load -> {
                val intent = Intent(this, Material1Activity::class.java)
                data.isUnload = false
                intent.putExtra("data", data)
                startActivity(intent)

            }
            R.id.shome_unload -> {

                val intent = Intent(this, UnloadTaskActivity::class.java)
                data.isUnload = true
                intent.putExtra("data", data)
                startActivity(intent)
            }
        }

    }
}
