package app.vsptracker.activities

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.SelectMachineNumberAdapter
import app.vsptracker.adapters.SelectStateAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_machine_change.*
import retrofit2.Retrofit
import java.util.*

private const val REQUEST_ACCESS_FINE_LOCATION = 1

class MachineTypeActivity : BaseActivity(), View.OnClickListener {

    private var machineLocations = ArrayList<Material>()
    private var selectedMachineSite = Material(0, "Select Machine Site")
    private var selectedMachineType = Material(0, "Select Machine Type")
    private var selectedMachineLocation = Material(0, "Select Machine Location")
    private var selectedMachineNumber = Material(0, "Select Machine Number")
    private val tag = this::class.java.simpleName
//    private lateinit var db: DatabaseAdapter
//    private lateinit var myHelper: MyHelper
//    lateinit var gpsLocation: GPSLocation
//    var latitude: Double = 0.0
//    var longitude: Double = 0.0

    private lateinit var location: Location
    private var locationManager: LocationManager? = null
    private lateinit var retrofit: Retrofit
//    private lateinit var retrofitAPI: RetrofitAPI
//    private lateinit var myDataPushSave: MyDataPushSave

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.onActivityCreateSetTheme(this)
//        setContentView(R.layout.activity_machine_change)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_machine_change, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(7).isChecked = true

        myHelper.log("meter.hourStartGPSLocation:${myHelper.getMeter().hourStartGPSLocation}")
//        if(myHelper.getIsMachineStopped() || myHelper.getMachineID() <1){
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }

//        myHelper = MyHelper(tag, this@MachineTypeActivity)
//        db = DatabaseAdapter(this)

//        this.retrofit = Retrofit.Builder()
//            .baseUrl(RetrofitAPI.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
//        myDataPushSave = MyDataPushSave(this)

        selectMachineSite()
        selectMachineType()
//        selectMachineLocation()
        selectMachineNumber()

        machine_number1.isEnabled = enableList()
        machine_location.isEnabled = enableList()
//        myHelper.log("enableList${enableList()}")

//        startGPS()
//        gpsLocation = GPSLocation()
//        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(machine_type_main_layout)
//            false
//        })


        machine_type_main_layout.setOnTouchListener { _, _ ->
            myHelper.hideKeyboard(machine_type_main_layout)
            false
        }
        mt_site.setOnTouchListener { _, _ ->
            myHelper.hideKeyboard(machine_type)
            false
        }
        machine_type.setOnTouchListener { _, _ ->
            myHelper.hideKeyboard(machine_type)
            false
        }
        machine_location.setOnTouchListener { _, _ ->
            myHelper.hideKeyboard(machine_location)
            false
        }
        machine_number1.setOnTouchListener { _, _ ->
            myHelper.hideKeyboard(machine_number1)
            false
        }

//        if(myHelper.getMachineID() > 0){
//            val myData = MyData()
//
//            if(myHelper.getMeter().isMachineStartTimeCustom)
//                myData.isStartHoursCustom = 1
//
//            myData.startHours = myHelper.getMeter().startHours
//            myData.machineTypeId = myHelper.getMachineTypeID()
//
//            myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)
//
//            val meter = myHelper.getMeter()
//            myData.startTime = meter.hourStartTime
//            myData.stopTime = System.currentTimeMillis()
//
//            myData.loadingGPSLocation = meter.hourStartGPSLocation
//            myData.unloadingGPSLocation = gpsLocation
//
//            saveMachineHour(myData)

//        }
        machine_save.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.machine_save -> {

                machine_number.text.toString()
                when {
                    selectedMachineSite.id == 0 -> myHelper.toast("Please Select Site")
                    selectedMachineType.id == 0 -> myHelper.toast("Please Select Machine Type")
                    selectedMachineNumber.id == 0 -> myHelper.toast("Please Select Machine Number")
                    else -> {
                        when {
                            //Selected Machine is Different from Previous Machine
                            selectedMachineNumber.id != myHelper.getMachineID() -> {

                                val myData = MyData()
//                                saveMachineHour1(myData)

                                myData.siteId = myHelper.getMachineSettings().siteId
                                myData.machineId = myHelper.getMachineID()
                                myData.orgId = myHelper.getLoginAPI().org_id
                                myData.operatorId = myHelper.getOperatorAPI().id
                                myData.machineId = myHelper.getMachineID()
                                val currentTime = System.currentTimeMillis()
                                myData.stopTime = currentTime

                                val meter = myHelper.getMeter()
//                                myData.startTime = meter.hourStartTime
                                myData.loadingGPSLocation = meter.hourStartGPSLocation
                                myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
                                myData.unloadingGPSLocation = gpsLocation
                                myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
                                myData.machine_stop_reason_id = -2
                                if (meter.isMachineStartTimeCustom)
                                    myData.isStartHoursCustom = 1
                                myData.startHours = meter.startHours
                                myData.startTime = meter.machineStartTime
                                myData.machineTypeId = myHelper.getMachineTypeID()
                                myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)


                                myData.time = currentTime.toString()
                                myData.date = myHelper.getDate(currentTime.toString())

                                myDataPushSave.pushInsertMachineHour(myData)


                                myHelper.setMachineTypeID(selectedMachineType.id)
                                myHelper.setMachineNumber(selectedMachineNumber.number)
                                myHelper.setMachineID(selectedMachineNumber.id)
//                                TODO Check this
//                                myHelper.setIsMachineStopped(true, "Machine Changed", -2)
                                myHelper.setIsNavEnabled(false)
                                val data = MyData()
                                myHelper.setLastJourney(data)
                                val intent = Intent(this, HourMeterStartActivity::class.java)
                                startActivity(intent)

                            }
//                            else -> {
//
//                                myHelper.setMachineTypeID(selectedMachineType.id)
//                                myHelper.setMachineNumber(selectedMachineNumber.number)
//                                myHelper.setMachineID(selectedMachineNumber.id)
//
//                            }
                        }


                    }
                }
            }
        }
    }
