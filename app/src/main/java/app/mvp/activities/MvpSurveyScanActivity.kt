package app.mvp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyEnum.Companion.MVP_ZOOM_LEVEL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.data.kml.KmlLayer
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_survey_scan.*
import kotlinx.android.synthetic.main.app_bar_base.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MvpSurveyScanActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback,
                              GoogleMap.OnMarkerClickListener {
  
  private var location1: Location? = null
  private val tag = this::class.java.simpleName
  private lateinit var lastLocation: Location
  private var locationManager1: LocationManager? = null
  private var imageCapture: ImageCapture? = null
  private var videoCapture: VideoCapture<Recorder>? = null
  private var recording: Recording? = null
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var map: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private var mapGPSLocation: GPSLocation = GPSLocation()
  private var mInterval: Long = 1000 // 1 seconds by default, can be changed later
  private var mHandler: Handler? = null
  private var isCapturingImage = false
  var checkpoint_label = "Label Scan"
  
  var mStatusChecker: Runnable = object : Runnable {
    override fun run() {
      try {
        takePhoto()
      } finally {
        mHandler!!.postDelayed(this, mInterval)
      }
    }
  }
  
  var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      // There are no request codes
      val intent: Intent? = result.data
      myHelper.log("resultLauncher")
      
      val bundle: Bundle? = intent!!.extras
      if (bundle != null) {
        myData = bundle.getSerializable("myData") as MyData
        myHelper.log("onActivityResult----:$myData")
        mInterval = myData.timer_interval
      }
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_survey_scan, contentFrameLayout)
    
    myHelper.setTag(tag)
    myData = myHelper.getLastJourney()
    myHelper.log("myData:$myData")
    toolbar_title.text = myData.mvp_orgs_project_name + " / " + myData.mvp_orgs_files_name + " / Data Collection / Scan"
    
    
    if (allPermissionsGranted()) {
      startCamera()
    } else {
      ActivityCompat.requestPermissions(
        this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
      )
    }
    
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    cameraExecutor = Executors.newSingleThreadExecutor()
    startGPS1()
    mHandler = Handler()
    mvp_survey_scan_capture.text = resources.getString(R.string.start_image_capture)
    
    mvp_survey_scan_back.setOnClickListener(this)
    mvp_survey_scan_capture.setOnClickListener(this)
    mvp_survey_scan_pause.setOnClickListener(this)
    mvp_survey_scan_settings.setOnClickListener(this)
  }
  
  override fun onResume() {
    super.onResume()
    base_nav_view.setCheckedItem(base_nav_view.menu.getItem(5))
    refreshViews(location1)
  }
  
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_survey_scan_back -> {
        finish()
      }
      R.id.mvp_survey_scan_capture -> {
        checkpoint_label = mvp_survey_scan_label.text.toString()
        myHelper.log("checkpoint_label:${checkpoint_label.length}")
        myHelper.log(checkpoint_label)
        
        when {
          location1 == null -> myHelper.showErrorDialog("No GPS Data", "Please change your location to continue.")
          checkpoint_label.isEmpty() -> myHelper.showErrorDialog("Label Scan", "Please enter label scan to continue scan.")
          else -> {
            myHelper.log("isCapturingImage:$isCapturingImage")
            if (isCapturingImage) {
              myHelper.log("stopRepeatingTask:$checkpoint_label")
              stopRepeatingTask()
              mvp_survey_scan_pause.visibility = View.GONE
              mvp_survey_scan_settings.visibility = View.VISIBLE
              isCapturingImage = false
              mvp_survey_scan_label.isEnabled = true
              mvp_survey_scan_capture.text = resources.getString(R.string.start_image_capture)
              mvp_survey_scan_label.text.clear()
            } else {
              myHelper.log("startRepeatingTask")
              mvp_survey_scan_label.isEnabled = false
              isCapturingImage = true
              mvp_survey_scan_pause.visibility = View.VISIBLE
              mvp_survey_scan_settings.visibility = View.GONE
              startRepeatingTask()
              mvp_survey_scan_capture.text = resources.getString(R.string.stop_image_caputer)
            }
          }
        }
      }
      R.id.mvp_survey_scan_pause -> {
        if (isCapturingImage) {
          myHelper.log("pauseRepeatingTask")
          stopRepeatingTask()
          isCapturingImage = false
          mvp_survey_scan_pause.text = "Resume"
        } else {
          myHelper.log("resumeRepeatingTask")
          isCapturingImage = true
          startRepeatingTask()
          mvp_survey_scan_pause.text = "Pause"
        }
      }
      R.id.mvp_survey_scan_settings -> {
        val intent = Intent(this, SettingsActivity::class.java)
        myData.name = "Scan Settings"
        myData.type = MyEnum.SETTINGS_TYPE_MVP_SCAN
        myData.timer_interval = mInterval
        intent.putExtra("myData", myData)
//                startActivity(intent)
        resultLauncher.launch(intent)
      }
    }
  }
  
  private fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    
    cameraProviderFuture.addListener({
      // Used to bind the lifecycle of cameras to the lifecycle owner
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
      
      // Preview
      val preview = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(viewFinder.surfaceProvider)
        }
      
      imageCapture = ImageCapture.Builder()
        .build()
      
      // Select back camera as a default
      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
      
      try {
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()
        
        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
          this, cameraSelector, preview, imageCapture
        )
        
      }
      catch (exc: Exception) {
        Log.e(TAG, "Use case binding failed", exc)
      }
      
    }, ContextCompat.getMainExecutor(this))
  }
  
  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>, grantResults:
    IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (allPermissionsGranted()) {
        startCamera()
      } else {
        Toast.makeText(
          this,
          "Permissions not granted by the user.",
          Toast.LENGTH_SHORT
        ).show()
        finish()
      }
    }
  }
  
  private fun getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
      File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) {
      Log.i(TAG, mediaDir.path); mediaDir
    } else {
      filesDir
    }
  }
  
  private fun takePhoto() {
    // Get a stable reference of the modifiable image capture use case
    val imageCapture = imageCapture ?: return
    
    val file_name = "${myHelper.getOrgID()}_${myHelper.getUserID()}_${myData.project_id}_${myData.mvp_orgs_files_id}_${myHelper.getCurrentTimeMillis()}.jpg"
    
    // Create time stamped name and MediaStore entry.
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, name)
      put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
      put(MediaStore.MediaColumns.DATA, "image/jpeg")
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
      }
    }
    val metadata = ImageCapture.Metadata()
    metadata.location = location1
    // Create output options object which contains file + metadata

