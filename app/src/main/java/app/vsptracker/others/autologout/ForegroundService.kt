package app.vsptracker.others.autologout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import java.util.concurrent.TimeUnit

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    private val tag = "ForegroundService"
    
    companion object {
        
        fun startService(context: Context, title: String, text: String, duration :Long) {
            val workManager = WorkManager.getInstance(context)
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("title", title)
            startIntent.putExtra("text", text)
            ContextCompat.startForegroundService(context, startIntent)
            
            val myWorkRequest = OneTimeWorkRequestBuilder<AutoLogoutWorker>()
                .setInitialDelay(duration, TimeUnit.MINUTES)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag(MyEnum.WORKER_AUTO_LOGOUT)
                .build()
            workManager.enqueue(myWorkRequest)
        }
        
        fun stopService(context: Context) {
            val myHelper = MyHelper("ForegroundService", context)
            try {
                val stopIntent = Intent(context, ForegroundService::class.java)
                context.stopService(stopIntent)
                val workManager = WorkManager.getInstance(context)
                workManager.cancelAllWorkByTag(MyEnum.WORKER_AUTO_LOGOUT)
            }
            catch (e: Exception) {
                myHelper.log(e.message.toString())
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val title = intent?.getStringExtra("title")
        val text = intent?.getStringExtra("text")
        createNotificationChannel()
        val notificationIntent = Intent(this, OperatorLoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.truck_transparent)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setSound(null)
            .build()
        notification.color = ContextCompat.getColor(this, R.color.colorPrimary)
        startForeground(1, notification)
        return START_STICKY
    }
    
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
//                NotificationManager.IMPORTANCE_DEFAULT // to enable sound
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}