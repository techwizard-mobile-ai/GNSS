package app.mvp.activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_mvp_survey_scan.*
import kotlinx.android.synthetic.main.app_bar_base.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MvpSurveyScanActivity : BaseActivity(), View.OnClickListener {
    
    private val tag = this::class.java.simpleName
    private lateinit var lastLocation: Location
    private var locationManager: LocationManager? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_mvp_survey_scan, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true
        
        myHelper.setTag(tag)
        myData = myHelper.getLastJourney()
        myHelper.log("myData:$myData")
        toolbar_title.text = myData.mvp_orgs_project_name + " / " + myData.mvp_orgs_folder_name + " / Data Collection / Scan"
        startGPS()
        
        
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        mvp_survey_scan_capture.setOnClickListener(this)
        
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
    
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    myHelper.log("Photo capture failed: ${exc.message}")
                }
                
                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Image saved successfully: ${output.savedUri}"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    myHelper.toast(msg)
                    myHelper.log(msg)
//                    Log.d(TAG, msg)
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
        cameraExecutor.shutdown()
    }
    
    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.mvp_orgs_projects_back -> {
                finish()
            }
            R.id.mvp_survey_scan_capture -> {
                takePhoto()
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
        override fun onLocationChanged(location: Location) {
//            makeUseOfLocation(location)
            myHelper.log("location----$location")
            
            mvp_survey_scan_gps_data.text =
                "Latitude: ${location.latitude}  Longitude: ${location.longitude}  Accuracy: ${location.accuracy}  Altitude: ${location.altitude}  Speed: ${location.speed}  Bearing: ${location.bearing}  Time: ${
                    myHelper.getDateTimeWithSeconds(location.time)
                }"
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
}
