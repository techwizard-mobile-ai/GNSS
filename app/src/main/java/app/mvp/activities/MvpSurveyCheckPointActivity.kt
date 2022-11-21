package app.mvp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.others.MyEnum
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
import kotlinx.android.synthetic.main.activity_mvp_survey_check_point.*
import kotlinx.android.synthetic.main.app_bar_base.*
import java.io.File
import java.io.FileInputStream

private const val ZOOM_LEVEL: Float = 19.0f

class MvpSurveyCheckPointActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback,
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
    layoutInflater.inflate(R.layout.activity_mvp_survey_check_point, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(5).isChecked = true
    
    myHelper.setTag(tag)
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    toolbar_title.text = myData.mvp_orgs_project_name + " / " + myData.mvp_orgs_files_name + " / Data Collection / Check Point"
    
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    startGPS1()
    
    mvp_survey_checkpoint_back.setOnClickListener(this)
    mvp_survey_checkpoint_settings.setOnClickListener(this)
    mvp_survey_checkpoint_record.setOnClickListener(this)
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_survey_checkpoint_back -> {
        finish()
      }
      R.id.mvp_survey_checkpoint_settings -> {
        val intent = Intent(this, SettingsActivity::class.java)
        myData.name = "Record Check Point Settings"
        myData.type = MyEnum.SETTINGS_TYPE_MVP_RECORD_CHECKPOINT
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.mvp_survey_checkpoint_record -> {
        myHelper.log("Record Checkpoint")
        myHelper.log("myData:$myData")
        
        val checkpoint_label = mvp_survey_checkpoint_details.text.toString()
        myHelper.log("checkpoint_label:${checkpoint_label.length}")
        myHelper.log(checkpoint_label)
        when {
          checkpoint_label.isEmpty() -> {
            myHelper.showErrorDialog("CheckPoint Details", "Please enter checkpoint details to record checkpoint.")
          }
          else -> {
            val myData1 = MyData()
            val aws_path =
              myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/CheckPoints/${myHelper.getValidFileName(checkpoint_label)}_${myHelper.getOrgID()}_${myHelper.getUserID()}_${myData.project_id}_${myData.mvp_orgs_files_id}_${myHelper.getCurrentTimeMillis()}"
            val relative_path =
              myData.relative_path + "${myHelper.getLoginAPI().name}_${myHelper.getLoginAPI().id}/Data Collection/CheckPoints/${checkpoint_label}"
            
            myData1.org_id = myHelper.getOrgID()
            myData1.user_id = myHelper.getUserID()
            myData1.project_id = myData.project_id
            myData1.mvp_orgs_files_id = myData.mvp_orgs_files_id
            myData1.admin_file_type_id = MyEnum.ADMIN_FILE_TYPE_TAPU_CHECKPOINT
            myData1.security_level = myHelper.getLoginAPI().role
            myData1.aws_path = aws_path
            myData1.relative_path = relative_path
            myData1.file_description = mvp_survey_checkpoint_details.text.toString()
            myData1.loadingGPSLocation = gpsLocation
            myData1.unloadingGPSLocation = gpsLocation
            myData1.upload_status = 2
            myData1.file_level = (relative_path.split("/").size - 1)
            myData1.security_level = myHelper.getLoginAPI().role
            if (myDataPushSave.pushInsertSurveyRecordCheckPoint(myData1) > 0) {
              myHelper.toast("CheckPoint recorded successfully")
              mvp_survey_checkpoint_details.setText("")
            } else myHelper.showErrorDialog("CheckPoint not recorded", "Please try again later.")
            
          }
        }
      }
    }
  }
  
  
  private fun startGPS1() {
    myHelper.log("startGPS1111__called")
    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    try {
      locationManager?.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        1000,
        0f,
        locationListener1
      )
      
    }
    catch (ex: SecurityException) {
      myHelper.log("No Location Available:${ex.message}")
      myHelper.showGPSDisabledAlertToUser()
    }
    
  }
  
  val locationListener1: LocationListener = object : LocationListener {
    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {

//            myHelper.log("location----$location")
      myHelper.setGPSLayout(
        location,
        mvp_survey_checkpoint_gps_data_acc,
        mvp_survey_checkpoint_gps_data_lat,
        mvp_survey_checkpoint_gps_data_long,
        mvp_survey_checkpoint_gps_data_alt,
        mvp_survey_checkpoint_gps_data_speed,
        mvp_survey_checkpoint_gps_data_bearing,
        mvp_survey_checkpoint_gps_data_time
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
      
      
      marker!!.showInfoWindow()
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
          try {
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
  
  override fun onMarkerClick(p0: Marker) = false
}