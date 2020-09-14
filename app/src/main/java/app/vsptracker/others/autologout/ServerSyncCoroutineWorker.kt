package app.vsptracker.others.autologout

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.vsptracker.others.MyDataPushSave
import app.vsptracker.others.MyHelper

class ServerSyncCoroutineWorker(context: Context, params: WorkerParameters) :
        CoroutineWorker(context, params) {
    val tag = "ServerSyncCoroutineWorker"
    val myHelper = MyHelper(tag, context)
    val myDataPushSave = MyDataPushSave(context)
    override suspend fun doWork(): Result {
        val type = inputData.getInt("type", -1)
        myHelper.log("ServerSyncCoroutineWorker__Type:$type")
        
        return try {
            myDataPushSave.checkUpdateServerSyncDataCoroutine(type)
            Result.success()
        }
        catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
