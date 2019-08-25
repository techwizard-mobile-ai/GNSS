package com.lysaan.malik.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R

import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.activity_eselect_work.*

class ESelectWorkActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_eselect_work, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        eswork_sidecasting.setOnClickListener(this)
        eswork_off_loading.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.eswork_sidecasting -> {
                val intent = Intent(this, ESideCastingActivity::class.java)
                data.eWorkActionType = 1
                intent.putExtra("data", data)
                startActivity(intent)
            }
            R.id.eswork_off_loading -> {
                val intent = Intent(this, EOffLoadingActivity::class.java)
                data.eWorkActionType = 2
                intent.putExtra("data", data)
                startActivity(intent)
            }
        }

    }
}
