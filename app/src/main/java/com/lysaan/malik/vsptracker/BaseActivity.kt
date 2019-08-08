package com.lysaan.malik.vsptracker

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.view.View
import com.lysaan.malik.vsptracker.activities.LoginActivity
import com.lysaan.malik.vsptracker.activities.MachineStatusActivity
import com.lysaan.malik.vsptracker.activities.MachineTypeActivity
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.app_bar_base.*


open class BaseActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val TAG1 = "BaseActivity"
    private lateinit var myHelper: MyHelper
//    private lateinit var sessionManager: SessionManager
//
//    internal var PRIVATE_MODE = 0
//    private val PREF_NAME = "VSPTracker"
//    private val KEY_NIGHT_MODE= "night_mode"

//    internal var pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
//    internal var editor = pref.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

//        val nightMode = pref.getBoolean(KEY_NIGHT_MODE, false)
//
//        if(nightMode){
//            setTheme(R.style.AppTheme_AppBarOverlay)
//        }else{
//            setTheme(R.style.AppTheme)
//        }

        Utils.onActivityCreateSetTheme(this);

//        setTheme(R.style.AppTheme_AppBarOverlay)

        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        myHelper = MyHelper(TAG1, this)
        myHelper.log("NightMode: ${myHelper.isNightMode()}")
//        if(myHelper.isNightMode()){
//            setTheme(R.style.AppTheme_AppBarOverlay)
//        }else{
//            setTheme(R.style.AppTheme)
//        }


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.base_nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        when(myHelper.getMachineType()){
            1 -> {
                toolbar_title.text = "VSP Tracker - Excavator"
            }
            2 -> {
                toolbar_title.text = "VSP Tracker - Scrapper"
            }
            3 -> {
                toolbar_title.text = "VSP Tracker - Truck"

            }
        }



        base_machine_status.setOnClickListener {
            val intent = Intent(this@BaseActivity, MachineStatusActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        if(myHelper.getIsMachineStopped()){

            val text = "<font color=#FF382A>Machine is Stopped. </font><font color=#024064><u>Click here to Start Machine</u>.</font>"

//            val text1 = "<font color=#FFFFFF>Don't have an account?</font> <font color=#CA333A><b>SIGN UP</b></font>"
            base_machine_status.setText(Html.fromHtml(text))

            base_machine_status.visibility = View.VISIBLE
            myHelper.log("Is Machine Stopped: ${myHelper.getIsMachineStopped()}")
        }else{
            base_machine_status.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {

//                val intent = Intent (this, EHomeActivity::class.java)
//                startActivity(intent)

                myHelper.startHomeActivityByType()
            }
            R.id.nav_settings-> {

            }
            R.id.nav_logout-> {

                val intent = Intent (this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            R.id.nav_stop_machine -> {

                val intent = Intent (this, MachineStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_night_mode -> {



                if(myHelper.isNightMode()){
                    Utils.changeToTheme(this@BaseActivity, 0);
                    myHelper.setNightMode(false)
                }else {
                    Utils.changeToTheme(this@BaseActivity, 1);
                    myHelper.setNightMode(true)
                }
//
//                this.finish()
//                val intent = Intent(this, this::class.java)
//                this.startActivity(intent)

            }
            R.id.nav_change_machine -> {

                val intent = Intent (this, MachineTypeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_email-> {
                doEmail()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun doEmail() {

        var versionCode = BuildConfig.VERSION_CODE
        val device = android.os.Build.DEVICE
        val build = Build.BRAND
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val andoridOS = Build.VERSION.SDK_INT.toString()

        val details =
            "App_version:$versionCode--Device:$device--Build:$build--Manufacturer:$manufacturer--Model:$model--AndroidOS:$andoridOS"

        val email = "errors@vsptracker.com"
        val addressees: Array<String> = arrayOf(email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addressees)
            putExtra(Intent.EXTRA_SUBJECT, "Error Reporting About Android App")
            putExtra(Intent.EXTRA_TEXT, "Hi, I like to notify you about an error I faced while using App. Device Details: $details")
            intent.setType("message/rfc822")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
