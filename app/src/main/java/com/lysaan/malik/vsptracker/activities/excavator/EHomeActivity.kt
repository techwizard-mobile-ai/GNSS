package com.lysaan.malik.vsptracker.activities.excavator

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.Location1Activity
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_ehome.*

class EHomeActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_ehome, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }
        ehome_logout.setOnClickListener(this)
        ehome_loading.setOnClickListener(this)
        ehome_trenching.setOnClickListener(this)
        ehome_digging.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.ehome_loading -> {
                val intent = Intent(this, Location1Activity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }

            R.id.ehome_trenching -> {
                val intent = Intent(this, ESelectWorkActivity::class.java)
                data.eWorkType = 2
                intent.putExtra("data", data)
                startActivity(intent)
            }

            R.id.ehome_digging -> {
                val intent = Intent(this, ESelectWorkActivity::class.java)
                data.eWorkType = 1
                intent.putExtra("data", data)
                startActivity(intent)
            }

            R.id.ehome_logout -> {
                helper.logout(this)
            }
        }
    }


}
