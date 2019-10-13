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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.lysaan.malik.vsptracker.BuildConfig
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.SelectMachineNumberAdapter
import com.lysaan.malik.vsptracker.adapters.SelectStateAdapter
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.MyDataResponse
import com.lysaan.malik.vsptracker.classes.GPSLocation
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_machine_type.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MachineTypeActivity : AppCompatActivity(), View.OnClickListener {

    private var machineLocations =  ArrayList<Material>()
    private var selectedMachineSite = Material(0, "Select Machine Site")
    private var selectedMachineType = Material(0, "Select Machine Type")
    private var selectedMachineLocation = Material(0, "Select Machine Location")
    private var selectedMachineNumber = Material(0, "Select Machine Number")
    protected lateinit var db: DatabaseAdapter
    private lateinit var myHelper: MyHelper
    private val TAG = this::class.java.simpleName

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

        setContentView(R.layout.activity_machine_type)

        myHelper = MyHelper(TAG, this@MachineTypeActivity)
        db = DatabaseAdapter(this)

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

//        var machineTypes = myHelper.getMachineLocations()

        myHelper.log("MachineHours--:${db.getMachinesHours()}")
        selectMachineSite()
        selectMachineType()
//        selectMachineLocation()
        selectMachineNumber()

        machine_number1.isEnabled = enableList()
        machine_location.isEnabled = enableList()
        myHelper.log("enableList${enableList()}")

        startGPS()
        gpsLocation = GPSLocation()
        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type_main_layout)
            false
        })

        mt_site.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type)
            false
        })


        machine_type.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_type)
            false
        })

        machine_location.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_location)
            false
        })

        machine_number1.setOnTouchListener(View.OnTouchListener { v, event ->
            myHelper.hideKeyboard(machine_number1)
            false
        })

        if(myHelper.getMachineID() > 0){
            val myData = MyData()

            if(myHelper.getMeter().isMachineTimeCustom)
                myData.isStartHoursCustom = 1


            myHelper.log("MachineHour:${db.getMachineHours(myHelper.getMachineID())}")
            myData.startHours = db.getMachineHours(myHelper.getMachineID()).totalHours
            myData.machineTypeId = myHelper.getMachineTypeID()

            myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)

            val meter = myHelper.getMeter()
            myData.startTime = meter.hourStartTime
            myData.stopTime = System.currentTimeMillis()

            myData.loadingGPSLocation = meter.hourStartGPSLocation
            myData.unloadingGPSLocation = gpsLocation

            saveMachineHour(myData)

        }
        machine_save.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.machine_save -> {

                val machineNumber = machine_number.text.toString()
                if (selectedMachineSite.id == 0) {
                    myHelper.toast("Please Select Site")
                }else if (selectedMachineType.id == 0) {
                    myHelper.toast("Please Select Machine Type")
                }
//                else if (selectedMachineLocation.id == 0) {
//                    myHelper.toast("Please Select Machine Location")
//                }
                else if (selectedMachineNumber.id == 0) {
                    myHelper.toast("Please Select Machine Number")
                } else {

                    if(selectedMachineNumber.id == myHelper.getMachineID()){
                        //Same Machine

                        myHelper.setMachineTypeID(selectedMachineType.id)
                        myHelper.setMachineNumber(selectedMachineNumber.number)
                        myHelper.setMachineID(selectedMachineNumber.id)
                    }else{
                        //Different machine


                        myHelper.setMachineTypeID(selectedMachineType.id)
                        myHelper.setMachineNumber(selectedMachineNumber.number)
                        myHelper.setMachineID(selectedMachineNumber.id)



                    }

//                    myHelper.startHomeActivityByType(MyData())
                    val intent = Intent(this, HourMeterStartActivity::class.java)
                    startActivity(intent)
//                    finishAffinity()
                }
            }
        }
    }


    fun saveMachineHour(myData: MyData){

//        cv.put(COL_MACHINE_NUMBER, datum.loadedMachineNumber)


        myData.siteId = myHelper.getMachineSettings().siteId
        myData.machineId = myHelper.getMachineID()
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.operatorId = myHelper.getOperatorAPI().id

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
                myHelper.log("pushMachineHour:$response")
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

    private fun selectMachineSite() {
        var machineTypes = db.getSites()
        machineTypes.add(0, Material(0, "Select Site"))
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        mt_site!!.setAdapter(selectMaterialAdapter)
        mt_site.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        mt_site.setSelection(0, false)
        mt_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineSite = machineTypes.get(position)
                val machineSettings = myHelper.getMachineSettings()
                machineSettings.siteId = selectedMachineSite.id
                myHelper.setMachineSettings(machineSettings)
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                selectMachineNumber()
//                selectMachineLocation()

                if (machineTypes.get(position).id != 0) {
                    mt_site.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    mt_site.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))

                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun enableList() = (selectedMachineSite.id >0 && selectedMachineType.id > 0)
    private fun selectMachineType() {
        var machineTypes = db.getMachinesTypes()
        machineTypes.add(0, Material(0, "Select Machine Type"))
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        machine_type!!.setAdapter(selectMaterialAdapter)
        machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_type.setSelection(0, false)
        machine_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineType = machineTypes.get(position)
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                if (machineTypes.get(position).id != 0) {
                    machine_type.setBackground(resources.getDrawable(R.drawable.spinner_border))
                    selectMachineNumber()
                } else {
                    machine_type.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                    selectMachineNumber()
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
    private fun selectMachineLocation() {

        machineLocations = db.getLocations()
        myHelper.log("machineLocations:$machineLocations")
        machineLocations.add(0, Material(0, "Select Machine Location"))
//        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        val selectMaterialAdapter = SelectStateAdapter(this, machineLocations)
        machine_location!!.setAdapter(selectMaterialAdapter)
        machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_location.setSelection(0, false)
        machine_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineLocation = machineLocations.get(position)
                if (machineLocations.get(position).id != 0) {
                    machine_location.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_location.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineLocations.get(position).toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }
    private fun selectMachineNumber() {
//        var machineTypes = myHelper.getMachineLocations()
        val machineTypes = db.getMachines(selectedMachineType.id)
        val machine = Material(0, "")
        machine.number = "Select Machine Number"
        machineTypes.add(0, machine)
//        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        val selectMaterialAdapter = SelectMachineNumberAdapter(this@MachineTypeActivity, machineTypes)
        machine_number1!!.setAdapter(selectMaterialAdapter)
        machine_number1.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
        machine_number1.setSelection(0, false)
        machine_number1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    arg0: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
            ) {
                selectedMachineNumber = machineTypes.get(position)
                if (machineTypes.get(position).id != 0) {
                    machine_number1.setBackground(resources.getDrawable(R.drawable.spinner_border))
                } else {
                    machine_number1.setBackground(resources.getDrawable(R.drawable.disabled_spinner_border))
                }
                Log.e(TAG, machineTypes.get(position).toString())
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
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
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
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
