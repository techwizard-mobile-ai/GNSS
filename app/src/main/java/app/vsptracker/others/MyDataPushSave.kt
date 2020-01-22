package app.vsptracker.others

import android.content.Context
import android.content.Intent
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.delay.EWorkResponse
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.operators.OperatorResponse
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataResponse
import app.vsptracker.classes.Material
import app.vsptracker.database.DatabaseAdapter
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * This class will be used for All APIs Calls and Database Actions. It will do following Actions.
 * 1. Insert / Update will be used for Inserting / Updating  data before API call in Database.
 * 2. checkUpdateServerSyncData will be used for sending data to Server.
 * 3. PushInsert will be used for interacting with others activities and this method will be protected.
 * 4. fetch will be used for Getting Data by APIs and Saving in Database.
 * 5. All Methods Parameters will be provided by calling Activity and this class will be used
 * only for APIs and Database Calls except few common parameter for multiple usage of one method.
 * 6. Internet Connection will be checked in this Class. All Actions for Database Save and API
 * Calls will be made only using this Class for Background Actions.
 * 7. Save Data in App Database.
 * 8. Get Data from Server using API Calls and Update in App Database.
 * 9. Push Data to Server using API Calls.
 * 10. After Successful Push Call save data with isSync = 1 in App Database.
 * 11. If API call is not Successful, save data with isSync = 0 in App Database.
 * 12. Stop Time, Unloading GPS Location could be set in This Class as this class will be used in BaseActivity children
 * and Other Activities.
 */


private const val REQUEST_ACCESS_FINE_LOCATION = 1

