package app.vsptracker.activities

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.mvp.activities.MvpHomeActivity
import app.vsptracker.BuildConfig
import app.vsptracker.R
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyEnum.Companion.MVP
import app.vsptracker.others.MyHelper
import app.vsptracker.others.Utils
import app.vsptracker.others.autologout.ForegroundService
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val REQUEST_ACCESS_FINE_LOCATION = 1

class OperatorLoginActivity : AppCompatActivity(), View.OnClickListener {
  private val tag = this::class.java.simpleName
  private lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  lateinit var gpsLocation: GPSLocation
  var latitude: Double = 0.0
  var longitude: Double = 0.0
  private lateinit var location: Location
  private var locationManager: LocationManager? = null
  private lateinit var retrofit: Retrofit
  private lateinit var retrofitAPI: RetrofitAPI
  private lateinit var myDataPushSave: MyDataPushSave
  lateinit var signin_pb: ProgressBar
  lateinit var signin_image: ImageView
  lateinit var signin_pin: EditText
  lateinit var login_main_layout: ScrollView
  lateinit var signin_signin: TextView
  lateinit var company_signin: TextView
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    Utils.onActivityCreateSetTheme(this)
    
    setContentView(R.layout.activity_operator_login)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    
    signin_pb = findViewById(R.id.signin_pb)
    signin_image = findViewById(R.id.signin_image)
    signin_pin = findViewById(R.id.signin_pin)
    login_main_layout = findViewById(R.id.login_main_layout)
    signin_signin = findViewById(R.id.signin_signin)
    company_signin = findViewById(R.id.company_signin)
    
    myHelper = MyHelper(tag, this)
    myHelper.setProgressBar(signin_pb)
    db = DatabaseAdapter(this)
    
    myDataPushSave = MyDataPushSave(this)
    
    gpsLocation = GPSLocation()
    
    this.retrofit = Retrofit.Builder().baseUrl(getString(R.string.api_url)).addConverterFactory(GsonConverterFactory.create()).client(myHelper.skipSSLOkHttpClient().build()).build()
    this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    
    when (myHelper.getMachineTypeID()) {
      1 -> Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.excavator_1)).into(signin_image)
      2 -> Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.scraper_1)).into(signin_image)
      3 -> Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.dump_truck_1)).into(signin_image)
      4 -> Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.road_truck_1)).into(signin_image)
      else -> Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.welcomenew)).into(signin_image)
    }
    
    startGPS()
