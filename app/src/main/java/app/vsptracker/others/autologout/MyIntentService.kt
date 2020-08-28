package app.vsptracker.others.autologout

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.activities.truck.THomeActivity


class MyIntentService : IntentService("MyIntentService") {
    
    override fun onHandleIntent(intent: Intent?) {
        intent?.apply {
            when (intent.action) {
                ACTION_SEND_TEST_MESSAGE -> {
                    val message = getStringExtra(EXTRA_MESSAGE)
                    println(message)
                    Log.e("MyIntentService", message)
                    Log.e("applicationContext", applicationContext.packageName)
        
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    showNotification(applicationContext)
                    try {
                        val intent1 = Intent(applicationContext, OperatorLoginActivity::class.java)
//                        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        intent1.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        applicationContext.startActivity(intent1)
                    }
                    catch (e: Exception) {
                        Log.e("MyIntentService", "App_Check:${e.localizedMessage}")
                    }
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("LocalService", "Received start id $startId: $intent")
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT)
        val alarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] = restartServicePendingIntent
    
        val intent = Intent("com.android.ServiceStopped")
        sendBroadcast(intent)
        
        super.onTaskRemoved(rootIntent)
    }
    
    private fun showNotification(appContext: Context) {
        val NOTIFICATION_ID = 234
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val CHANNEL_ID = "my_channel_01"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "my_channel"
            val description = "VSPTracker Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            mChannel.enableVibration(false)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(mChannel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("You are still logged in.")
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentText("Please logout from VSP Tracker if you are not working")
        val resultIntent = Intent(appContext, OperatorLoginActivity::class.java)
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        resultIntent.putExtra("isAutoLogoutCall", false)
        val stackBuilder = TaskStackBuilder.create(appContext)
        stackBuilder.addParentStack(THomeActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        builder.setOngoing(true)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    companion object {
        const val ACTION_SEND_TEST_MESSAGE = "ACTION_SEND_TEST_MESSAGE"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }
    
}