class MyDataPushSave(private val context: Context) {
    val noInternetMessage = "No Internet Connection.\nData Not Uploaded to Server but Saved in App."
    private val tag = this::class.java.simpleName
    private val myHelper = MyHelper(tag, context)
    private val db = DatabaseAdapter(context)
    private val retrofit = Retrofit.Builder()
        .baseUrl(RetrofitAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    var isBackgroundCall = true
    
    /**
     * These array will be populated if there is any ServerSync Data
     * For Each time old data will be removed after server sync
     */
    private var myDataList = ArrayList<MyData>()
    private var eWorkList = ArrayList<EWork>()
    private var serverSyncList = ArrayList<ServerSyncAPI>()
    
    /**
     * All Company Data will be fetched from Server using API Calls by this Class only.
     * Old data will be replaced with new one.
     */
    fun fetchOrgData() {
        fetchMachinesAutoLogouts()
    }
    
    /**
     * Fetch Machines Logout Times and Save in Database.
     * This Logout Time is different for each Site and each Machine Type.
     * After inactivity of Operator, Logout function will be called and required actions will be taken.
     */
    private fun fetchMachinesAutoLogouts() {
        val call = this.retrofitAPI.getMachinesAutoLogouts(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token,
            myHelper.getOperatorAPI().id,
            myHelper.getDeviceDetailsString()
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesAutoLogouts(responseBody.data as ArrayList<OperatorAPI>)
//                    fetchMachinesHours()
                    fetchMachinesTasks()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }
    
    /**
     * No Need to Sync this data as It will not work properly when Operator don't have Internet connection
     * on Login ang Logout and data will not be Synced with Server.
     * Secondly this data can be corrected by Operator on Login/Logout by matching it with Machine Odometer.
     * Thirdly this data will only be used for Machine Inspection nothing more.
     */
/*    private fun fetchMachinesHours() {
        val call = this.retrofitAPI.getMachinesHours(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<MyDataListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataListResponse>,
                response: retrofit2.Response<MyDataListResponse>
            ) {
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesHours(responseBody.data as ArrayList<MyData>)
                    fetchMachinesTasks()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }
            
            override fun onFailure(call: retrofit2.Call<MyDataListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }*/
    
    private fun fetchMachinesTasks() {
        val call = this.retrofitAPI.getMachinesTasks(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesTasks(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMaterials()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun fetchMaterials() {
        val call = this.retrofitAPI.getMaterials(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMaterials(responseBody.data as ArrayList<OperatorAPI>)
                    fetchLocations()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun fetchLocations() {
        val call = this.retrofitAPI.getLocations(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertLocations(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMachines()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun fetchMachines() {
        val call = this.retrofitAPI.getMachines(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachines(responseBody.data as ArrayList<OperatorAPI>)
                    fetchStopReasons()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun fetchStopReasons() {
        val call = this.retrofitAPI.getStopReasons(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertStopReasons(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMachinesPlants()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun fetchMachinesPlants() {
        
        val call = this.retrofitAPI.getMachinesPlants(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesPlants(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesBrands()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }
    
    private fun fetchMachinesBrands() {
        
        val call = this.retrofitAPI.getMachinesBrands(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesBrands(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesTypes()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }
    
    private fun fetchMachinesTypes() {
        
        val call = this.retrofitAPI.getMachinesTypes(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesTypes(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchSites()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }
    
    private fun fetchSites() {
        
        val call = this.retrofitAPI.getSites(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("Sites:${operatorResponse.data}.")
                    db.insertSites(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchOperators()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }
    
    private fun fetchOperators() {
        
        val call = this.retrofitAPI.getOperators(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertOperators(operatorResponse.data as ArrayList<OperatorAPI>)
                    when {
                        !isBackgroundCall -> {
                            val intent = Intent(context, OperatorLoginActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                    
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }
    
    fun pushInsertDelay(eWork: EWork) {
//        when {
//            myHelper.isOnline() -> pushDelay(eWork)
//            else -> {
//                myHelper.toast(noInternetMessage)
        insertDelay(eWork)
//            }
//        }
    
    }
    
    private fun pushDelay(eWork: EWork) {
        val call = this.retrofitAPI.pushDelay(myHelper.getLoginAPI().auth_token, eWork)
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
                val responseBody = response.body()
                myHelper.run {
                    log("Response:$response")
                    log("ResponseBody:$responseBody")
                }
                when {
                    responseBody!!.success -> eWork.isSync = 1
                    else -> when (responseBody.message) {
                        "Token has expired" -> {
                            myHelper.run {
                                log("Token Expired:$response")
                                refreshToken()
                            }
                        }
                        else -> myHelper.toast(responseBody.message)
                    }
                }
                insertDelay(eWork)
            }
            
            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
                insertDelay(eWork)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun insertDelay(eWork: EWork) {
        val insertID = db.insertDelay(eWork)
        myHelper.log("saveDelayID: $insertID")
        if (insertID > 0)
            checkUpdateServerSyncData()
    }
    
    /**
     * This method will do following actions.
     * 1. Save Machine Hours as Machine is Stopped.
     * 2. Push Machine Hours To Server.
     * 3. Insert Machine Hours in Database.
     * 4. Stop Waiting (Delay) if Started.
     * 5. Stop Daily Mode if Started.
     */
    fun pushInsertMachineHour(myData: MyData, pushServerSync: Boolean = true): Boolean {
        
        
        val currentTime = System.currentTimeMillis()
        
        val meter = myHelper.getMeter()
        if (meter.isMachineStartTimeCustom)
            myData.isStartHoursCustom = 1
        myData.loadingGPSLocation = meter.hourStartGPSLocation
        myData.startHours = meter.startHours
        myData.startTime = meter.machineStartTime
        
        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        
        myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
//        myData.machine_stop_reason_id = -2
        if (myHelper.isDailyModeStarted()) myData.isDayWorks = 1 else myData.isDayWorks = 0


//        myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)
        
        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime.toString())
        
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.machineId = myHelper.getMachineID()
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()
        
        myData.stopTime = currentTime
        myData.totalTime = myData.stopTime - myData.startTime
//        myData.unloadingGPSLocation = gpsLocation
        
        myHelper.log("pushInsertMachineHour:$myData")
//        when {
//            myHelper.isOnline() -> pushMachineHour(myData)
//            else -> {
//                myHelper.toast(noInternetMessage)
        insertMachineHour(myData, pushServerSync)
//            }
//        }
        myHelper.stopDelay(myData.unloadingGPSLocation)
        myHelper.stopDailyMode()
        
        return true
    }
    
    private fun pushMachineHour(myData: MyData) {
        
        val call = this.retrofitAPI.pushMachinesHours(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val responseBody = response.body()
                myHelper.run {
                    log("response:$response")
                    log("responseBody:$responseBody")
                }
                if (responseBody!!.success) {
                    myData.isSync = 1
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                insertMachineHour(myData)
            }
            
            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                insertMachineHour(myData)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun insertMachineHour(myData: MyData, pushServerSync: Boolean = true) {
        val insertID = db.insertMachineHours(myData)
        if (insertID > 0 && pushServerSync)
            checkUpdateServerSyncData(false)
    }
    
    fun pushInsertOperatorHour(myData: MyData) {
        myHelper.log("pushInsertOperatorHour:$myData")
//        when {
//            myHelper.isOnline() -> pushOperatorHour(myData)
//            else -> {
//                myHelper.toast(noInternetMessage)
        insertOperatorHour(myData)
//            }
//        }
    }
    
    private fun pushOperatorHour(myData: MyData) {
        
        val call = this.retrofitAPI.pushOperatorHour(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val responseBody = response.body()
                myHelper.run {
                    log("response:$response")
                    log("responseBody:$responseBody")
                }
                if (responseBody!!.success) {
                    myData.isSync = 1
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                insertOperatorHour(myData)
            }
            
            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                insertOperatorHour(myData)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun insertOperatorHour(myData: MyData) {
        val insertID = db.insertOperatorHour(myData)
        if (insertID > 0)
            checkUpdateServerSyncData()
    }
    
    /**
     * Machine Stop Entry is Pushed when Machine is Started Again.
     * When Machine is Started, Entry will be pushed to Portal and then
     * it will update Machine Stop Entry in Database.
     */
    fun pushUpdateMachineStop(myData: MyData) {
        myHelper.log("pushUpdateMachineStop:$myData")
//        when {
//            myHelper.isOnline() -> pushMachinesStop(myData)
//            else -> {
//                myHelper.toast(noInternetMessage)
        updateMachineStop(myData)
//            }
//        }
    }
    
    private fun pushMachinesStop(machineData: MyData) {
        
        val call = this.retrofitAPI.pushMachinesStops(
            myHelper.getLoginAPI().auth_token,
            machineData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                myHelper.log("pushMachinesStop:$response")
                val responseBody = response.body()
                myHelper.log("pushMachinesStopData:${responseBody}")
                if (responseBody!!.success) {
                    machineData.isSync = 1
//                    myHelper.pushIsMachineRunning(1, responseBody.data.id)
//                    pushIsMachineRunning(machineData)
                
                } else {
//                    pushIsMachineRunning(machineData)
                    if (responseBody.message == "Token has expired") {
                        myHelper.run {
                            log("Token Expired:$response")
                            refreshToken()
                        }
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                updateMachineStop(machineData)
            }
            
            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                
                updateMachineStop(machineData)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    private fun updateMachineStop(machineData: MyData) {
        val updateID = db.updateMachineStop(machineData)
        myHelper.log("updateMachineStopID:$updateID")
        if (updateID > 0)
            checkUpdateServerSyncData()
    }
    
    /**
     * This method is called when a machine is stopped and saved in database.
     * When machine is started again this database entry is updated (by method updateMachineStop) and same data is
     * being pushed to Server.
     * If Machine is Breakdown then reset Last Journey and break loop otherwise Don't reset Last Journey
     */
    fun insertMachineStop(myData: MyData, material: Material, resetJourney: Boolean = false): Long {
        val currentTime = System.currentTimeMillis()
        myData.startTime = currentTime

//        val time = System.currentTimeMillis()
        myData.time = currentTime.toString()
        
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()
        myData.machine_stop_reason_id = material.id
        myData.date = myHelper.getDate(currentTime.toString())
        myData.machineId = myHelper.getMachineID()
        
        val insertID = db.insertMachineStop(myData)
        myHelper.log("insertMachineStopID:$insertID")
        if (insertID > 0) myHelper.toast("Machine Stopped due to " + material.name) else myHelper.toast("Machine Stop Entry not Saved in Database.")
        myHelper.stopMachine(insertID, material, resetJourney)
        return insertID
    }
    
    /**
     * This method is called when Loading is done on RLoadActivity. Load data is saved in database but not
     * pushed to server. When Unloading is done, this data is first retrieved from database and updated
     * data is Pushed to server and Updated in Database.
     */
    fun insertTrip(myData: MyData): Long {
        val insertID = db.insertTrip(myData)
        myHelper.log("insertTripID:$insertID")
        return insertID
    }
    
    fun pushUpdateTrip(myData: MyData) {
//        when {
//            myHelper.isOnline() -> pushTrip(myData)
//            else -> {
//                myHelper.toast(noInternetMessage)
        updateTrip(myData)
//            }
//        }
    }
    
    private fun pushTrip(myData: MyData) {
        myHelper.log("pushTrip:$myData")
        
        val call = this.retrofitAPI.pushTrip(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                myHelper.log(response.toString())
                val responseBody = response.body()
                myHelper.log("EWorkResponse:$responseBody")
                if (responseBody!!.success) {
                    myData.isSync = 1
                    updateTrip(myData)
                    
                } else {
                    updateTrip(myData)
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
            }
            
            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
//                myHelper.hideDialog()
//                saveTrip(myData)
                updateTrip(myData)
                myHelper.log("Failure" + t.message)
            }
        })
    }
    
    private fun updateTrip(myData: MyData) {
        val updateID = db.updateTrip(myData)
        myHelper.log("updateTripID:$updateID")
        
        if (updateID > 0)
            checkUpdateServerSyncData()
    }
    
    fun pushInsertSideCasting(eWork: EWork) {
        
        eWork.siteId = myHelper.getMachineSettings().siteId
        eWork.machineId = myHelper.getMachineID()
        eWork.orgId = myHelper.getLoginAPI().org_id
        eWork.operatorId = myHelper.getOperatorAPI().id
        eWork.machineTypeId = myHelper.getMachineTypeID()
        eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
        
        myHelper.log("pushInsertSideCasting:$eWork")
//        when {
//            myHelper.isOnline() -> pushSideCasting(eWork)
//            else -> {
//                myHelper.toast(noInternetMessage)
        insertEWork(eWork, true)
//            }
//        }
    }
    
    private fun pushSideCasting(eWork: EWork) {
        val call = this.retrofitAPI.pushSideCastings(
            myHelper.getLoginAPI().auth_token,
            eWork
        )
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
                myHelper.log("$response")
                val responseBody = response.body()
                myHelper.log("pushSideCastings:$responseBody")
                if (responseBody!!.success) {
                    eWork.isSync = 1
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.run {
                            log("Token Expired:$response")
                            refreshToken()
                        }
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                insertEWork(eWork)
            }
            
            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
                insertEWork(eWork)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }
    
    fun insertEWork(eWork: EWork, pushToServer: Boolean = false): Long {
        eWork.machineTypeId = myHelper.getMachineTypeID()
        eWork.machineId = myHelper.getMachineID()
        val insertID = db.insertEWork(eWork)
        myHelper.log("insertID:$insertID")
        
        if (insertID > 0 && pushToServer)
            checkUpdateServerSyncData()
        return insertID
    }
    
    fun pushUpdateEWork(eWork: EWork): Int {

//        when {
//            myHelper.isOnline() -> pushEWork(eWork)
//            else -> {
//                myHelper.toast(noInternetMessage)
        updateEWork(eWork)
//            }
//        }
        return 1
    }
    
    private fun pushEWork(eWork: EWork) {
        
        myHelper.log("pushSideCastings:$eWork")
        val call = this.retrofitAPI.pushSideCastings(
            myHelper.getLoginAPI().auth_token,
            eWork
        )
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
                myHelper.log("$response")
                val responseBody = response.body()
                myHelper.log("pushSideCastings:$responseBody")
                if (responseBody!!.success) {
                    eWork.isSync = 1
                } else {
                    eWork.isSync = 0
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                updateEWork(eWork)
            }
            
            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
                updateEWork(eWork)
                myHelper.toast(t.message.toString())
                myHelper.log("Failure" + t.message)
            }
        })
    }
    
    private fun updateEWork(eWork: EWork) {
        val updatedID = db.updateEWork(eWork)
        myHelper.log("updateEworkID:$updatedID")
        
        if (updatedID > 0)
            checkUpdateServerSyncData()
    }
    
    fun insertELoad(myData: MyData): Long {
        val insertID = db.insertELoad(myData)
        myHelper.log("insertELoadID:$insertID")
        if (insertID > 0)
            checkUpdateServerSyncData()
        return insertID
    }
    
    fun insertEWorkOffLoad(eWork: EWork): Long {
        
        eWork.orgId = myHelper.getLoginAPI().org_id
        eWork.operatorId = myHelper.getOperatorAPI().id
        eWork.machineTypeId = myHelper.getMachineTypeID()
        eWork.machineId = myHelper.getMachineID()
        
        val insertID = db.insertEWorkOffLoad(eWork)
        myHelper.log("insertEWorkOffLoadID:$insertID")
        return insertID
    }
    
    private fun checkUpdateServerSyncData(showDialog: Boolean = true) {
        // remove all previous data if any and make list empty
        if (myDataList.size > 0)
            myDataList.removeAll(ArrayList())
        
        if (eWorkList.size > 0)
            eWorkList.removeAll(ArrayList())
        
        if (serverSyncList.size > 0)
            serverSyncList.removeAll(ArrayList())
        
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
        
        
        if (myHelper.isOnline()) {
            if (serverSyncList.size > 0)
//                Handler().postDelayed({
//                    //Do something after 100ms
//                }, 5 * 1000)
                pushUpdateServerSync(showDialog)
        } else {
            myHelper.toast(noInternetMessage)
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
    
    private fun pushUpdateServerSync(showDialog: Boolean = true) {
//        val client = OkHttpClient()
        if (showDialog)
            myHelper.showDialog()
        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) //.sslSocketFactory(sslSocketFactory, trustManager)
            .followRedirects(false)
            .followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .cache(null) //new Cache(sContext.getCacheDir(),10*1024*1024)
            .build()
        
        
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
                
                myHelper.log("response:$response")
                val responseString = response.body()!!.string()
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
                        updateServerSync(data)
                        
                    } else {
                        if (responseJObject.getString("message ") == "Token has expired") {
                            myHelper.log("Token Expired:$responseJObject")
                            myHelper.refreshToken()
                        } else {
                            myHelper.toast(responseJObject.getString("message "))
                        }
                    }
                    if (showDialog)
                        myHelper.hideDialog()
                }
                catch (e: Exception) {
                    if (showDialog)
                        myHelper.hideDialog()
                    myHelper.log("${e.message}")
                }
            }
            
            override fun onFailure(call: Call, e: IOException) {
                myHelper.run {
                    if (showDialog)
                        hideDialog()
                    toast(e.message.toString())
                    log("Exception: ${e.printStackTrace()}")
                }
            }
        })
    }
    
    private fun updateServerSync(data: List<ServerSyncAPI>) {
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
    }
    
}
