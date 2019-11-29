package app.vsptracker.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.ServerSyncAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.ServerSyncModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_server_sync.*

class ServerSyncActivity : BaseActivity(), View.OnClickListener {

    private val adapterList = ArrayList<ServerSyncModel>()
    private val myDataList = ArrayList<MyData>()
    private val eWorkList = ArrayList<EWork>()
    private val serverSyncList = ArrayList<ServerSyncAPI>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_server_sync, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(9).isChecked = true

        addToList(1, "Operators Hours", db.getOperatorsHours())
        addToList(2, "Trucks Trips", db.getTripsByTypes(3))
        addToList(3, "Scrapers Trips", db.getTripsByTypes(2))
        addToList(4, "Scrapers Trimmings", db.getEWorks(3))
        addToList(5, "Excavators Loadings", db.getELoadHistory())
        addToList(6, "Excavators Trenching", db.getEWorks(2))
        addToList(7, "Excavators Digging", db.getEWorks(1))
        addToList(8, "Machines Stops", db.getMachinesStops())
        addToList(9, "Machines Hours", db.getMachinesHours())
        addToList(10, "Operators Waiting", db.getWaits())

        if (adapterList.size > 0) {
            val mAdapter = ServerSyncAdapter(this, adapterList)
            ss_rv.layoutManager = LinearLayoutManager(this as Activity, RecyclerView.VERTICAL, false)
            ss_rv.adapter = mAdapter
        }
        server_sync_upload.setOnClickListener(this)
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
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
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
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
            eWorkList.addAll(remaining)

            val serverSyncAPI = ServerSyncAPI()
            serverSyncAPI.type = type
            serverSyncAPI.name = name
            serverSyncAPI.myEWorkList.addAll(remaining)
            serverSyncList.add(serverSyncAPI)
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.server_sync_upload -> {
                if (myHelper.isOnline()) myDataPushSave.pushUpdateServerSync(serverSyncList)
                else myHelper.toast("No Internet Connection. Please Connect to Internet and Try Again.")
            }
        }
    }
}
