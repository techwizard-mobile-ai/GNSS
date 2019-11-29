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
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.ServerSyncModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_server_sync.*

class ServerSyncActivity : BaseActivity(), View.OnClickListener {

    private val adapterList = ArrayList<ServerSyncModel>()
    private val serverSyncMyDataList = ArrayList<MyData>()
    private val serverSyncEWorkList = ArrayList<EWork>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_server_sync, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(9).isChecked = true

        addToList("Operators Hours", db.getOperatorsHours())
        addToList("Trucks Trips", db.getTripsByTypes(3))
        addToList("Scrapers Trips", db.getTripsByTypes(2))
        addToList("Scrapers Trimmings", db.getEWorks(3))
        addToList("Excavators Loadings", db.getELoadHistory())
        addToList("Excavators Trenching", db.getEWorks(2))
        addToList("Excavators Digging", db.getEWorks(1))
        addToList("Machines Stops", db.getMachinesStops())
        addToList("Machines Hours", db.getMachinesHours())
        addToList("Operators Waiting", db.getWaits())

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
    private fun addToList(name: String, list: ArrayList<MyData>) {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        val remaining = list.filter { it.isSync == 0 }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        if (remaining.isNotEmpty()) {
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
            serverSyncMyDataList.addAll(remaining)
        }
    }

    @JvmName("MyData")
    private fun addToList(name: String, list: ArrayList<EWork>) {
        val total = list.size
        val synced = list.filter { it.isSync == 1 }.size
        val remaining = list.filter { it.isSync == 0 }
        myHelper.log("$name:$total, isSynced:$synced, isRemaining:${remaining.size}")
        if (remaining.isNotEmpty()) {
            adapterList.add(ServerSyncModel(name, total, synced, remaining.size))
            serverSyncEWorkList.addAll(remaining)
        }
    }

    /*
    //        Scraper Trips
            val st = db.getTripsByTypes(2)
            val stTotal = st.size
            val stIsSynced = st.filter{ it.isSync == 1}.size
            val stRemaining = st.filter{ it.isSync == 0}.size



    //        Scraper Trimming
            val str = db.getEWorks(3)
            val strTotal = str.size
            val strIsSynced = str.filter{ it.isSync == 1}.size
            val strRemaining = str.filter{ it.isSync == 0}.size
            if(ttRemaining > 0){
                ss_truck_trips.visibility = View.VISIBLE
                ss_truck_total_trips.text = ":  $ttTotal"
                ss_truck_total_trips_synced.text = ":  $ttIsSynced"
                ss_trucks_total_trips_remaining.text = ":  $ttRemaining"
            }else{
                ss_truck_trips.visibility = View.GONE
            }
            if(stRemaining > 0){
                ss_scraper_trips.visibility = View.VISIBLE
                ss_scraper_total_trips.text = ":  $stTotal"
                ss_scraper_total_trips_synced.text = ":  $stIsSynced"
                ss_scraper_total_trips_remaining.text = ":  $stRemaining"
            }else{
                ss_scraper_trips.visibility = View.VISIBLE
            }
            private fun getTrips() {
                //        Truck Trips
        //        val tt = db.getTripsByTypes(3)
        //        val ttTotal = tt.size
        //        val ttIsSynced = tt.filter { it.isSync == 1 }.size
        //        val ttRemaining = tt.filter { it.isSync == 0 }.size
        //        myHelper.log("Truck Trips:$ttTotal, isSynced:$ttIsSynced, isRemaining:$ttRemaining")
        //        if (ttRemaining > 0) {
        //            serverSyncList.add(ServerSyncModel("Truck Trips", ttTotal, ttIsSynced, ttRemaining))
        //        }
        //        addToList("Operators Hours", db.getOperatorsHours())
        //        addToList("Truck Trips", db.getTripsByTypes(3))
        //        addToList("Scraper Trips", db.getTripsByTypes(2))

                //        Scraper Trips
        //        val st = db.getTripsByTypes(2)
        //        val stTotal = st.size
        //        val stIsSynced = st.filter { it.isSync == 1 }.size
        //        val stRemaining = st.filter { it.isSync == 0 }.size
        //        myHelper.log("Scraper Trips:$stTotal, isSynced:$stIsSynced, isRemaining:$stRemaining")
        //        if (ttRemaining > 0) {
        //            serverSyncList.add(ServerSyncModel("Scraper Trips", stTotal, stIsSynced, stRemaining))
        //        }
            }*/
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.server_sync_upload -> {
                myHelper.log("serverSyncMyData:$serverSyncMyDataList")
                myHelper.log("serverSyncEWork:$serverSyncEWorkList")
            }
        }
    }
}
