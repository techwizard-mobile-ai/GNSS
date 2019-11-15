package app.vsptracker.others

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData


object Utils {
    private var sTheme: Int = 0

    const val tag = "Utils"
    @SuppressLint("StaticFieldLeak")
    private lateinit var myHelper: MyHelper

    fun changeToTheme(activity: Activity, theme: Int) {
        sTheme = theme

        var dataNew = MyData()
        val bundle: Bundle? = activity.intent.extras
        if (bundle != null) {
            dataNew = bundle.getSerializable("myData") as MyData
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

        myHelper = MyHelper(tag, activity)
        when {
            myHelper.isNightMode() -> activity.setTheme(R.style.AppTheme_NightMode)
            else -> activity.setTheme(R.style.AppTheme_NoActionBar)
        }

    }
}