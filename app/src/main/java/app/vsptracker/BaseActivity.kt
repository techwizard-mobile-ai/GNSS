package app.vsptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.work.WorkManager
import app.vsptracker.activities.*
import app.vsptracker.activities.common.MachineBreakdownActivity
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.DeviceDetails
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import app.vsptracker.others.Utils
import app.vsptracker.others.autologout.ForegroundService
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.app_bar_base.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
    var autoLogoutTime = 0L
    private lateinit var workManager: WorkManager
    
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Utils.onActivityCreateSetTheme(this)
        
        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        workManager = WorkManager.getInstance(application)
        
        myHelper = MyHelper(tag1, this)
        if (myHelper.getIsMachineStopped() || myHelper.getMachineID() < 1) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        
        gpsLocation = GPSLocation()
        db = DatabaseAdapter(this)
        
        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(myHelper.skipSSLOkHttpClient().build())
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
        
        toolbar_title.text = "${myHelper.getMachineDetails()} : ${myHelper.getOperatorAPI().name}"
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
        
        val headerView: View = navView.getHeaderView(0)
        val navHeaderDeviceDetails: TextView = headerView.findViewById(R.id.nav_header_device_details)
        val navTitle: TextView = headerView.findViewById(R.id.nav_header_title)
//        val navSubTitle : TextView = headerView.findViewById(R.id.nav_header_sub_title)
        val deviceDetails = DeviceDetails()
        navHeaderDeviceDetails.text = "VSPT_VERSION: ${deviceDetails.VSPT_VERSION_NAME} (${deviceDetails.VSPT_VERSION_CODE})\n" +
                "Mfr. : ${deviceDetails.MANUFACTURER}\n" +
                "DEVICE : ${deviceDetails.DEVICE}\n" +
                "MODEL : ${deviceDetails.MODEL}\n" +
                "ANDROID_API : ${deviceDetails.ANDROID_SDK_API}\n"
        
        val appAPI = myHelper.getLatestVSPTVersion()
        
        val versionTitle = if (appAPI.version_code > deviceDetails.VSPT_VERSION_CODE) {
            "Latest Version:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${appAPI.version_name} (${appAPI.version_code})<br/>" +
                    "Installed Version: <font color=#FF382A>${deviceDetails.VSPT_VERSION_NAME} (${deviceDetails.VSPT_VERSION_CODE})</font>"
        } else {
            "Latest Version:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${appAPI.version_name} (${appAPI.version_code})<br/>" +
                    "Installed Version:&nbsp;${deviceDetails.VSPT_VERSION_NAME} (${deviceDetails.VSPT_VERSION_CODE})"
        }
        
        
        navTitle.text = HtmlCompat.fromHtml(versionTitle, HtmlCompat.FROM_HTML_MODE_LEGACY)
//        navSubTitle.text = "Test just"
//        MachineAutoLogout is time set by administrator. If App is not in use for time greater than AutoLogoutTime
//        which is different for Machine Type for Different sites then user should be Auto logout from App
//        and all data should be sent to server.
        val machinesAutoLogout = db.getMachinesAutoLogout()
