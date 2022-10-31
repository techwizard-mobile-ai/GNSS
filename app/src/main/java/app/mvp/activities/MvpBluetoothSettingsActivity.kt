package app.mvp.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.others.Utils
import com.google.android.material.navigation.NavigationView


class MvpBluetoothSettingsActivity  : BaseActivity(), View.OnClickListener {
  private var mBluetoothAdapter: BluetoothAdapter? = null
  private val REQUEST_ENABLE_BT = 3
  
  lateinit var bluetoothManager: BluetoothManager
  var bluetoothAdapter: BluetoothAdapter? = null
  
  private val PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED,
    Manifest.permission.BLUETOOTH_ADVERTISE
  )
  private val PERMISSIONS_LOCATION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED,
    Manifest.permission.BLUETOOTH_ADVERTISE
  )
  
  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_mvp_bluetooth_settings, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(5).isChecked = true
    myHelper.setTag(Utils.tag)
    
    checkPermissions()
    
  }
  
  private fun checkPermissions() {
    val permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
//    if (permission1 != PackageManager.PERMISSION_GRANTED) {
//      // We don't have permission so prompt the user
//      myHelper.log("External storage permission not granted")
//      ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,1)
//    } else
      if (permission2 != PackageManager.PERMISSION_GRANTED) {
      myHelper.log("bluetooth permission not granted")
      ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1)
    }else{
      myHelper.log("All permissions granted")
  
      val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
      myHelper.log("mBluetoothAdapter.isEnabled: ${mBluetoothAdapter.isEnabled}")
  
      val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
      myHelper.log("pairedDevices.size:${pairedDevices?.size}")
      pairedDevices?.forEach { device ->
        val deviceName = device.name
        val deviceHardwareAddress = device.address // MAC address
        myHelper.log("device.name ${device.name}")
      }
      
//      myHelper.log("mBluetoothAdapter.bondedDevices.size: ${mBluetoothAdapter.bondedDevices.size}")

    }
  }

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.mvp_corrections_bluetooth_settings -> {
        myHelper.log("mvp_corrections_bluetooth_settings")
      }
      R.id.mvp_corrections_receiver_settings -> {
        myHelper.log("mvp_corrections_receiver_settings")
      }
      R.id.mvp_corrections_ntrip_settings -> {
        myHelper.log("mvp_corrections_ntrip_settings")
      }
    }
  }
  
  
}