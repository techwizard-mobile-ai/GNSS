package app.vsptracker.others.autologout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.vsptracker.R
import app.vsptracker.others.MyHelper

public class RebootReceiver : BroadcastReceiver() {
    
    val tag = "RebootReceiver"
    lateinit var myHelper: MyHelper
    
    override fun onReceive(context: Context, intent: Intent?) {
        myHelper = MyHelper(tag, context)
        if (myHelper.getOperatorAPI().id > 0) {
            ForegroundService.startService(context, context.getString(R.string.machine_hours_running), context.getString(R.string.auto_loggingout_from_vspt), 0)
        }
    }
}
