package com.lysaan.malik.vsptracker

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.LoadHistoryActivity
import com.lysaan.malik.vsptracker.activities.MachineTypeActivity
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.adapters.BaseNavigationAdapter
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.Data
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.app_bar_base.*


open class BaseActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val TAG1 = "BaseActivity"
    protected lateinit var helper: Helper
    protected lateinit var db : DatabaseAdapter
    protected lateinit var data : Data


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this);
//        setTheme(R.style.AppTheme_AppBarOverlay)

        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        helper = Helper(TAG1, this)
        db = DatabaseAdapter(this)
        data = Data()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.base_nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        when(helper.getMachineType()){
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
            val intent = Intent(this@BaseActivity, MachineStatus1Activity::class.java)
            startActivity(intent)
        }

        helper.hideKeybaordOnClick(base_content_frame)

        val navItems = ArrayList<Material>()
        navItems.add(Material(1,"Loading Machine"))
        navItems.add(Material(2,"Loaded Material"))
        navItems.add(Material(3,"Loading Location"))
        navItems.add(Material(4,"Loaded Material Weigh"))

        val aa = BaseNavigationAdapter(this@BaseActivity,navItems)
        base_navigation_rv.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        base_navigation_rv!!.setAdapter(aa)
    }

    override fun onResume() {
        super.onResume()
        if(helper.getIsMachineStopped()){

            val text = "<font color=#FF382A>Machine is Stopped. </font><font color=#106d14><u>Click here to Start Machine</u>.</font>"
            base_machine_status.setText(Html.fromHtml(text))

            base_machine_status.visibility = View.VISIBLE
            helper.log("Is Machine Stopped: ${helper.getIsMachineStopped()}")
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
        when (item.itemId) {
            R.id.nav_home -> {
                val data = Data()
                helper.startHomeActivityByType(data)
            }
            R.id.nav_settings-> {

            }
            R.id.nav_logout-> {

                val intent = Intent (this, HourMeterStopActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            R.id.nav_stop_machine -> {

                val intent = Intent (this, MachineStatus1Activity::class.java)
                startActivity(intent)
            }
            R.id.nav_night_mode -> {

                helper.log("theme:"+applicationContext.theme)
                helper.log("theme1:"+theme)
                if(helper.isNightMode()){
                    Utils.changeToTheme(this@BaseActivity, 0);
                    helper.setNightMode(false)
                }else {
                    Utils.changeToTheme(this@BaseActivity, 1);
                    helper.setNightMode(true)
                }
            }
            R.id.nav_change_machine -> {
                val intent = Intent (this, MachineTypeActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_load_history -> {
                val intent = Intent (this, LoadHistoryActivity::class.java)
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
