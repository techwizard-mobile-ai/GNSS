package app.vsptracker.activities

import android.annotation.SuppressLint
import android.location.Location
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
import com.google.maps.android.data.kml.KmlLayer
import kotlinx.android.synthetic.main.activity_map1.*
import java.io.File
import java.io.FileInputStream

private const val ZOOM_LEVEL: Float = 19.0f

class Map1Activity : BaseActivity(), View.OnClickListener, OnMapReadyCallback,
                     GoogleMap.OnMarkerClickListener {
    
    private val tag = this::class.java.simpleName
    private lateinit var map: GoogleMap
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    
    private var mapGPSLocation: GPSLocation = GPSLocation()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_map1, contentFrameLayout)
        
        myHelper.setTag(tag)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mapGPSLocation = bundle.getSerializable("gpsLocation") as GPSLocation
            myHelper.log("mapGPSLocation:$mapGPSLocation")
        }

//        if(myHelper.getIsMachineStopped()|| myHelper.getMachineID() <1){
//        if( !myHelper.isNavEnabled()){
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        map1_finish.setOnClickListener(this)
        
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        myHelper.setIsMapOpened(false)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        myHelper.setIsMapOpened(false)
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.map1_finish -> {
                myHelper.setIsMapOpened(false)
                finish()
            }
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
