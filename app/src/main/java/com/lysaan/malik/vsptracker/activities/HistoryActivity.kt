package com.lysaan.malik.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.ELoadingHistoryAdapter
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_history, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(5).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        when (myHelper.getMachineTypeID()) {
            1 -> {
                lh_title.text = "Excavator Loading History"
            }
            2 -> {
                lh_title.text = "Scraper Loading History"
            }
            3 -> {
                lh_title.text = "Truck Loading History"
            }
            else -> {
                lh_title.text = "Machine Loading History"
            }
        }


        myHelper.log("Loads:${db.getELoadHistroy()}")

        val mAdapter = ELoadingHistoryAdapter(this@HistoryActivity, db.getELoadHistroy())
        lh_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        lh_rv!!.setAdapter(mAdapter)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {

        }
    }

}
