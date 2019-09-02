package com.lysaan.malik.vsptracker.activities.common


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.activity_lmachine1.*


class LMachine1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_lmachine1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
//        myData = myHelper.getLastJourney()

        when (myData.nextAction) {
            0 -> {
                lm_title.text = "Select Loading Machine"
            }
            1 -> {
                lm_title.text = "Select Unloading Machine"
            }
            2 -> {
                lm_title.text = "Select Back Loading Machine"
            }
            3 -> {
                lm_title.text = "Select Back Unloading Machine"
            }
        }

        val gv = findViewById(R.id.tlm_gridview) as GridView
        val machines = myHelper.getMachines()
        machines.removeAt(0)
        val adapter = CustomGrid(this@LMachine1Activity, machines)

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("Selected Machine: " + machines.get(position).name)

            if (myData.isForLoadResult) {
                val intent = intent
                myData.loadingMachine = machines.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForUnloadResult) {
                val intent = intent
                myData.unloadingMachine = machines.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackLoadResult) {
                val intent = intent
                myData.backLoadingMachine = machines.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackUnloadResult) {
                val intent = intent
                myData.backUnloadingMachine = machines.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                when (myData.nextAction) {
                    0 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        myData.loadingMachine = machines.get(position).name
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this, Location1Activity::class.java)
                        myData.unloadingMachine = machines.get(position).name
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        myData.backLoadingMachine = machines.get(position).name
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(this, Location1Activity::class.java)
                        myData.backUnloadingMachine = machines.get(position).name
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }
            }
        })

    }


    override fun onClick(view: View?) {
        when (view!!.id) {
//            R.id.ehome_next -> {
//                val intent = Intent(this@Material1Activity, ELoadActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}
