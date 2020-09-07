package app.vsptracker.others.autologout

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.vsptracker.others.MyHelper

class AutoLogoutWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    val tag = "AutoLogoutWorker"
    val myHelper = MyHelper(tag, ctx)
    override fun doWork(): Result {
        Log.e(tag, "App_Check:doWork")
        return try {
            myHelper.log("App_Check:${myHelper.getOperatorAPI().id }")
            myHelper.clearLoginData()
            
//              Launch the app to Auto Logout from app
//            val intent = Intent(appContext, OperatorLoginActivity::class.java)
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("isAutoLogoutCall", false)
//            appContext.startActivity(intent)
            Result.success()
        }
        catch (e: Exception) {
            Log.e(tag, "Auto Logout Failure ${e.localizedMessage}")
            Result.failure()
        }
    }

}
