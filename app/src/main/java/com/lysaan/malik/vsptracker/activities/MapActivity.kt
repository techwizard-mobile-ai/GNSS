package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R


//class MapActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback {
class MapActivity : BaseActivity(), View.OnClickListener{
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_map, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
        }
    }

}
