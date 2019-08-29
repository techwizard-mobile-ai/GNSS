package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.ELoadingHistoryAdapter
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_history, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(5).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle.getSerializable("data") as Data
            helper.log("data:$data")
        }

        when (helper.getMachineType()) {
            1 -> {
                lh_title.text = "Excavator Loading History"
            }
            2 -> {
                lh_title.text = "Scrapper Loading History"
            }
            3 -> {
                lh_title.text = "Truck Loading History"
            }
            else -> {
                lh_title.text = "Machine Loading History"
            }
        }


        helper.log("Loads:${db.getELoadHistroy()}")

        val mAdapter = ELoadingHistoryAdapter(this@HistoryActivity, db.getELoadHistroy())
        lh_rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        lh_rv!!.setAdapter(mAdapter)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {

        }
    }

}
