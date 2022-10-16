package app.vsptracker.others

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lysaan.malikab.addgroceries.others.GoogleMarket

class AppRater {
  
  private val market = GoogleMarket()
  
  fun rateNow(context: Context) {
    try {
      //            Uri.parse("market://details?id=" + appId;
      val intent = Intent(Intent.ACTION_VIEW, market.getMarketURI(context))
      //            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="));
      context.startActivity(intent)
    }
    catch (activityNotFoundException1: ActivityNotFoundException) {
      Log.e(AppRater::class.java.simpleName, "Market Intent not found")
    }
    
  }
}