package com.lysaan.malik.vsptracker

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
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.activities.DayWorksActivity
import com.lysaan.malik.vsptracker.activities.DelayActivity
import com.lysaan.malik.vsptracker.activities.MachineTypeActivity
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.classes.EWork
import com.lysaan.malik.vsptracker.classes.GPSLocation
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.app_bar_base.*


open class BaseActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var location: Location
    lateinit var gpsLocation: GPSLocation
    val TAG1 = "BaseActivity"
    protected lateinit var helper: Helper
    protected lateinit var db: DatabaseAdapter
    protected lateinit var data: Data
    private var locationManager: LocationManager? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private val REQUEST_ACCESS_FINE_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        helper = Helper(TAG1, this)
        gpsLocation = GPSLocation()
        db = DatabaseAdapter(this)

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

        when (helper.getMachineType()) {
            1 -> {
                toolbar_title.text = "VSP Tracker - Excavator"
            }
            2 -> {
                toolbar_title.text = "VSP Tracker - Scrapper"
            }
            3 -> {
                toolbar_title.text = "VSP Tracker - Truck"
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



        helper.hideKeybaordOnClick(base_content_frame)
//          TODO Delete this navigation as it is changed
//        val navItems = ArrayList<Material>()
//        navItems.add(Material(1, "Loading Machine"))
//        navItems.add(Material(2, "Loaded Material"))
//        navItems.add(Material(3, "Loading Location"))
//        navItems.add(Material(4, "Loaded Material Weigh"))
//
//        val aa = BaseNavigationAdapter(this@BaseActivity, navItems)
//        base_navigation_rv.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
//        base_navigation_rv!!.setAdapter(aa)
    }

    override fun onResume() {
        super.onResume()
        if (helper.getIsMachineStopped()) {
            base_machine_status_layout.visibility = View.VISIBLE

            when (helper.getMachineStoppedReason()) {
                "Weather" -> {
                    Glide.with(this).load(resources.getDrawable(R.drawable.ic_action_beach_access))
                            .into(base_machine_status_icon)
                }
                "Other 1" -> {
                    Glide.with(this).load(resources.getDrawable(R.drawable.ic_action_restaurant))
                            .into(base_machine_status_icon)
                }
                else -> {
                    Glide.with(this).load(resources.getDrawable(R.drawable.ic_action_report))
                            .into(base_machine_status_icon)
                }
            }


            val text =
                    "<font color=#FF382A>Machine is Stopped. </font><font color=#106d14><u>Click here to Start Machine</u>.</font>"
            base_machine_status.setText(Html.fromHtml(text))
            helper.log("Is Machine Stopped: ${helper.getIsMachineStopped()}")
            helper.log("Machine Stopped Reason: ${helper.getMachineStoppedReason()}")
        } else {
            base_machine_status_layout.visibility = View.GONE
        }

        if (helper.isDailyModeStarted()) {
            val text =
                    "<font color=#FF382A>Day Works is ON. </font><font color=#106d14><u>Switch Standard Mode</u>.</font>"
            base_daily_mode.setText(Html.fromHtml(text))
            base_daily_mode.visibility = View.VISIBLE
        } else {
            base_daily_mode.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val data = Data()
                helper.startHomeActivityByType(data)
            }
            R.id.nav_day_works -> {

                val intent = Intent(this, DayWorksActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                helper.logout(this)
            }
            R.id.nav_stop_machine -> {

                val intent = Intent(this, MachineStatus1Activity::class.java)
                startActivity(intent)
            }
            R.id.nav_night_mode -> {

                helper.log("theme:" + applicationContext.theme)
                helper.log("theme1:" + theme)
                if (helper.isNightMode()) {
                    Utils.changeToTheme(this@BaseActivity, 0);
                    helper.setNightMode(false)
                } else {
                    Utils.changeToTheme(this@BaseActivity, 1);
                    helper.setNightMode(true)
                }
            }
            R.id.nav_change_machine -> {
                val intent = Intent(this, MachineTypeActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_load_history -> {
                helper.startHistoryByType()
            }
            R.id.nav_email -> {
                doEmail()
            }

            R.id.nav_delay -> {
                val intent = Intent(this, DelayActivity::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun stopDelay(): Long {
        val gpslocation = gpsLocation

        helper.stopDelay(gpslocation)

        val meter = helper.getMeter()
        val eWork = EWork()
        eWork.startTime = meter.delayStartTime
        eWork.stopTime = meter.delayStopTime
        eWork.totalTime = meter.delayTotalTime
        eWork.loadingGPSLocation = meter.delayStartGPSLocation
        eWork.unloadingGPSLocation = meter.delayStopGPSLocation


        val insertID = db.insertDelay(eWork)
        if(insertID > 0){
            helper.log("Delay Stopped.\nStart Time: ${helper.getTime(meter.dailyModeStartTime)}Hrs.\nTotal Time: ${helper.getFormatedTime(meter.delayTotalTime)}")
        }else{
            helper.log("Delay Not Stopped.")
        }
        return insertID
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

        val email = "errors@vsptracker.com"
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
            helper.log("Permission Granted.")
            locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0f,
                    locationListener
            );

        } catch (ex: SecurityException) {
            helper.log("No Location Available:${ex.message}")
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

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startGPS()
                } else {
                    helper.log("GPS Permission Permanently Denied.")
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
                snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.maxLines = 5  //Or as much as you need
        snackbar.show()
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location?) {
            makeUseofLocation(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            helper.log("Status Changed.")
        }

        override fun onProviderEnabled(provider: String) {
            helper.log("Location Enabled.")
        }

        override fun onProviderDisabled(provider: String) {
            helper.log("Location Disabled.")
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