//    val photoFile = File.createTempFile(name, ".jpg", getOutputDirectory())
    val photoFile = myHelper.createTempFile(file_name, getOutputDirectory())
    val outputOptions = ImageCapture.OutputFileOptions
      .Builder(
        photoFile
      )
//      .Builder(
//        contentResolver,
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//        contentValues
//      )
      .setMetadata(metadata)
      .build()
    
    // Set up image capture listener, which is triggered after photo has
    // been taken
    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(this),
      object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
          myHelper.toast("Photo capture failed:" + exc.message)
        }
        
        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
          val msg = "Image saved successfully: ${output.savedUri}"
          if (location1 == null) {
            myHelper.showErrorDialog("No GPS Data", "Please change your location to continue.")
            stopRepeatingTask()
            mvp_survey_scan_pause.visibility = View.GONE
            mvp_survey_scan_settings.visibility = View.VISIBLE
            isCapturingImage = false
            mvp_survey_scan_label.isEnabled = true
            mvp_survey_scan_capture.text = resources.getString(R.string.start_image_capture)
            mvp_survey_scan_label.text.clear()
          } else {
            
            val location = LatLng(location1!!.latitude, location1!!.longitude)
            val myData1 = MyData()
            val aws_path =
              myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Scan/${myHelper.getValidFileName(checkpoint_label)}/${file_name}"
            val relative_path =
              myData.relative_path + "${myHelper.getLoginAPI().name}_${myHelper.getLoginAPI().id}/Data Collection/Scan/${checkpoint_label}/${file_name}"
            
            myData1.org_id = myHelper.getOrgID()
            myData1.user_id = myHelper.getUserID()
            myData1.project_id = myData.project_id
            myData1.mvp_orgs_files_id = myData.mvp_orgs_files_id
            myData1.admin_file_type_id = MyEnum.ADMIN_FILE_TYPE_TAPU_SCAN
            myData1.security_level = myHelper.getLoginAPI().role
            myData1.aws_path = aws_path
            myData1.relative_path = relative_path
            myData1.loadingGPSLocation = gpsLocation
            myData1.unloadingGPSLocation = gpsLocation
            myData1.upload_status = 2
            myData1.file_level = (relative_path.split("/").size - 1)
            myData1.security_level = myHelper.getLoginAPI().role
            myData1.size = photoFile.length().toInt()
            myData1.file_details = "{ \"size\": ${photoFile.length()} }"
            if (myDataPushSave.pushInsertSurveyRecordCheckPoint(myData1) > 0) {
              myHelper.log("Scan recorded successfully")
              val label_name1: String = myData1.relative_path.substringAfterLast("/")
              map.addMarker(MarkerOptions().position(location).title(label_name1).icon(myHelper.bitmapFromVector(R.drawable.ic_camera_scan)))
            } else myHelper.showErrorDialog("Scan image not captured!", "Please try again later.")
            myHelper.log(msg)
            myHelper.awsFileUpload(aws_path, photoFile)
          }
        }
      }
    )
  }
  
  private fun captureVideo() {}
  
  private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
      baseContext, it
    ) == PackageManager.PERMISSION_GRANTED
  }
  
  override fun onDestroy() {
    super.onDestroy()
    stopRepeatingTask()
    cameraExecutor.shutdown()
  }
  
  fun startRepeatingTask() {
    mStatusChecker.run()
  }
  
  fun stopRepeatingTask() {
    mHandler!!.removeCallbacks(mStatusChecker)
  }
  
  private fun startGPS1() {
    locationManager1 = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    try {
      locationManager1?.requestLocationUpdates(
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
  
  fun refreshViews(location: Location?) {
    
    if (location != null) {
      myHelper.setGPSLayout(location, mvp_survey_scan_gps_data_acc, mvp_survey_scan_gps_data_lat, mvp_survey_scan_gps_data_long, mvp_survey_scan_gps_data_alt, mvp_survey_scan_gps_data_speed, mvp_survey_scan_gps_data_bearing, mvp_survey_scan_gps_data_time)
//      if (isCapturingImage) {
      when {
        location.accuracy >= 1 -> {
//            mvp_survey_scan_capture.setBackgroundColor(resources.getColor(R.color.red))
          mvp_survey_scan_capture.backgroundTintList = ContextCompat.getColorStateList(this@MvpSurveyScanActivity, R.color.red);
        }
        location.accuracy <= 0.05 -> {
//            mvp_survey_scan_capture.setBackgroundColor(resources.getColor(R.color.green))
          mvp_survey_scan_capture.backgroundTintList = ContextCompat.getColorStateList(this@MvpSurveyScanActivity, R.color.green);
        }
        else -> {
//            mvp_survey_scan_capture.setBackgroundColor(resources.getColor(R.color.yellow))
          mvp_survey_scan_capture.backgroundTintList = ContextCompat.getColorStateList(this@MvpSurveyScanActivity, R.color.yellow);
        }
      }

//      } else {
////        mvp_survey_scan_capture.setBackgroundColor(resources.getColor(R.color.colorPrimary))
//        mvp_survey_scan_capture.backgroundTintList = ContextCompat.getColorStateList(this@MvpSurveyScanActivity, R.color.colorPrimary);
//      }
    }
    
  }
  
  val locationListener1: LocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
//      myHelper.log("Status onLocationChanged.")
      location1 = location
      refreshViews(location1)
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
  
  companion object {
    private const val TAG = "CameraXApp"
    private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private const val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS =
      mutableListOf(
        Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
      ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
          add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
      }.toTypedArray()
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
          map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MVP_ZOOM_LEVEL))
          map.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, MVP_ZOOM_LEVEL))
        }
      }
      
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, MVP_ZOOM_LEVEL))
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, MVP_ZOOM_LEVEL))
      
    } else {
      map.isMyLocationEnabled = true
      fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
          lastLocation = location
          val currentLatLng = LatLng(location.latitude, location.longitude)
          map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MVP_ZOOM_LEVEL))
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
    addMarkers()
  }
  
  fun addMarkers() {
    map.clear()
    val selectedLabel_aws_path1 = myData.aws_path + "${myHelper.getValidFileName(myHelper.getLoginAPI().name)}_${myHelper.getLoginAPI().id}/Data_Collection/Scan/"
    db.getMvpOrgsFiles(selectedLabel_aws_path1!!).forEach {
      val label_name1: String = it.relative_path.substringAfterLast("/")
      map.addMarker(MarkerOptions().position(LatLng(it.loadingGPSLocation.latitude, it.loadingGPSLocation.longitude)).title(label_name1).icon(myHelper.bitmapFromVector(R.drawable.ic_camera_scan)))
    }
  }
  
  override fun onMarkerClick(p0: Marker) = false
}
