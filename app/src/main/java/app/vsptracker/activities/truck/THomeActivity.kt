package app.vsptracker.activities.truck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.activities.common.LMachineActivity
import app.vsptracker.activities.common.UnloadTaskActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_thome.*

class THomeActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_thome, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(tag, this)

//        var bundle :Bundle ?=intent.extras
//        if(bundle != null){
//            myData = bundle!!.getSerializable("myData") as MyData
//            myHelper.log("myData:$myData")
//        }

        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")

        when (myHelper.getNextAction()) {
            0, 2 -> {
                myHelper.setToDoLayout(thome_load_button)
            }
            1, 3 -> {
                myHelper.setToDoLayout(thome_unload_button)
            }
        }

        thome_logout.setOnClickListener(this)
        thome_load.setOnClickListener(this)
        thome_unload.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
//
//    override fun onPause() {
//        super.onPause()
//        stopGPS()
//    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.thome_load -> {
                val intent = Intent(this, LMachineActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_unload -> {
                val intent = Intent(this, UnloadTaskActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_logout -> {

//                myHelper.log("LocationLat:${latitude}")
//                myHelper.log("LocationLongg:${longitude}")
//                myHelper.log("Loads:${db.getTrips()}")

                myHelper.logout(this)
            }
        }

    }

}