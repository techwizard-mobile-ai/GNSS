package app.mvp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.classes.GPSLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.data.kml.KmlLayer
import kotlinx.android.synthetic.main.activity_mvp_survey_survey.*
import kotlinx.android.synthetic.main.app_bar_base.*
import java.io.File
import java.io.FileInputStream

private const val ZOOM_LEVEL: Float = 19.0f

class MvpSurveySurveyActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback,
                                GoogleMap.OnMarkerClickListener {
    
    private val tag = this::class.java.simpleName
    private lateinit var map: GoogleMap
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var locationManager: LocationManager? = null
    
    private var mapGPSLocation: GPSLocation = GPSLocation()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_mvp_survey_survey, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper.setTag(tag)
        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")
        toolbar_title.text = myData.mvp_orgs_project_name + " / " + myData.mvp_orgs_folder_name + " / Data Collection / Survey"
        
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startGPS()
        
        mvp_survey_survey_gps_data_antenna_height.text = "Antenna Height: 2.000m"
        mvp_survey_survey_gps_data_antenna_height.setOnClickListener(this)
        mvp_survey_survey_back.setOnClickListener(this)
//        mvp_survey_home_check_point.setOnClickListener(this)
//        mvp_survey_home_start_survey.setOnClickListener(this)
//        mvp_survey_home_start_scan.setOnClickListener(this)
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.mvp_survey_survey_gps_data_antenna_height -> {
                myHelper.toast("Antenna Height")
            }
            R.id.mvp_survey_survey_back -> {
                finish()
            }
        }
    }
    
    
    private fun startGPS() {
        myHelper.log("startGPS1111__called")
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
    
    val locationListener: LocationListener = object : LocationListener {
        @SuppressLint("SetTextI18n")
        override fun onLocationChanged(location: Location) {

//            myHelper.log("location----$location")
            
            myHelper.setGPSLayout(
                location,
                mvp_survey_survey_gps_data_acc,
                mvp_survey_survey_gps_data_lat,
                mvp_survey_survey_gps_data_long,
                mvp_survey_survey_gps_data_alt,
                mvp_survey_survey_gps_data_speed,
                mvp_survey_survey_gps_data_bearing,
                mvp_survey_survey_gps_data_time
            )
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
    
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        setUpMap()
    }
    
    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        
        if (mapGPSLocation.latitude != 0.0 && mapGPSLocation.longitude != 0.0) {
            
            val lat = mapGPSLocation.latitude
            val longitude = mapGPSLocation.longitude
            myHelper.log("In SetupMap:$mapGPSLocation")
            val location1 = LatLng(lat, longitude)
            val marker = map.addMarker(
                MarkerOptions()
                    .position(location1)
                    .title(mapGPSLocation.locationName)
            )
            
            
            marker.showInfoWindow()
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, ZOOM_LEVEL))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, ZOOM_LEVEL))
                }
            }
            
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, ZOOM_LEVEL))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, ZOOM_LEVEL))
            
        } else {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL))
//                    val resourceID = R.raw.drury_xhunua
                    try {
//                        val fileName = "drury_xhunua"
////                        val fileName = "dury_south"
//                        val resourceID = this.resources.getIdentifier(fileName, "raw", this.packageName)
//                        val layer = KmlLayer(map, resourceID, applicationContext)
//                        layer.addLayerToMap()
                        val currentOrgsMap = db.getCurrentOrgsMap()
                        if (currentOrgsMap !== null && !currentOrgsMap.aws_path.isNullOrEmpty()) {
                            val file = File(myHelper.getKMLFileName(currentOrgsMap.aws_path))
                            myHelper.log("fileName:${currentOrgsMap.aws_path}")
                            val inputStream: FileInputStream = file.inputStream()
                            val layer = KmlLayer(map, inputStream, applicationContext)
                            layer.addLayerToMap()
                        }
                    }
                    catch (e: Exception) {
                        myHelper.log("kmlLayerException:${e.localizedMessage}")
                    }
                }
            }
        }
    }
    
    override fun onMarkerClick(p0: Marker?) = false
}