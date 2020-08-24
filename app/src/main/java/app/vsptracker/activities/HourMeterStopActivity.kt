package app.vsptracker.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.activities.excavator.EHistoryActivity
import app.vsptracker.activities.scrapper.SHistoryActivity
import app.vsptracker.activities.truck.THistoryActivity
import app.vsptracker.adapters.DelayHistoryAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.others.MyEnum
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.fragment_delay_history.view.*
import kotlinx.android.synthetic.main.logout_notification.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {
    
    private val tag = this::class.java.simpleName
    
    private val adapterList = ArrayList<ServerSyncModel>()
    private val myDataList = ArrayList<MyData>()
    private val eWorkList = ArrayList<EWork>()
    private val serverSyncList = ArrayList<ServerSyncAPI>()
    private var isAutoLogoutCall = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(11).isChecked = true
        
        myHelper.setTag(tag)
        
        if (myHelper.getIsMachineStopped()) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//          Machine is already stopped so there is No need to show Hour Meter
            hm_layout.visibility = View.GONE
        } else {
            hm_layout.visibility = View.VISIBLE
        }
        
        myData = MyData()
        
        val meter = myHelper.getMeter()
        if (meter.isMachineStartTimeCustom)
            myData.isStartHoursCustom = 1
        myData.startHours = myHelper.getMeterTimeForFinish()
        
        myData.startTime = meter.machineStartTime
        
        myData.loadingGPSLocation = meter.hourStartGPSLocation
        
        myHelper.log("getMeterTimeForFinish:${myHelper.getMeterTimeForFinish()}")
        sfinish_reading.setText(myHelper.getMeterTimeForFinish())
        
        myHelper.log("onCreate:$myData")
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isAutoLogoutCall = bundle.getBoolean("isAutoLogoutCall")
            if (isAutoLogoutCall) {
                myHelper.log("isAutoLogoutCall:$isAutoLogoutCall")
                logout(isAutoLogoutCall)
            }
        }
    
        hm_summary_operator.text = ":  ${myHelper.getOperatorAPI().name}"
        hm_summary_login.text = ":  ${myHelper.getDateTime(myHelper.getMeter().hourStartTime)}"
        hm_summary_machine.text = ":  ${myHelper.getMachineDetails()}"
        hm_summary_working_time.text = ":  ${myHelper.getFormattedTime(System.currentTimeMillis() - myHelper.getMeter().hourStartTime)}"
    
        when (myHelper.getMachineTypeID()) {
            MyEnum.EXCAVATOR -> {
                sm_summary_prod_dig_layout.visibility = View.VISIBLE
                hm_summary_prod_dig.text = ":  ${db.getCurrentLoginELoadHistory().size}"
                
                sm_summary_trenching_layout.visibility = View.VISIBLE
                hm_summary_trenching.text = ":  ${db.getCurrentLoginEWorks(MyEnum.EXCAVATOR_TRENCHING).size}"
                
                sm_summary_gen_dig_layout.visibility = View.VISIBLE
                hm_summary_gen_dig.text = ":  ${db.getCurrentLoginEWorks(MyEnum.EXCAVATOR_GEN_DIGGING).size}"
            }
            MyEnum.SCRAPER -> {
                sm_summary_trips_layout.visibility = View.VISIBLE
                hm_summary_trips.text = ":  ${db.getCurrentLoginTrips().size}"
                
                sm_summary_trimming_layout.visibility = View.VISIBLE
                hm_summary_trimming.text = ":  ${db.getCurrentLoginEWorks(MyEnum.SCRAPER_TRIMMING).size}"
            
            }
            MyEnum.TRUCK -> {
                sm_summary_trips_layout.visibility = View.VISIBLE
                hm_summary_trips.text = ":  ${db.getCurrentLoginTrips().size}"
            }
        }
        
        
        sfinish_minus.setOnClickListener(this)
        sfinish_plus.setOnClickListener(this)
        hm_stop_logout.setOnClickListener(this)
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.sfinish_minus -> {
                val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
                if (value > 0) {
                    val newValue = value - 0.1
                    sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
                } else {
                    myHelper.toast("Please Enter Valid Meter Value.")
                    sfinish_reading.setText(myHelper.getRoundedDecimal(value.toDouble()).toString())
                }
                
            }
            
            R.id.sfinish_plus -> {
                val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
                val newValue = value + 0.1
                sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
            }
            
            R.id.hm_stop_logout -> {
                if (!myHelper.isOnline()) {
                    updatedNotification()
                } else {
                    logout()
                }
            }
        }
    }
    
    @SuppressLint("InflateParams")
    private fun updatedNotification() {
        
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.logout_notification, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(true)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        
        
        mDialogView.logout_ok_btn.setOnClickListener {
            mAlertDialog.dismiss()
            logout()
        }
        mDialogView.logout_cancel_btn.setOnClickListener {
            mAlertDialog.dismiss()
        }
        
    }
    
    private fun logout(isAutoLogoutCall: Boolean = false) {
        
        val operatorAPI = myHelper.getOperatorAPI()
        operatorAPI.unloadingGPSLocation = gpsLocation
        operatorAPI.orgId = myHelper.getLoginAPI().org_id
        operatorAPI.siteId = myHelper.getMachineSettings().siteId
        operatorAPI.operatorId = operatorAPI.id
        when {
            myHelper.isDailyModeStarted() -> operatorAPI.isDayWorks = 1
            else -> operatorAPI.isDayWorks = 0
        }
        operatorAPI.stopTime = System.currentTimeMillis()
        operatorAPI.totalTime = operatorAPI.stopTime - operatorAPI.startTime
        operatorAPI.loadingGPSLocationString = myHelper.getGPSLocationToString(operatorAPI.loadingGPSLocation)
        operatorAPI.unloadingGPSLocationString = myHelper.getGPSLocationToString(operatorAPI.unloadingGPSLocation)
//        Calling db.insertOperatorHour because this time, data will not be pushed to server as whole data
//        will be pushed to server and a Dialog Box will appear in this activity.
//        Using pushInsertOperatorHour method there could be multiple OkHttp Requests to server.
//        myDataPushSave.pushInsertOperatorHour(operatorAPI)
        db.insertOperatorHour(operatorAPI)
        
        myHelper.log("isMachineStopped:${myHelper.getIsMachineStopped()}")
//        If machine is already stopped in Machine Breakdown OR Machine Stop Adapter
//        then Machine Hour is already inserted. But If machine is not stopped then stop machine and
//        Insert Machine Hour
        if (!myHelper.getIsMachineStopped()) {
            myHelper.log("Machine is not stopped.")
            val totalHours = myHelper.getMeterValidValue(sfinish_reading.text.toString())
            if (!myHelper.getMeterTimeForFinish().equals(totalHours, true)) {
                val meter = myHelper.getMeter()
                meter.isMachineStopTimeCustom = true
                myData.isTotalHoursCustom = 1
                myData.startHours = meter.startHours
                myHelper.setMeter(meter)
                myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: $totalHours")
            } else {
                val meter = myHelper.getMeter()
                meter.isMachineStopTimeCustom = false
                myData.isTotalHoursCustom = 0
                myData.startHours = meter.startHours
                myHelper.setMeter(meter)
                myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForFinish()}, New Reading: $totalHours")
            }
            val value = totalHours.toDouble()
            val minutes = value * 60
            val newMinutes = myHelper.getRoundedInt(minutes)
            myHelper.log("Minutes: $newMinutes")
            myHelper.setMachineTotalTime(newMinutes)
            myData.totalHours = totalHours
            myHelper.log("Before saveMachineHour:$myData")
            if (isAutoLogoutCall) {
                myData.machine_stop_reason_id = -3
            } else {
                myData.machine_stop_reason_id = -1
            }
            myData.isSync = 0
            myData.unloadingGPSLocation = gpsLocation
            
            myDataPushSave.pushInsertMachineHour(myData, false)
        }
        
        checkUpdateServerSyncData()
        
    }
    
    /**
     * This method will do following actions.
     * 1. Delete Operator Login PIN and end session.
     * 2. Clear Last Journey Data and End Trip Loop.
     * 3. Clear App previous Activities.
     * 4. Redirect to Operator Login Activity.
     */
    private fun clearLoginData() {
        myHelper.setOperatorAPI(MyData())
        val data = MyData()
        myHelper.setLastJourney(data)
        
        val intent = Intent(this, OperatorLoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    
    private fun checkUpdateServerSyncData() {
        // remove all previous data if any and make list empty
        if (myDataList.size > 0)
            myDataList.removeAll(ArrayList())
        
        if (eWorkList.size > 0)
            eWorkList.removeAll(ArrayList())
        
        if (serverSyncList.size > 0)
            serverSyncList.removeAll(ArrayList())
        // TODO convert this to a method which will be used in all other activities / classes for adding data which need to be synced.
        addToList(1, "Operators Hours", db.getOperatorsHours("ASC"))
        addToList(2, "Trucks Trips", db.getTripsByTypes(3, "ASC"))
        addToList(3, "Scrapers Trips", db.getTripsByTypes(2, "ASC"))
        addToList(4, "Scrapers Trimmings", db.getEWorks(3, "ASC"))
        addToList(5, "Excavators Prod. Digging", db.getELoadHistory("ASC"))
        addToList(6, "Excavators Trenching", db.getEWorks(2, "ASC"))
        addToList(7, "Excavators Gen. Digging", db.getEWorks(1, "ASC"))
        addToList(8, "Machines Stops", db.getMachinesStops("ASC"))
        addToList(9, "Machines Hours", db.getMachinesHours("ASC"))
        addToList(10, "Operators Waiting", db.getWaits("ASC"))
        
        
        if (myHelper.isOnline() && serverSyncList.size > 0) {
            pushUpdateServerSync()
        } else {
            clearLoginData()
            myHelper.toast(myDataPushSave.noInternetMessage)
        }
    }
    
    /**
     * This method will check if there are any Remaining entries which are not Synced.
     * If there are any remaining entries then it will add that data to List.
     * This list will be added to RecyclerView to Display to User.
     */
    private fun addToList(type: Int, name: String, list: ArrayList<MyData>) {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        val remaining = list.filter { it.isSync == 0 }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        if (remaining.isNotEmpty()) {
            myDataList.addAll(remaining)
            
            val serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myDataList.addAll(remaining)
            serverSyncList.add(serverSyncAPI)
        }
    }
    
    /**
     * method addToList Over Riding
     */
    @JvmName("MyData")
    private fun addToList(type: Int, name: String, list: ArrayList<EWork>) {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        val remaining = list.filter { it.isSync == 0 }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        if (remaining.isNotEmpty()) {
            eWorkList.addAll(remaining)
            
            val serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myEWorkList.addAll(remaining)
            serverSyncList.add(serverSyncAPI)
        }
    }
    
    private fun pushUpdateServerSync() {
        myHelper.showDialog()
//        val client = OkHttpClient()
    
        val client = myHelper.unSafeOkHttpClient().build()
        val formBody = FormBody.Builder()
            .add("token", myHelper.getLoginAPI().auth_token)
            .add("operator_id", myHelper.getOperatorAPI().id.toString())
            .add("device_details", myHelper.getDeviceDetailsString())
            .add("data", myHelper.getServerSyncDataAPIString(serverSyncList))
            .build()
        
        val request = Request.Builder()
            .url("https://vsptracker.app/api/v1/orgsserversync/store")
            .post(formBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                myHelper.hideDialog()
                val responseString = response.body!!.string()
                val responseJObject = JSONObject(responseString)
                val success = responseJObject.getBoolean("success")
                try {
                    if (success) {
                        val gson = GsonBuilder().create()
//                      this data is used for convert data arraylist into object to convert in gson
//                      here converting back
                        val data1 = responseJObject.getString("data")
//                      this is complete data sent to server
//                      val dataArray = dataObj.getJSONArray("data")
//                      val dataArray = JSONArray(data1)
                        val data = gson.fromJson(data1, Array<ServerSyncAPI>::class.java).toList()
                        myHelper.log("data:${data}")
//                      here I am getting complete list of data with type. Now I have to update each entry in
//                      App Database and change their status from isSync 0 to 1 as these entries are successfully updated in Portal Database.
                        if (updateServerSync(data)) {
                            runOnUiThread {
                                myHelper.toast("All Data Uploaded to Server Successfully.")
                                clearLoginData()
                            }
                        }
                        
                    } else {
                        if (responseJObject.getString("message ") == "Token has expired") {
                            myHelper.log("Token Expired:$responseJObject")
                            myHelper.refreshToken()
                        } else {
                            myHelper.toast(responseJObject.getString("message "))
                        }
                    }
                }
                catch (e: Exception) {
                    myHelper.log("${e.message}")
                }
            }
            
            override fun onFailure(call: Call, e: IOException) {
                myHelper.run {
                    hideDialog()
                    toast(e.message.toString())
                    log("Exception: ${e.message}")
                }
            }
        })
    }
    
    private fun updateServerSync(data: List<ServerSyncAPI>): Boolean {
        data.forEach { serverSyncAPI ->
            when (serverSyncAPI.type) {
                1 -> {
                    myHelper.log("Operators Hours")
                    db.updateOperatorsHours(serverSyncAPI.myDataList)
                }
                2 -> {
                    myHelper.log("Trucks Trips")
                    db.updateTrips(serverSyncAPI.myDataList)
                }
                3 -> {
                    myHelper.log("Scrapers Trips")
                    db.updateTrips(serverSyncAPI.myDataList)
                }
                4 -> {
                    myHelper.log("Scrapers Trimming")
                    db.updateEWorks(serverSyncAPI.myEWorkList)
                }
                5 -> {
                    myHelper.log("Excavators Production Digging")
                    db.updateELoads(serverSyncAPI.myDataList)
                }
                6 -> {
                    myHelper.log("Excavators Trenching")
                    db.updateEWorks(serverSyncAPI.myEWorkList)
                }
                7 -> {
                    myHelper.log("Excavators General Digging")
                    db.updateEWorks(serverSyncAPI.myEWorkList)
                }
                8 -> {
                    myHelper.log("Machines Stops:${serverSyncAPI.myDataList}")
                    db.updateMachinesStops(serverSyncAPI.myDataList)
                }
                9 -> {
                    myHelper.log("Machines Hours:${serverSyncAPI.myDataList}")
                    db.updateMachinesHours(serverSyncAPI.myDataList)
                }
                10 -> {
                    myHelper.log("Operators Waiting")
                    db.updateWaits(serverSyncAPI.myEWorkList)
                }
            }
        }
        return true
    }
}
