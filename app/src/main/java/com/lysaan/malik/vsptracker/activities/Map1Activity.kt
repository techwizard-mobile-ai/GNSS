package com.lysaan.malik.vsptracker.activities

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.GPSLocation
import kotlinx.android.synthetic.main.activity_map1.*

class Map1Activity : BaseActivity(), View.OnClickListener, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    private val ZOOM_LEVEL: Float = 19.0f

    private val TAG = this::class.java.simpleName
    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private var mapGPSLocation: GPSLocation = GPSLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_map1, contentFrameLayout)
//        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
//        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            mapGPSLocation = bundle!!.getSerializable("gpsLocation") as GPSLocation
            myHelper.log("mapGPSLocation:$mapGPSLocation")
        }


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

    private fun setUpMap() {

        if (mapGPSLocation.latitude != 0.0 && mapGPSLocation.longitude != 0.0) {
            val lat = mapGPSLocation.latitude
            val longg = mapGPSLocation.longitude
            myHelper.log("In SetupMap:$mapGPSLocation")
            val location = LatLng(lat, longg)
            map!!.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(mapGPSLocation.locationName)
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
        }

        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_LEVEL))
            }
        }

    }

    override fun onMarkerClick(p0: Marker?) = false

}
