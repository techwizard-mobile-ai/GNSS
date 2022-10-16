package app.vsptracker.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.activities.truck.THomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
  
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    
    
    val NOTIFICATION_ID = 234
    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentTitle(remoteMessage.notification!!.title)
      .setAutoCancel(true)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setContentText(remoteMessage.notification!!.body)
    val resultIntent = Intent(this, OperatorLoginActivity::class.java)
    val stackBuilder = TaskStackBuilder.create(this)
    stackBuilder.addParentStack(THomeActivity::class.java)
    stackBuilder.addNextIntent(resultIntent)
    val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    builder.setContentIntent(resultPendingIntent)
    builder.setOngoing(true)
    notificationManager.notify(NOTIFICATION_ID, builder.build())
    
    
  }
  
  override fun onNewToken(newToken: String) {
    super.onNewToken(newToken)
    val myHelper = MyHelper(TAG, this)
    myHelper.log("myHelper__newToken:$newToken")
    myHelper.setFcmToken(newToken)
  }
  
  companion object {
    private val TAG = "MyFirebaseMessagingService"
  }
}
