package app.vsptracker.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        
        myHelper.refreshToken()
        myHelper.log(db.getAdminCheckForms().toString())
        /**
         * If Operator is Logged in and App is Launched this Code block will be executed.
         * If Internet is Available Fetch Company Data and Replace Old Data.
         * Call launchHomeForLoggedIn() method.
         */
        
        when {
            myHelper.getOperatorAPI().id > 0 -> {
                when {
                    // Fetch Company Data when App is Launched, This will be useful when App will be updated and Operator Data will be erased.
                    myHelper.isOnline() -> fetchOrgData()
                }
                launchHomeForLoggedIn()
            }
        }
        
        signin_pin.setText(MyEnum.loginPin)
        
        myHelper.hideKeyboardOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        
        myHelper.requestPermissions()
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {
                val pin = signin_pin.text.toString()
                // myHelper.log("pin:" + pin)
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

//                        myHelper.setOperatorAPI(db.getOperator(pin))
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
    
    override fun onResume() {
        super.onResume()
        startGPS()
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
            else ->
                myHelper.startHomeActivityByType(MyData())
        }
    }
    
    private fun fetchOrgData() {
        myHelper.toast("Login Successful.\nFetching Company Data in background.")
        myDataPushSave.fetchOrgData()
    }
    
    private fun startGPS() {
        
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            // myHelper.log("Permission Granted.")
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                locationListener
            )
            
        }
        catch (ex: SecurityException) {
            // myHelper.log("No Location Available:${ex.message}")
            myHelper.showGPSDisabledAlertToUser()
        }
        
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
            // myHelper.log("Status Changed.")
        }
        
        override fun onProviderEnabled(provider: String) {
            // myHelper.log("Location Enabled.")
        }
        
        override fun onProviderDisabled(provider: String) {
            // myHelper.log("Location Disabled.")
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
