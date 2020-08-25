package app.vsptracker.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.vsptracker.BuildConfig
import app.vsptracker.R
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import app.vsptracker.others.Utils
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_operator_login.*
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Utils.onActivityCreateSetTheme(this)
        
        setContentView(R.layout.activity_operator_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        
        myHelper = MyHelper(tag, this)
        myHelper.setProgressBar(signin_pb)
        db = DatabaseAdapter(this)
        
        myDataPushSave = MyDataPushSave(this)
        
        gpsLocation = GPSLocation()
        
        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(myHelper.unSafeOkHttpClient().build())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
        
        when (myHelper.getMachineTypeID()) {
            1 -> {
                Glide.with(this)
                    .load(ContextCompat.getDrawable(this, R.drawable.excavator))
                    .into(signin_image)
            }
            2 -> {
                Glide.with(this)
                    .load(ContextCompat.getDrawable(this, R.drawable.scraper))
                    .into(signin_image)
            }
            3 -> {
                Glide.with(this)
                    .load(ContextCompat.getDrawable(this, R.drawable.truck))
                    .into(signin_image)
            }
            else -> {
                Glide.with(this)
                    .load(ContextCompat.getDrawable(this, R.drawable.welcomenew))
                    .into(signin_image)
            }
        }
        
        startGPS()
        
        // If Company credentials are saved then fetched Company Data otherwise redirect to Company Login Page.
        if (!myHelper.getLoginAPI().email.isNullOrBlank() && !myHelper.getLoginAPI().pass.isNullOrBlank()) {
            fetchOrgData()
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        
        // Refresh AccessToken if there is Internet connection
        if (myHelper.isOnline())
            myHelper.refreshToken()
            myHelper.log("refreshToken()")
        /**
         * If Operator is Logged in and App is Launched this Code block will be executed.
         * If Internet is Available Fetch Company Data and Replace Old Data.
         * Call launchHomeForLoggedIn() method.
         */
        
        when {
            myHelper.getOperatorAPI().id > 0 -> {
//                when {
//                    myHelper.isOnline() -> fetchOrgData()
//                }
                launchHomeForLoggedIn()
            }
        }
        
        signin_pin.setText(MyEnum.loginPin)
        
        myHelper.hideKeyboardOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        company_signin.setOnClickListener(this)
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
                    myHelper.toast("PIN Minimum Length should be 3 Characters")
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
                        myHelper.toast("Invalid PIN.\nPlease Enter Correct PIN.")
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
            else ->{
                val dueCheckForms = db.getAdminCheckFormsDue()
                myHelper.checkDueCheckForms(dueCheckForms)
                
            }
            
        }
    }
    
    private fun fetchOrgData() {
        myHelper.toast("Login Successful.\nFetching Company Data in background.")
        myDataPushSave.fetchOrgData()
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
            // myHelper.log("No Location Available:${ex.message}")
            requestPermission()
        }
        
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
