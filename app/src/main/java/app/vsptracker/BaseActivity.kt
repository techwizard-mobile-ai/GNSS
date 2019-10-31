package app.vsptracker

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
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
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.activities.DayWorksActivity
import app.vsptracker.activities.DelayActivity
import app.vsptracker.activities.MachineTypeActivity
import app.vsptracker.activities.Map1Activity
import app.vsptracker.activities.common.MachineStatus1Activity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.delay.EWorkResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataResponse
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.Utils
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_base.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class BaseActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var menu: Menu
    private lateinit var location: Location
    val TAG1 = "BaseActivity"
    protected lateinit var myHelper: app.vsptracker.MyHelper
    protected lateinit var db: DatabaseAdapter
    protected lateinit var myData: MyData
    private var locationManager: LocationManager? = null
    lateinit var gpsLocation: GPSLocation
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private val REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var bottomNavigation: BottomNavigationView

    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(app.vsptracker.R.layout.activity_base)
        val toolbar: Toolbar = findViewById(app.vsptracker.R.id.toolbar)
        setSupportActionBar(toolbar)

        myHelper = app.vsptracker.MyHelper(TAG1, this)
        gpsLocation = GPSLocation()
        db = DatabaseAdapter(this)

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)


        val drawerLayout: DrawerLayout = findViewById(app.vsptracker.R.id.drawer_layout)
        val navView: NavigationView = findViewById(app.vsptracker.R.id.base_nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            app.vsptracker.R.string.navigation_drawer_open,
            app.vsptracker.R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        when (myHelper.getMachineTypeID()) {
            1 -> {
                toolbar_title.text = "Excavator # ${myHelper.getMachineNumber() } : ${myHelper.getOperatorAPI().name}"
            }
            2 -> {
                toolbar_title.text = "Scraper # ${myHelper.getMachineNumber() } : ${myHelper.getOperatorAPI().name}"
            }
            3 -> {
                toolbar_title.text = "Truck # ${myHelper.getMachineNumber() } :  ${myHelper.getOperatorAPI().name}"
            }
        }

        base_machine_status.setOnClickListener {
            val intent = Intent(this@BaseActivity, MachineStatus1Activity::class.java)
            startActivity(intent)
        }

        base_daily_mode.setOnClickListener {
            val intent = Intent(this@BaseActivity, DayWorksActivity::class.java)
            startActivity(intent)
        }

        myHelper.hideKeybaordOnClick(base_content_frame)

        bottomNavigation = findViewById(app.vsptracker.R.id.base_navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        menu = bottomNavigation.getMenu()
        menu.get(0).setCheckable(false)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            myHelper.log("isChecked:${item.isChecked}")
            myHelper.log("isEnabled:${item.isEnabled}")
            myHelper.log("itemId:${item.itemId}")
            myHelper.log("title:${item.title}")
            myHelper.log("menuInfo:${item.menuInfo}")
            when (item.itemId) {
                app.vsptracker.R.id.navb_map -> {
                    if(!myHelper.getIsMapOpened()){
                        myHelper.setIsMapOpened(true)
                        val intent = Intent(this, Map1Activity::class.java)
                        startActivity(intent)
                    }else{
                        myHelper.toast("Map is Already Opened.")
                    }
                }
                app.vsptracker.R.id.navb_delay -> {
                    if(myHelper.getIsMachineStopped()){
                        myHelper.toast("Please Start Machine First.")
                    }else{
                        toggleDelay()
                    }
                }

            }
            false
        }


    override fun onResume() {
        super.onResume()
        startGPS()

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
                    Glide.with(this).load(resources.getDrawable(app.vsptracker.R.drawable.ic_action_beach_access))
                        .into(base_machine_status_icon)
                }
                "Other 1" -> {
                    Glide.with(this).load(resources.getDrawable(app.vsptracker.R.drawable.ic_action_restaurant))
                        .into(base_machine_status_icon)
                }
                else -> {
                    Glide.with(this).load(resources.getDrawable(app.vsptracker.R.drawable.ic_action_report))
                        .into(base_machine_status_icon)
                }
            }