/*
    private fun saveMachineHour1(myData: MyData) {

        myData.siteId = myHelper.getMachineSettings().siteId
        myData.machineId = myHelper.getMachineID()
        myData.orgId = myHelper.getLoginAPI().org_id
        myData.operatorId = myHelper.getOperatorAPI().id
        myData.machineId = myHelper.getMachineID()
        myData.stopTime = System.currentTimeMillis()

        myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
        myData.unloadingGPSLocation = gpsLocation
        myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        myData.machine_stop_reason_id = -2

        if (myHelper.getMeter().isMachineStartTimeCustom)
            myData.isStartHoursCustom = 1

        myData.startHours = myHelper.getMeter().startHours
        myData.machineTypeId = myHelper.getMachineTypeID()

        myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)

        val meter = myHelper.getMeter()
        myData.startTime = meter.hourStartTime
        myData.stopTime = System.currentTimeMillis()

        myData.loadingGPSLocation = meter.hourStartGPSLocation
        myData.unloadingGPSLocation = gpsLocation


//        val time = System.currentTimeMillis()
//        myData.time = time.toString()
//        myData.date = myHelper.getDate(time.toString())

        myDataPushSave.pushInsertMachineHour(myData)
//        if (myHelper.isOnline()){
//            pushMachineHour(myData)
//        }else{
//            db.insertMachineHours(myData)
//        }
    }*/

    /*
        private fun pushMachineHour(myData: MyData){

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
                    myHelper.log("pushMachinesHours:$response")
                    if (responseBody!!.success) {
                        myData.isSync = 1
                        db.insertMachineHours(myData)

                    } else {
                        db.insertMachineHours(myData)

                        if (responseBody.message == "Token has expired") {
                            myHelper.log("Token Expired:$responseBody")
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
    private fun selectMachineSite() {
        val machineTypes = db.getSites()
        machineTypes.add(0, Material(0, "Select Site"))
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        mt_site!!.adapter = selectMaterialAdapter
        mt_site.background = ContextCompat.getDrawable(this, R.drawable.disabled_spinner_border)
        mt_site.setSelection(0, false)
        mt_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineSite = machineTypes[position]
                val machineSettings = myHelper.getMachineSettings()
                machineSettings.siteId = selectedMachineSite.id
                myHelper.setMachineSettings(machineSettings)
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                selectMachineNumber()

                if (machineTypes[position].id != 0) {
                    mt_site.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
                } else {
                    mt_site.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
                }
                Log.e(tag, machineTypes[position].toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun enableList() = (selectedMachineSite.id > 0 && selectedMachineType.id > 0)
    private fun selectMachineType() {
        val machineTypes = db.getMachinesTypes()
        machineTypes.add(0, Material(0, "Select Machine Type"))
        val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
        machine_type!!.adapter = selectMaterialAdapter
        machine_type.background = ContextCompat.getDrawable(this, R.drawable.disabled_spinner_border)
        machine_type.setSelection(0, false)
        machine_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineType = machineTypes[position]
                machine_number1.isEnabled = enableList()
                machine_location.isEnabled = enableList()
                myHelper.log("enableList${enableList()}")
                if (machineTypes[position].id != 0) {
                    machine_type.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
                    selectMachineNumber()
                } else {
                    machine_type.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
                    selectMachineNumber()
                }
                Log.e(tag, machineTypes[position].toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun selectMachineLocation() {

        machineLocations = db.getLocations()
//        myHelper.log("machineLocations:$machineLocations")
        machineLocations.add(0, Material(0, "Select Machine Location"))
        val selectMaterialAdapter = SelectStateAdapter(this, machineLocations)
        machine_location!!.adapter = selectMaterialAdapter
        machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
        machine_location.setSelection(0, false)
        machine_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineLocation = machineLocations[position]
                if (machineLocations[position].id != 0) {
                    machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
                } else {
                    machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
                }
                Log.e(tag, machineLocations[position].toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun selectMachineNumber() {
        val machineTypes = db.getMachines(selectedMachineType.id)
        val machine = Material(0, "")
        machine.number = "Select Machine Number"
        machineTypes.add(0, machine)
        val selectMaterialAdapter = SelectMachineNumberAdapter(this@MachineTypeActivity, machineTypes)
        machine_number1!!.adapter = selectMaterialAdapter
        machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
        machine_number1.setSelection(0, false)
        machine_number1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                position: Int, arg3: Long
            ) {
                selectedMachineNumber = machineTypes[position]
                if (machineTypes[position].id != 0) {
                    machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
                } else {
                    machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
                }
                Log.e(tag, machineTypes[position].toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
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

    }*/
}