//        val bundle: Bundle? = intent.extras
//        if (bundle != null) {
//            var isAutoLogoutCall = false
//            isAutoLogoutCall = bundle.getBoolean("isAutoLogoutCall")
//            myHelper.log("App_Check:isAutoLogoutCall1:$isAutoLogoutCall")
//            if (isAutoLogoutCall) {
//                myHelper.log("App_Check:startActivity:HourMeterStopActivity")
//                val intent = Intent(this, HourMeterStopActivity::class.java)
//                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra("isAutoLogoutCall", true)
//                startActivity(intent)
//                finishAffinity()
//            }
//        } else {
    // If Company credentials are saved then fetched Company Data otherwise redirect to Company Login Page.
    if (!myHelper.getLoginAPI().email.isNullOrBlank() && !myHelper.getLoginAPI().pass.isNullOrBlank()) {
      myHelper.log("packageName.equals(TAPUTAPU):" + packageName.equals(MVP))
      fetchOrgData()
      if (packageName.equals(MVP)) {
        val intent = Intent(this, MvpHomeActivity::class.java)
        startActivity(intent)
      } else if (myHelper.getOperatorAPI().id > 0) {
        /**
         * If Operator is Logged in and App is Launched this Code block will be executed.
         * If Internet is Available Fetch Company Data and Replace Old Data.
         * Call launchHomeForLoggedIn() method.
         */
        launchHomeForLoggedIn()
      }
    } else {
      myHelper.setLoginAPI(LoginAPI())
      myHelper.setIsMapOpened(false)
      myHelper.setOperatorAPI(MyData())
      myHelper.setLastJourney(MyData())
      val intent = Intent(this, LoginActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(intent)
      
    }
    
    // Refresh AccessToken if there is Internet connection
    if (myHelper.isOnline()) {
      myHelper.log("refreshToken()")
      myHelper.refreshToken()
    }
    
    signin_pin.setText(MyEnum.loginPin)
    myHelper.hideKeyboardOnClick(login_main_layout)
    signin_signin.setOnClickListener(this)
    company_signin.setOnClickListener(this)
//        }
  
  
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.company_signin -> {
        val intent = Intent(this@OperatorLoginActivity, LoginActivity::class.java)
        startActivity(intent)
      }
      R.id.signin_signin -> {
        val pin = signin_pin.text.toString()
        if (pin.length < 3) {
          myHelper.toast(getString(R.string.pin_minimum_length))
        } else {
          if (db.getOperator(pin).id > 0) {
            val operator = db.getOperator(pin)
            val operatorAPI = MyData()
            operatorAPI.id = operator.id
            operatorAPI.name = operator.name
            operatorAPI.startTime = System.currentTimeMillis()
            operatorAPI.loadingGPSLocation = gpsLocation
            myHelper.setOperatorAPI(operatorAPI)
            launchHome()
            if (myHelper.isOnline()) {
              fetchOrgData()
            }
          } else {
            myHelper.toast(getString(R.string.invalid_pin))
          }
        }
      }
    }
  }
  
  /**
   * This method will be called when operator Login. This method will do following actions.
   * 1. If Machine is Not already selected Go to MachineTypeActivity to Select Site and Machine.
   * 2. If Machine is stopped go to MachineStatusActivity to Start Machine.
   * 3. If above conditions are false Go to HourMeterStartActivity
   * as new Machine is Selected and Hours to be set and Machine Time to be started.
   * Note: Machine status will be updated in all activities before launching Home Activity
   */
  private fun launchHome() {
    when {
      myHelper.getMachineID() < 1 -> {
        val intent = Intent(this@OperatorLoginActivity, MachineTypeActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
      myHelper.getIsMachineStopped() -> {
        val intent = Intent(this@OperatorLoginActivity, MachineStatusActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
      else -> {
        val intent = Intent(this@OperatorLoginActivity, HourMeterStartActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
    }
  }
  
  /**
   * This method will do following actions.
   * 1. If Machine is Not already selected Go to MachineTypeActivity to Select Site and Machine.
   * 2. If Machine is stopped go to MachineStatusActivity to Start Machine.
   * 3. If above conditions are false Launch Home Screen for selected Machine Type.
   * Note: Machine status will be updated in all activities before launching Home Activity
   */
  private fun launchHomeForLoggedIn() {
    when {
      myHelper.getMachineID() < 1 -> {
        val intent = Intent(this@OperatorLoginActivity, MachineTypeActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
      myHelper.getIsMachineStopped() -> {
        val intent = Intent(this@OperatorLoginActivity, MachineStatusActivity::class.java)
        startActivity(intent)
        finishAffinity()
      }
      else -> {
        val dueCheckForms = db.getAdminCheckFormsDue()
        myHelper.checkDueCheckForms(dueCheckForms)
        myDataPushSave.checkUpdateServerSyncData(MyEnum.SERVER_SYNC_UPDATE_MACHINE_STATUS)
        
      }
      
    }
  }
  
  override fun onResume() {
    super.onResume()
    ForegroundService.stopService(this)
    val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
    mNotificationManager!!.cancelAll()
  }
  
  private fun fetchOrgData() {
    myHelper.toast(getString(R.string.login_successful_fetching_data))
    myDataPushSave.fetchOrgData()
  }
  
  fun showErrorDialog(title: String, explanation: String = "") {
    
    val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_error, null)
    val error_title = mDialogView.findViewById<TextView>(R.id.error_title)
    val error_explanation = mDialogView.findViewById<TextView>(R.id.error_explanation)
    val error_ok = mDialogView.findViewById<TextView>(R.id.error_ok)
    
    error_title.text = title
    if (explanation.isNotBlank()) {
      error_explanation.text = explanation
      error_explanation.visibility = View.VISIBLE
    }
    
    val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
    val mAlertDialog = mBuilder.show()
    mAlertDialog.setCancelable(false)
    
    val window = mAlertDialog.window
    val wlp = window!!.attributes
    
    wlp.gravity = Gravity.CENTER
    window.attributes = wlp
    
    error_ok.setOnClickListener {
      mAlertDialog.dismiss()
      locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
      try {
        locationManager?.requestLocationUpdates(
          LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
        )
        
      }
      catch (ex: SecurityException) {
        // myHelper.log("No Location Available:${ex.message}")
        requestPermission()
      }
    }
  }
  
  private fun startGPS() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      showErrorDialog(
        "Location permission needed", "We require your location permission to access and track location-based information from your mobile device, while using the App and in the background, to provide certain location-based services also when the app is not is use."
      )
    } else {
      locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
      try {
        locationManager?.requestLocationUpdates(
          LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
        )
        
      }
      catch (ex: SecurityException) {
        // myHelper.log("No Location Available:${ex.message}")
        requestPermission()
      }
    }
  }
  
  private fun requestPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // Permission is not granted
      
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(
          this, Manifest.permission.ACCESS_FINE_LOCATION
        )
      ) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        
        ActivityCompat.requestPermissions(
          this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION
        )
        
      } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(
          this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION
        )
        
      }
      
      
    } else {
      // Permission has already been granted
      
      if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        //                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        locationManager!!.requestLocationUpdates(
          LocationManager.GPS_PROVIDER, 1000, 0f, locationListener
        )
      } else {
        showGPSDisabledAlertToUser()
      }
    }
  }
  
  private fun showGPSDisabledAlertToUser() {
    try {
      val alertDialogBuilder = AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog)
      alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false).setPositiveButton(
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
    catch (e: Exception) {
      myHelper.log("${e.localizedMessage}")
    }
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_ACCESS_FINE_LOCATION -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          startGPS()
        } else {
          // myHelper.log("GPS Permission Permanently Denied.")
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
      findViewById(android.R.id.content), "You have previously declined GPS permission.\n" + "You must approve this permission in \"Permissions\" in the app settings on your device.", Snackbar.LENGTH_LONG
    ).setAction(
      "Settings"
    ) {
      startActivity(
        Intent(
          android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        )
      )
    }
    val snackbarView = snackbar.view
    val textView = snackbarView.findViewById(R.id.snackbar_text) as TextView
    textView.maxLines = 5  //Or as much as you need
    snackbar.show()
  }
  
  private val locationListener: LocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
      makeUseOfLocation(location)
    }
    
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
      // myHelper.log("Status Changed.")
    }
    
    override fun onProviderEnabled(provider: String) {
      // myHelper.log("Location Enabled.")
    }
    
    override fun onProviderDisabled(provider: String) {
      // myHelper.log("Location Disabled.")
      showGPSDisabledAlertToUser()
    }
  }
  
  private fun makeUseOfLocation(location1: Location) {
    latitude = location1.latitude
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
    
  }
}
