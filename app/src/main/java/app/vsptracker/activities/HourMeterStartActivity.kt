package app.vsptracker.activities

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_start.*

private const val REQUEST_ACCESS_FINE_LOCATION = 1
class HourMeterStartActivity : BaseActivity(), View.OnClickListener {
//    private lateinit var myData: MyData
    private val tag = this::class.java.simpleName
//    private lateinit var myHelper: MyHelper
//    private lateinit var db: DatabaseAdapter
//    var gpsLocation = GPSLocation()
//    var latitude: Double = 0.0
//    var longitude: Double = 0.0
    private lateinit var location: Location

    private var startReading = ""
    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.onActivityCreateSetTheme(this)
//        setContentView(R.layout.activity_hour_meter_start)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_hour_meter_start, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(7).isChecked = true

//        if(myHelper.getIsMachineStopped()){
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }
        myHelper = MyHelper(tag, this)


//        myHelper.setTag(tag)
        db = DatabaseAdapter(this)
        myData = MyData()
        myHelper.hideKeyboardOnClick(hour_meter_main_layout)

        var reading = db.getMachineHours(myHelper.getMachineID()).totalHours
        if (reading.isEmpty()) {
            reading = "0.0"
        }
        startReading = reading
        ms_reading.setText(myHelper.getRoundedDecimal(reading.toDouble()).toString())
        myHelper.log("Machine_HourMeter--reading--:${reading}")

//        gpsLocation = GPSLocation()
//        startGPS()
        if(myHelper.isOnline())
            fetchMachineMaxHours()
        ms_minus.setOnClickListener(this)
        ms_plus.setOnClickListener(this)
        ms_continue.setOnClickListener(this)
//        ms_reading.setText(myHelper.getRoundedDecimal(myHelper.getMachineTotalTime()/60.0).toString())
//        ms_reading.setText(myHelper.getMeterTimeForStart())

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ms_minus -> {
                val value = ms_reading.text.toString().toFloat()
                if (value > 0) {
                    val newValue = value - 0.1
                    ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
                }
            }

            R.id.ms_plus -> {
                val value = ms_reading.text.toString().toFloat()
                val newValue = value + 0.1
                ms_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
            }

            R.id.ms_continue -> {
                val meter = myHelper.getMeter()
                meter.hourStartGPSLocation = gpsLocation
                val currentTime = System.currentTimeMillis()
                meter.hourStartTime = currentTime
                meter.machineStartTime = currentTime
                meter.startHours = ms_reading.text.toString()

                if (!startReading.equals(ms_reading.text.toString(), true)) {
                    meter.isMachineStartTimeCustom = true
                    myHelper.log("Custom Time : True, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                } else {
                    meter.isMachineStartTimeCustom = false
                    myHelper.log("Custom Time : False, Original reading:${myHelper.getMeterTimeForStart()}, New Reading: ${ms_reading.text}")
                }


                myHelper.setMeter(meter)

                val value = ms_reading.text.toString().toDouble()
                val minutes = value * 60
                val newMinutes = myHelper.getRoundedInt(minutes)
                myHelper.log("Minutes: $newMinutes")
                myHelper.setMachineTotalTime(newMinutes)
//                myHelper.pushIsMachineRunning( 1)
                myHelper.setIsNavEnabled(true)
                myHelper.startHomeActivityByType(myData)

                myHelper.startMachine()
                finishAffinity()
            }
        }
    }


    private fun fetchMachineMaxHours() {
        myHelper.showDialog()
        val call = this.retrofitAPI.getMachineMaxHour(
            myHelper.getMachineID(),
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<MyDataListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataListResponse>,
                response: retrofit2.Response<MyDataListResponse>
            ) {
                myHelper.hideDialog()
                myHelper.log("Response:$response")
                val responseBody = response.body()
                myHelper.log("responseBody:$responseBody")
                if (responseBody!!.success && responseBody.data != null) {
                    ms_reading.setText(responseBody.data!![0].totalHours)
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataListResponse>, t: Throwable) {
                myHelper.hideDialog()
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

/*
    private fun startGPS() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            myHelper.log("Permission Granted.")
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
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton("Cancel"
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
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
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

    }*/
}
