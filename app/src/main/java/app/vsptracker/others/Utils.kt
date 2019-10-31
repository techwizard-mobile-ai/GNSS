package app.vsptracker.others

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import app.vsptracker.MyHelper
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData


object Utils {
    private var sTheme: Int = 0

    val THEME_MATERIAL_LIGHT = 0
    val THEME_YOUR_CUSTOM_THEME = 1

    val TAG1 = "Utils"
    private lateinit var myHelper: MyHelper

    fun changeToTheme(activity: Activity, theme: Int) {
        sTheme = theme

        var dataNew = MyData()
        var bundle: Bundle? = activity.intent.extras
        if (bundle != null) {
            dataNew = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$dataNew")
        }

        activity.finish()

        val intent = Intent(activity, activity.javaClass)
        intent.putExtra("myData", dataNew)
        activity.startActivity(intent)
        activity.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        )
    }

    fun onActivityCreateSetTheme(activity: Activity) {


        myHelper = MyHelper(TAG1, activity)

        if (myHelper.isNightMode()) {
            activity.setTheme(R.style.AppTheme_NightMode)
        } else {
            activity.setTheme(R.style.AppTheme_NoActionBar)
        }

    }
}