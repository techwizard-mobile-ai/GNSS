package app.vsptracker.others.autologout

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import app.vsptracker.others.MyHelper
import java.util.*


class RebootService : Service() {
    
    private val TAG = "AlarmService"
    
    override fun onCreate() {
        sendBroadcast()
        super.onCreate()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY
    }
    
    
    override fun onDestroy() {
        sendBroadcast()
        super.onDestroy()
    }
    
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }
    
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        sendBroadcast()
        super.onTaskRemoved(rootIntent)
    }
    
    private fun sendBroadcast() {
        val intent = Intent(this, RebootReceiver::class.java)
        //intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        val pendingIntent = PendingIntent.getBroadcast(
            this.applicationContext, 234324243, intent, 0
        )
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 50)
        
        val  myHelper = MyHelper(TAG, this )
        myHelper.log("isLoggedIn:${myHelper.getOperatorAPI().id}")
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            60000,
            pendingIntent
        )
        
//        alarmManager?.set(
//            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis(),
//            pendingIntent
//        )
    }
    
}