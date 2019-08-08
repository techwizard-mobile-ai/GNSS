package com.lysaan.malik.vsptracker.activities.excavator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.Toast
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid

class ELocation1Activity : BaseActivity(), View.OnClickListener {


    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_elocation1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        val gv = findViewById(R.id.gridview) as GridView
        val adapter = CustomGrid(this@ELocation1Activity, myHelper.getLocations1())

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("You Clicked at " + myHelper.getLocations().get(position).name)
        })

    }

    override fun onClick(view: View?) {
        when(view!!.id){
//            R.id.ehome_next -> {
//                val intent = Intent(this@EHome1Activity, ELoadTruckActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}

