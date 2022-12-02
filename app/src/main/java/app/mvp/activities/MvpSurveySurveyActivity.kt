package app.mvp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.classes.Material
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
import kotlinx.android.synthetic.main.activity_mvp_survey_survey.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.dialog_error.view.*
import kotlinx.android.synthetic.main.dialog_error.view.error_explanation
import kotlinx.android.synthetic.main.dialog_error.view.error_ok
import kotlinx.android.synthetic.main.dialog_error.view.error_title
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.dialog_input.view.*
import java.io.File
import java.io.FileInputStream


private const val ZOOM_LEVEL: Float = 19.0f

class MvpSurveySurveyActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
  
  private lateinit var adapter: CustomGridLMachine
  private var mvpOrgsProjects: ArrayList<Material> = ArrayList<Material>()
  private lateinit var gv: GridView
  private val tag = this::class.java.simpleName
  private lateinit var map: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var lastLocation: Location
  private var locationManager1: LocationManager? = null
  private var mapGPSLocation: GPSLocation = GPSLocation()
  private var selectedLabel: Material = Material()
  private var location1: Location? = null
  private var selectedLabel_aws_path: String? = null
  private var current_label_number = 0
  private var selected_label_position = -1
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_survey_survey, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(5).isChecked = true
    
    myHelper.setTag(tag)
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    toolbar_title.text = myData.mvp_orgs_project_name + " / " + myData.mvp_orgs_files_name + " / Data Collection / Survey"
    
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    startGPS1()
    
    gv = findViewById<GridView>(R.id.survey_labels_gridview)
    mvpOrgsProjects = db.getAdminMvpSurveysLabels(2)
    
    adapter = CustomGridLMachine(this@MvpSurveySurveyActivity, mvpOrgsProjects, 1)
    gv.adapter = adapter
    
    gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
      
      myHelper.log(mvpOrgsProjects[position].toString())
      selected_label_position = position
      selectedLabel = mvpOrgsProjects[position]
      current_label_number = 0
      selectedLabel_aws_path = myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Survey/${myHelper.getValidFileName(selectedLabel.number)}/${myHelper.getValidFileName("${selectedLabel.number}_${current_label_number}")}/"
      current_point.setText("${selectedLabel.number}_${current_label_number}_${db.getMvpOrgsFiles(selectedLabel_aws_path!!).size}")
      addMarkers(db.getMvpOrgsFiles(selectedLabel_aws_path!!))
    }

