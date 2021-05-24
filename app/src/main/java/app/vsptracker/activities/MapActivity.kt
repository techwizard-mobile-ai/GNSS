package app.vsptracker.activities

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView


class MapActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback{


    private val DEFAULT_ZOOM: Float = 10.0f
    private var mLastKnownLocation: Location? = Location("GPS")

    private val mDefaultLocation: LatLng? = LatLng(132.3, 63.1)
    private var mLocationPermissionGranted: Boolean = false

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 64
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mPlaceDetectionClient: PlaceDetectionClient? = null
    private var mGeoDataClient: GeoDataClient? = null

    private val TAG = this::class.java.simpleName
/*

    private val COLOR_BLACK_ARGB = -0x1000000

    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_GREEN_ARGB = -0xc771c4
    private val COLOR_PURPLE_ARGB = -0x7e387c
    private val COLOR_ORANGE_ARGB = -0xa80e9
    private val COLOR_BLUE_ARGB = -0x657db
    private val POLYGON_STROKE_WIDTH_PX = 8

    private val PATTERN_DASH_LENGTH_PX = 20
    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT = Dot()
    private val DASH = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val GAP = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT)

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP)
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_map, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)
//        startGPS()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }
/*

    private fun stylePolygon(polygon: Polygon) {
        var machineTypeId = ""
        // Get the myData object stored with the polygon.
        if (polygon.tag != null) {
            machineTypeId = polygon.tag.toString()
        }

        var pattern: List<PatternItem>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB

        when (machineTypeId) {
            // If no machineTypeId is given, allow the API to use the default.
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }

        polygon.strokePattern = pattern
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX.toFloat())
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    override fun onPolygonClick(polygon: Polygon?) {
        myHelper.toast("Clicked.")
        polygon!!.fillColor = resources.getColor(R.color.colorPrimary)


    }

    override fun onPolylineClick(polyline: Polyline?) {
        if ((polyline!!.getPattern() == null) || (!polyline!!.getPattern()!!.contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            polyline.setPattern(null);
        }
    }
*/


    private fun getLocationPermission() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
//        updateLocationUI()
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI(googleMap);

        // Get the current location of the device and set the position of the map.
//        getDeviceLocation(googleMap);

//        val lat = gpsLocation.latitude
//        val longg = gpsLocation.longitude
//        myHelper.log("$gpsLocation")
//        val sydney = LatLng(lat, longg)
//        googleMap!!.addMarker(
//            MarkerOptions()
//            .position(sydney)
//            .title("GPS Location") )
//        val zoomLevel = 10.0f;
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

/*        val polygon = googleMap!!.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )

        polygon.setTag("A");
        polygon.strokePattern = PATTERN_POLYGON_BETA
        stylePolygon(polygon)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))
        googleMap.setOnPolygonClickListener(this)*/
    }

    private fun updateLocationUI(mMap: GoogleMap?) {
        if (mMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true)
                mMap.getUiSettings().setMyLocationButtonEnabled(true)
            } else {
                mMap.setMyLocationEnabled(false)
                mMap.getUiSettings().setMyLocationButtonEnabled(false)
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            myHelper.log("${e.message}")
        }

    }
    private fun getDeviceLocation(mMap: GoogleMap?) {

        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.getLastLocation()
                locationResult.addOnCompleteListener {
                    if (it.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = it.getResult()
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    mLastKnownLocation!!.getLatitude(),
                                    mLastKnownLocation!!.getLongitude()
                                ), DEFAULT_ZOOM
                            )
                        )
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", it.getException())
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                mDefaultLocation,
                                DEFAULT_ZOOM
                            )
                        )
                        mMap.getUiSettings().setMyLocationButtonEnabled(false)
                    }
                }
                locationResult.addOnCompleteListener {  }
            }
        } catch (e: SecurityException) {
            myHelper.log("${e.message}")
        }

    }

    override fun onClick(view: View?) {
        when(view!!.id){
        }
    }

}
