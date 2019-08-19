package com.lysaan.malik.vsptracker.activities.common

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.others.Data
import android.app.Activity


class Material1Activity : BaseActivity(), View.OnClickListener {


    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_material1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        helper = Helper(TAG, this)


        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }

        val gv = findViewById(R.id.em_gridview) as GridView
        val materials = helper.getMaterials()
        materials.removeAt(0)
        val adapter = CustomGrid(this@Material1Activity, materials)





        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            helper.toast("Selected Material: " + materials.get(position).name)

            if(data.isForLoadResult){
                val intent = intent
                data.loadingMaterial = materials.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else if (data.isForUnloadResult){
                val intent = intent
                data.unloadingMaterial = materials.get(position).name
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                if(data.isUnload){
                    val intent = Intent(this, Location1Activity::class.java)
                    data.unloadingMaterial = materials.get(position).name
                    intent.putExtra("data", data)
                    startActivity(intent)
                }else{
                    val intent = Intent(this, Location1Activity::class.java)
                    data.loadingMaterial = materials.get(position).name
                    intent.putExtra("data", data)
                    startActivity(intent)
                }
            }




        })

    }

    override fun onClick(view: View?) {
        when(view!!.id){
//            R.id.ehome_next -> {
//                val intent = Intent(this@Material1Activity, ELoadTruckActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}
