package app.vsptracker.others.autologout

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper

class AutoLogoutWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  val tag = "AutoLogoutWorker"
  val myHelper = MyHelper(tag, context)
  val myDataPushSave = MyDataPushSave(context)
  override fun doWork(): Result {
    myHelper.log("App_Check:doWork")
    return try {
      //get Input Data back using "inputData" variable
      val workerType = inputData.getInt("type", 0)
      myHelper.log("workerType:$workerType")
      when (workerType) {
        MyEnum.WORKER_TYPE_AUTO_LOGOUT -> myDataPushSave.logout(MyEnum.LOGOUT_TYPE_AUTO)
//                MyEnum.WORKER_TYPE_APP_LOCKS -> myHelper.lockOtherApps()
      }


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
