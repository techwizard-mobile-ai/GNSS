package app.vsptracker

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.work.WorkManager
import app.mvp.activities.MvpCorrectionsSettingsActivity
import app.mvp.activities.MvpHomeActivity
import app.mvp.activities.MvpSettingsActivity
import app.mvp.activities.MvpStartDataCollectionActivity
import app.vsptracker.activities.*
import app.vsptracker.activities.common.MachineBreakdownActivity
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.DeviceDetails
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyEnum.Companion.MVP
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
import java.util.*


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
    if ((myHelper.getIsMachineStopped() || myHelper.getMachineID() < 1) && !packageName.equals(MyEnum.MVP)) {
      drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
    
    gpsLocation = GPSLocation()
    db = DatabaseAdapter(this)
    
    this.retrofit = Retrofit.Builder().baseUrl(getString(R.string.api_url)).addConverterFactory(GsonConverterFactory.create()).client(myHelper.skipSSLOkHttpClient().build()).build()
    this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    
    myDataPushSave = MyDataPushSave(this)
    
    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.base_nav_view)
    val toggle = ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
    navView.setNavigationItemSelectedListener(this)
    
    if (!packageName.equals(MyEnum.MVP)) {
      toolbar_title.text = "${myHelper.getMachineDetails()} : ${myHelper.getOperatorAPI().name}"
    } else {
      toolbar_title.text = "${myHelper.getLoginAPI().name} : ${myHelper.getLoginAPI().email}"
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
    
    val headerView: View = navView.getHeaderView(0)
    val navTitle: TextView = headerView.findViewById(R.id.nav_header_title)
    val navSubTitle: TextView = headerView.findViewById(R.id.nav_header_sub_title)
    val deviceDetails = DeviceDetails()
    
    val appAPI = myHelper.getLatestVSPTVersion()
    
    val versionTitle = if (appAPI.version_code > deviceDetails.VSPT_VERSION_CODE) {
      "Latest Version:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${appAPI.version_name} (${appAPI.version_code})<br/>" + "Installed Version: <font color=#FF382A>${deviceDetails.VSPT_VERSION_NAME} (${deviceDetails.VSPT_VERSION_CODE})</font>"
    } else {
      "Latest Version:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${appAPI.version_name} (${appAPI.version_code})<br/>" + "Installed Version:&nbsp;${deviceDetails.VSPT_VERSION_NAME} (${deviceDetails.VSPT_VERSION_CODE})"
    }
    navTitle.text = HtmlCompat.fromHtml(versionTitle, HtmlCompat.FROM_HTML_MODE_LEGACY)
    navSubTitle.text = "${getString(R.string.override_name)}: ${getString(R.string.nav_header_subtitle)}"
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
        "\nLogout\nautoLogoutTime = $autoLogoutTime " + "\nlogoutStartTime = ${myHelper.getAutoLogoutStartTime()}--${myHelper.getDateTime(myHelper.getAutoLogoutStartTime())}" + "\nrunningTime = $runningTime" + "\ndifference = $difference"
      )
      if (difference > 0 && autoLogoutTime > 0) {
        myHelper.log("Logout Time Completed.")
        logout(MyEnum.LOGOUT_TYPE_AUTO, gpsLocation)
      } else {
        myHelper.log("AutoLogout not functional---------------")
      }
    }
    if (autoLogoutTime > 0) {
      startHandler()
    }
    if (myHelper.getOperatorAPI().id < 1 && !packageName.equals(MyEnum.MVP)) {
      val intent = Intent(this, OperatorLoginActivity::class.java)
      startActivity(intent)
    }
    myHelper.requestPermissions()
    
  }
  
  fun startWorkManager() {
    myHelper.log("App_Check:startWorkManager")
    myHelper.log("App_Check:autoLogoutTime:$autoLogoutTime")
    if (autoLogoutTime > 0) {
      // We are starting Foreground service to show Notification as it will prevent app from kill If operator is not logged out.
      ForegroundService.startService(this, getString(R.string.machine_hours_running), getString(R.string.please_logout_if_not_working), autoLogoutTime)
    }
  }
  
  fun cancelWorkManager() {
    myHelper.log("App_Check:cancelWorkManager")
    workManager.cancelAllWorkByTag(MyEnum.WORKER_TAG_AUTO_LOGOUT)
  }
  
  override fun onPause() {
    super.onPause()
    myHelper.log("getCurrentRunningApp:${myHelper.getCurrentRunningApp()}")
    myHelper.log(applicationContext.packageName)
    if (myHelper.getCurrentRunningApp() != applicationContext.packageName) {
      myHelper.log("bringToFrontNow")
      val intent = Intent(this, OperatorLoginActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
      intent.action = Intent.ACTION_MAIN
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      startActivity(intent)
      
      val mylamda = Thread({
        Thread.sleep(15 * 1000)
        myHelper.log("thread start")
//                myHelper.log(myHelper.getCurrentRunningApp())
        currentRunningApps1()
//                runOnUiThread {
//                    startActivity(intent)
//                }
      })
      mylamda.start()
    }
    startWorkManager()
  }
  
  @SuppressLint("WrongConstant")
  fun currentRunningApps1() {
    myHelper.log("needPermissionForBlocking:${needPermissionForBlocking(this)}")
    myHelper.log("currentRunningApps1")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val usm: UsageStatsManager = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
      val time = System.currentTimeMillis()
      val appList: List<UsageStats> = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000 * 10000, time)
      if (appList != null && appList.size === 0) {
        myHelper.log("######### NO APP FOUND ##########")
      }
      if (appList != null && appList.size > 0) {
        val mySortedMap: SortedMap<Long, UsageStats> = TreeMap<Long, UsageStats>()
        for (usageStats in appList) {
//                    myHelper.log("usage stats executed : " + usageStats.packageName.toString() + "\t\t ID: ")
          mySortedMap[usageStats.lastTimeUsed] = usageStats
        }
        if (mySortedMap != null && !mySortedMap.isEmpty()) {
          val currentApp: String = mySortedMap[mySortedMap.lastKey()]!!.packageName
          myHelper.log("currentApp:$currentApp")
          if (currentApp != this.packageName) {
            myHelper.log("start vsptracker")
//                        val mIntent = packageManager.getLaunchIntentForPackage(this.packageName)
//                        if (mIntent != null) {
//                            this.startActivity(mIntent)
//                        }
//                        val startMain = Intent(Intent.ACTION_MAIN)
//                        startMain.addCategory(Intent.CATEGORY_HOME)
//                        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(startMain)
            
            val am = applicationContext.getSystemService("activity") as ActivityManager
            val forceStopPackage = am.javaClass.getDeclaredMethod("forceStopPackage", String::class.java)
            forceStopPackage.isAccessible = true;
            forceStopPackage.invoke(am, currentApp);
            
          }
        }
      }
    }
  }
  
  fun needPermissionForBlocking(context: Context): Boolean {
    return try {
      val packageManager = context.packageManager
      val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
      val appOpsManager: AppOpsManager = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
      val mode: Int = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
      mode != AppOpsManager.MODE_ALLOWED
    }
    catch (e: PackageManager.NameNotFoundException) {
      true
    }
  }
  
  override fun onUserInteraction() {
    super.onUserInteraction()
    if (autoLogoutTime > 0) {
      stopHandler()
      startHandler()
    }
    
  }
  
  internal fun stopHandler() {
    myHelper.log("stopHandler")
    handler.removeCallbacks(r)
    handler.removeCallbacksAndMessages(r)
  }
  
  private fun startHandler() {
    myHelper.log("startHandler")
    myHelper.setAutoLogoutStartTime(System.currentTimeMillis())
    handler.postDelayed(r, autoLogoutTime)
  }
  
  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    
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
          Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_beach_access)).into(base_machine_status_icon)
        }
        MyEnum.STOP_REASON_OTHER1 -> {
          Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_restaurant)).into(base_machine_status_icon)
        }
        else -> {
          Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_action_report)).into(base_machine_status_icon)
        }
      }
      
      val text = "<font color=#106d14><u>Click here to Start Machine</u>.</font>"
      base_machine_status.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
      
    } else {
      base_machine_status_layout.visibility = View.GONE
    }
    
    if (myHelper.isDailyModeStarted()) {
      val text = "<font color=#FF382A>Day Works is ON. </font><font color=#106d14><u>Switch Standard Mode</u>.</font>"
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
    
    when {
      !myHelper.isOnline() && BuildConfig.APPLICATION_ID.equals("app.mvp") -> no_internet.visibility = View.VISIBLE
      else -> no_internet.visibility = View.GONE
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
        when {
          (packageName.equals(MVP)) -> {
            val intent = Intent(this, MvpHomeActivity::class.java)
            startActivity(intent)
            myHelper.setIsMapOpened(false)
          }
          else -> {
            val data = MyData()
            myHelper.startHomeActivityByType(data)
            myHelper.setIsMapOpened(false)
          }
        }
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
        if (packageName.equals(MVP)) {
          myHelper.setLoginAPI(LoginAPI())
          myHelper.setIsMapOpened(false)
          myHelper.setOperatorAPI(MyData())
          myHelper.setLastJourney(MyData())
          val intent = Intent(this, LoginActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          startActivity(intent)
        } else {
          myHelper.logout(this)
          myHelper.setIsMapOpened(false)
        }
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
      R.id.nav_privacy -> {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_url)))
        startActivity(intent)
        myHelper.setIsMapOpened(false)
      }
      R.id.nav_ntrip_client -> {
//        myHelper.launchNtripClient()
        val intent = Intent(this, MvpCorrectionsSettingsActivity::class.java)
        startActivity(intent)
        myHelper.setIsMapOpened(false)
      }
      R.id.nav_last_task -> {
        myHelper.setIsMapOpened(false)
//        val intent = Intent(this, MvpSurveyHomeActivity::class.java)
        if (myHelper.getLastJourney().project_id == 0 || myHelper.getLastJourney().mvp_orgs_files_id == 0) {
          myHelper.showErrorDialog("No Last Task Selected!", "Please select site and task from Home screen.")
//          val intent = Intent(this, MvpHomeActivity::class.java)
//          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//          startActivity(intent)
        } else {
          val intent = Intent(this, MvpStartDataCollectionActivity::class.java)
          startActivity(intent)
        }
      }
      R.id.nav_settings -> {
        val intent = Intent(this, MvpSettingsActivity::class.java)
        val appSettings = MyData()
        appSettings.name = "App Settings"
        intent.putExtra("myData", appSettings)
        startActivity(intent)
        myHelper.setIsMapOpened(false)
      }
      R.id.nav_export -> {
        val intent = Intent(this, ExportDataActivity::class.java)
        val appSettings = MyData()
        appSettings.name = "Export Data"
        intent.putExtra("myData", appSettings)
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
  
  // Using this method as we have to make stop waiting and change works mode if it is days work.
  fun logout(machine_stop_reason_id: Int, gpsLocation: GPSLocation = GPSLocation(), sfinish_reading: String = myHelper.getMeterTimeForFinish()) {
    // If waiting is started then stop waiting
    if (myHelper.isDelayStarted()) stopDelay()
    myDataPushSave.logout(machine_stop_reason_id, gpsLocation, sfinish_reading)
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
        Intent.EXTRA_TEXT, "Hi, I like to notify you about an error I faced while using App. Device Details: $details"
      )
      intent.type = "message/rfc822"
    }
    if (intent.resolveActivity(packageManager) != null) {
      startActivity(intent)
    }
  }
  
  fun startGPS() {
    myHelper.log("startGPS__called")
    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    try {
      locationManager?.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
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
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      MyEnum.REQUEST_ACCESS_FINE_LOCATION -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          startGPS()
        } else {
          myHelper.log("GPS Permission Permanently Denied.")
          myHelper.showPermissionDisabledAlertToUser(
            resources.getString(R.string.gps_permission_title), resources.getString(R.string.gps_permission_explanation)
          )
        }
        return
      }
      
      MyEnum.REQUEST_WRITE_EXTERNAL_STORAGE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
          myHelper.log("Permission denied.")
          myHelper.showPermissionDisabledAlertToUser(
            resources.getString(R.string.storage_permission_title), resources.getString(R.string.storage_permission_explanation)
          )
        }
        return
      }
    }
  }
  
  val locationListener: LocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
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
  
  private fun makeUseOfLocation(location1: Location) {
//        myHelper.log("makeUseOfLocation ${location1.latitude}")
    var verticalAccuracyMeters: Float = 0F
    var speedAccuracyMetersPerSecond: Float = 0F
    var bearingAccuracyDegrees: Float = 0F
    var hasVerticalAccuracy = false
    var hasSpeedAccuracy = false
    var hasBearingAccuracy = false
    var isComplete = false
    var isMock = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      verticalAccuracyMeters = location1.verticalAccuracyMeters
      hasVerticalAccuracy = location1.hasVerticalAccuracy()
      speedAccuracyMetersPerSecond = location1.speedAccuracyMetersPerSecond
      hasSpeedAccuracy = location1.hasSpeedAccuracy()
      bearingAccuracyDegrees = location1.bearingAccuracyDegrees
      hasBearingAccuracy = location1.hasBearingAccuracy()
    }
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      isMock = location1.isMock
    }
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      isComplete = location1.isComplete
    }
    
    latitude = location1!!.latitude
    longitude = location1.longitude
    location = location1
    
    gpsLocation.latitude = location1.latitude
    gpsLocation.longitude = location1.longitude
    gpsLocation.altitude = location1.altitude
    
    gpsLocation.accuracy = location1.accuracy
    gpsLocation.bearing = location1.bearing
    gpsLocation.speed = location1.speed
    
    gpsLocation.provider = location1.provider.toString()
    gpsLocation.elapsedRealtimeNanos = location1.elapsedRealtimeNanos
//        gpsLocation.extras = location1.extras
    gpsLocation.time = location1.time
    gpsLocation.verticalAccuracyMeters = verticalAccuracyMeters
    gpsLocation.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond
    gpsLocation.bearingAccuracyDegrees = bearingAccuracyDegrees
    gpsLocation.hasVerticalAccuracy = hasVerticalAccuracy
    gpsLocation.hasSpeedAccuracy = hasSpeedAccuracy
    gpsLocation.hasBearingAccuracy = hasBearingAccuracy
    gpsLocation.isComplete = isComplete
    gpsLocation.isMock = isMock
    gpsLocation.bearingTo = location1.bearingTo(location1)
    
  }
}

