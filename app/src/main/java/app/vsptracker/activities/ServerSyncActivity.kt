package app.vsptracker.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.ServerSyncAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.others.MyEnum
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_server_sync.*
import kotlinx.android.synthetic.main.ss_updated_notification.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ServerSyncActivity : BaseActivity(), View.OnClickListener {
    
    private lateinit var mAdapter: ServerSyncAdapter
    
    private val adapterList = ArrayList<ServerSyncModel>()
    
    //    private val myDataList = ArrayList<MyData>()
//    private val eWorkList = ArrayList<EWork>()
    private val serverSyncList = ArrayList<ServerSyncAPI>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_server_sync, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(9).isChecked = true
        
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
//        addToList(11, "CheckForms Completed", db.getAdminCheckFormsCompleted("ASC"))
//        addToListCheckFormsCompleted(11, "CheckForms Completed", db.getAdminCheckFormsCompleted("ASC"))
        
        refreshData()
        
        server_sync_upload.setOnClickListener(this)
    }
    
    fun uploadCompletedCheckForms(){
        addToList(11, "CheckForms Completed", db.getAdminCheckFormsCompleted("ASC"))
    }
    
    fun refreshData() {
        
        myHelper.log("adapterListSize:${adapterList.size}")
        if (adapterList.size > 0) {
            mAdapter = ServerSyncAdapter(this, adapterList)
            ss_rv.layoutManager = LinearLayoutManager(this as Activity, RecyclerView.VERTICAL, false)
            ss_rv.adapter = mAdapter
            no_server_sync_data.visibility = View.GONE
            ss_rv.visibility = View.VISIBLE
        } else {
            no_server_sync_data.visibility = View.VISIBLE
            ss_rv.visibility = View.GONE
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
        myHelper.log("completedCheckForms:$list")
        if (remaining.isNotEmpty()) {
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
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
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
//            eWorkList.addAll(remaining)
            
            val serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myEWorkList.addAll(remaining)
            serverSyncList.add(serverSyncAPI)
        }
    }
    
    private fun removeItem(myData: MyData): Boolean {
        if (myData.isSync == 0) {
            myData.checkFormData.forEach { checkFormDatum ->
                checkFormDatum.answerDataObj.imagesList.forEach { images ->
                    if (images.localImagePath.isNotBlank() && images.awsImagePath.isBlank()) {
                        return true
                    }
                }
            }
            return false
        } else {
            return true
        }
    }
    
    private fun addToListCheckFormsCompleted(type: Int, name: String, list: ArrayList<MyData>) {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        myHelper.log("totalList:$list")
        myHelper.log("syncedList:$synced")
//        val remaining = ArrayList<MyData>()
        
        for (i in 0 until list.size - 1) {
            if (removeItem(list[i])) {
                list.remove(list[i])
            }
        }
        
        myHelper.log("list:${list.size}")
        if (list.isNotEmpty()) {
            
            adapterList.add(ServerSyncModel(name, total, synced, total - synced))
            val serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myDataList.addAll(list)
            serverSyncList.add(serverSyncAPI)
        }
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.server_sync_upload -> {
                if (adapterList.size > 0) {
                    if (myHelper.isOnline()) {
                        pushUpdateServerSync(serverSyncList)
                    } else myHelper.showErrorDialog(resources.getString(R.string.no_internet_connection), resources.getString(R.string.no_internet_explanation))
                } else myHelper.toast(resources.getString(R.string.no_offline_data_to_sync_to_server))
            }
        }
    }
    
    private fun pushUpdateServerSync(serverSyncList: ArrayList<ServerSyncAPI>) {
        myHelper.showDialog()
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("token", myHelper.getLoginAPI().auth_token)
            .add("operator_id", myHelper.getOperatorAPI().id.toString())
            .add("device_details", myHelper.getDeviceDetailsString())
            .add("data", myHelper.getServerSyncDataAPIString(serverSyncList))
            .build()
        
        val request = Request.Builder()
            .url(MyEnum.BASE_URL + "orgsserversync/store")
            .post(formBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                myHelper.hideDialog()
                val responseString = response.body!!.string()
                myHelper.log("pushUpdateServerSync:${response}")
                myHelper.log("pushUpdateServerSync:${response.body.toString()}")
                val responseJObject = JSONObject(responseString)
                val success = responseJObject.getBoolean("success")
                myHelper.log("success:$success")
                val message = responseJObject.getString("message")
                myHelper.log("message:$message")
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
//                                myHelper.toast("All Data Uploaded to Server Successfully.")
                                mAdapter.notifyDataSetChanged()
                                updatedNotification()
                            }
                        }
                        
                    } else {
                        if (responseJObject.getString("message ") == "Token has expired") {
                            myHelper.log("Token Expired:$responseJObject")
                            myHelper.refreshToken()
                        } else {
                            myHelper.toast(responseJObject.getString("message"))
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
                    log("Exception: ${e.printStackTrace()}")
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
                11 -> {
                    myHelper.log("CheckForms Completed")
                    db.updateAdminCheckFormsCompleted(serverSyncAPI.myDataList)
                    serverSyncAPI.myDataList.forEach {
                        db.updateAdminCheckFormsData(it.checkFormData)
                    }
                }
            }
        }
        return true
    }
    
    @SuppressLint("InflateParams")
    private fun updatedNotification() {
        
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.ss_updated_notification, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(false)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        
        
        mDialogView.delete_conversation_yes.setOnClickListener {
            mAlertDialog.dismiss()
//            finish()
//            startActivity(intent)
//            recreate()
            myHelper.startHomeActivityByType(MyData())
        }
        mDialogView.delete_conversation_cancel.setOnClickListener {
            mAlertDialog.dismiss()
//            finish()
//            startActivity(intent)
//            recreate()
            myHelper.startHomeActivityByType(MyData())
        }
        
    }
}