//    mvp_survey_survey_gps_data_antenna_height.text = "Antenna Height: 2.000m"
    mvp_survey_survey_gps_data_antenna_height.setOnClickListener(this)
    mvp_survey_survey_file_description.setOnClickListener(this)
    mvp_survey_survey_back.setOnClickListener(this)
    mvp_survey_survey_settings.setOnClickListener(this)
    mvp_survey_point.setOnClickListener(this)
  }
  
  fun plus(position: Int) {
//    myHelper.log(mvpOrgsProjects[position].toString())
    if (selected_label_position == position) {
      if (current_label_number < 20) current_label_number++
      selectedLabel_aws_path = myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Survey/${myHelper.getValidFileName(selectedLabel.number)}/${myHelper.getValidFileName("${selectedLabel.number}_${current_label_number}")}/"
      current_point.setText("${selectedLabel.number}_${current_label_number}_${db.getMvpOrgsFiles(selectedLabel_aws_path!!).size}")
      addMarkers(db.getMvpOrgsFiles(selectedLabel_aws_path!!))
    }
  }
  
  fun minus(position: Int) {
//    myHelper.log(mvpOrgsProjects[position].toString())
    if (selected_label_position == position) {
      if (current_label_number > 0) current_label_number--
      selectedLabel_aws_path = myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Survey/${myHelper.getValidFileName(selectedLabel.number)}/${myHelper.getValidFileName("${selectedLabel.number}_${current_label_number}")}/"
      current_point.setText("${selectedLabel.number}_${current_label_number}_${db.getMvpOrgsFiles(selectedLabel_aws_path!!).size}")
      addMarkers(db.getMvpOrgsFiles(selectedLabel_aws_path!!))
    }
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_survey_survey_gps_data_antenna_height -> {
        myHelper.log("last Journey:${myHelper.getLastJourney()}")
        showInputDialog(1, this)
      }
      R.id.mvp_survey_survey_file_description -> {
        myHelper.log("last Journey:${myHelper.getLastJourney()}")
        showInputDialog(2, this)
      }
      R.id.mvp_survey_survey_back -> {
        finish()
      }
      R.id.mvp_survey_survey_settings -> {
        val intent = Intent(this, MvpSurveySettingsActivity::class.java)
        myData.name = "Survey Settings"
        myData.type = MyEnum.SETTINGS_TYPE_MVP_SURVEY
        intent.putExtra("myData", myData)
        startActivity(intent)
      }
      R.id.mvp_survey_point -> {
        myHelper.log("survey point:$selectedLabel")
        when {
          location1 == null -> myHelper.showErrorDialog("No GPS Data", "Please change your location to continue.")
          selectedLabel.id == 0 -> myHelper.showErrorDialog("Empty Label!", "Please select a label to continue survey")
          else -> {
            val location = LatLng(location1!!.latitude, location1!!.longitude)
            map.addMarker(MarkerOptions().position(location).icon(myHelper.bitmapFromVector(R.drawable.ic_circle_16)))
            val file_name = "${selectedLabel.number}_${current_label_number}_${db.getMvpOrgsFiles(selectedLabel_aws_path!!).size + 1}"
            // get Point attribute and then reset value of point attribute as it should only be used for next point capturing
            val lastJourney = myHelper.getLastJourney()
            val file_description = lastJourney.file_description
            lastJourney.file_description = ""
            myHelper.setLastJourney(lastJourney)
            current_point.setText(file_name)
            val myData1 = MyData()
            val aws_path =
              myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Survey/${myHelper.getValidFileName(selectedLabel.number)}/${myHelper.getValidFileName("${selectedLabel.number}_${current_label_number}")}/${myHelper.getOrgID()}_${myHelper.getUserID()}_${myData.project_id}_${myData.mvp_orgs_files_id}_${myHelper.getCurrentTimeMillis()}"
            val relative_path =
              myData.relative_path + "${myHelper.getLoginAPI().name}_${myHelper.getLoginAPI().id}/Data Collection/Survey/${selectedLabel.number}/${selectedLabel.number}_$current_label_number/${file_name}"
            myData1.org_id = myHelper.getOrgID()
            myData1.user_id = myHelper.getUserID()
            myData1.project_id = myData.project_id
            myData1.mvp_orgs_files_id = myData.mvp_orgs_files_id
            myData1.admin_file_type_id = selectedLabel.admin_file_type_id
            myData1.security_level = myHelper.getLoginAPI().role
            myData1.aws_path = aws_path
            myData1.relative_path = relative_path
            myData1.file_description = file_description
            myData1.loadingGPSLocation = gpsLocation
            myData1.unloadingGPSLocation = gpsLocation
            myData1.upload_status = 2
            myData1.file_level = (relative_path.split("/").size - 1)
            myData1.security_level = myHelper.getLoginAPI().role
            myData1.size = 0
            myData1.file_details = "{ \"size\": 0 }"
            if (myDataPushSave.pushInsertSurveyRecordCheckPoint(myData1) > 0) myHelper.log("Survey recorded successfully") else myHelper.showErrorDialog("Scan image not captured!", "Please try again later.")
            myHelper.log(myData1.toString())
          }
        }
      }
    }
  }
  
  private fun showInputDialog(type: Int, context: Context) {
    
    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null)
    var title = "Antenna Height"
    var explanation = "Please enter valid antenna height in m."
    when (type) {
      1 -> {
        title = "Antenna Height"
        explanation = "Please enter valid antenna height in m."
        mDialogView.mvp_survey_dialog_input.hint = "Please enter decimal value for antenna height"
        mDialogView.mvp_survey_dialog_input.setText(myHelper.getLastJourney().antenna_height.toString())
      }
      2 -> {
        title = "Point Attribute"
        explanation = "Please enter point attribute for next survey point."
        mDialogView.mvp_survey_dialog_input.hint = "Please enter point attribute text"
        mDialogView.mvp_survey_dialog_input.inputType = InputType.TYPE_CLASS_TEXT
      }
    }
    
    mDialogView.error_title.text = title
    if (explanation.isNotBlank()) {
      mDialogView.error_explanation.text = explanation
      mDialogView.error_explanation.visibility = View.VISIBLE
    }
    
    val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
    val mAlertDialog = mBuilder.show()
    mAlertDialog.setCancelable(true)
    
    val window = mAlertDialog.window
    val wlp = window!!.attributes
    
    wlp.gravity = Gravity.CENTER
    window.attributes = wlp
    
    mDialogView.error_cancel.setOnClickListener {
      mAlertDialog.dismiss()
    }
    
    mDialogView.error_ok.setOnClickListener {
      when {
        type == 1 && myHelper.isDecimal(mDialogView.mvp_survey_dialog_input.text.toString()) -> {
          val lastJourney = myHelper.getLastJourney()
          lastJourney.antenna_height = mDialogView.mvp_survey_dialog_input.text.toString().toDouble()
          myHelper.setLastJourney(lastJourney)
          mvp_survey_survey_gps_data_antenna_height.text = "Antenna Height: ${myHelper.getLastJourney().antenna_height} m"
          mAlertDialog.dismiss()
        }
        type == 2 -> {
          val lastJourney = myHelper.getLastJourney()
          lastJourney.file_description = mDialogView.mvp_survey_dialog_input.text.toString()
          myHelper.setLastJourney(lastJourney)
          mAlertDialog.dismiss()
        }
        else -> {
          myHelper.toast("Please enter valid antenna height in m.")
        }
      }
    }
  }
  
  override fun onResume() {
    super.onResume()
    selectedLabel = Material()
    mvpOrgsProjects = db.getAdminMvpSurveysLabels(2)
    adapter = CustomGridLMachine(this@MvpSurveySurveyActivity, mvpOrgsProjects, 1)
    gv.adapter = adapter
    current_point.setText("")
    mvp_survey_survey_gps_data_antenna_height.text = "Antenna Height: ${myHelper.getLastJourney().antenna_height} m"
  }
  
  private fun startGPS1() {
    locationManager1 = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    try {
      locationManager1?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, locationListener1)
    }
    catch (ex: SecurityException) {
      myHelper.log("No Location Available:${ex.message}")
      myHelper.showGPSDisabledAlertToUser()
    }
  }
  
  val locationListener1: LocationListener = object : LocationListener {
    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
      location1 = location
      myHelper.setGPSLayout(
        location, mvp_survey_survey_gps_data_acc, mvp_survey_survey_gps_data_lat, mvp_survey_survey_gps_data_long, mvp_survey_survey_gps_data_alt, mvp_survey_survey_gps_data_speed, mvp_survey_survey_gps_data_bearing, mvp_survey_survey_gps_data_time
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
      val marker = map.addMarker(MarkerOptions().position(location1).title(mapGPSLocation.locationName))
      
      marker?.showInfoWindow()
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
  
  fun addMarkers(myDataList: ArrayList<MyData>) {
    map.clear()
    myDataList.forEach {
      myHelper.log("AddMarker:${it.loadingGPSLocation.latitude}")
      val location = LatLng(it.loadingGPSLocation.latitude, it.loadingGPSLocation.longitude)
      map.addMarker(MarkerOptions().position(location).icon(myHelper.bitmapFromVector(R.drawable.ic_circle_16)))
    }
  }
  
  override fun onMarkerClick(p0: Marker) = false
}