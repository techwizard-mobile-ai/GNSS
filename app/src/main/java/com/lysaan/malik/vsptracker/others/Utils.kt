package com.lysaan.malik.vsptracker.others

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data


object Utils {
    private var sTheme: Int = 0

    val THEME_MATERIAL_LIGHT = 0
    val THEME_YOUR_CUSTOM_THEME = 1

    val TAG1 = "Utils"
    private lateinit var helper: Helper

    fun changeToTheme(activity: Activity, theme: Int) {
        sTheme = theme

        var dataNew = Data()
        var bundle: Bundle? = activity.intent.extras
        if (bundle != null) {
            dataNew = bundle!!.getSerializable("data") as Data
            helper.log("data:$dataNew")
        }

        activity.finish()

        val intent = Intent(activity, activity.javaClass)
        intent.putExtra("data", dataNew)
        activity.startActivity(intent)
        activity.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        )
    }

    fun onActivityCreateSetTheme(activity: Activity) {


        helper = Helper(TAG1, activity)

        if (helper.isNightMode()) {
            activity.setTheme(R.style.AppTheme_NightMode)
        } else {
            activity.setTheme(R.style.AppTheme_NoActionBar)
        }

    }
}