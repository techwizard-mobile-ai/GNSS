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
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.excavator.ELoadActivity
import com.lysaan.malik.vsptracker.adapters.CustomGrid
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.classes.MyData
import kotlinx.android.synthetic.main.activity_material1.*

class Material1Activity : BaseActivity(), View.OnClickListener {


    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_material1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(TAG, this)


        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        // nextAction 0 = Do Loading
        // nextAction 1 = Do Unloading
        // nextAction 2 = Do Back Loading
        // nextAction 3 = Do Back Unloading

        when(myData.nextAction){
            0-> material_title.text = "Select Loading Material"
            1-> material_title.text = "Select Unloading Material"
            2-> material_title.text = "Select Back Loading Material"
            3-> material_title.text = "Select Back Unloading Material"
        }
        val gv = findViewById(R.id.em_gridview) as GridView

        lateinit var materials: ArrayList<Material>
        if (myHelper.getMachineType() == 2) {
            materials = myHelper.getScraperMaterials()
        } else {
            materials = myHelper.getMaterials()
        }
        materials.removeAt(0)
//        val adapter = CustomGrid(this@Material1Activity, materials)
        val adapter = CustomGrid(this@Material1Activity, db.getMaterials())

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("Selected Material: " + materials.get(position).name)

            if (myData.isForLoadResult) {
                val intent = intent
                myData.loadingMaterial = materials.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForUnloadResult) {
                val intent = intent
                myData.unloadingMaterial = materials.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackLoadResult) {
                val intent = intent
                myData.backLoadingMaterial = materials.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackUnloadResult) {
                val intent = intent
                myData.backUnloadingMaterial = materials.get(position).name
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                when (myHelper.getMachineType()) {
                    1 -> {
                        val intent = Intent(this, ELoadActivity::class.java)
                        myData.loadingMaterial = materials.get(position).name

                        myHelper.setLastJourney(myData)
//                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
//                    2 -> {
//                        if (myData.isUnload) {
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.unloadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        } else {
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.loadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        }
//                    }
                    2, 3 -> {

                        when (myData.nextAction) {
                            0 -> {
                                val intent = Intent(this, Location1Activity::class.java)
                                myData.loadingMaterial = materials.get(position).name
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            1 -> {
                                val intent = Intent(this, Location1Activity::class.java)
                                myData.unloadingMaterial = materials.get(position).name
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            2 -> {
                                val intent = Intent(this, Location1Activity::class.java)
                                myData.backLoadingMaterial = materials.get(position).name
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            3 -> {
                                val intent = Intent(this, Location1Activity::class.java)
                                myData.backUnloadingMaterial = materials.get(position).name
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                        }
//                        if(myData.isForBackUnload){
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.backUnloadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        }else if(myData.isForBackLoad){
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.backLoadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        }else if (myData.isUnload) {
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.unloadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        } else {
//                            val intent = Intent(this, Location1Activity::class.java)
//                            myData.loadingMaterial = materials.get(position).name
//                            intent.putExtra("myData", myData)
//                            startActivity(intent)
//                        }
                    }
                }

            }

        })

        ematerial1_back.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ematerial1_back -> {
                finish()
            }
        }
    }
}
