package app.vsptracker.others

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.work.*
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.serverSync.ServerSyncResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.classes.Material
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.autologout.ForegroundService
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
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
    private val tag = this::class.java.simpleName
    private val myHelper = MyHelper(tag, context)
    private val db = DatabaseAdapter(context)
    private val retrofit = Retrofit.Builder()
        .baseUrl(RetrofitAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(myHelper.skipSSLOkHttpClient().build())
        .build()
    private val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    var isBackgroundCall = true
    private var serverSyncList = ArrayList<ServerSyncAPI>()
    
    val workManager = WorkManager.getInstance(context)
    
    /**
     * All Company Data will be fetched from Server using API Calls by this Class only.
     * Old data will be replaced with new one.
     */
    fun fetchOrgData(isBackgroundCall: Boolean = true) {
        this.isBackgroundCall = isBackgroundCall
        getServerSync()
    }
    
    /**
     * This single API call will be used for getting all data from Server.
     * It has replaced 11 API calls with single Call and more Sync Data calls
     * can be added in this function.
     * It will reduce Internet Usage and User Wait time by 11 times.
     */
    internal fun getServerSync() {
        myHelper.log("deviceDetails:${myHelper.getDeviceDetailsString()}")
        val call = this.retrofitAPI.getServerSync(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token,
            myHelper.getOperatorAPI().id,
            myHelper.getDeviceDetailsString()
        )
        call.enqueue(object : retrofit2.Callback<ServerSyncResponse> {
            override fun onResponse(
                call: retrofit2.Call<ServerSyncResponse>,
                response: retrofit2.Response<ServerSyncResponse>
            ) {
                myHelper.log("Response:$response")
                try {
                    val responseBody = response.body()
                    if (responseBody!!.success) {
                        val getServerSyncData = responseBody.get_data
                        getServerSyncData.forEach { serverSyncAPI ->
                            when (serverSyncAPI.type) {
                                1 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachinesAutoLogouts(serverSyncAPI.operatorAPIList)
                                }
                                2 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachinesTasks(serverSyncAPI.operatorAPIList)
                                }
                                3 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMaterials(serverSyncAPI.operatorAPIList)
                                }
                                4 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertLocations(serverSyncAPI.operatorAPIList)
                                }
                                5 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachines(serverSyncAPI.operatorAPIList)
                                }
                                6 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertStopReasons(serverSyncAPI.operatorAPIList)
                                }
                                7 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachinesPlants(serverSyncAPI.operatorAPIList)
                                }
                                8 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachinesBrands(serverSyncAPI.operatorAPIList)
                                }
                                9 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertMachinesTypes(serverSyncAPI.operatorAPIList)
                                }
                                10 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertSites(serverSyncAPI.operatorAPIList)
                                }
                                11 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.operatorAPIList.size}")
                                    db.insertOperators(serverSyncAPI.operatorAPIList)
                                }
                                12 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertQuestionsTypes(serverSyncAPI.myDataList)
                                }
                                13 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertQuestions(serverSyncAPI.myDataList)
                                }
                                14 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertAdminCheckFormsSchedules(serverSyncAPI.myDataList)
                                }
                                15 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertAdminCheckForms(serverSyncAPI.myDataList)
                                }
                                16 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertAdminCheckFormsCompletedServer(serverSyncAPI.myDataList)
                                }
                                17 -> {
                                    myHelper.log("${serverSyncAPI.type}--${serverSyncAPI.name}--${serverSyncAPI.myDataList.size}")
                                    db.insertOrgsMaps(serverSyncAPI.myDataList)
                                }
                            }
                        }
                        myHelper.hideProgressBar()
                        when {
                            !isBackgroundCall -> {
                                val intent = Intent(context, OperatorLoginActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        myHelper.hideProgressBar()
                        myHelper.toast(responseBody.message)
                    }
                }
                catch (e: Exception) {
                    myHelper.log("getServerSync:${e.localizedMessage}")
                }
        
            }
    
            override fun onFailure(call: retrofit2.Call<ServerSyncResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }
    
    
    fun insertDelay(eWork: EWork) {
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
        if (myHelper.isDailyModeStarted()) myData.isDayWorks = 1 else myData.isDayWorks = 0
        
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
        
        myHelper.log("pushInsertMachineHour:$myData")
        insertMachineHour(myData, pushServerSync)
        myHelper.stopDelay(myData.unloadingGPSLocation)
        myHelper.stopDailyMode()
    
        return true
    }
    
    private fun insertMachineHour(myData: MyData, pushServerSync: Boolean = true) {
        val insertID = db.insertMachineHours(myData)
        if (insertID > 0 && pushServerSync)
            checkUpdateServerSyncData()
    }
    
    fun insertOperatorHour(gpsLocation: GPSLocation) {
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
        val insertID = db.insertOperatorHour(operatorAPI)

//        This method is called when Mode is changed. So Reset Start Time of Operator Hour
        val operator = myHelper.getOperatorAPI()
        operator.startTime = System.currentTimeMillis()
        operator.loadingGPSLocation = gpsLocation
        myHelper.setOperatorAPI(operator)
        
        if (insertID > 0)
            checkUpdateServerSyncData()
    }
    
    /**
     * it will update Machine Stop Entry in Database.
     * and there is separate call in method which is calling this method to update
     * machine status on Server
     */
    fun updateMachineStop(machineData: MyData) {
        val updateID = db.updateMachineStop(machineData)
        myHelper.log("updateMachineStopID:$updateID")
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
        myData.totalTime = 0
        myData.stopTime = 0 // When inserting Machine hour data, this stop time and total time is also
        // updated (values are from machine hours records ) which is incorrect
        // so here we are updating this data to this value, stop_time and total_time will be updated when a machine is started again
        // and there is call to updateMachineStops
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
        // This stopTime could be of Last Unloading data saved in Repeat Journey, So
        // init it to 0, this value will be updated in RUnload Activity
        myData.stopTime = 0
        val insertID = db.insertTrip(myData)
        myHelper.log("insertTripID:$insertID")
        return insertID
    }
    
    fun updateTrip(myData: MyData) {
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
        insertEWork(eWork, true)
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
    
    fun updateEWork(eWork: EWork): Int {
        val updatedID = db.updateEWork(eWork)
        myHelper.log("updateEworkID:$updatedID")
        
        if (updatedID > 0)
            checkUpdateServerSyncData()
        return updatedID
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
    
    fun checkUpdateServerSyncData(type: Int = MyEnum.SERVER_SYNC_DATA_BACKGROUND) {
        val data = Data.Builder()
        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putInt("type", type)
        val myWorkRequest = OneTimeWorkRequestBuilder<ServerSyncCoroutineWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(data.build())
            .addTag(MyEnum.WORKDER_SERVER_SYNC)
            .build()
        // Run the worker synchronously
        workManager.enqueue(myWorkRequest)
        
    }
    
    fun checkUpdateServerSyncDataCoroutine(type: Int) {
        if (serverSyncList.size > 0)
            serverSyncList.removeAll(ArrayList())
        
        addToList(1, myHelper.getTypeName(1), db.getOperatorsHours("ASC"))?.let { serverSyncList.add(it) }
        addToList(2, myHelper.getTypeName(2), db.getTripsByTypes(MyEnum.TRUCK, "ASC"))?.let { serverSyncList.add(it) }
        addToList(3, myHelper.getTypeName(3), db.getTripsByTypes(MyEnum.SCRAPER, "ASC"))?.let { serverSyncList.add(it) }
        addToList(4, myHelper.getTypeName(4), db.getEWorks(MyEnum.SCRAPER_TRIMMING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(5, myHelper.getTypeName(5), db.getELoadHistory("ASC"))?.let { serverSyncList.add(it) }
        addToList(6, myHelper.getTypeName(6), db.getEWorks(MyEnum.EXCAVATOR_TRENCHING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(7, myHelper.getTypeName(7), db.getEWorks(MyEnum.EXCAVATOR_GEN_DIGGING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(8, myHelper.getTypeName(8), db.getMachinesStops("ASC"))?.let { serverSyncList.add(it) }
        addToList(9, myHelper.getTypeName(9), db.getMachinesHours("ASC"))?.let { serverSyncList.add(it) }
        addToList(10, myHelper.getTypeName(10), db.getWaits("ASC"))?.let { serverSyncList.add(it) }
        // Uploading Completed CheckForms images to AWS might take time so skip Completed CheckForms upload to server and
        // uploading images to AWS Bucket when operator logout from app.
        if (type != MyEnum.SERVER_SYNC_DATA_LOGOUT)
            addToList(11, myHelper.getTypeName(11), db.getAdminCheckFormsCompleted("ASC"))?.let { serverSyncList.add(it) }
        
        if (myHelper.isOnline()) {
            // Check if there is data to sync with server OR it is API call to update machine status on server then must
            // make API call
            if (serverSyncList.size > 0 || type == MyEnum.SERVER_SYNC_UPDATE_MACHINE_STATUS) {
                val serverSyncAPI = serverSyncList.find { it.type == 11 }
                // If type is Completed CheckForms then upload remaining CheckForms Answer images to AWS
                if (serverSyncAPI != null) {
                    this.serverSyncList.find { it.type == 11 }!!.myDataList = myHelper.uploadImagesToAWS(serverSyncAPI.myDataList)
                }
                pushUpdateServerSync(type)
            }
        } else {
            myHelper.toast(context.getString(R.string.data_not_pushed))
        }
    }
    
    /**
     * This method will check if there are any Remaining entries which are not Synced.
     * If there are any remaining entries then it will add that data to List.
     * This list will be added to RecyclerView to Display to User.
     */
    @JvmName("MyData")
    fun addToList(type: Int, name: String, list: ArrayList<MyData>): ServerSyncAPI? {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        // TODO uncomment this code when all machine stops entries are updated to Portal
        // val remaining = list.filter { it.isSync == 0 }
        // As some machines stops entries are not updated on Portal, it is better to update all records for a limited time.
        // Purpose of this block of code is to sync machine stops data with portal, when all data will be synced to portal
        // we can remove this code with val remaining = list.filter { it.isSync == 0 }
        val remaining: List<MyData> = when (type) {
            8 -> {
                list
            }
            else -> {
                list.filter { it.isSync == 0 }
            }
        }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        var serverSyncAPI: ServerSyncAPI? = null
        if (remaining.isNotEmpty()) {
            serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.servserSyncModel = ServerSyncModel(name, total, synced, remaining.size)
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myDataList.addAll(remaining)
        }
        return serverSyncAPI
    }
    
    /**
     * method addToList Over Riding
     */
    @JvmName("EWork")
    fun addToList(type: Int, name: String, list: ArrayList<EWork>): ServerSyncAPI? {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        val remaining = list.filter { it.isSync == 0 }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        var serverSyncAPI: ServerSyncAPI? = null
        if (remaining.isNotEmpty()) {
            serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.servserSyncModel = ServerSyncModel(name, total, synced, remaining.size)
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myEWorkList.addAll(remaining)
        }
        return serverSyncAPI
    }
    
    private fun pushUpdateServerSync(type: Int) {
        myHelper.log("pushUpdateServerSync:$type")
        val showDialog = type == MyEnum.SERVER_SYNC_DATA_DIALOG
        if (showDialog)
            myHelper.showDialog(context.getString(R.string.uploading_data_message))
    
        val client = myHelper.skipSSLOkHttpClient().build()
        val data = myHelper.getServerSyncDataAPIString(serverSyncList)
        val operatorID = myHelper.getOperatorAPI().id
        val deviceDetails = myHelper.getDeviceDetailsString()
        val machineID = myHelper.getMachineID().toString()
        // Operator ID = 0 when Logging out from App
        val isMachineRunning = if (myHelper.getIsMachineStopped() || operatorID == 0) "0" else "1"
        myHelper.log("operatorID:$operatorID machineID:$machineID is_running:$isMachineRunning")
        val formBody = FormBody.Builder()
            .add("token", myHelper.getLoginAPI().auth_token)
            .add("operator_id", operatorID.toString())
            .add("device_details", deviceDetails)
            .add("machine_id", machineID)
            .add("is_running", isMachineRunning)
            .add("data", data)
            .build()
    
        val request = Request.Builder()
            .url("${MyEnum.BASE_URL}orgsserversync/store")
            .post(formBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
        
                myHelper.log("response:$response")
                try {
                    val responseString = response.body!!.string()
                    val responseJObject = JSONObject(responseString)
                    val success = responseJObject.getBoolean("success")
                    if (success) {
                        val gson = GsonBuilder().create()
//                      this data is used for convert data arraylist into object to convert in gson
//                      here converting back
                        val data1 = responseJObject.getString("data")
//                      this is complete data sent to server
//                      val dataArray = dataObj.getJSONArray("data")
//                      val dataArray = JSONArray(data1)
                        val serverSyncAPIList = gson.fromJson(data1, Array<ServerSyncAPI>::class.java).toList()
                        myHelper.log("serverSyncAPIList:${serverSyncAPIList}")
//                      here I am getting complete list of data with type. Now I have to update each entry in
//                      App Database and change their status from isSync 0 to 1 as these entries are successfully updated in Portal Database.
                        if (updateServerSync(serverSyncAPIList)) {
                            if (showDialog) {
                                runOnUiThread {
                                    myHelper.toast(context.getString(R.string.all_data_uploaded))
                                }
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
    
    fun updateServerSync(data: List<ServerSyncAPI>): Boolean {
        try {
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
                    11 -> {
                        myHelper.log("CheckForms Completed")
                        db.updateAdminCheckFormsCompleted(serverSyncAPI.myDataList)
                        serverSyncAPI.myDataList.forEach {
                            db.updateAdminCheckFormsData(it.checkFormData)
                        }
                        // As Completed CheckForms are fetched from Server, making this API call will update
                        // Company Completed CheckForms Data
                        fetchOrgData()
                    }
                }
            }
            return true
        }
        catch (e: Exception) {
            myHelper.log("updateServerSyncException:${e.localizedMessage}")
            myHelper.toast("updateServerSyncException:${e.localizedMessage}")
            return false
        }
    
    }
    
    fun logout(machine_stop_reason_id: Int, gpsLocation: GPSLocation = GPSLocation(), sfinish_reading: String = myHelper.getMeterTimeForFinish()) {
        val operatorAPI = myHelper.getOperatorAPI()
        val myData = MyData()
        val meter = myHelper.getMeter()
        // Logout Operator if ID is greater than 0, otherwise clear credentials and stop service
        if (operatorAPI.id > 0) {
            if (meter.isMachineStartTimeCustom)
                myData.isStartHoursCustom = 1
            myData.startHours = myHelper.getMeterTimeForFinish()
            myData.startTime = meter.machineStartTime
            myData.loadingGPSLocation = meter.hourStartGPSLocation
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
                val totalHours = myHelper.getMeterValidValue(sfinish_reading)
                if (!myHelper.getMeterTimeForFinish().equals(totalHours, true)) {
//                val meter = myHelper.getMeter()
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
                myData.machine_stop_reason_id = machine_stop_reason_id
                myData.isSync = 0
                myData.unloadingGPSLocation = gpsLocation
            
                pushInsertMachineHour(myData, false)
            }
            checkUpdateServerSyncData(MyEnum.SERVER_SYNC_DATA_LOGOUT)
            if (machine_stop_reason_id == MyEnum.AUTO_LOGOUT) {
                ForegroundService.stopService(context)
                val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
                mNotificationManager!!.cancelAll()
            }
            myHelper.clearLoginData()
        } else {
            // Operator is already logged out
            ForegroundService.stopService(context)
            val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            mNotificationManager!!.cancelAll()
            myHelper.clearLoginData()
        }
    }
}
