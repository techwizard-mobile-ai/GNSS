package app.vsptracker.others.autologout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.activities.truck.THomeActivity

public class AlarmReceiver : BroadcastReceiver() {
    
    
    override fun onReceive(context: Context, intent: Intent?) {
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_LONG).show()
        showNotification(context)
        
//        generateNotification(context)
        
        //restartJobScheduler(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            restartJobScheduler(context)
//        else
            restartService(context)
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
    
    private fun restartJobScheduler(context: Context) {
        Log.e("MyBroadCastReceiver", "onReceive")
//        context.startForegroundService(service);
//        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
//        val myJob: Job = dispatcher.newJobBuilder()
//            .setService(MyJobService::class.java)
//            .setTag("myFCMJob")
//            .build()
//        dispatcher.mustSchedule(myJob)
    }
    
    private fun restartService(context: Context) {
        val restartServiceIntent = Intent(context, AlarmService::class.java)
        context.startService(restartServiceIntent)
    }
    
}