//            val text ="<font color=#FF382A>Machine is Stopped. </font><font color=#106d14><u>Click here to Start Machine</u>.</font>"
            val text ="<font color=#106d14><u>Click here to Start Machine</u>.</font>"
            base_machine_status.setText(Html.fromHtml(text))
            myHelper.log("Is Machine Stopped: ${myHelper.getIsMachineStopped()}")
            myHelper.log("Machine Stopped Reason: ${myHelper.getMachineStoppedReason()}")

        } else {
            base_machine_status_layout.visibility = View.GONE
        }

        if (myHelper.isDailyModeStarted()) {
            val text =
                "<font color=#FF382A>Day Works is ON. </font><font color=#106d14><u>Switch Standard Mode</u>.</font>"
            base_daily_mode.setText(Html.fromHtml(text))
            base_daily_mode.visibility = View.VISIBLE
        } else {
            base_daily_mode.visibility = View.GONE
        }

        if(myHelper.isDelayStarted()){
            menu.findItem(app.vsptracker.R.id.navb_delay).title= "Waiting Started"
            menu.findItem(app.vsptracker.R.id.navb_delay).icon = getDrawable(app.vsptracker.R.drawable.ic_started)
            menu.findItem(app.vsptracker.R.id.navb_delay).setChecked(true)

        }else{
            menu.findItem(app.vsptracker.R.id.navb_delay).title= "Waiting Stopped"
            menu.findItem(app.vsptracker.R.id.navb_delay).icon = getDrawable(app.vsptracker.R.drawable.ic_stopped)
            menu.findItem(app.vsptracker.R.id.navb_map).setChecked(true)
        }

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(app.vsptracker.R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            myHelper.toast("Please Use App Navigation Buttons.")
//            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {



        when (item.itemId) {
            app.vsptracker.R.id.nav_home -> {
                val data = MyData()
                myHelper.startHomeActivityByType(data)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
            app.vsptracker.R.id.nav_day_works -> {

                val intent = Intent(this, DayWorksActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
            app.vsptracker.R.id.nav_logout -> {
                myHelper.logout(this)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
            app.vsptracker.R.id.nav_stop_machine -> {

                val intent = Intent(this, MachineStatus1Activity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
            app.vsptracker.R.id.nav_night_mode -> {

                myHelper.log("theme:" + applicationContext.theme)
                myHelper.log("theme1:" + theme)
                if (myHelper.isNightMode()) {
                    Utils.changeToTheme(this@BaseActivity, 0);
                    myHelper.setNightMode(false)
                } else {
                    Utils.changeToTheme(this@BaseActivity, 1);
                    myHelper.setNightMode(true)
                }
            }
            app.vsptracker.R.id.nav_change_machine -> {
                val intent = Intent(this, MachineTypeActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }

            app.vsptracker.R.id.nav_load_history -> {
                myHelper.startHistoryByType()
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
            app.vsptracker.R.id.nav_email -> {
                doEmail()
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }

            app.vsptracker.R.id.nav_delay -> {
                val intent = Intent(this, DelayActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
//                finishFromChild(Map1Activity())
            }
        }
        val drawerLayout: DrawerLayout = findViewById(app.vsptracker.R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun toggleDelay() {
        if(myHelper.isDelayStarted()){
            stopDelay()
        }else{
            startDelay()
        }
    }

    override fun onPause() {
        super.onPause()
        stopGPS()
    }
    fun startDelay(){
        if(!myHelper.isDelayStarted()){
            myHelper.toast("Waiting Started.")
            myHelper.startDelay(gpsLocation)
            menu.findItem(app.vsptracker.R.id.navb_delay).icon = getDrawable(app.vsptracker.R.drawable.ic_started)
            menu.findItem(app.vsptracker.R.id.navb_delay).title= "Waiting Started"
            menu.findItem(app.vsptracker.R.id.navb_delay).setChecked(true)
            val intent = Intent(this@BaseActivity, DelayActivity::class.java)
            startActivity(intent)
        }
    }
    fun stopDelay() {

        if(myHelper.isDelayStarted()){
            val gpslocation = gpsLocation
            myHelper.stopDelay(gpslocation)

            val meter = myHelper.getMeter()
            val eWork = EWork()
            eWork.startTime = meter.delayStartTime
            eWork.stopTime = meter.delayStopTime
            eWork.totalTime = meter.delayTotalTime
            eWork.loadingGPSLocation = meter.delayStartGPSLocation
            eWork.unloadingGPSLocation = meter.delayStopGPSLocation

            menu.findItem(app.vsptracker.R.id.navb_delay).icon = getDrawable(app.vsptracker.R.drawable.ic_stopped)
            menu.findItem(app.vsptracker.R.id.navb_delay).title= "Waiting Stopped"
            menu.findItem(app.vsptracker.R.id.navb_map).setChecked(true)

            eWork.machineId = myHelper.getMachineID()
            eWork.orgId = myHelper.getLoginAPI().org_id
            eWork.siteId = myHelper.getMachineSettings().siteId
            eWork.operatorId = myHelper.getOperatorAPI().id
            eWork.machineTypeId = myHelper.getMachineTypeID()
            val time = System.currentTimeMillis()
            eWork.time = time.toString()

            if(myHelper.isDailyModeStarted()){
                eWork.isDaysWork = 1
            }else {
                eWork.isDaysWork = 0
            }


            eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
            eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
            if(myHelper.isOnline()){
                pushDelay(eWork)
            }
            saveDelay(eWork)
//            else{
//                myHelper.toast("No Internet Connection.\nDelay Not Uploaded to Server.")
//                saveDelay(eWork)
//            }


        }
    }

    fun saveMachineHour(myData: MyData){

//        cv.put(COL_MACHINE_NUMBER, datum.loadedMachineNumber)


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
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())

        if (myHelper.isOnline()){
            pushMachineHour(myData);
        }else{
            db.insertMachineHours(myData)
        }
    }
    fun pushMachineHour(myData: MyData){

        val call = this.retrofitAPI.pushMachineHour(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val response = response.body()
                myHelper.log("pushSideCasting:$response")
                if (response!!.success && response.data != null) {
                    myData.isSync = 1
//                    saveDelay(eWork)
                    db.insertMachineHours(myData)

                } else {
                    db.insertMachineHours(myData)

                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                db.insertMachineHours(myData)

                myHelper.toast(t.message.toString())
                myHelper.log("Failure" + t.message)
            }
        })
    }
    fun pushSideCasting(eWork: EWork){

        eWork.siteId = myHelper.getMachineSettings().siteId
        eWork.machineId = myHelper.getMachineID()
        eWork.orgId = myHelper.getLoginAPI().org_id
        eWork.operatorId = myHelper.getOperatorAPI().id
        eWork.machineTypeId = myHelper.getMachineTypeID()

//        eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
        eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)


        myHelper.log("pushSideCasting:$eWork")
        val call = this.retrofitAPI.pushSideCasting(
            myHelper.getLoginAPI().auth_token,
            eWork
        )
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
                val response = response.body()
                myHelper.log("pushSideCasting:$response")
                if (response!!.success && response.data != null) {
                    eWork.isSync = 1
//                    saveDelay(eWork)

                } else {
//                    saveDelay(eWork)
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
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
    fun pushDelay(eWork: EWork){
//        myHelper.showDialog()
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
                val response = response.body()
                myHelper.log("EWorkResponse:$response")
                if (response!!.success && response.data != null) {
                    eWork.isSync = 1
//                    saveDelay(eWork)

                } else {
//                    saveDelay(eWork)
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
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
        if(insertID > 0){

            val meter = myHelper.getMeter()
            myHelper.toast("Waiting Stopped.\nStart Time: ${myHelper.getTime(meter.delayStartTime)}Hrs.\nTotal Time: ${myHelper.getFormatedTime(meter.delayTotalTime)}")
            var dataNew = MyData()
            var bundle: Bundle? = this.intent.extras
            if (bundle != null) {
                dataNew = bundle!!.getSerializable("myData") as MyData
            }
//            this.finish()
            intent.putExtra("myData", dataNew)
//            myHelper.startHomeActivityByType(dataNew)
//            onBackPressed();
            finishFromChild(DelayActivity())

        }else{
            myHelper.toast("Waiting Not Stopped.")
        }

    }


    fun pushLoad(myData: MyData){

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
        if(myHelper.isDailyModeStarted()){
            myData.isDaysWork = 1
        }else {
            myData.isDaysWork = 0
        }
        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime)

        myHelper.log("pushLoad:$myData")

        val call = this.retrofitAPI.pushLoad(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val response = response.body()
                myHelper.log("EWorkResponse:$response")
                if (response!!.success && response.data != null) {
                    myHelper.toast("Load Pushed to Server Successfully.")
                } else {
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                myHelper.toast("Failure" + t.message)
            }
        })
    }

    fun doEmail() {

        var versionCode = BuildConfig.VERSION_CODE
        val device = android.os.Build.DEVICE
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
            intent.setType("message/rfc822")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    fun stopGPS() {
        locationManager?.removeUpdates(locationListener)
    }
    fun startGPS() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?;
        try {
            myHelper.log("Permission Granted.")
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                locationListener
            );

        } catch (ex: SecurityException) {
            myHelper.log("No Location Available:${ex.message}")
            requestPermission()
        }

    }
    fun requestPermission() {
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
        val alertDialogBuilder = AlertDialog.Builder(this, app.vsptracker.R.style.ThemeOverlay_AppCompat_Dialog)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Goto Settings Page\nTo Enable GPS"
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton("Cancel",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                }
            })
        var alert = alertDialogBuilder.create()
        alert.show()
    }
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
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
                    Uri.parse("package:" + app.vsptracker.BuildConfig.APPLICATION_ID)
                )
            )
        }
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById(app.vsptracker.R.id.snackbar_text) as TextView
        textView.maxLines = 5  //Or as much as you need
        snackbar.show()
    }
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location?) {
            makeUseofLocation(location)
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
    private fun makeUseofLocation(location1: Location?) {
        latitude = location1!!.latitude
        longitude = location1!!.longitude
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

