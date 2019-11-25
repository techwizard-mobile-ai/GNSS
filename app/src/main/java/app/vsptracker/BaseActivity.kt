package app.vsptracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.activities.*
import app.vsptracker.activities.common.MachineBreakdownActivity
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.delay.EWorkResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataResponse
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyHelper
import app.vsptracker.others.Utils
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.app_bar_base.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val REQUEST_ACCESS_FINE_LOCATION = 1

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var r: Runnable
    private lateinit var handler: Handler
    private lateinit var menu: Menu
    private lateinit var location: Location
    private val tag1 = "BaseActivity"
    protected lateinit var myHelper: MyHelper
    protected lateinit var db: DatabaseAdapter
    protected lateinit var myData: MyData
    private var locationManager: LocationManager? = null
    lateinit var gpsLocation: GPSLocation
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI
    lateinit var myDataPushSave: MyDataPushSave
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        myHelper = MyHelper(tag1, this)
        if (myHelper.getIsMachineStopped() || myHelper.getMachineID() < 1) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        gpsLocation = GPSLocation()
        db = DatabaseAdapter(this)

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        myDataPushSave = MyDataPushSave(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.base_nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        when (myHelper.getMachineTypeID()) {
            1 -> {
                toolbar_title.text = "Excavator # ${myHelper.getMachineNumber()} : ${myHelper.getOperatorAPI().name}"
            }
            2 -> {
                toolbar_title.text = "Scraper # ${myHelper.getMachineNumber()} : ${myHelper.getOperatorAPI().name}"
            }
            3 -> {
                toolbar_title.text = "Truck # ${myHelper.getMachineNumber()} :  ${myHelper.getOperatorAPI().name}"
            }
        }

        base_machine_status.setOnClickListener {
            val intent = Intent(this@BaseActivity, MachineStatusActivity::class.java)
            startActivity(intent)
        }

        base_daily_mode.setOnClickListener {
            val intent = Intent(this@BaseActivity, DayWorksActivity::class.java)
            startActivity(intent)
        }

        myHelper.hideKeyboardOnClick(base_content_frame)

        bottomNavigation = findViewById(R.id.base_navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        menu = bottomNavigation.menu
        menu[0].isCheckable = false

//        handler = Handler()
//        r = Runnable {
//            myHelper.toast("User is Inacitve for 1 minute.")
//            myHelper.log("User is Inacitve for 1 minute.")
//
//            myHelper.setOperatorAPI(MyData())
//            val intent = Intent(this, OperatorLoginActivity::class.java)
//            startActivity(intent)
//        }
//        startHandler()
    }

/*
    override fun onUserInteraction() {
        super.onUserInteraction()
        myHelper.log("--------------User Interacted")
//        stopHandler()
//        startHandler()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
//        stopHandler()
//        startHandler()
    }

    private fun stopHandler() {
        myHelper.log("stopHandler")
        handler.removeCallbacks(r)
        handler.removeCallbacksAndMessages(null)
    }

    private fun startHandler() {
        myHelper.log("startHandler")
        handler.postDelayed(r, 1 * 60000) //for 1 minutes
//        val autoLogout = db.getMachinesAutoLogout()[0]
//        myHelper.log("AutoLogout:$autoLogout")
//        handler.postDelayed(r, (autoLogout.autoLogoutTime!!.toLong() *60 * 1000)) //for 30 sec
    }*/

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            if(myHelper.isNavEnabled())
            when (item.itemId) {
                R.id.navb_map -> {
                    if (!myHelper.getIsMapOpened()) {
                        myHelper.setIsMapOpened(true)
                        val intent = Intent(this, Map1Activity::class.java)
                        startActivity(intent)
                    } else {
                        myHelper.toast("Map is Already Opened.")
                    }
                }
                R.id.navb_delay -> {
                    when {
                        myHelper.getIsMachineStopped() -> myHelper.toast("Please Start Machine First.")
                        myHelper.getMachineID() < 1 -> myHelper.toast("Please Select Machine First.")
                        else -> toggleDelay()
                    }
                }

            }
            false
        }

    override fun onResume() {
        super.onResume()
        startGPS()
//        If Navigation is Disabled Lock Side Menu
        if( !myHelper.isNavEnabled()){
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
//        base_nav_view.setCheckedItem(base_nav_view.checkedItem.itemId)

//        myHelper.log("CurrentActivity:${this::class.java.simpleName}")
//        onNavigationItemSelected(base_nav_view.getMenu().getItem(0));
//        if(this::class.java.simpleName == "THomeActivity"){
//            onNavigationItemSelected(base_nav_view.menu.getItem(0));
//        }


        if (myHelper.getIsMachineStopped()) {
            base_machine_status_layout.visibility = View.VISIBLE

            when (myHelper.getMachineStoppedReason()) {
                "Weather" -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_beach_access))
                        .into(base_machine_status_icon)
                }
                "Other 1" -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_restaurant))
                        .into(base_machine_status_icon)
                }
                else -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_report))
                        .into(base_machine_status_icon)
                }
            }


