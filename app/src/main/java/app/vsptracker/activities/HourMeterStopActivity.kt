package app.vsptracker.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.others.MyEnum
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_hour_meter_stop.*
import kotlinx.android.synthetic.main.logout_notification.view.*

class HourMeterStopActivity : BaseActivity(), View.OnClickListener {
  
  private val tag = this::class.java.simpleName
  
  private val adapterList = ArrayList<ServerSyncModel>()
  private val serverSyncList = ArrayList<ServerSyncAPI>()
  private var isAutoLogoutCall = false
  
  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
    layoutInflater.inflate(R.layout.activity_hour_meter_stop, contentFrameLayout)
    val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
    navigationView.menu.getItem(11).isChecked = true
    
    myHelper.setTag(tag)
    
    if (myHelper.getIsMachineStopped()) {
      drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//          Machine is already stopped so there is No need to show Hour Meter
      hm_layout.visibility = View.GONE
    } else {
      hm_layout.visibility = View.VISIBLE
    }
    sfinish_reading.setText(myHelper.getMeterTimeForFinish())
    
    val bundle: Bundle? = intent.extras
    if (bundle != null) {
      isAutoLogoutCall = bundle.getBoolean("isAutoLogoutCall")
      if (isAutoLogoutCall) {
        myHelper.log("App_Check:isAutoLogoutCall:$isAutoLogoutCall")
        logout(isAutoLogoutCall)
      }
    }
    
    hm_summary_operator.text = ":  ${myHelper.getOperatorAPI().name}"
    hm_summary_login.text = ":  ${myHelper.getDateTime(myHelper.getMeter().hourStartTime)}"
    hm_summary_machine.text = ":  ${myHelper.getMachineDetails()}"
    hm_summary_working_time.text = ":  ${myHelper.getFormattedTime(System.currentTimeMillis() - myHelper.getMeter().hourStartTime)}"
    
    when (myHelper.getMachineTypeID()) {
      MyEnum.EXCAVATOR -> {
        sm_summary_prod_dig_layout.visibility = View.VISIBLE
        hm_summary_prod_dig.text = ":  ${db.getCurrentLoginELoadHistory().size}"
        
        sm_summary_trenching_layout.visibility = View.VISIBLE
        hm_summary_trenching.text = ":  ${db.getCurrentLoginEWorks(MyEnum.EXCAVATOR_TRENCHING).size}"
        
        sm_summary_gen_dig_layout.visibility = View.VISIBLE
        hm_summary_gen_dig.text = ":  ${db.getCurrentLoginEWorks(MyEnum.EXCAVATOR_GEN_DIGGING).size}"
      }
      MyEnum.SCRAPER -> {
        sm_summary_trips_layout.visibility = View.VISIBLE
        hm_summary_trips.text = ":  ${db.getCurrentLoginTrips().size}"
        
        sm_summary_trimming_layout.visibility = View.VISIBLE
        hm_summary_trimming.text = ":  ${db.getCurrentLoginEWorks(MyEnum.SCRAPER_TRIMMING).size}"
        
      }
      MyEnum.TRUCK -> {
        sm_summary_trips_layout.visibility = View.VISIBLE
        hm_summary_trips.text = ":  ${db.getCurrentLoginTrips().size}"
      }
    }
    
    
    sfinish_minus.setOnClickListener(this)
    sfinish_plus.setOnClickListener(this)
    hm_stop_logout.setOnClickListener(this)
  }
  
  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.sfinish_minus -> {
        val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
        if (value > 0) {
          val newValue = value - 0.1
          sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
        } else {
          myHelper.toast("Please Enter Valid Meter Value.")
          sfinish_reading.setText(myHelper.getRoundedDecimal(value.toDouble()).toString())
        }
      }
      
      R.id.sfinish_plus -> {
        val value = myHelper.getMeterValidValue(sfinish_reading.text.toString()).toFloat()
        val newValue = value + 0.1
        sfinish_reading.setText(myHelper.getRoundedDecimal(newValue).toString())
      }
      
      R.id.hm_stop_logout -> {
        if (!myHelper.isOnline()) {
          updatedNotification()
        } else {
          logout()
        }
      }
    }
  }
  
  @SuppressLint("InflateParams")
  private fun updatedNotification() {
    
    val mDialogView = LayoutInflater.from(this).inflate(R.layout.logout_notification, null)
    val mBuilder = AlertDialog.Builder(this)
      .setView(mDialogView)
    val mAlertDialog = mBuilder.show()
    mAlertDialog.setCancelable(true)
    
    val window = mAlertDialog.window
    val wlp = window!!.attributes
    
    wlp.gravity = Gravity.CENTER
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
    window.attributes = wlp
    
    
    mDialogView.logout_ok_btn.setOnClickListener {
      mAlertDialog.dismiss()
      logout()
    }
    mDialogView.logout_cancel_btn.setOnClickListener {
      mAlertDialog.dismiss()
    }
    
  }
  
  private fun logout(isAutoLogoutCall: Boolean = false) {
    var machineStopReasonID = MyEnum.LOGOUT_TYPE_OPERATOR
    if (isAutoLogoutCall) {
      machineStopReasonID = MyEnum.LOGOUT_TYPE_AUTO
    }
    // AutoLogoutWorker is not running but handler is running to check Logout time
    // So Cancel Handler when Operator manually logout.
    stopHandler()
    // Using BaseActivity logout method as we have to stop waiting and change days work mode.
    logout(machineStopReasonID, gpsLocation, sfinish_reading.text.toString())
  }
}