//        myHelper.log("machinesAutoLogout:" + db.getMachinesAutoLogout()[0].toString())
        
        if (machinesAutoLogout.size > 0) {
            try {
                autoLogoutTime = machinesAutoLogout[0].autoLogoutTime!!.toLong() * 60 * 1000 //converting minutes in Milliseconds
            }
            catch (e: Exception) {
                myHelper.log("autoLogoutTimeException: ${e.message}")
            }
        }
        myHelper.log("autoLogoutTime = $autoLogoutTime")
        
        handler = Handler()
        r = Runnable {
            val runningTime = System.currentTimeMillis() - myHelper.getAutoLogoutStartTime()
            val difference = runningTime - autoLogoutTime
            myHelper.log(
                "\nLogout\nautoLogoutTime = $autoLogoutTime " +
                        "\nlogoutStartTime = ${myHelper.getAutoLogoutStartTime()}--${myHelper.getDateTime(myHelper.getAutoLogoutStartTime())}" +
                        "\nrunningTime = $runningTime" +
                        "\ndifference = $difference"
            )
            if (difference > 0 && autoLogoutTime > 0) {
                myHelper.log("Logout Time Completed.")
//                val intent = Intent(this, HourMeterStopActivity::class.java)
//                intent.putExtra("isAutoLogoutCall", true)
//                startActivity(intent)
            } else {
                myHelper.log("AutoLogout not functional---------------")
            }
        }
        if (autoLogoutTime > 0) {
            startHandler()
        }
        if (myHelper.getOperatorAPI().id < 1) {
            val intent = Intent(this, OperatorLoginActivity::class.java)
            startActivity(intent)
        }
        myHelper.requestPermissions()
    }
    
    fun startWorkManager() {
        myHelper.log("App_Check:startWorkManager")
        ForegroundService.startService(this, getString(R.string.machine_hours_running), getString(R.string.please_logout_if_not_working), 10)
//        Toast.makeText(this, "do work method", Toast.LENGTH_LONG).show()
//        val myWorkRequest = OneTimeWorkRequestBuilder<AutoLogoutWorker>()
//            .setInitialDelay(1, TimeUnit.MINUTES)
//            .setBackoffCriteria(
//                BackoffPolicy.LINEAR,
//                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
//                TimeUnit.MILLISECONDS)
//            .addTag(MyEnum.WORKER_AUTO_LOGOUT)
//            .build()
//        workManager.enqueue(myWorkRequest)

//        val timeToTrigger = System.currentTimeMillis() + 10 * 1000
//        val pendingShowList = PendingIntent.getActivity(this, 1, Intent(this, OperatorLoginActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
//        val pendingIntent = PendingIntent.getBroadcast(this, 1, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val manager = AlarmManager();
//        manager.setAlarmClock(AlarmManager.AlarmClockInfo(timeToTrigger, pendingShowList), pendingIntent)

//         var alarmMgr: AlarmManager? = null
//         lateinit var alarmIntent: PendingIntent
//
//        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmIntent = Intent(this, OperatorLoginActivity::class.java).let { intent ->
//            PendingIntent.getBroadcast(this, 0, intent, 0)
//        }
//
//        alarmMgr?.set(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime() + 60 * 1000,
//            alarmIntent
//        )

//        val alarmManager =
//            this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
//        val pendingIntent =
//            PendingIntent.getActivity(this, 1, Intent(this, OperatorLoginActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
////        if (pendingIntent != null && alarmManager != null) {
////            alarmManager.cancel(pendingIntent)
////        }
//        alarmManager?.set(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime() + 60 * 1000,
//            pendingIntent
//        )

//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val startTime: Calendar = Calendar.getInstance()
//        startTime.add(Calendar.MINUTE, 1)
////        startTime.add(Calendar.SECOND, 10)
//        val intent = Intent(this@BaseActivity, OperatorLoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        val pendingIntent = PendingIntent.getActivity(this@BaseActivity, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        alarmManager[AlarmManager.RTC, startTime.timeInMillis] = pendingIntent

//        val alarmIntent = Intent(this, OperatorLoginActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0,
//            alarmIntent, 0
//        )
//        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val interval = 1000 * 60 * 1
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval.toLong(), pendingIntent)
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.SECOND, 10)
//        MyAlarmManager.setAlarm(applicationContext, calendar.timeInMillis, "App_Check:Test Message!")