//            val text ="<font color=#FF382A>Machine is Stopped. </font><font color=#106d14><u>Click here to Start Machine</u>.</font>"
            val text = "<font color=#106d14><u>Click here to Start Machine</u>.</font>"
            base_machine_status.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
            myHelper.log("Is Machine Stopped: ${myHelper.getIsMachineStopped()}")
            myHelper.log("Machine Stopped Reason: ${myHelper.getMachineStoppedReason()}")

        } else {
            base_machine_status_layout.visibility = View.GONE
        }

        if (myHelper.isDailyModeStarted()) {
            val text =
                "<font color=#FF382A>Day Works is ON. </font><font color=#106d14><u>Switch Standard Mode</u>.</font>"
            base_daily_mode.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
            base_daily_mode.visibility = View.VISIBLE
        } else {
            base_daily_mode.visibility = View.GONE
        }

        if (myHelper.isDelayStarted()) {
            menu.findItem(R.id.navb_delay).title = "Stop Waiting"
            menu.findItem(R.id.navb_delay).icon = getDrawable(R.drawable.ic_started)
            menu.findItem(R.id.navb_delay).isChecked = true

        } else {
            menu.findItem(R.id.navb_delay).title = "Start Waiting"
            menu.findItem(R.id.navb_delay).icon = getDrawable(R.drawable.ic_stopped)
            menu.findItem(R.id.navb_map).isChecked = true
        }

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            myHelper.toast("Please Use App Navigation Buttons.")
//            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {
                val data = MyData()
                myHelper.startHomeActivityByType(data)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_day_works -> {

                val intent = Intent(this, DayWorksActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_machine_breakdown -> {

                val intent = Intent(this, MachineBreakdownActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_logout -> {
                myHelper.logout(this)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_stop_machine -> {

                val intent = Intent(this, MachineStatusActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_night_mode -> {

                myHelper.log("theme:" + applicationContext.theme)
                myHelper.log("theme1:$theme")
                if (myHelper.isNightMode()) {
                    Utils.changeToTheme(this@BaseActivity, 0)
                    myHelper.setNightMode(false)
                } else {
                    Utils.changeToTheme(this@BaseActivity, 1)
                    myHelper.setNightMode(true)
                }
            }
            R.id.nav_change_machine -> {
                val intent = Intent(this, MachineTypeActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }

            R.id.nav_load_history -> {
                myHelper.startHistoryByType()
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_email -> {
                doEmail()
                myHelper.setIsMapOpened(false)
            }

            R.id.nav_delay -> {
                val intent = Intent(this, DelayActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }

            R.id.nav_hours_reporting-> {
                val intent = Intent(this, HoursReportingActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_server_sync-> {
                val intent = Intent(this, ServerSyncActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun toggleDelay() {
        if (myHelper.isDelayStarted()) {
            stopDelay()
        } else {
            startDelay()
        }
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }

    private fun startDelay() {
        if (!myHelper.isDelayStarted()) {
            myHelper.toast("Waiting Started.")
            myHelper.startDelay(gpsLocation)
            menu.findItem(R.id.navb_delay).icon = getDrawable(R.drawable.ic_started)
            menu.findItem(R.id.navb_delay).title = "Stop Waiting"
            menu.findItem(R.id.navb_delay).isChecked = true
            val intent = Intent(this@BaseActivity, DelayActivity::class.java)
            startActivity(intent)
        }
    }

    fun stopDelay() {

        if (myHelper.isDelayStarted()) {
            val gpslocation = gpsLocation
            myHelper.stopDelay(gpslocation)

            val meter = myHelper.getMeter()
            val eWork = EWork()
            eWork.startTime = meter.delayStartTime
            eWork.stopTime = meter.delayStopTime
            eWork.totalTime = meter.delayTotalTime
            eWork.loadingGPSLocation = meter.delayStartGPSLocation
            eWork.unloadingGPSLocation = meter.delayStopGPSLocation

            menu.findItem(R.id.navb_delay).icon = getDrawable(R.drawable.ic_stopped)
            menu.findItem(R.id.navb_delay).title = "Start Waiting"
            menu.findItem(R.id.navb_map).isChecked = true

            eWork.machineId = myHelper.getMachineID()
            eWork.orgId = myHelper.getLoginAPI().org_id
            eWork.siteId = myHelper.getMachineSettings().siteId
            eWork.operatorId = myHelper.getOperatorAPI().id
            eWork.machineTypeId = myHelper.getMachineTypeID()
            val time = System.currentTimeMillis()
            eWork.time = time.toString()

            when {
                myHelper.isDailyModeStarted() -> eWork.isDaysWork = 1
                else -> eWork.isDaysWork = 0
            }

            eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
            eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
            myDataPushSave.pushInsertDelay(eWork)

//            val meter = myHelper.getMeter()
            myHelper.toast("Waiting Stopped.\nStart Time: ${myHelper.getTime(meter.delayStartTime)}Hrs.\nTotal Time: ${myHelper.getFormattedTime(meter.delayTotalTime)}")
            var dataNew = MyData()
            val bundle: Bundle? = this.intent.extras
            if (bundle != null) {
                dataNew = bundle.getSerializable("myData") as MyData
            }
            intent.putExtra("myData", dataNew)
            finishFromChild(DelayActivity())

//            when {
//                myHelper.isOnline() -> pushDelay(eWork)
//            }
//            saveDelay(eWork)
        }
    }

    /**
     * This method will do following actions.
     * 1. Save Machine Hours as Machine is Stopped.
     * 2. Push Machine Hours To Server.
     * 3. Insert Machine Hours in Database.
     * 4. Stop Waiting (Delay) if Started.
     * 5. Stop Daily Mode if Started.
     * 6. Clear Operator Login Credentials and Make Operator Logout.
     * 7. Reset Last Journey.
     * 8. Redirect to Operator Login Screen.
     */
    fun saveMachineHour(myData: MyData) {

        myData.siteId = myHelper.getMachineSettings().siteId
        myData.machineId = myHelper.getMachineID()
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()

        myData.stopTime = System.currentTimeMillis()

        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        myData.unloadingGPSLocation = gpsLocation

        myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)

        val time = System.currentTimeMillis()
        myData.stopTime = time
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())

//        if (myHelper.isOnline()) {
//            pushMachineHour(myData)
//        } else {
//            db.insertMachineHours(myData)
//        }
        myDataPushSave.pushInsertMachineHour(myData)

        myHelper.stopDelay(gpsLocation)
        myHelper.stopDailyMode()
        myHelper.setOperatorAPI(MyData())

        val data = MyData()
        myHelper.setLastJourney(data)

        val intent = Intent(this, OperatorLoginActivity::class.java)
        startActivity(intent)
        finishAffinity()

    }

    /*    private fun pushMachineHour(myData: MyData) {

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
                    myHelper.log("pushSideCastings:$responseBody")
                    if (responseBody!!.success) {
                        myData.isSync = 1
    //                    saveDelay(eWork)
                        db.insertMachineHours(myData)

                    } else {
                        db.insertMachineHours(myData)

                        if (responseBody.message == "Token has expired") {
                            myHelper.log("Token Expired:$response")
                            myHelper.refreshToken()
                        } else {
                            myHelper.toast(responseBody.message)
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                    db.insertMachineHours(myData)

                    myHelper.toast(t.message.toString())
                    myHelper.log("Failure" + t.message)
                }
            })
        }*/

    fun pushSideCasting(eWork: EWork) {

        eWork.siteId = myHelper.getMachineSettings().siteId
        eWork.machineId = myHelper.getMachineID()
        eWork.orgId = myHelper.getLoginAPI().org_id
        eWork.operatorId = myHelper.getOperatorAPI().id
        eWork.machineTypeId = myHelper.getMachineTypeID()

//        eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
        eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)


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
                val responseBody = response.body()
                myHelper.log("pushSideCastings:$responseBody")
                if (responseBody!!.success) {
                    eWork.isSync = 1
//                    saveDelay(eWork)

                } else {
//                    saveDelay(eWork)
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
//                saveDelay(eWork)
                myHelper.toast(t.message.toString())
                myHelper.log("Failure" + t.message)
            }
        })
    }

    /*
    private fun pushDelay(eWork: EWork) {
        myHelper.log("pushDelay:$eWork")
        val call = this.retrofitAPI.pushDelay(
            myHelper.getLoginAPI().auth_token,
            eWork
        )
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
//                myHelper.hideDialog()
                val responseBody = response.body()
                myHelper.log("EWorkResponse:$response")
                if (responseBody!!.success) {
                    eWork.isSync = 1
//                    saveDelay(eWork)

                } else {
//                    saveDelay(eWork)
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
//                myHelper.hideDialog()
//                saveDelay(eWork)
                myHelper.toast(t.message.toString())
                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun saveDelay(eWork: EWork) {

        val insertID = db.insertDelay(eWork)
        if (insertID > 0) {

            val meter = myHelper.getMeter()
            myHelper.toast("Waiting Stopped.\nStart Time: ${myHelper.getTime(meter.delayStartTime)}Hrs.\nTotal Time: ${myHelper.getFormattedTime(meter.delayTotalTime)}")
            var dataNew = MyData()
            val bundle: Bundle? = this.intent.extras
            if (bundle != null) {
                dataNew = bundle.getSerializable("myData") as MyData
            }
            intent.putExtra("myData", dataNew)
            finishFromChild(DelayActivity())
        } else {
            myHelper.toast("Waiting Not Stopped.")
        }
    }*/
    fun pushLoad(myData: MyData) {

        myData.loadingGPSLocation = gpsLocation
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.siteId = myHelper.getMachineSettings().siteId
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineTypeId = myHelper.getMachineTypeID()
        myData.machineId = myHelper.getMachineID()

        stopDelay()
        val currentTime = System.currentTimeMillis()
        myData.startTime = currentTime
        myData.stopTime = currentTime
        myData.totalTime = myData.stopTime - myData.startTime
        if (myHelper.isDailyModeStarted()) {
            myData.isDaysWork = 1
        } else {
            myData.isDaysWork = 0
        }
        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime)

        myHelper.log("pushLoads:$myData")

        val call = this.retrofitAPI.pushLoads(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val responseBody = response.body()
                myHelper.log("EWorkResponse:$response")
                if (responseBody!!.success) {
                    myHelper.toast("Load Pushed to Server Successfully.")
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$responseBody")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                myHelper.toast("Failure" + t.message)
            }
        })
    }

    private fun doEmail() {

        val versionCode = BuildConfig.VERSION_CODE
        val device = Build.DEVICE
        val build = Build.BRAND
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val andoridOS = Build.VERSION.SDK_INT.toString()

        val details =
            "App_version:$versionCode--Device:$device--Build:$build--Manufacturer:$manufacturer--Model:$model--AndroidOS:$andoridOS"

        val email = "support@vsptracker.com"
        val addressees: Array<String> = arrayOf(email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addressees)
            putExtra(Intent.EXTRA_SUBJECT, "Error Reporting About Android App")
            putExtra(
                Intent.EXTRA_TEXT,
                "Hi, I like to notify you about an error I faced while using App. Device Details: $details"
            )
            intent.type = "message/rfc822"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun startGPS() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
//            myHelper.log("Permission Granted.")
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                locationListener
            )

        } catch (ex: SecurityException) {
            myHelper.log("No Location Available:${ex.message}")
            requestPermission()
        }

    }

    private fun stopGPS() {
        locationManager?.removeUpdates(locationListener)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )

            }


        } else {
            // Permission has already been granted

            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0f,
                    locationListener
                )
            } else {
                showGPSDisabledAlertToUser()
            }
        }
    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Goto Settings Page\nTo Enable GPS"
            ) { _, _ ->
                val callGPSSettingIntent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startGPS()
                } else {
                    myHelper.log("GPS Permission Permanently Denied.")
                    showSettings()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showSettings() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "You have previously declined GPS permission.\n" + "You must approve this permission in \"Permissions\" in the app settings on your device.",
            Snackbar.LENGTH_LONG
        ).setAction(
            "Settings"
        ) {
            startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        }
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById(R.id.snackbar_text) as TextView
        textView.maxLines = 5  //Or as much as you need
        snackbar.show()
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            makeUseOfLocation(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            myHelper.log("Status Changed.")
        }

        override fun onProviderEnabled(provider: String) {
            myHelper.log("Location Enabled.")
        }

        override fun onProviderDisabled(provider: String) {
            myHelper.log("Location Disabled.")
            showGPSDisabledAlertToUser()
        }
    }

    private fun makeUseOfLocation(location1: Location?) {
        latitude = location1!!.latitude
        longitude = location1.longitude
        location = location1

        gpsLocation.latitude = location1.latitude
        gpsLocation.longitude = location1.longitude
        gpsLocation.altitude = location1.altitude

        gpsLocation.accuracy = location1.accuracy
        gpsLocation.bearing = location1.bearing
        gpsLocation.speed = location1.speed

        gpsLocation.provider = location1.provider
        gpsLocation.elapsedRealtimeNanos = location1.elapsedRealtimeNanos
//        gpsLocation.extras = location1.extras
        gpsLocation.time = location1.time

    }
}

