package com.lysaan.malik.vsptracker.others

import android.app.Activity
import android.content.Intent
import com.lysaan.malik.vsptracker.R


object Utils {
    private var sTheme: Int = 0

    val THEME_MATERIAL_LIGHT = 0
    val THEME_YOUR_CUSTOM_THEME = 1

    fun changeToTheme(activity: Activity, theme: Int) {
        sTheme = theme
        activity.finish()
        activity.startActivity(Intent(activity, activity.javaClass))
        activity.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    fun onActivityCreateSetTheme(activity: Activity) {
        when (sTheme) {
            THEME_MATERIAL_LIGHT -> activity.setTheme(R.style.AppTheme_NoActionBar)
            THEME_YOUR_CUSTOM_THEME -> activity.setTheme(R.style.AppTheme_NightMode)
            else -> activity.setTheme(R.style.AppTheme)
        }
    }
}