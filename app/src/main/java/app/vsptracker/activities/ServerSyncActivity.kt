package app.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_server_sync.*

class ServerSyncActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_server_sync, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(9).isChecked = true
//        Scraper Trips
        val st = db.getTripsByTypes(2)
        val stTotal = st.size
        val stIsSynced = st.filter{ it.isSync == 1}.size
        val stRemaining = st.filter{ it.isSync == 0}.size

//        Truck Trips
        val tt = db.getTripsByTypes(3)
        val ttTotal = tt.size
        val ttIsSynced = tt.filter{ it.isSync == 1}.size
        val ttRemaining = tt.filter{ it.isSync == 0}.size

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


        myHelper.log("Truck Trips:$ttTotal, isSynced:$ttIsSynced, isRemaining:$ttRemaining")
        myHelper.log("Scraper Trips:$stTotal, isSynced:$stIsSynced, isRemaining:$stRemaining")

    }
    override fun onClick(view: View?) {
    }
}
