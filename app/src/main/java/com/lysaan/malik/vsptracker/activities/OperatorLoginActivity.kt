package com.lysaan.malik.vsptracker.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.lysaan.malik.vsptracker.BuildConfig
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.operators.OperatorAPI
import com.lysaan.malik.vsptracker.apis.operators.OperatorResponse
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.MyDataListResponse
import com.lysaan.malik.vsptracker.classes.GPSLocation
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_operator_login.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OperatorLoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    protected lateinit var db: DatabaseAdapter
    lateinit var gpsLocation: GPSLocation
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var location: Location

    private val REQUEST_ACCESS_FINE_LOCATION = 1

    private var locationManager: LocationManager? = null

    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(com.lysaan.malik.vsptracker.R.layout.activity_operator_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        myHelper = MyHelper(TAG, this)
        myHelper.setProgressBar(signin_pb)
        db = DatabaseAdapter(this)
        gpsLocation = GPSLocation()

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        when (myHelper.getMachineTypeID()) {
            1 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.excavator))
                    .into(signin_image)
            }
            2 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.scraper))
                    .into(signin_image)
            }
            3 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.truck))
                    .into(signin_image)
            }
            else -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.welcomenew))
                    .into(signin_image)
            }
        }

        startGPS()
        myHelper.refreshToken()

//        myHelper.log("MachinesHours:${db.getMachinesHours()}")

        if (myHelper.getOperatorAPI().id > 0) {
            launchHome()
            if (myHelper.isOnline()) {
                fetchOrgData()
            }
        }



        myHelper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {
                var pin = signin_pin.text.toString()
                myHelper.log("pin:" + pin)
                if (pin.length < 3) {
                    myHelper.toast("PIN Minimum Length should be 3 Characters")
                } else {
                    myHelper.log("OperatorPIN")
                    if (db.getOperator(pin).id > 0) {
                        myHelper.setOperatorAPI(db.getOperator(pin))
                        launchHome()
                    } else {
                        myHelper.toast("Invalid PIN.\nPlease Enter Correct PIN.")
                    }
                }
            }
        }
    }

    /*    fun operatorLogin(pin: String) {
            myHelper.showProgressBar()
            val call = this.retrofitAPI.getOperatorLogin(
                myHelper.getLoginAPI().org_id,
                pin,
                myHelper.getLoginAPI().auth_token
            )
            call.enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: retrofit2.Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
                ) {
                    val loginResponse = response.body()
                    if (loginResponse!!.success && loginResponse.data != null) {
                        myHelper.toast("Operator Login Successful.")
                        myHelper.setOperatorAPI(loginResponse.data)
                        syncData()
                    } else {
                        if (loginResponse.message!!.equals("Token has expired")) {
                            myHelper.log("Token Expired:$loginResponse")
                            myHelper.refreshToken()
                        } else {
                            myHelper.toast(loginResponse.message)
                            myHelper.log("loginMessage:$loginResponse")
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                    myHelper.hideProgressBar()
                    myHelper.log("Failure" + t.message)
                }
            })
        }*/
    fun launchHome() {
        if (myHelper.getMachineID() < 1) {
            myHelper.log("No machine Number is entered.")
            val intent = Intent(this@OperatorLoginActivity, MachineTypeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else if (myHelper.getIsMachineStopped()) {
            val intent = Intent(this@OperatorLoginActivity, MachineStatus1Activity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {

            val intent = Intent(this@OperatorLoginActivity, HourMeterStartActivity::class.java)
            startActivity(intent)
            finishAffinity()

//            myHelper.startHomeActivityByType(MyData())
        }
    }

    fun fetchOrgData() {
        myHelper.toast("Fetching Company Data in background.")
//        fetchMachinesTasks()
        fetchMachinesHours()
    }

    private fun fetchMachinesHours() {
        val call = this.retrofitAPI.getMachinesHours(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<MyDataListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataListResponse>,
                response: retrofit2.Response<MyDataListResponse>
            ) {
                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachinesHours(response.data as ArrayList<MyData>)
                    fetchMachinesTasks()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                // myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchMachinesTasks() {
        val call = this.retrofitAPI.getMachinesTasks(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachinesTasks(response.data as ArrayList<OperatorAPI>)
                    fetchMaterials()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchMaterials() {
        val call = this.retrofitAPI.getMaterials(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMaterials(response.data as ArrayList<OperatorAPI>)
                    fetchLocations()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchLocations() {
        val call = this.retrofitAPI.getLocations(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertLocations(response.data as ArrayList<OperatorAPI>)
                    fetchMachines()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchMachines() {
        val call = this.retrofitAPI.getMachines(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachines(response.data as ArrayList<OperatorAPI>)
                    fetchStopReasons()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchStopReasons() {
        val call = this.retrofitAPI.getStopReasons(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertStopReasons(response.data as ArrayList<OperatorAPI>)
                    fetchMachinesPlants()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchMachinesPlants() {

        val call = this.retrofitAPI.getMachinesPlants(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("MachinesPlants:${operatorResponse.data}.")
                    db.insertMachinesPlants(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesBrands()
                } else {

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchMachinesBrands() {

        val call = this.retrofitAPI.getMachinesBrands(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("MachinesBrands:${operatorResponse.data}.")
                    db.insertMachinesBrands(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesTypes()
                } else {

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchMachinesTypes() {

        val call = this.retrofitAPI.getMachinesTypes(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("MachinesTypes:${operatorResponse.data}.")
                    db.insertMachinesTypes(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchSites()
                } else {

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchSites() {

        val call = this.retrofitAPI.getSites(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("Sites:${operatorResponse.data}.")
                    db.insertSites(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchOperators()
                } else {

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchOperators() {

        val call = this.retrofitAPI.getOperators(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("Operators:${operatorResponse.data}.")
                    db.insertOperators(operatorResponse.data as ArrayList<OperatorAPI>)
                } else {

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
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
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog)
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