//        val alarmIntent = Intent(this, AlarmService::class.java)
//        startService(alarmIntent)
    }
    
    fun cancelWorkManager() {
        myHelper.log("App_Check:cancelWorkManager")
//        ForegroundService.stopService(this)
        workManager.cancelAllWorkByTag(MyEnum.WORKER_AUTO_LOGOUT)
    }
    
    override fun onPause() {
        super.onPause()
        startWorkManager()
    }
    
    override fun onUserInteraction() {
        super.onUserInteraction()
        if (autoLogoutTime > 0) {
            stopHandler()
            startHandler()
        }
        
    }
    
    private fun stopHandler() {
        myHelper.log("stopHandler")
        handler.removeCallbacks(r)
        handler.removeCallbacksAndMessages(r)
    }
    
    private fun startHandler() {
        myHelper.log("startHandler")
        myHelper.setAutoLogoutStartTime(System.currentTimeMillis())
        handler.postDelayed(r, autoLogoutTime)
    }
    
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            
            if (myHelper.isNavEnabled()) {
                when (item.itemId) {
                    R.id.navb_map -> {
                        if (!myHelper.getIsMapOpened()) {
                            myHelper.setIsMapOpened(true)
                            val intent = Intent(this, Map1Activity::class.java)
                            startActivity(intent)
                        } else {
                            myHelper.toast(getString(R.string.map_already_opened))
                        }
                    }
                    R.id.navb_delay -> {
                        when {
                            myHelper.getIsMachineStopped() -> myHelper.toast(getString(R.string.start_machine_first))
                            myHelper.getMachineID() < 1 -> myHelper.toast(getString(R.string.select_machine_first))
                            else -> toggleDelay()
                        }
                    }
                }
            } else {
                myHelper.toast(getString(R.string.proceed_to_next_screen))
            }
            
            false
        }
    
    override fun onResume() {
        super.onResume()
//        myHelper.log("App_Check:onResume")
        cancelWorkManager()
        startGPS()
//        If Navigation is Disabled Lock Side Menu
        if (!myHelper.isNavEnabled()) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        
        if (myHelper.getIsMachineStopped()) {
            base_machine_status_layout.visibility = View.VISIBLE
            
            when (myHelper.getMachineStoppedReason()) {
                MyEnum.STOP_REASON_WEATHER -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_beach_access))
                        .into(base_machine_status_icon)
                }
                MyEnum.STOP_REASON_OTHER1 -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_restaurant))
                        .into(base_machine_status_icon)
                }
                else -> {
                    Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_report))
                        .into(base_machine_status_icon)
                }
            }
            
            val text = "<font color=#106d14><u>Click here to Start Machine</u>.</font>"
            base_machine_status.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
            
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
            menu.findItem(R.id.navb_delay).title = getString(R.string.stop_waiting)
            menu.findItem(R.id.navb_delay).icon = ContextCompat.getDrawable(this, R.drawable.ic_started)
            menu.findItem(R.id.navb_delay).isChecked = true
            
        } else {
            menu.findItem(R.id.navb_delay).title = getString(R.string.start_waiting)
            menu.findItem(R.id.navb_delay).icon = ContextCompat.getDrawable(this, R.drawable.ic_stopped)
            menu.findItem(R.id.navb_map).isChecked = true
        }
        
    }
    
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            myHelper.toast(getString(R.string.use_app_navigation_buttons))
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
    
            R.id.nav_hours_reporting -> {
                val intent = Intent(this, HoursReportingActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_server_sync -> {
                val intent = Intent(this, ServerSyncActivity::class.java)
                startActivity(intent)
                myHelper.setIsMapOpened(false)
            }
            R.id.nav_checkforms -> {
                val intent = Intent(this, CheckFormsActivity::class.java)
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
    
    private fun startDelay() {
        if (!myHelper.isDelayStarted()) {
            myHelper.toast(getString(R.string.waiting_started))
            myHelper.startDelay(gpsLocation)
            menu.findItem(R.id.navb_delay).icon = ContextCompat.getDrawable(this, R.drawable.ic_started)
            menu.findItem(R.id.navb_delay).title = getString(R.string.stop_waiting)
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
    
            menu.findItem(R.id.navb_delay).icon = ContextCompat.getDrawable(this, R.drawable.ic_stopped)
            menu.findItem(R.id.navb_delay).title = getString(R.string.start_waiting)
            menu.findItem(R.id.navb_map).isChecked = true
            
            eWork.machineId = myHelper.getMachineID()
            eWork.orgId = myHelper.getLoginAPI().org_id
            eWork.siteId = myHelper.getMachineSettings().siteId
            eWork.operatorId = myHelper.getOperatorAPI().id
            eWork.machineTypeId = myHelper.getMachineTypeID()
            val time = System.currentTimeMillis()
            eWork.time = time.toString()
            
            when {
                myHelper.isDailyModeStarted() -> eWork.isDayWorks = 1
                else -> eWork.isDayWorks = 0
            }
            
            eWork.loadingGPSLocationString = myHelper.getGPSLocationToString(eWork.loadingGPSLocation)
            eWork.unloadingGPSLocationString = myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
            myDataPushSave.insertDelay(eWork)

            myHelper.toast("Waiting Stopped.\nStart Time: ${myHelper.getTime(meter.delayStartTime)}Hrs.\nTotal Time: ${myHelper.getFormattedTime(meter.delayTotalTime)}")
            var dataNew = MyData()
            val bundle: Bundle? = this.intent.extras
            if (bundle != null) {
                dataNew = bundle.getSerializable("myData") as MyData
            }
            intent.putExtra("myData", dataNew)
            finishFromChild(DelayActivity())

        }
    }
    
    private fun doEmail() {
        
        val details = myHelper.getDeviceDetailsString()
        
        val email = "support@vsptracker.app"
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
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                locationListener
            )
            
        }
        catch (ex: SecurityException) {
            myHelper.log("No Location Available:${ex.message}")
            myHelper.showGPSDisabledAlertToUser()
        }
        
    }
    
    private fun stopGPS() {
        locationManager?.removeUpdates(locationListener)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MyEnum.REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startGPS()
                } else {
                    myHelper.log("GPS Permission Permanently Denied.")
                    myHelper.showPermissionDisabledAlertToUser(
                        resources.getString(R.string.gps_permission_title),
                        resources.getString(R.string.gps_permission_explanation)
                    )
                }
                return
            }
    
            MyEnum.REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    myHelper.log("Permission denied.")
                    myHelper.showPermissionDisabledAlertToUser(
                        resources.getString(R.string.storage_permission_title),
                        resources.getString(R.string.storage_permission_explanation)
                    )
                }
                return
            }
        }
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
            myHelper.showGPSDisabledAlertToUser()
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

