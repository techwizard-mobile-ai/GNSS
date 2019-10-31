package app.vsptracker.others

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import app.vsptracker.R
import app.vsptracker.activities.OperatorLoginActivity


/**
 * Implementation of App Widget functionality.
 */
class VSPTWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
//    val widgetText = context.getString(R.string.app_name)
//    val views = RemoteViews(context.packageName, R.layout.vsptwidget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
//    appWidgetManager.updateAppWidget(appWidgetId, views)

    val intent = Intent(context, OperatorLoginActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val remoteViews = RemoteViews(context.packageName, R.layout.vsptwidget)

    remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)


}