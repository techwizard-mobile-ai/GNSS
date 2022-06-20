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
    
    private val serverSyncList = ArrayList<ServerSyncAPI>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_server_sync, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(10).isChecked = true
        populateLists()
    
        server_sync_upload.setOnClickListener(this)
    }
    
    private fun populateLists() {
        if (serverSyncList.size > 0)
            serverSyncList.removeAll(ArrayList())
        if (adapterList.size > 0)
            adapterList.removeAll(ArrayList())
        
        myDataPushSave.addToList(1, myHelper.getTypeName(1), db.getOperatorsHours("ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(2, myHelper.getTypeName(2), db.getTripsByTypes(MyEnum.TRUCK, "ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(3, myHelper.getTypeName(3), db.getTripsByTypes(MyEnum.SCRAPER, "ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(4, myHelper.getTypeName(4), db.getEWorks(MyEnum.SCRAPER_TRIMMING, "ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(5, myHelper.getTypeName(5), db.getELoadHistory("ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(6, myHelper.getTypeName(6), db.getEWorks(MyEnum.EXCAVATOR_TRENCHING, "ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(7, myHelper.getTypeName(7), db.getEWorks(MyEnum.EXCAVATOR_GEN_DIGGING, "ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        // TODO uncomment this code when all machine stops entries are updated to Portal
//        myDataPushSave.addToList(8, myHelper.getTypeName(8), db.getMachinesStops("ASC"))?.let {
//            serverSyncList.add(it)
//            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
//        }
        myDataPushSave.addToList(9, myHelper.getTypeName(9), db.getMachinesHours("ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(10, myHelper.getTypeName(10), db.getWaits("ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        myDataPushSave.addToList(11, myHelper.getTypeName(11), db.getAdminCheckFormsCompleted("ASC"))?.let {
            serverSyncList.add(it)
            it.servserSyncModel?.let { it1 -> adapterList.add(it1) }
        }
        refreshAdapter()
    }
    
    private fun refreshAdapter() {
        
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
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.server_sync_upload -> {
                if (adapterList.size > 0) {
                    if (myHelper.isOnline()) {
                        val serverSyncAPI = serverSyncList.find { it.type == 11 }
                        if (serverSyncAPI != null) {
                            serverSyncList.find { it.type == 11 }!!.myDataList = myHelper.uploadImagesToAWS(serverSyncAPI.myDataList)
                        }
                        pushUpdateServerSync(serverSyncList)
                    } else myHelper.showErrorDialog(resources.getString(R.string.no_internet_connection), resources.getString(R.string.no_internet_explanation))
                } else myHelper.toast(resources.getString(R.string.no_offline_data_to_sync_to_server))
            }
        }
    }
    
    private fun pushUpdateServerSync(serverSyncList: ArrayList<ServerSyncAPI>) {
        myHelper.showDialog(getString(R.string.uploading_data_message))
        myHelper.log("pushUpdateServerSync:$serverSyncList")
        val client = myHelper.skipSSLOkHttpClient().build()
        val formBody = FormBody.Builder()
            .add("token", myHelper.getLoginAPI().auth_token)
            .add("operator_id", myHelper.getOperatorAPI().id.toString())
            .add("device_details", myHelper.getDeviceDetailsString())
            .add("data", myHelper.getServerSyncDataAPIString(serverSyncList))
            .build()
        
        val request = Request.Builder()
            .url(getString(R.string.api_url) + "orgsserversync/store")
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
                        if (myDataPushSave.updateServerSync(data)) {
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
            myHelper.startHomeActivityByType(MyData())
        }
        mDialogView.delete_conversation_cancel.setOnClickListener {
            mAlertDialog.dismiss()
            myHelper.startHomeActivityByType(MyData())
        }
    }
}
