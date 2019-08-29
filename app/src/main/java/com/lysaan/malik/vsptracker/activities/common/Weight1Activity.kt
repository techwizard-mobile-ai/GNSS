package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_weight1.*

class Weight1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_weight1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        w1_next.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.w1_next -> {

                if(data.isForLoadResult){
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else if (data.isForUnloadResult){
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else if(data.isForBackLoadResult){
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.backUnloadedWeight = sload_weight.toDouble()
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else if (data.isForBackUnloadResult){
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.backUnloadedWeight = sload_weight.toDouble()
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else if(data.isForBackLoad){
                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.backUnloadedWeight = sload_weight.toDouble()
                    helper.log("$data")

                    val data1 = helper.getLastJourney()
                    data1.backLoadingMachine = data.backLoadingMachine
                    data1.backLoadingMaterial = data.backLoadingMaterial
                    data1.backLoadingLocation = data.backLoadingLocation
                    data1.backUnloadedWeight = data.backUnloadedWeight
                    helper.setLastJourney(data1)

                    val intent = Intent(this, RLoadActivity::class.java)
                    intent.putExtra("data", data)
                    startActivity(intent)
                }else{

                    val sload_weight = sload_weight.text.toString()
                    if(!sload_weight.isNullOrBlank())
                        data.unloadingWeight = sload_weight.toDouble()
                        helper.log("$data")

                    val data1 = helper.getLastJourney()
                    data1.loadingMachine = data.loadingMachine
                    data1.loadingMaterial = data.loadingMaterial
                    data1.loadingLocation = data.loadingLocation
                    data1.unloadingWeight = data.unloadingWeight
                    helper.setLastJourney(data1)

                    val intent = Intent(this, RLoadActivity::class.java)
                    intent.putExtra("data", data)
                    startActivity(intent)

                }

            }
        }
    }
}
