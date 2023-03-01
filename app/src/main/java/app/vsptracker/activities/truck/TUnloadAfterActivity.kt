package app.vsptracker.activities.truck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.common.LMachineActivity
import app.vsptracker.activities.common.RLoadActivity
import app.vsptracker.apis.trip.MyData

class TUnloadAfterActivity : BaseActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  
  lateinit var tul_back_load: Button
  lateinit var tul_after_new: Button
  lateinit var tul_after_repeat: Button
  lateinit var tul_after_finish: Button
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_tunload_after, contentFrameLayout)
    
    tul_back_load = findViewById(R.id.tul_back_load)
    tul_after_new = findViewById(R.id.tul_after_new)
    tul_after_repeat = findViewById(R.id.tul_after_repeat)
    tul_after_finish = findViewById(R.id.tul_after_finish)
    
    myHelper.setTag(tag)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            myData = bundle!!.getSerializable("myData") as MyData
//            myHelper.log("myData:$myData")
//        }
    
    myData = myHelper.getLastJourney()
    
    myHelper.log("myData:$myData")
    
    when (myData.nextAction) {
      3 -> {
        tul_back_load.visibility = View.GONE
      }
      else -> {
        tul_back_load.visibility = View.VISIBLE
      }
    }
    tul_after_new.setOnClickListener(this)
    tul_after_repeat.setOnClickListener(this)
    tul_after_finish.setOnClickListener(this)
    tul_back_load.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.tul_back_load -> {
        val intent = Intent(this, LMachineActivity::class.java)
        myHelper.setNextAction(2)
        myData = myHelper.getLastJourney()
//                myData.isForBackLoad = true
        myData.nextAction = 2
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.tul_after_new -> {
        val intent = Intent(this, LMachineActivity::class.java)
        val data = MyData()
        myHelper.setLastJourney(data)
        intent.putExtra("myData", data)
        startActivity(intent)
      }
      R.id.tul_after_repeat -> {
        when (myData.nextAction) {
          0 -> {
            myData.repeatJourney = 1
          }
          3 -> {
            myData.repeatJourney = 2
          }
        }
//                myData.isRepeatJourney = true
//                myData.repeatJourney = 1
        myData.nextAction = 0
        myHelper.setLastJourney(myData)
//                intent.putExtra("myData", myData)
        val intent = Intent(this, RLoadActivity::class.java)
        startActivity(intent)
      }
      R.id.tul_after_finish -> {
        val intent = Intent(this, HourMeterStopActivity::class.java)
        startActivity(intent)
      }
    }
  }
}
