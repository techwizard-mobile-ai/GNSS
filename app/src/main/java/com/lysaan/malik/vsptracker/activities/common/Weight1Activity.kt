package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import kotlinx.android.synthetic.main.activity_weight1.*

class Weight1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_weight1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        w1_next.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.w1_next -> {

                if (myData.isForLoadResult) {
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if (!sload_weight.isNullOrBlank())
                        myData.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForUnloadResult) {
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if (!sload_weight.isNullOrBlank())
                        myData.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackLoadResult) {
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if (!sload_weight.isNullOrBlank())
                        myData.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (myData.isForBackUnloadResult) {
                    val intent = intent
                    val sload_weight = sload_weight.text.toString()
                    if (!sload_weight.isNullOrBlank())
                        myData.unloadingWeight = sload_weight.toDouble()
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val sload_weight = sload_weight.text.toString()
                    if (!sload_weight.isNullOrBlank()) {
                        myData.unloadingWeight = sload_weight.toDouble()
                    }
                    myHelper.log("$myData")

                    when (myData.nextAction) {
                        0 -> {
//                            val sload_weight = sload_weight.text.toString()
//                            if (!sload_weight.isNullOrBlank())
//                                myData.unloadingWeight = sload_weight.toDouble()
//                            myHelper.log("$myData")

                            val data1 = myHelper.getLastJourney()
                            data1.loadingMachine = myData.loadingMachine
                            data1.loadingMaterial = myData.loadingMaterial
                            data1.loadingLocation = myData.loadingLocation
                            data1.unloadingWeight = myData.unloadingWeight
                            myHelper.setLastJourney(data1)

                            val intent = Intent(this, RLoadActivity::class.java)
                            intent.putExtra("myData", myData)
                            startActivity(intent)
                        }
                        2 -> {


                            val data1 = myHelper.getLastJourney()
                            data1.loadingMachine = myData.loadingMachine
                            data1.loadingMaterial = myData.loadingMaterial
                            data1.loadingLocation = myData.loadingLocation
                            data1.unloadingWeight = myData.unloadingWeight
                            myHelper.setLastJourney(data1)

                            val intent = Intent(this, RLoadActivity::class.java)
                            intent.putExtra("myData", myData)
                            startActivity(intent)
                        }
                    }
                }
//                if (myData.isForBackLoad) {
//                    val sload_weight = sload_weight.text.toString()
//                    if (!sload_weight.isNullOrBlank())
//                        myData.unloadingWeight = sload_weight.toDouble()
//                    myHelper.log("$myData")
//
//                    val data1 = myHelper.getLastJourney()
//                    data1.loadingMachine = myData.loadingMachine
//                    data1.loadingMaterial = myData.loadingMaterial
//                    data1.loadingLocation = myData.loadingLocation
//                    data1.unloadingWeight = myData.unloadingWeight
//                    myHelper.setLastJourney(data1)
//
//                    val intent = Intent(this, RLoadActivity::class.java)
//                    intent.putExtra("myData", myData)
//                    startActivity(intent)
//                } else {
//
//                    val sload_weight = sload_weight.text.toString()
//                    if (!sload_weight.isNullOrBlank())
//                        myData.unloadingWeight = sload_weight.toDouble()
//                    myHelper.log("$myData")
//
//                    val data1 = myHelper.getLastJourney()
//                    data1.loadingMachine = myData.loadingMachine
//                    data1.loadingMaterial = myData.loadingMaterial
//                    data1.loadingLocation = myData.loadingLocation
//                    data1.unloadingWeight = myData.unloadingWeight
//                    myHelper.setLastJourney(data1)
//
//                    val intent = Intent(this, RLoadActivity::class.java)
//                    intent.putExtra("myData", myData)
//                    startActivity(intent)
//
//                }

            }
        }
    }
}
