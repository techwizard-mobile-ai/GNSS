package app.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.SelectMachineNumberAdapter
import app.vsptracker.adapters.SelectStateAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.others.MyEnum.Companion.LOGOUT_TYPE_MACHINE_CHANGED
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_machine_change.*
import java.util.*

private const val REQUEST_ACCESS_FINE_LOCATION = 1

class MachineTypeActivity : BaseActivity(), View.OnClickListener {
  
  private var machineLocations = ArrayList<Material>()
  private var selectedMachineSite = Material(0, "Select Machine Site")
  private var selectedMachineType = Material(0, "Select Machine Type")
  private var selectedMachineLocation = Material(0, "Select Machine Location")
  private var selectedMachineNumber = Material(0, "Select Machine Number")
  private val tag = this::class.java.simpleName
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//        Utils.onActivityCreateSetTheme(this)
//        setContentView(R.layout.activity_machine_change)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_machine_change, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(7).isChecked = true
    
    myHelper.log("meter.hourStartGPSLocation:${myHelper.getMeter().hourStartGPSLocation}")
//        if(myHelper.getIsMachineStopped() || myHelper.getMachineID() <1){
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        }

//        myHelper = MyHelper(tag, this@MachineTypeActivity)
//        db = DatabaseAdapter(this)

//        this.retrofit = Retrofit.Builder()
//            .baseUrl(RetrofitAPI.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
//        myDataPushSave = MyDataPushSave(this)
    
    selectMachineSite()
    selectMachineType()
//        selectMachineLocation()
    selectMachineNumber()
    
    machine_number1.isEnabled = enableList()
    machine_location.isEnabled = enableList()
//        myHelper.log("enableList${enableList()}")

