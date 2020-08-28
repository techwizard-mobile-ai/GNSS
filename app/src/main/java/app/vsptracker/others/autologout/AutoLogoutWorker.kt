package app.vsptracker.others.autologout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.activities.truck.THomeActivity

class AutoLogoutWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    val tag = "AutoLogoutWorker"
    override fun doWork(): Result {
        val appContext = applicationContext
        Log.e(tag, "App_Check:doWork")
        return try {
            // Launch the app to Auto Logout from app
//            val intent = Intent(appContext, OperatorLoginActivity::class.java)
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("isAutoLogoutCall", true)
//            appContext.startActivity(intent)
//            Toast.makeText(appContext, "Worker do work method", Toast.LENGTH_LONG).show()
//            Log.e(tag, "App_Check:startActivity")
//            val intent = Intent(appContext, HourMeterStopActivity::class.java)
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("isAutoLogoutCall", true)
//            appContext.startActivity(intent)
            showNotification(appContext)
            Result.success()
        }
        catch (e: Exception) {
            Log.e(tag, "Auto Logout Failure ${e.localizedMessage}")
            Result.failure()
        }
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
        resultIntent.putExtra("isAutoLogoutCall", true)
        val stackBuilder = TaskStackBuilder.create(appContext)
        stackBuilder.addParentStack(THomeActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        builder.setOngoing(true)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
