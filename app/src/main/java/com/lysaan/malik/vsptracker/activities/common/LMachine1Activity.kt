package com.lysaan.malik.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.classes.Data
import kotlinx.android.synthetic.main.activity_lmachine1.*

class LMachine1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_lmachine1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }
//        data = helper.getLastJourney()

        when(data.nextAction){
            0 -> {lm_title.text = "Select Loading Machine"}
            1 -> {lm_title.text = "Select Unloading Machine"}
            2 -> {lm_title.text = "Select Back Loading Machine"}
            3 -> {lm_title.text = "Select Back Unloading Machine"}
        }
//        if(data.isForBackLoad){
//            lm_title.text = "Select Back Loading Machine"
//        }else if(data.isForBackUnload){
//            lm_title.text = "Select Back Unloading Machine"
//        }else if(data.isUnload){
//            lm_title.text = "Select Unloading Machine"
//        }else{
//            lm_title.text = "Select Loading Machine"
//        }

        val gv = findViewById(R.id.tlm_gridview) as GridView
        val machines = helper.getMachines()
        machines.removeAt(0)
        val adapter = CustomGrid(this@LMachine1Activity, machines)

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            helper.toast("Selected Machine: " + machines.get(position).name)

            if(data.isForLoadResult){
                val intent = intent
                data.loadingMachine = machines.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else if (data.isForUnloadResult){
                val intent = intent
                data.unloadingMachine = machines.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else if(data.isForBackLoadResult){
                val intent = intent
                data.backLoadingMachine = machines.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else if (data.isForBackUnloadResult){
                val intent = intent
                data.backUnloadingMachine = machines.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else {
                when(data.nextAction){
                    0 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        data.loadingMachine = machines.get(position).name
                        intent.putExtra("data", data)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this, Location1Activity::class.java)
                        data.unloadingMachine = machines.get(position).name
                        intent.putExtra("data", data)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        data.backLoadingMachine = machines.get(position).name
                        intent.putExtra("data", data)
                        startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(this, Location1Activity::class.java)
                        data.backUnloadingMachine = machines.get(position).name
                        intent.putExtra("data", data)
                        startActivity(intent)
                    }
                }
            }
//                if(data.isForBackUnload){
//                val intent = Intent(this, Location1Activity::class.java)
//                data.backUnloadingMachine = machines.get(position).name
//                intent.putExtra("data", data)
//                startActivity(intent)
//            }else if(data.isForBackLoad){
//                val intent = Intent(this, Material1Activity::class.java)
//                data.backLoadingMachine = machines.get(position).name
//                intent.putExtra("data", data)
//                startActivity(intent)
//            }else if(data.isUnload){
//                    val intent = Intent(this, Location1Activity::class.java)
//                    data.unloadingMachine = machines.get(position).name
//                    intent.putExtra("data", data)
//                    startActivity(intent)
//                }else{
//                    val intent = Intent(this, Material1Activity::class.java)
//                    data.loadingMachine = machines.get(position).name
//                    intent.putExtra("data", data)
//                    startActivity(intent)
//            }
        })

    }


    override fun onClick(view: View?) {
        when(view!!.id){
//            R.id.ehome_next -> {
//                val intent = Intent(this@Material1Activity, ELoadActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}
