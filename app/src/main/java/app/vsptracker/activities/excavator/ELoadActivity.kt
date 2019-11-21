package app.vsptracker.activities.excavator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.common.LocationActivity
import app.vsptracker.activities.common.MaterialActivity
import app.vsptracker.adapters.ELoadingAdapter
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_eload.*
private const val REQUEST_MATERIAL = 2
private const val REQUEST_LOCATION = 3
class ELoadActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_eload, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper = MyHelper(tag, this)


        myData = myHelper.getLastJourney()
        eload_material.text = myData.loadingMaterial
        eload_location.text = myData.loadingLocation


        load_truck_load.setOnClickListener(this)
        eload_back.setOnClickListener(this)
        eload_finish.setOnClickListener(this)
        eload_material.setOnClickListener(this)
        eload_location.setOnClickListener(this)


        val loadHistory = db.getELoadHistory()
        if (loadHistory.size > 0) {
            elh_rv.visibility = View.VISIBLE
            val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
            val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            elh_rv.layoutManager = layoutManager1
            elh_rv!!.adapter = aa
        } else {
            elh_rv.visibility = View.INVISIBLE
        }

    }


    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.load_truck_load -> {
                stopDelay()
                myData.loadingMachine = myHelper.getMachineNumber()
                myData.loadedMachine = getString(R.string.load)

                myData.loadingGPSLocation = gpsLocation
                myData.loadTypeId = 1
                myData.orgId = myHelper.getLoginAPI().org_id
                myData.operatorId = myHelper.getOperatorAPI().id
                if(myHelper.isOnline()){
                    pushLoad(myData)
                }
                val insertID = db.insertELoad(myData)
                if (insertID > 0) {
                    myHelper.toast("Loading Successful.\nLoaded Truck Number # $insertID")

                    val loadHistory = db.getELoadHistory()
                    if (loadHistory.size > 0) {
                        elh_rv.visibility = View.VISIBLE
                        val aa = ELoadingAdapter(this@ELoadActivity, loadHistory)
                        val layoutManager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        elh_rv.layoutManager = layoutManager1
                        elh_rv!!.adapter = aa
                    } else {
                        elh_rv.visibility = View.INVISIBLE
                    }

                } else {
                    myHelper.toast("Error while Saving Record.")
                }

            }
            R.id.eload_back -> {
                myHelper.startHomeActivityByType(MyData())
            }
            R.id.eload_finish -> {
                val intent = Intent(this, HourMeterStopActivity::class.java)
                startActivity(intent)
            }
            R.id.eload_material -> {
                val intent = Intent(this, MaterialActivity::class.java)
//                val data1 = myHelper.getLastJourney()
                myData.isForLoadResult = true
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_MATERIAL)
            }
            R.id.eload_location -> {
                val intent = Intent(this, LocationActivity::class.java)
//                val data1 = myHelper.getLastJourney()
                myData.isForLoadResult = true
                intent.putExtra("myData", myData)
                startActivityForResult(intent, REQUEST_LOCATION)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            val bundle: Bundle? = intent!!.extras
            if (bundle != null) {
                myData = bundle.getSerializable("myData") as MyData
                myHelper.log("myData:$myData")


                eload_material.text = myData.loadingMaterial
                eload_location.text = myData.loadingLocation

                myData.isForUnloadResult = false
                myData.isForLoadResult = false
                myHelper.setLastJourney(myData)

            }

        } else {
            myHelper.toast("Request can not be completed.")
        }
    }
}
