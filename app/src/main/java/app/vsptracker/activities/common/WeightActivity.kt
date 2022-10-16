package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_weight.*

class WeightActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_weight, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(0).isChecked = true
    
    myHelper = MyHelper(tag, this)
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      myData = bundle.getSerializable("myData") as MyData
      myHelper.log("myData:$myData")
    }
    
    w1_next.setOnClickListener(this)
    
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.w1_next -> {
        
        when {
          myData.isForLoadResult -> {
            val intent = intent
            val sLoadWeight = sload_weight.text.toString()
            if (!sLoadWeight.isBlank())
              myData.unloadingWeight = sLoadWeight.toDouble()
            intent.putExtra("myData", myData)
            setResult(Activity.RESULT_OK, intent)
            finish()
          }
          myData.isForUnloadResult -> {
            val intent = intent
            val sLoadWeight = sload_weight.text.toString()
            if (!sLoadWeight.isBlank())
              myData.unloadingWeight = sLoadWeight.toDouble()
            intent.putExtra("myData", myData)
            setResult(Activity.RESULT_OK, intent)
            finish()
          }
          myData.isForBackLoadResult -> {
            val intent = intent
            val sLoadWeight = sload_weight.text.toString()
            if (!sLoadWeight.isBlank())
              myData.unloadingWeight = sLoadWeight.toDouble()
            intent.putExtra("myData", myData)
            setResult(Activity.RESULT_OK, intent)
            finish()
          }
          myData.isForBackUnloadResult -> {
            val intent = intent
            val sLoadWeight = sload_weight.text.toString()
            if (!sLoadWeight.isBlank())
              myData.unloadingWeight = sLoadWeight.toDouble()
            intent.putExtra("myData", myData)
            setResult(Activity.RESULT_OK, intent)
            finish()
          }
          else -> {
            val sLoadWeight = sload_weight.text.toString()
            if (!sLoadWeight.isBlank()) {
              myData.unloadingWeight = sLoadWeight.toDouble()
            }
            myHelper.log("$myData")
            
            when (myData.nextAction) {
              0 -> {
                //                            val sload_weight = sload_weight.text.toString()
                //                            if (!sload_weight.isNullOrBlank())
                //                                myData.unloadingWeight = sload_weight.toDouble()
                //                            myHelper.log("$myData")
                
                val data1 = myHelper.getLastJourney()
                data1.loading_machine_id = myData.loading_machine_id
                data1.loading_material_id = myData.loading_material_id
                data1.loading_location_id = myData.loading_location_id
                data1.unloadingWeight = myData.unloadingWeight
                myHelper.setLastJourney(data1)
                
                val intent = Intent(this, RLoadActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
              }
              2 -> {
                
                
                val data1 = myHelper.getLastJourney()
                data1.loading_machine_id = myData.loading_machine_id
                data1.loading_material_id = myData.loading_material_id
                data1.loading_location_id = myData.loading_location_id
                data1.unloadingWeight = myData.unloadingWeight
                myHelper.setLastJourney(data1)
                
                val intent = Intent(this, RLoadActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
              }
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
