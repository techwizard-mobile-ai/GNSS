package app.vsptracker.activities.common


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.excavator.ELoadActivity
import app.vsptracker.adapters.CustomGrid
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_material.*

class MaterialActivity : BaseActivity(), View.OnClickListener {
    
    
    private val tag = this::class.java.simpleName
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_material, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper = MyHelper(tag, this)
        
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
        
        // nextAction 0 = Do Loading
        // nextAction 1 = Do Unloading
        // nextAction 2 = Do Back Loading
        // nextAction 3 = Do Back Unloading
        
        when (myData.nextAction) {
            0 -> material_title.text = getString(R.string.select_loading_material)
            1 -> material_title.text = getString(R.string.select_unloading_material)
            2 -> material_title.text = getString(R.string.select_back_loading_material)
            3 -> material_title.text = getString(R.string.select_back_unloading_material)
        }
        val gv = findViewById<GridView>(R.id.em_gridview)
        
        val materials: ArrayList<Material> = db.getMaterials()
        myHelper.log("Materials:$materials")
        
        val adapter = CustomGrid(this@MaterialActivity, materials)
        
        gv.adapter = adapter
        gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            myHelper.toast("Selected Material: " + materials[position].name)
            
            when {
                myData.isForLoadResult -> {
                    val intent = intent
                    myData.loading_material_id = materials[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForUnloadResult -> {
                    val intent = intent
                    myData.unloading_material_id = materials[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForBackLoadResult -> {
                    val intent = intent
                    myData.back_loading_material_id = materials[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForBackUnloadResult -> {
                    val intent = intent
                    myData.back_unloading_material_id = materials[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> when (myHelper.getMachineTypeID()) {
                    1 -> {
                        val intent = Intent(this, ELoadActivity::class.java)
                        myData.loading_material_id = materials[position].id
                        
                        myHelper.setLastJourney(myData)
                        startActivity(intent)
                    }
                    2, 3 -> {
                        
                        when (myData.nextAction) {
                            0 -> {
                                val intent = Intent(this, LocationActivity::class.java)
                                myData.loading_material_id = materials[position].id
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            1 -> {
                                val intent = Intent(this, LocationActivity::class.java)
                                myData.unloading_material_id = materials[position].id
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            2 -> {
                                val intent = Intent(this, LocationActivity::class.java)
                                myData.back_loading_material_id = materials[position].id
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                            3 -> {
                                val intent = Intent(this, LocationActivity::class.java)
                                myData.back_unloading_material_id = materials[position].id
                                intent.putExtra("myData", myData)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            
        }
        
        ematerial1_back.setOnClickListener(this)
        
    }
    
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ematerial1_back -> {
                finish()
            }
        }
    }
}
