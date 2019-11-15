package app.vsptracker.activities.scrapper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.activities.common.Material1Activity
import app.vsptracker.activities.common.UnloadTaskActivity
import app.vsptracker.activities.excavator.ESideCastingActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_shome.*

class SHomeActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_shome, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(tag, this)

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

        shome_logout.setOnClickListener(this)
        thome_load.setOnClickListener(this)
        thome_unload.setOnClickListener(this)
        shome_trimming.setOnClickListener(this)


    }
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.shome_trimming ->{
                val intent = Intent(this, ESideCastingActivity::class.java)
                myData.eWorkType = 3
                myData.eWorkActionType = 1
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_load -> {
                val intent = Intent(this, Material1Activity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.thome_unload -> {
//                val intent = Intent(this, Location1Activity::class.java)
                val intent = Intent(this, UnloadTaskActivity::class.java)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
            R.id.shome_logout -> {
                myHelper.logout(this)
            }
        }

    }
}
