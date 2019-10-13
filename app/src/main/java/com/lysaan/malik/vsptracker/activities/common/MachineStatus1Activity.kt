package com.lysaan.malik.vsptracker.activities.common



import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.HourMeterStartActivity
import com.lysaan.malik.vsptracker.adapters.MachineStatusAdapter
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.MyDataResponse
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_machine_status1.*

class MachineStatus1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_machine_status1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(2).isChecked = true

        myHelper.setTag(TAG)


        if (myHelper.getIsMachineStopped()) {
            machine_status_title.text = "Machine Stopped Reason"
            machine_start_layout.visibility = View.VISIBLE
            machine_status_rv.visibility = View.GONE
            machine_stopped_reason.text = myHelper.getMachineStoppedReason()
        } else {
            machine_status_title.text = "Select Machine Stop Reason"
            machine_start_layout.visibility = View.GONE
            machine_status_rv.visibility = View.VISIBLE
        }

        if(myHelper.getIsMachineStopped()){
            machine_status_logout.visibility = View.VISIBLE
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }else{
            machine_status_logout.visibility = View.GONE
        }

//        val stoppedReasons = myHelper.getMachineStopReasons()
        val stoppedReasons = db.getStopReasons()
//        stoppedReasons.removeAt(0)


        val mAdapter = MachineStatusAdapter(this, stoppedReasons)
        machine_status_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        machine_status_rv.setAdapter(mAdapter)


//        val gv = findViewById(R.id.machinestatus_gridview) as GridView
//        val stoppedReasons = myHelper.getMachineStopReasons()
//        stoppedReasons.removeAt(0)
//        val adapter = CustomGrid(this@MachineStatus1Activity, stoppedReasons)
//        gv.setAdapter(adapter)
//
//        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
//            myHelper.toast("Machine Stopped due to : " + stoppedReasons.get(position).name)
//            myHelper.setIsMachineStopped(true , stoppedReasons.get(position).name)
//            myHelper.stopMachine()
//            myHelper.startHomeActivityByType(MyData())
//        })

        machine_status_start.setOnClickListener(this)
        machine_status_logout.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.machine_status_logout -> {
                myHelper.logout(this)
            }

            R.id.machine_status_start -> {

//                val machineData = MyData()
                val machineData = db.getMachineStatus(myHelper.getMeter().machineDbID)
                val currentTime = System.currentTimeMillis()
                machineData.stopTime = currentTime
                machineData.totalTime = currentTime - machineData.startTime
                machineData.time = currentTime.toString()
                machineData.date = myHelper.getDate(currentTime)

                machineData.recordID = myHelper.getMeter().machineDbID
                machineData.unloadingGPSLocation = gpsLocation

                machineData.loadingGPSLocationString = myHelper.getGPSLocationToString(machineData.loadingGPSLocation)
                machineData.unloadingGPSLocationString = myHelper.getGPSLocationToString(machineData.unloadingGPSLocation)

                machineData.orgId = myHelper.getLoginAPI().org_id
                machineData.siteId = myHelper.getMachineSettings().siteId
                machineData.operatorId = myHelper.getOperatorAPI().id
                machineData.machineTypeId = myHelper.getMachineTypeID()
                machineData.machineId = myHelper.getMachineID()
                machineData.machine_stop_reason_id = myHelper.getMachineStoppedReasonID()

                if(myHelper.isDailyModeStarted()){
                    machineData.isDaysWork = 1
                }else {
                    machineData.isDaysWork = 0
                }

                if(myHelper.isOnline()){
                    pushMachineStatus(machineData)
                }
//                else{
                    myHelper.toast("No Internet Connection.\nDelay Not Uploaded to Server.")
                    updateMachineStatus(machineData)
//                }

            }
        }
    }

    fun pushMachineStatus(machineData: MyData){

        myHelper.log("pushMachineStatus:$machineData")
        val call = this.retrofitAPI.pushMachineStatus(
            myHelper.getLoginAPI().auth_token,
            machineData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {

                val response = response.body()
                myHelper.log("pushMachineStatus:$response")
                if (response!!.success && response.data != null) {
                    machineData.isSync = 1
//                    updateMachineStatus(machineData)

                } else {
//                    updateMachineStatus(machineData)
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {

//                updateMachineStatus(machineData)
                myHelper.toast(t.message.toString())
                myHelper.log("Failure" + t.message)
            }
        })
    }


    private fun updateMachineStatus(machineData: MyData) {
        val updateID = db.updateMachineStatus(machineData)
//                if (updateID > 0) {
        myHelper.toast("Machine Started Successfully")
        myHelper.setIsMachineStopped(false, "", 0)
        if(myHelper.getIsMachineStopped()){
            val intent = Intent(this, HourMeterStartActivity::class.java)
            startActivity(intent)
        }else{
            myHelper.startHomeActivityByType(MyData())
        }
//                } else {
//                    myHelper.toast("Machine Not Started. Due to App Deleted Cache.")
//                }
    }
}
