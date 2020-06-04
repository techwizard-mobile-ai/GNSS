package app.vsptracker.aws

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import java.io.File


class MyService : Service() {
    private var transferUtility: TransferUtility? = null
    override fun onCreate() {
        super.onCreate()
        val util = Util()
        transferUtility = util.getTransferUtility(this)
    }
    
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val key = intent.getStringExtra(INTENT_KEY_NAME)
        val file = intent.getSerializableExtra(INTENT_FILE) as File
        val transferOperation = intent.getStringExtra(INTENT_TRANSFER_OPERATION)
        val transferObserver: TransferObserver
        when (transferOperation) {
            TRANSFER_OPERATION_DOWNLOAD -> {
                Log.d(TAG, "Downloading $key")
                transferObserver = transferUtility!!.download(key, file)
                transferObserver.setTransferListener(DownloadListener())
            }
            TRANSFER_OPERATION_UPLOAD -> {
                Log.d(TAG, "Uploading $key")
                transferObserver = transferUtility!!.upload(key, file)
                transferObserver.setTransferListener(UploadListener())
            }
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    
    private inner class DownloadListener : TransferListener {
        private var notifyDownloadActivityNeeded = true
        
        // Simply updates the list when notified.
        override fun onError(id: Int, e: Exception) {
            Log.e(TAG, "onError: $id", e)
            if (notifyDownloadActivityNeeded) {
//                DownloadActivity.initData()
                notifyDownloadActivityNeeded = false
            }
        }
        
        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Log.d(
                TAG, String.format(
                    "onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent
                )
            )
            if (notifyDownloadActivityNeeded) {
//                DownloadActivity.initData()
                notifyDownloadActivityNeeded = false
            }
        }
        
        override fun onStateChanged(id: Int, state: TransferState) {
            Log.d(TAG, "onStateChanged: $id, $state")
            if (notifyDownloadActivityNeeded) {
//                DownloadActivity.initData()
                notifyDownloadActivityNeeded = false
            }
        }
    }
    
    private inner class UploadListener : TransferListener {
        private var notifyUploadActivityNeeded = true
        
        // Simply updates the list when notified.
        override fun onError(id: Int, e: Exception) {
            Log.e(TAG, "onError: $id", e)
            if (notifyUploadActivityNeeded) {
//                UploadActivity.initData()
                notifyUploadActivityNeeded = false
            }
        }
        
        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Log.d(
                TAG, String.format(
                    "onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent
                )
            )
            if (notifyUploadActivityNeeded) {
//                UploadActivity.initData()
                notifyUploadActivityNeeded = false
            }
        }
        
        override fun onStateChanged(id: Int, state: TransferState) {
            Log.d(TAG, "onStateChanged: $id, $state")
            if (notifyUploadActivityNeeded) {
//                UploadActivity.initData()
                notifyUploadActivityNeeded = false
            }
        }
    }
    
    companion object {
        const val INTENT_KEY_NAME = "key"
        const val INTENT_FILE = "file"
        const val INTENT_TRANSFER_OPERATION = "transferOperation"
        const val TRANSFER_OPERATION_UPLOAD = "upload"
        const val TRANSFER_OPERATION_DOWNLOAD = "download"
        private val TAG = MyService::class.java.simpleName
    }
}
