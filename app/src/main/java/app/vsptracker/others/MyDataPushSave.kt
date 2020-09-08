package app.vsptracker.others

import android.app.Activity
import android.content.Context
import android.content.Intent
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.serverSync.ServerSyncResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.database.DatabaseAdapter
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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
    
    /**
     * All Company Data will be fetched from Server using API Calls by this Class only.
     * Old data will be replaced with new one.
     */
    fun fetchOrgData() {
        getServerSync()
    }
    
    /**
     * This single API call will be used for getting all data from Server.
     * It has replaced 11 API calls with single Call and more Sync Data calls
     * can be added in this function.
     * It will reduce Internet Usage and User Wait time by 11 times.
     */
    private fun getServerSync() {
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
    
    fun insertOperatorHour(myData: MyData) {
        val insertID = db.insertOperatorHour(myData)
        if (insertID > 0)
            checkUpdateServerSyncData()
    }
    
    /**
     * Machine Stop Entry is Pushed when Machine is Started Again.
     * When Machine is Started, Entry will be pushed to Portal and then
     * it will update Machine Stop Entry in Database.
     */
    fun updateMachineStop(machineData: MyData) {
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
            checkUpdateServerSyncData(true)
        return insertID
    }
    
    fun updateEWork(eWork: EWork): Int {
        val updatedID = db.updateEWork(eWork)
        myHelper.log("updateEworkID:$updatedID")
        
        if (updatedID > 0)
            checkUpdateServerSyncData(true)
        return updatedID
    }
    
    fun insertELoad(myData: MyData): Long {
        val insertID = db.insertELoad(myData)
        myHelper.log("insertELoadID:$insertID")
        if (insertID > 0)
            checkUpdateServerSyncData(true)
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
    
    fun checkUpdateServerSyncData(showDialog: Boolean = false, isLogoutCall: Boolean = false) {
        if (serverSyncList.size > 0)
            serverSyncList.removeAll(ArrayList())
        
        addToList(1, "Operators Hours", db.getOperatorsHours("ASC"))?.let { serverSyncList.add(it) }
        addToList(2, "Trucks Trips", db.getTripsByTypes(MyEnum.TRUCK, "ASC"))?.let { serverSyncList.add(it) }
        addToList(3, "Scrapers Trips", db.getTripsByTypes(MyEnum.SCRAPER, "ASC"))?.let { serverSyncList.add(it) }
        addToList(4, "Scrapers Trimmings", db.getEWorks(MyEnum.SCRAPER_TRIMMING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(5, "Excavators Prod. Digging", db.getELoadHistory("ASC"))?.let { serverSyncList.add(it) }
        addToList(6, "Excavators Trenching", db.getEWorks(MyEnum.EXCAVATOR_TRENCHING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(7, "Excavators Gen. Digging", db.getEWorks(MyEnum.EXCAVATOR_GEN_DIGGING, "ASC"))?.let { serverSyncList.add(it) }
        addToList(8, "Machines Stops", db.getMachinesStops("ASC"))?.let { serverSyncList.add(it) }
        addToList(9, "Machines Hours", db.getMachinesHours("ASC"))?.let { serverSyncList.add(it) }
        addToList(10, "Operators Waiting", db.getWaits("ASC"))?.let { serverSyncList.add(it) }
        // Uploading Completed CheckForms images to AWS might take time so skip Completed CheckForms upload to server and
        // uploading images to AWS Bucket when operator logout from app.
        if (!isLogoutCall)
            addToList(11, "CheckForms Completed", db.getAdminCheckFormsCompleted("ASC"))?.let { serverSyncList.add(it) }
        
        
        if (myHelper.isOnline()) {
            if (serverSyncList.size > 0) {
                val serverSyncAPI = serverSyncList.find { it.type == 11 }
                // If type is Completed CheckForms then upload remaining CheckForms Answer images to AWS
                if (serverSyncAPI != null) {
                    this.serverSyncList.find { it.type == 11 }!!.myDataList = myHelper.uploadImagesToAWS(serverSyncAPI.myDataList)
                }
                pushUpdateServerSync(showDialog, isLogoutCall)
            }
        } else {
            myHelper.toast(MyEnum.NO_INTERNET_MESSAGE)
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
        val remaining = list.filter { it.isSync == 0 }
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
    
    private fun pushUpdateServerSync(showDialog: Boolean = false, isLogoutCall: Boolean = false) {
        if (showDialog) {
            if (isLogoutCall) {
                myHelper.showDialog(context.getString(R.string.logging_out_message))
            } else {
                myHelper.showDialog()
            }
        }
        val client = myHelper.skipSSLOkHttpClient().build()
        
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
                        val data = gson.fromJson(data1, Array<ServerSyncAPI>::class.java).toList()
                        myHelper.log("data:${data}")
//                      here I am getting complete list of data with type. Now I have to update each entry in
//                      App Database and change their status from isSync 0 to 1 as these entries are successfully updated in Portal Database.
                        if (isLogoutCall) {
                            if (updateServerSync(data)) {
                                runOnUiThread {
                                    myHelper.toast(context.getString(R.string.all_data_uploaded))
                                    myHelper.clearLoginData()
                                    (context as Activity).finishAffinity()
                                }
                            }
                        } else {
                            updateServerSync(data)
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
}
