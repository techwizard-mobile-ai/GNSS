package com.lysaan.malik.vsptracker.activities.excavator

import android.content.Intent
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

class EHome1Activity : BaseActivity(), View.OnClickListener {


    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    var grid: GridView? = null
    var web = arrayOf(
        "Material 1",
        "Material 2",
        "Material 3",
        "Material 4",
        "Material 5",
        "Other 1"
    )
    var imageId = intArrayOf(
        R.drawable.scraper,
        R.drawable.truck,
        R.drawable.welcome,
        R.drawable.excavator,
        R.drawable.scraper,
        R.drawable.truck
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_ehome1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        val gv = findViewById(R.id.gridview) as GridView
        val adapter = CustomGrid(this@EHome1Activity, myHelper.getMaterials())

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("You Clicked at " + myHelper.getMaterials().get(position).name)
            val intent = Intent(this, ELocation1Activity::class.java)
            startActivity(intent)
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