//        startGPS()
//        gpsLocation = GPSLocation()
//        machine_type_main_layout.setOnTouchListener(View.OnTouchListener { v, event ->
//            myHelper.hideKeyboard(machine_type_main_layout)
//            false
//        })
    
    machine_type_main_layout.setOnTouchListener { view, _ ->
      view.performClick()
      myHelper.hideKeyboard(machine_type_main_layout)
      false
    }
    mt_site.setOnTouchListener { view, _ ->
      view.performClick()
      myHelper.hideKeyboard(machine_type)
      false
    }
    machine_type.setOnTouchListener { view, _ ->
      view.performClick()
      myHelper.hideKeyboard(machine_type)
      false
    }
    machine_location.setOnTouchListener { view, _ ->
      view.performClick()
      myHelper.hideKeyboard(machine_location)
      false
    }
    machine_number1.setOnTouchListener { view, _ ->
      view.performClick()
      myHelper.hideKeyboard(machine_number1)
      false
    }
    
    
    if (myHelper.getMachineID() == 0)
      myHelper.setIsNavEnabled(false)
    machine_save.setOnClickListener(this)
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.machine_save -> {
        
        machine_number.text.toString()
        when {
          selectedMachineSite.id == 0 -> myHelper.toast("Please Select Site")
          selectedMachineType.id == 0 -> myHelper.toast("Please Select Machine Type")
          selectedMachineNumber.id == 0 -> myHelper.toast("Please Select Machine Number")
          else -> {
            when {
              //Selected Machine is Different from Previous Machine
              selectedMachineNumber.id != myHelper.getMachineID() -> {
                // save old machine status, this will be used for  updating old machine status to stopped on server
                val oldMachineStatus = MyData()
                oldMachineStatus.machineId = myHelper.getMachineID()
                oldMachineStatus.isRunning = 0
                oldMachineStatus.isSync = 0
                myHelper.setOldMachineStatus(oldMachineStatus)
                val myData = MyData()
                myData.siteId = myHelper.getMachineSettings().siteId
                myData.machineId = myHelper.getMachineID()
                myData.orgId = myHelper.getLoginAPI().org_id
                myData.operatorId = myHelper.getOperatorAPI().id
                myData.machineId = myHelper.getMachineID()
                val currentTime = System.currentTimeMillis()
                myData.stopTime = currentTime
                
                val meter = myHelper.getMeter()
                if (meter.isMachineStartTimeCustom)
                  myData.isStartHoursCustom = 1
                myData.loadingGPSLocation = meter.hourStartGPSLocation
                myData.startHours = meter.startHours
                myData.startTime = meter.machineStartTime
                
                myData.loadingGPSLocationString = myHelper.getGPSLocationToString(myData.loadingGPSLocation)
                myData.unloadingGPSLocation = gpsLocation
                myData.unloadingGPSLocationString = myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
                myData.machine_stop_reason_id = LOGOUT_TYPE_MACHINE_CHANGED
                if (myHelper.isDailyModeStarted()) myData.isDayWorks = 1 else myData.isDayWorks = 0
                
                myData.totalTime = myData.stopTime - myData.startTime
                
                myData.machineTypeId = myHelper.getMachineTypeID()
                myData.totalHours = myHelper.getMeterTimeForFinishCustom(myData.startHours)
                
                myData.time = currentTime.toString()
                myData.date = myHelper.getDate(currentTime.toString())
                
                if (myData.machineId > 0)
                  myDataPushSave.pushInsertMachineHour(myData)
                
                myHelper.setMachineTypeID(selectedMachineType.id)
                myHelper.setMachineNumber(selectedMachineNumber.number)
                myHelper.setMachineID(selectedMachineNumber.id)
//                                myHelper.setIsNavEnabled(false)
                val data = MyData()
                myHelper.setLastJourney(data)
                myHelper.setIsNavEnabled(true)
                val intent = Intent(this, HourMeterStartActivity::class.java)
                startActivity(intent)
                
              }
            }
          }
        }
      }
    }
  }
  
  private fun selectMachineSite() {
    val machineTypes = db.getSites()
    machineTypes.add(0, Material(0, "Select Site"))
    val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
    mt_site!!.adapter = selectMaterialAdapter
    mt_site.background = ContextCompat.getDrawable(this, R.drawable.disabled_spinner_border)
    mt_site.setSelection(0, false)
    mt_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      
      override fun onItemSelected(
        arg0: AdapterView<*>, arg1: View,
        position: Int, arg3: Long
      ) {
        selectedMachineSite = machineTypes[position]
        val machineSettings = myHelper.getMachineSettings()
        machineSettings.siteId = selectedMachineSite.id
        myHelper.setMachineSettings(machineSettings)
        machine_number1.isEnabled = enableList()
        machine_location.isEnabled = enableList()
        myHelper.log("enableList${enableList()}")
        selectMachineNumber()
        
        if (machineTypes[position].id != 0) {
          mt_site.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
        } else {
          mt_site.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
        }
        Log.e(tag, machineTypes[position].toString())
      }
      
      override fun onNothingSelected(arg0: AdapterView<*>) {
      
      }
    }
  }
  
  private fun enableList() = (selectedMachineSite.id > 0 && selectedMachineType.id > 0)
  private fun selectMachineType() {
    val machineTypes = db.getMachinesTypes()
    machineTypes.add(0, Material(0, "Select Machine Type"))
    val selectMaterialAdapter = SelectStateAdapter(this@MachineTypeActivity, machineTypes)
    machine_type!!.adapter = selectMaterialAdapter
    machine_type.background = ContextCompat.getDrawable(this, R.drawable.disabled_spinner_border)
    machine_type.setSelection(0, false)
    machine_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      
      override fun onItemSelected(
        arg0: AdapterView<*>, arg1: View,
        position: Int, arg3: Long
      ) {
        selectedMachineType = machineTypes[position]
        machine_number1.isEnabled = enableList()
        machine_location.isEnabled = enableList()
        myHelper.log("enableList${enableList()}")
        if (machineTypes[position].id != 0) {
          machine_type.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
          selectMachineNumber()
        } else {
          machine_type.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
          selectMachineNumber()
        }
        Log.e(tag, machineTypes[position].toString())
      }
      
      override fun onNothingSelected(arg0: AdapterView<*>) {
      
      }
    }
  }
  
  private fun selectMachineLocation() {
    
    machineLocations = db.getLocations()
//        myHelper.log("machineLocations:$machineLocations")
    machineLocations.add(0, Material(0, "Select Machine Location"))
    val selectMaterialAdapter = SelectStateAdapter(this, machineLocations)
    machine_location!!.adapter = selectMaterialAdapter
    machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
    machine_location.setSelection(0, false)
    machine_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      
      override fun onItemSelected(
        arg0: AdapterView<*>, arg1: View,
        position: Int, arg3: Long
      ) {
        selectedMachineLocation = machineLocations[position]
        if (machineLocations[position].id != 0) {
          machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
        } else {
          machine_location.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
        }
        Log.e(tag, machineLocations[position].toString())
      }
      
      override fun onNothingSelected(arg0: AdapterView<*>) {
      
      }
    }
  }
  
  private fun selectMachineNumber() {
    val machineTypes = db.getMachines(selectedMachineType.id)
    val machine = Material(0, "")
    machine.number = "Select Machine Number"
    machineTypes.add(0, machine)
    val selectMaterialAdapter = SelectMachineNumberAdapter(this@MachineTypeActivity, machineTypes)
    machine_number1!!.adapter = selectMaterialAdapter
    machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
    machine_number1.setSelection(0, false)
    machine_number1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      
      override fun onItemSelected(
        arg0: AdapterView<*>, arg1: View,
        position: Int, arg3: Long
      ) {
        selectedMachineNumber = machineTypes[position]
        if (machineTypes[position].id != 0) {
          machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.spinner_border)
        } else {
          machine_number1.background = ContextCompat.getDrawable(applicationContext, R.drawable.disabled_spinner_border)
        }
        Log.e(tag, machineTypes[position].toString())
      }
      
      override fun onNothingSelected(arg0: AdapterView<*>) {
      
      }
    }
  }
}
