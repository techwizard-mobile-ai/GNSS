package app.vsptracker.others

//import android.app.ProgressDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.vsptracker.BuildConfig
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.LoginActivity
import app.vsptracker.activities.Map1Activity
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.activities.common.RLoadActivity
import app.vsptracker.activities.common.RUnloadActivity
import app.vsptracker.activities.excavator.EHistoryActivity
import app.vsptracker.activities.excavator.EHomeActivity
import app.vsptracker.activities.scrapper.SHistoryActivity
import app.vsptracker.activities.scrapper.SHomeActivity
import app.vsptracker.activities.scrapper.SUnloadAfterActivity
import app.vsptracker.activities.truck.THistoryActivity
import app.vsptracker.activities.truck.THomeActivity
import app.vsptracker.activities.truck.TUnloadAfterActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.login.AppAPI
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.serverSync.ServerSyncDataAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.aws.MyService
import app.vsptracker.aws.Util
import app.vsptracker.classes.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.dialog_error.view.*
import kotlinx.android.synthetic.main.dialog_permissions.view.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong

@SuppressLint("SimpleDateFormat")
class MyHelper(var TAG: String, val context: Context) {
    @Suppress("DEPRECATION")
    private var dialog: ProgressDialog? = null
    private var progressBar: ProgressBar? = null
    private var sessionManager: SessionManager = SessionManager(context)
    private val gson = Gson()
    private lateinit var retrofit: Retrofit
    private lateinit var retrofitAPI: RetrofitAPI
    
    // AWS Upload variables
    var util: Util = Util()
    var transferUtility: TransferUtility? = util.getTransferUtility(context)
    
    fun getOldMachineStatus() = sessionManager.getOldMachineStatus()
    fun setOldMachineStatus(myData: MyData) {
        sessionManager.setOldMachineStatus(myData)
    }
    
    /**
     * Returns the filename for the given Uri
     * @param uri the Uri
     * @return String representing the file name (DISPLAY_NAME)
     */
    private fun getDisplayName(uri: Uri): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        // If the display name is not found for any reason, use the Uri path as a fallback.
        log("Couldnt determine DISPLAY_NAME for Uri.  Falling back to Uri path: " + uri.path)
        return uri.path.toString()
    }
    
    /**
     * Copies the resource associated with the Uri to a new File in the cache directory, and returns the File
     * @param uri the Uri
     * @return a copy of the Uri's content as a File in the cache directory
     * @throws IOException if openInputStream fails or writing to the OutputStream fails
     */
    @Throws(IOException::class)
    fun readContentToFile(uri: Uri): File {
        val file = File(context.cacheDir, this.getDisplayName(uri))
        context.contentResolver.openInputStream(uri).use { `in` ->
            FileOutputStream(file, false).use { out ->
                val buffer = ByteArray(1024)
                run {
                    var len: Int
                    while (`in`!!.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                }
                return file
            }
        }
    }
    
    /*
     * Begins to upload the file specified by the file path.
     */
    fun awsFileUpload(path: String, file: File) {
//        TransferObserver observer = transferUtility.upload("checkforms/"+file.getName(),file);
        
        val observer: TransferObserver = transferUtility!!.upload(path + file.name, file)
        log("awsFileUpload:$observer")
    }
    
    fun awsFileDownload(currentOrgsMap: MyData?) {
        log("awsFileDownload:${currentOrgsMap?.aws_path}")
        if (currentOrgsMap !== null)
            if (!currentOrgsMap.aws_path.isNullOrEmpty() && currentOrgsMap.isDownloaded == 0) {
                if (isOnline()) {
                    toast(context.getString(R.string.downloading_site_map))
                    log("startDownload:$currentOrgsMap")
                    val key = currentOrgsMap.aws_path
                    val file = File(getKMLFileName(currentOrgsMap.aws_path))
                    val context: Context = context.applicationContext
                    val intent = Intent(context, MyService::class.java)
                    intent.putExtra(MyService.INTENT_KEY_NAME, key)
                    intent.putExtra("currentOrgsMap", currentOrgsMap)
                    intent.putExtra(MyService.INTENT_TRANSFER_OPERATION, MyService.TRANSFER_OPERATION_DOWNLOAD)
                    intent.putExtra(MyService.INTENT_FILE, file)
                    context.startService(intent)
                } else {
                    toast(context.getString(R.string.map_not_updated))
                }
            }
    }
    
    fun getFcmToken(): String = sessionManager.getFcmToken()
    
    fun setFcmToken(fcmToken: String) {
        sessionManager.setFcmToken(fcmToken)
    }
    
    fun getAutoLogoutStartTime() = sessionManager.getAutoLogoutStartTime()
    fun setAutoLogoutStartTime(autoLogoutStartTime: Long) {
        sessionManager.setAutoLogoutStartTime(autoLogoutStartTime)
    }
    
    /**
     * Check if Meter Reading isEmpty OR is just a decimal return 0 else return value
     * Without this it was causing NumberFormatException when trying to convert into float OR Double
     */
    fun getMeterValidValue(meterReading: String): String {
        return if (meterReading.isNotEmpty() && !meterReading.equals(".", true)) {
            meterReading
        } else "0.0"
    }
    
    fun isNavEnabled() = sessionManager.getNav()
    fun setIsNavEnabled(status: Boolean) = sessionManager.setNav(status)
    
    fun getMachineSettings() = sessionManager.getMachineSettings()
    fun setMachineSettings(material: Material) {
        sessionManager.setMachineSettings(material)
    }
    
    fun getMachineID() = sessionManager.getMachineID()
    fun setMachineID(id: Int) {
        sessionManager.setMachineID(id)
    }
    
    fun getOperatorAPI() = sessionManager.getOperatorAPI()
    fun setOperatorAPI(loginAPI: MyData) {
        sessionManager.setOperatorAPI(loginAPI)
    }
    
    fun getLoginAPI() = sessionManager.getLoginAPI()
    fun setLoginAPI(loginAPI: LoginAPI) {
        sessionManager.setLoginAPI(loginAPI)
    }
    
    fun refreshToken() {
        log("inside refreshToken")
        val client = skipSSLOkHttpClient().build()
        val formBody = FormBody.Builder()
            .add("email", getLoginAPI().email)
            .add("password", getLoginAPI().pass)
            .add("role", MyEnum.ROLE_OPERATOR)
            .add("ttl", MyEnum.TTL)
            .build()
        val request = Request.Builder()
            .url("${context.getString(R.string.api_url)}org/users/login1")
            .post(formBody)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
//                log("request:${response.request}")
//                log("handshake:${response.handshake}")
//                log("protocol:${response.protocol}")
//                log("networkResponse:${response.networkResponse}")
//                log("isRedirect:${response.isRedirect}")
//                log("headers:${response.headers}")
                
                val responseString = response.body!!.string()
                try {
                    val responseJObject = JSONObject(responseString)
                    log("RefreshTokenResponse:$responseString")
                    val success = responseJObject.getBoolean("success")
                    if (success) {
                        val gson = GsonBuilder().create()
                        val loginAPI = gson.fromJson(responseJObject.getString("data"), LoginAPI::class.java)
                        loginAPI.pass = getLoginAPI().pass
                        
                        val app = gson.fromJson(responseJObject.getString("app"), AppAPI::class.java)
                        log("app:$app")
                        setLatestVSPTVersion(app)
                        val appVersionCode = BuildConfig.VERSION_CODE
                        @Suppress("ConstantConditionIf")
                        if (app.version_code > appVersionCode && app.is_critical > 0) {
                            log("Update App")
                            val appRater = AppRater()
                            appRater.rateNow(context)
                        } else {
                            setLoginAPI(loginAPI)
                        }
                    } else {
                        toast(responseJObject.getString("message"))
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    }
                }
                catch (e: Exception) {
                    
                    toast("refreshTokenException:" + e.localizedMessage)
                    log("refreshTokenException:" + e.localizedMessage)
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
                
            }
            
            override fun onFailure(call: Call, e: IOException) {
                toast("Failed refresh token: ${e.printStackTrace()}")
                log("Failed refresh token: ${e.printStackTrace()}")
                // Failure could be due to Network, so no need to redirect user to Company Login Screen.
//                val intent = Intent(context, LoginActivity::class.java)
//                context.startActivity(intent)
            }
        })
    }
    
    fun getLatestVSPTVersion(): AppAPI = sessionManager.getLatestVSPTVersion()
    
    fun setLatestVSPTVersion(appAPI: AppAPI) {
        sessionManager.setLatestVSPTVersion(appAPI)
    }
    
    fun getServerSyncDataAPIString(serverSyncList: ArrayList<ServerSyncAPI>): String {
        val serverSyncDataAPI = ServerSyncDataAPI()
        serverSyncDataAPI.data = serverSyncList
        return gson.toJson(serverSyncDataAPI, ServerSyncDataAPI::class.java)
    }
    
    fun getDeviceDetailsString(): String {
        val deviceDetails = DeviceDetails()
        deviceDetails.fcmToken = getFcmToken()
        return gson.toJson(deviceDetails, DeviceDetails::class.java)
    }
    
    fun getIsMapOpened() = sessionManager.getLastJourney().isMapOpened
    fun setIsMapOpened(isMapOpened: Boolean) {
        val lastJourney = sessionManager.getLastJourney()
        lastJourney.isMapOpened = isMapOpened
        sessionManager.setLastJourney(lastJourney)
    }
    
    fun getUserID() = getOperatorAPI().id
    fun getStringToGPSLocation(stringGPSLocation: String?): GPSLocation {
//        log("getStringToGPSLocation:$stringGPSLocation")
        return if (stringGPSLocation == null)
            GPSLocation()
        else gson.fromJson(stringGPSLocation, GPSLocation::class.java)
        
        
    }
    
    fun getGPSLocationToString(gpsMaterial: GPSLocation): String {
        return gson.toJson(gpsMaterial)
    }
    
    fun showOnMap(gpsLocation: GPSLocation, title: String) {
//        val lat = gpsLocation.latitude
//        val longg = gpsLocation.longitude
//        val geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + longg + " ($title)";
//        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
//        mapIntent.setPackage("com.google.android.apps.maps")
//        context.startActivity(mapIntent)
        
        
        setIsMapOpened(true)
        val intent = Intent(context, Map1Activity::class.java)
        gpsLocation.locationName = title
        intent.putExtra("gpsLocation", gpsLocation)
        context.startActivity(intent)
        
    }
    
    // nextAction 0 = Do Loading
    // nextAction 1 = Do Unloading
    // nextAction 2 = Do Back Loading
    // nextAction 3 = Do Back Unloading
    fun setNextAction(nextAction: Int) {
        val data = getLastJourney()
        data.nextAction = nextAction
        setLastJourney(data)
    }
    
    fun getNextAction() = getLastJourney().nextAction
    fun setToDoLayout(view: com.google.android.material.floatingactionbutton.FloatingActionButton) {
        val width = context.resources.getDimensionPixelSize(R.dimen._120sdp)
        val height = context.resources.getDimensionPixelSize(R.dimen._120sdp)
        val layoutParams = FrameLayout.LayoutParams(width, height)
        @Suppress("USELESS_CAST")
        view.layoutParams = layoutParams as ViewGroup.LayoutParams?
    }
    
    fun getWorkMode(): String {
        return if (isDailyModeStarted()) {
            "Day Works"
        } else {
            "Standard Mode"
        }
    }
    
    fun stopDelay(gpsMaterial: GPSLocation) {
        val meter = getMeter()
        meter.isDelayStarted = false
        meter.delayStopTime = System.currentTimeMillis()
        meter.delayTotalTime = meter.delayStopTime - meter.delayStartTime
        meter.delayStopGPSLocation = gpsMaterial
        setMeter(meter)
    }
    
    fun stopDailyMode() {
        val currentTime = System.currentTimeMillis()
        if (isDailyModeStarted()) {
            val meter = getMeter()
            meter.isDailyModeStarted = false
            val startTime = meter.dailyModeStartTime
            val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime
            meter.dailyModeTotalTime = 0L
            setMeter(meter)
            toast("Day Works Stopped.\nStart Time: ${getTime(getMeter().dailyModeStartTime)}.\nTotal Time: ${getMinutesFromMillisec(totalTime)}")
        }
        
    }
    
    fun startDelay(gpsMaterial: GPSLocation) {
        val currentTime = System.currentTimeMillis()
        if (!isDelayStarted()) {
            val meter = getMeter()
            meter.isDelayStarted = true
            meter.delayStartTime = currentTime
            meter.delayStartGPSLocation = gpsMaterial
//            toast("Waiting Started.")
            setMeter(meter)
        } else {
            toast("Waiting is already Started.")
        }
    }
    
    fun startDailyMode() {
        
        val currentTime = System.currentTimeMillis()
        if (!isDailyModeStarted()) {
            val meter = getMeter()
            meter.isDailyModeStarted = true
            meter.dailyModeStartTime = currentTime
            toast("Day Works Started.")
            setMeter(meter)
        } else {
            val meter = getMeter()
            val startTime = meter.dailyModeStartTime
            val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime
            toast(
                "Day Works Already Started." +
                        "\nStart Time: ${getTime(getMeter().dailyModeStartTime)}." +
                        "\nTotal Time: ${getMinutesFromMillisec(totalTime)}"
            )
        }
        
    }
    
    fun isDelayStarted() = sessionManager.getMeter().isDelayStarted
    fun isDailyModeStarted() = sessionManager.getMeter().isDailyModeStarted
    fun showStopMessage(startTime: Long) {
        toast(
            "Please Stop Work First.\n" +
                    "Work Duration : ${getTotalTimeVSP(startTime)} (VSP Meter).\n" +
                    "Work Duration : ${getTotalTimeMinutes(startTime)} (Minutes)"
        )
    }
    
    fun getFormattedTime(millis: Long): String {
        
        return String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }
    
    internal fun getMinutesFromMillisec(totalTime: Long): Long {
        return (totalTime / 1000 / 60)
    }
    
    fun getTotalTimeVSP(startTime: Long): String {
        val minutes = getTotalTimeMinutes(startTime)
        return getRoundedDecimal(minutes / 60.0).toString()
    }
    
    fun getTotalTimeMinutes(startTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        val oNTime = currentTime - startTime
        val minutes = (oNTime / 1000 / 60)
        log("StartTime: $startTime, CurrentTime:$currentTime, TotalMinutes: $minutes")
        return minutes
    }
    
    /**
     * This method will get formatted date and it will return unix timestamp.
     * e.g date: 2020-07-24 22:45:42 and it will return timestamp: 1595612742000
     */
    fun getTimestampFromDate(date: String): Long {
        var timestamp: Long = 0L
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val parsedDate = dateFormat.parse(date)
            timestamp = parsedDate.time
        }
        catch (e: Exception) {
            log("getTimestampFromData:${e.localizedMessage}")
        }
        return timestamp
    }
    
    fun getDateTimeWithSeconds(s: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
            val netDate = Date(s)
            sdf.format(netDate)
        }
        catch (e: Exception) {
            log("getDatetime:${e}")
            s.toString()
        }
    }
    
    fun getDateTime(s: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
            val netDate = Date(s)
            sdf.format(netDate)
        }
        catch (e: Exception) {
            log("getDatetime:${e}")
            s.toString()
        }
    }
    
    fun getTime(s: Long): String {
        
        return if (s > 0) {
            try {
                //            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
                val sdf = SimpleDateFormat("HH:mm")
                val netDate = Date(s)
                sdf.format(netDate)
            }
            catch (e: Exception) {
                log("getDatetime:${e}")
                s.toString()
            }
        } else {
            ""
        }
        
    }
    
    fun getDate(s: String): String {
        return try {
            //            val sdf = SimpleDateFormat("MM/dd/yy")
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        }
        catch (e: Exception) {
            log("getDateString:${e}")
            s
        }
    }
    
    fun getDate(s: Long): String {
        return try {
            //            val sdf = SimpleDateFormat("MM/dd/yy")
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val netDate = Date(s)
            sdf.format(netDate)
        }
        catch (e: Exception) {
            log("getDate:${e}")
            s.toString()
        }
    }
    
    fun getLastJourney() = sessionManager.getLastJourney()
    fun setLastJourney(myData: MyData) {
        sessionManager.setLastJourney(myData)
    }
    
    fun getRoundedDecimal(minutes: Double): Double {
        val number3digits: Double = (minutes * 1000.0).roundToLong() / 1000.0
        val number2digits: Double = (number3digits * 100.0).roundToLong() / 100.0
        return (number2digits * 10.0).roundToLong() / 10.0
    }
    
    fun getRoundedInt(minutes: Double): Long {
        return getRoundedDecimal(minutes).roundToLong()
    }
    
    fun getMeter() = sessionManager.getMeter()
    fun setMeter(meter: Meter) {
        sessionManager.setMeter(meter)
    }
    
    fun getMeterTimeForFinish(): String {
        val meterONTime = getMachineTotalTime() + getMachineStartTime()
        return getRoundedDecimal(meterONTime / 60.0).toString()
    }
    
    fun getMeterTimeForFinishCustom(startHours2: String): String {
        val startHours1: String = when {
            startHours2.isEmpty() -> "0"
            else -> startHours2
        }
        val startHours = startHours1.toDouble()
        val startMinutes = startHours * 60
        val meterONTime = startMinutes + getMachineStartTime()
        return getRoundedDecimal(meterONTime / 60.0).toString()
    }
    
    fun getMeterTimeForStart(): String {
        return getRoundedDecimal(getMachineTotalTime() / 60.0).toString()
    }
    
    private fun getMachineTotalTime() = sessionManager.getMeter().machineTotalTime
    fun setMachineTotalTime(time: Long) {
        val meter = sessionManager.getMeter()
        meter.machineTotalTime = time
        sessionManager.setMeter(meter)
    }
    
    internal fun getMachineStartTime(): Long {
        val meter = sessionManager.getMeter()
        return if (meter.isMachineStopped) {
            0
        } else {
            val startTime = meter.machineStartTime
            val currentTime = System.currentTimeMillis()
            val onTime = currentTime - startTime
            val minutes = (onTime / 1000 / 60)
            minutes
        }
        
    }
    
    /**
     * Starting Calculating this time and When Machine will be started this time will be stopped.
     * This will be total Time of Machine Stopped. This time will be pushed to Database and Portal
     * on Start of Machine.
     * This method will do Following Actions.
     * 1. Save Machine Hours.
     * 2. Push Machine Hours.
     * 3. Push Machine Stops.
     * 4. Save Machine Stops.
     * 5. Push Machine Status and Save Machine Stop ID.
     */
    fun stopMachine(insertID: Long, material: Material, resetJourney: Boolean) {
        setIsMachineStopped(true, material.name, material.id)
        
        val meter = sessionManager.getMeter()
        val meterONTime = getMachineTotalTime() + getMachineStartTime()
        meter.machineTotalTime = meterONTime
        meter.isMachineStopped = true
        meter.machineDbID = insertID
        
        sessionManager.setMeter(meter)
        stopDailyMode()
//        If Machine is Breakdown Reset Journey otherwise Don't Reset Journey
        if (resetJourney) {
            val data = MyData()
            setLastJourney(data)
        }
        
    }
    
    fun startMachine() {
        val currentTime = System.currentTimeMillis()
        val meter = sessionManager.getMeter()
        meter.machineStartTime = currentTime
        meter.isMachineStopped = false
        meter.machineDbID = 0
        sessionManager.setMeter(meter)
    }
    
    fun isNightMode() = sessionManager.isNightMode()
    fun setNightMode(mode: Boolean) {
        sessionManager.setNightMode(mode)
    }
    
    fun getMachineStoppedReasonID() = sessionManager.getMachineStoppedReasonID()
    fun getIsMachineStopped() = sessionManager.getIsMachineStopped()
    fun setIsMachineStopped(status: Boolean, reason: String, id: Int) {
        sessionManager.setMachineStopped(status, reason, id)
    }
    
    fun getMachineStoppedReason() = sessionManager.getMachineStoppedReason()
    fun getMachineNumber() = sessionManager.getMachineNumber()
    fun setMachineNumber(number: String) {
        sessionManager.setMachineNumber(number)
    }
    
    //    machineTypeId = 1 excavator
    //    machineTypeId = 2 scrapper
    //    machineTypeId = 3 truck
    fun getMachineTypeID() = sessionManager.getMachineTypeID()
    
    fun setMachineTypeID(type: Int) {
        sessionManager.setMachineTypeID(type)
    }
    
    fun isOnline(): Boolean {
        return this.isNetworkAvailable()!!
    }
    
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
    
    fun hideKeyboardOnClick(view: View) {
        view.setOnTouchListener { v, _ ->
            v.performClick()
            hideKeyboard(view)
            false
        }
    }
    
    fun hideKeyboard(view: View) {
        view.requestFocus()
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
    
    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    
    fun showProgressBar() {
        if (progressBar!!.visibility == View.GONE && progressBar != null)
            progressBar!!.visibility = View.VISIBLE
    }
    
    @Suppress("SENSELESS_COMPARISON")
    fun hideProgressBar() {
        try {
            if (progressBar!!.visibility == View.VISIBLE)
                progressBar!!.visibility = View.GONE
        }
        catch (e: java.lang.Exception) {
            log("hideProgressBarExp:${e.message}")
        }
        
        
    }
    
    fun toast(message: String) {
        try {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
//            val v = toast.view.findViewById(android.R.id.message) as TextView
//            v.gravity = Gravity.CENTER
            toast.show()
        }
        catch (e: Exception) {
            log("toastException:${e.message}")
        }
        
    }
    
    fun log(message: String) {
//        if(message == "onFinish" || message == "cancelTimer Done" || message == "cancelTimer" || message == "onFinish--isTimeOver:false" || message == "onFinish--isTimeOver:true")
        Log.e(TAG, message)
    }
    
    fun isValidEmail(target: String): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
    
    fun setTag(tag: String) {
        this.TAG = tag
    }
    
    fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar!!
    }
    
    fun startHistoryByType() {
        when (getMachineTypeID()) {
            MyEnum.EXCAVATOR -> {
                val intent = Intent(context, EHistoryActivity::class.java)
                context.startActivity(intent)
            }
            MyEnum.SCRAPER -> {
                val intent = Intent(context, SHistoryActivity::class.java)
                context.startActivity(intent)
            }
            MyEnum.TRUCK -> {
                val intent = Intent(context, THistoryActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    
    //    machineTypeId = 1 excavator
    //    machineTypeId = 2 scrapper
    //    machineTypeId = 3 truck
    fun startHomeActivityByType(myData: MyData) {
        
        val lastJourney = getLastJourney()
        log("lastJourney:$lastJourney")
        // on start of Application, set isMapOpened to false, this is to reset map flag
        setIsMapOpened(false)
        
        when (getMachineTypeID()) {
            MyEnum.EXCAVATOR -> {
                val intent = Intent(context, EHomeActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            
            // Last Journey was saved and Machine was Stopped, Now Machine is Started so Last Journey should be continued
            // nextAction 0 = Do Loading
            // nextAction 1 = Do Unloading
            // nextAction 2 = Do Back Loading
            // nextAction 3 = Do Back Unloading
            
            //    repeatJourney 0 = No Repeat Journey
            //    repeatJourney 1 = Repeat Journey without Back Load
            //    repeatJourney 2 = Repeat Journey with Back Load
            
            MyEnum.SCRAPER -> {
                val intent: Intent = if (lastJourney.repeatJourney > 0 && (lastJourney.nextAction == 0 || lastJourney.nextAction == 2)) {
                    // Launch Load Screen
                    Intent(context, RLoadActivity::class.java)
                } else if (lastJourney.repeatJourney > 0 && (lastJourney.nextAction == 1 || lastJourney.nextAction == 3)) {
                    // Launch Unload Screen
                    Intent(context, RUnloadActivity::class.java)
                } else {
                    // No settings so Start Home Activity
                    Intent(context, SHomeActivity::class.java)
                }
                context.startActivity(intent)
            }
            MyEnum.TRUCK -> {
//                val intent = Intent(context, THomeActivity::class.java)
                val intent: Intent = if (lastJourney.repeatJourney > 0 && (lastJourney.nextAction == 0 || lastJourney.nextAction == 2)) {
                    // Launch Load Screen
                    Intent(context, RLoadActivity::class.java)
                } else if (lastJourney.repeatJourney > 0 && (lastJourney.nextAction == 1 || lastJourney.nextAction == 3)) {
                    // Launch Unload Screen
                    Intent(context, RUnloadActivity::class.java)
                } else {
                    // No settings so Start Home Activity
                    Intent(context, THomeActivity::class.java)
                }
                context.startActivity(intent)
                
            }
        }
    }
    
    fun startLoadAfterActivityByType(myData: MyData) {
        
        when (getMachineTypeID()) {
            MyEnum.EXCAVATOR -> {
                val intent = Intent(context, EHomeActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            MyEnum.SCRAPER -> {
                val intent = Intent(context, SUnloadAfterActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            MyEnum.TRUCK -> {
                val intent = Intent(context, TUnloadAfterActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
                
            }
        }
    }
    
    fun logout(activity: Activity) {
        val intent = Intent(activity, HourMeterStopActivity::class.java)
        activity.startActivity(intent)
    }
    
    fun hideDialog() {
        if (dialog!!.isShowing)
            dialog!!.dismiss()
    }
    
    @Suppress("DEPRECATION")
    fun showDialog(message: String = context.getString(R.string.loading_data_message)) {
        try {
            dialog = ProgressDialog.show(
                context, context.getString(R.string.app_name), message, true, false
            )
        }
        catch (exception: Exception) {
            log("showDialogException:$exception")
        }
    }
    
    fun printInsertion(tableName: String, insertedID: Long, datum: OperatorAPI) {
        log("$tableName--$insertedID--$datum")
    }
    
    fun printInsertion(tableName: String, insertedID: Long, datum: MyData) {
        log("$tableName--$insertedID--$datum")
    }
    
    fun getQuestionsIDsList(questionsData: String?): List<Int> {
        var questionsIDs = ArrayList<Int>()
        
        // If there is not questionsData String then it will contain an empty array []
        // So there are two brackets which count length of 2.
        if (questionsData!!.length > 2) {
            questionsIDs = questionsData.removeSurrounding("[", "]").split(",").map { it.toInt() } as ArrayList<Int>
        }
        return questionsIDs
    }
    
    fun toCommaSeparatedString(questionIDS: String?): String {
        val list = getQuestionsIDsList(questionIDS)
        return if (list.isNotEmpty()) {
            val nameBuilder = StringBuilder()
            for (item in list) {
                nameBuilder.append(item).append(", ")
            }
            nameBuilder.deleteCharAt(nameBuilder.length - 1)
            nameBuilder.deleteCharAt(nameBuilder.length - 1)
            nameBuilder.toString()
        } else {
            ""
        }
    }
    
    fun printInsertion(tableName: String, insertedID: Long, datum: CheckFormData) {
        log("$tableName--$insertedID--$datum")
    }
    
    fun getStringToAnswerData(answerData: String?): AnswerData {
//        log("getStringToGPSLocation:$stringGPSLocation")
        return if (answerData == null)
            AnswerData()
        else gson.fromJson(answerData, AnswerData::class.java)
        
        
    }
    
    fun getAnswerDataToString(answerData: AnswerData): String {
        return gson.toJson(answerData)
    }
    
    fun imageLoadFromURL(url: String, imageView: ImageView, myContext: Context) {

//        log("imageLoadFromURL:$url")
        if (!url.isBlank()) {
            
            Glide.with(myContext)
                .load(url)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
//                                myHelper.hideDialog()
                        return false
                    }
                    
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
//                                myHelper.hideDialog()
                        return false
                    }
                    
                })
                .into(imageView)
        } else {
//                    myHelper.hideDialog()
            log("else$url")
            imageView.setImageResource(R.drawable.user_img)
        }
        
    }
    
    fun imageLoad(drawableID: Int, imageView: ImageView) {
        try {
            Glide.with(context).load(drawableID).into(imageView)
        }
        catch (exception: Exception) {
            toast("$exception")
        }
    }
    
    fun imageLoad(filePath: Uri?, imageView: ImageView) {
        try {
            Glide.with(context).load(filePath).into(imageView)
        }
        catch (exception: Exception) {
            toast("$exception")
        }
    }
    
    fun imageLoad(bitmap: Bitmap?, imageView: ImageView) {
        try {
            Glide.with(context).load(bitmap).into(imageView)
//            toast("Image Attached Successfully.")
        }
        catch (exception: Exception) {
            toast("$exception")
        }
    }
    
    /**
     * Attached selected Image to Image Layout
     * When Long Clicked on Image, Remove that Image from Images List and Image Layout.
     * If isCompletedCheckFormsDetails = true, then this method is called for showing images
     * of completed checkforms. No need to remove image on Long Clicked
     */
    fun addImageToPhotoLayout(
        context: Context,
        imageBitmap: Bitmap?,
        imageURI: Uri?,
        imagesList: ArrayList<Images>,
        isCompletedCheckFormsDetails: Boolean = false
    ): LinearLayout? {
        val linearLayout = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        layoutParams.setMargins(context.resources.getDimensionPixelSize(R.dimen._10sdp), 0, context.resources.getDimensionPixelSize(R.dimen._10sdp), 0)
        linearLayout.layoutParams = layoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        
        val imageView = ImageView(context)
        val imageViewParam =
            LinearLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen._130sdp), context.resources.getDimensionPixelSize(R.dimen._130sdp))
        imageViewParam.gravity = Gravity.CENTER
        
        imageView.layoutParams = imageViewParam
        imageView.contentDescription = context.resources.getString(R.string.image_showing_issue)
        
        val imagePath = Images("", "");
        if (imageBitmap != null) {
            imageLoad(imageBitmap, imageView)
            imagePath.localImagePath = imageBitmap.toString()
        } else {
            imageLoad(imageURI, imageView)
            imagePath.localImagePath = imageURI.toString()
        }
        
        linearLayout.addView(imageView, 0)
        
        // If this method is used for Viewing images of Completed CheckForms then there is no need to Remove Image
        if (!isCompletedCheckFormsDetails)
            imageView.setOnLongClickListener {
                
                var position = -1
                for (i in 0 until imagesList.size) {
                    if (imagesList[i].localImagePath.equals(imagePath.localImagePath, true)) {
                        position = i
                    }
                    
                }
                // Remove Image from Image List
                if (position != -1)
                    imagesList.removeAt(position)
                // Remove Image from Image Layout
                linearLayout.removeView(imageView)
                toast("Image removed.")
                return@setOnLongClickListener true
            }
        return linearLayout
    }
    
    fun getFileName(checkform_id: Int, selectedQuestionID: Int): String {
        
        val currentTime = System.currentTimeMillis()
        return "${getLoginAPI().org_id}_${getMachineSettings().siteId}_${getOperatorAPI().id}_${checkform_id}_${selectedQuestionID}_${currentTime}"
    }
    
    fun getAWSFilePath(type: String): String {
        val calendar = Calendar.getInstance()
        
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get((Calendar.MONTH)) + 1 // due to 0 based indexing we need to add 1 to get accurate month number
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        
        val path =
            "${getLoginAPI().org_id}/$type/$currentYear/$currentMonth/$currentDay/${getMachineSettings().siteId}/${getMachineTypeID()}/${getMachineID()}/${getOperatorAPI().id}/"
        
        log("path:$path")
        return path
    }
    
    fun showErrorDialog(title: String, explanation: String = "") {
        
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_error, null)
        mDialogView.error_title.text = title
        if (explanation.isNotBlank()) {
            mDialogView.error_explanation.text = explanation
            mDialogView.error_explanation.visibility = View.VISIBLE
        }
        
        
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(true)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        
        mDialogView.error_ok.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
    
    /**
     * Show this dialog whenever user disable location from Settings even Location Permission is enabled.
     */
    fun showGPSDisabledAlertToUser() {
        try {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_permissions, null)
            
            mDialogView.permissions_title.text = context.resources.getString(R.string.gps_permission_title)
            mDialogView.permissions_sub_title.text = context.resources.getString(R.string.gps_permission_explanation)
            mDialogView.permissions_yes.text = context.resources.getString(R.string.gps_location_settings)
            mDialogView.permissions_no.text = context.resources.getString(R.string.cancel)
            
            mDialogView.cftd_save_bottom.visibility = View.VISIBLE
            mDialogView.permissions_no.visibility = View.VISIBLE
            
            
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.setCancelable(true)
            
            val window = mAlertDialog.window
            val wlp = window!!.attributes
            
            wlp.gravity = Gravity.CENTER
            window.attributes = wlp
            
            mDialogView.permissions_yes.setOnClickListener {
                mAlertDialog.dismiss()
                val callGPSSettingIntent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                context.startActivity(callGPSSettingIntent)
            }
            mDialogView.permissions_no.setOnClickListener {
                mAlertDialog.dismiss()
            }
            
        }
        catch (e: Exception) {
            log("${e.localizedMessage}")
        }
    }
    
    /**
     * Request Permissions from User in real time as per Marshmallow and above OS requirements.
     * onPermission result is handled in each Permission requesting Activity.
     * When Location Permission is granted startGPS method will be called in that activity.
     */
    fun requestPermissions() {
        
        // request GPS Permission
        if (ContextCompat.checkSelfPermission(
                (context as Activity),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            log("Permission is not granted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MyEnum.REQUEST_ACCESS_FINE_LOCATION
                )
                
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MyEnum.REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
        
        // request Storage Permission
        if (ContextCompat.checkSelfPermission(
                (context as Activity),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            log("Permission is not granted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MyEnum.REQUEST_WRITE_EXTERNAL_STORAGE
                )
                
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MyEnum.REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
    
    /**
     * If User deny Any Permission show this dialog. This dialog can not be cancelled
     * and user has to allow certain permission to use the app.
     */
    fun showPermissionDisabledAlertToUser(title: String, sub_title: String) {
        
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_permissions, null)
        
        mDialogView.permissions_title.text = title
        mDialogView.permissions_sub_title.text = sub_title
        
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(false)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        
        mDialogView.permissions_yes.setOnClickListener {
            mAlertDialog.dismiss()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    
    fun uploadImagesToAWS(completedCheckForms: ArrayList<MyData>): ArrayList<MyData> {
        completedCheckForms.forEach { completedCheckForm ->
            completedCheckForm.checkFormData.forEach { checkFormDatum ->
                checkFormDatum.answerDataObj.imagesList.forEach { images ->
                    if (images.localImagePath.isNotBlank() && images.awsImagePath.isBlank()) {
                        log("upload Image")
                        try {
                            val file = readContentToFile(Uri.parse(images.localImagePath))
                            val filePath = getAWSFilePath(MyEnum.CHECKFORMS_IMAGES)
                            awsFileUpload(filePath, file)
                            images.awsImagePath = filePath + file.name
                            log("fileAdded:${checkFormDatum.answerDataObj}")
                        }
                        catch (e: Exception) {
                            log("uploadException:${e.localizedMessage}")
                        }
                    } else {
                        log("image already updated")
                    }
                }
                // save answer_data in string as this will be uploaded to server and saved in database.
                checkFormDatum.answerDataString = getAnswerDataToString(checkFormDatum.answerDataObj)
            }
        }
        return completedCheckForms
    }
    
    /**
     * Check if CheckForm is for all sites, OR all Machine Types OR All Machines OR for this Machine
     */
    fun isValidCheckForm(adminCheckForm: MyData): Boolean {
        var isValidDueCheckForm = false
        
        
        if (adminCheckForm.siteId == 0) {
            // If CheckForm is for all sites then add CheckForm
            isValidDueCheckForm = true
        } else if (adminCheckForm.siteId == getMachineSettings().siteId) {
            // If CheckForm is for this Site then continue other checks
            if (adminCheckForm.machineTypeId == 0) {
                // If CheckForm is for all Machine Types then Add CheckForm
                isValidDueCheckForm = true
            } else if (adminCheckForm.machineTypeId == getMachineTypeID()) {
                // If CheckForm is for this Machine Type ID then continue other checks
                if (adminCheckForm.machineId == 0) {
                    // CheckForm is for All Machines then add CheckForm
                    isValidDueCheckForm = true
                } else if (adminCheckForm.machineId == getMachineID()) {
                    isValidDueCheckForm = true
                }
            }
        }
        
        log("isValidDueCheckForm:$isValidDueCheckForm")
        
        return isValidDueCheckForm
    }
    
    /**
     * Check if CheckForm is Due after days passed.
     */
    fun isDueCheckFormAfterDaysPassed(
        adminCheckForm: MyData,
        adminCheckFormsCompleted: MyData?,
        firstMachineHours: MyData
    ): Boolean {
        var isDueCheckFormAfterDaysPassed = false
        var lastDaysReading: Double = 0.0
        log("adminCheckFormsCompleted:$adminCheckFormsCompleted")
        log("firstMachineHours:$firstMachineHours")
        try {
            
            // Compare Machine Hours First reading with Admin CheckForms Completed Value and take whatever is higher
            // As this value will be compared with minimum days required for CheckForm to be Due
            if (adminCheckFormsCompleted != null) {
                if (adminCheckFormsCompleted.admin_checkforms_schedules_value != null && firstMachineHours.startTime > 0) {
                    lastDaysReading =
                        if ((adminCheckFormsCompleted.admin_checkforms_schedules_value!!.toDouble()) > (firstMachineHours.startTime) / MyEnum.ONE_DAY) adminCheckFormsCompleted.admin_checkforms_schedules_value!!.toDouble() else (firstMachineHours.startTime / MyEnum.ONE_DAY).toDouble()
                    log("adminCheckFormsCompleted_startTime:${getDateTime(adminCheckFormsCompleted!!.admin_checkforms_schedules_value!!.toLong())}")
                }
            } else {
                lastDaysReading = (firstMachineHours.startTime / MyEnum.ONE_DAY).toDouble()
            }
            
            val daysRequiredByAdminCheckForms = adminCheckForm.admin_checkforms_schedules_value!!.toDouble()
            log("firstMachineHours_startTime:${getDateTime(firstMachineHours.startTime)}")
            log("daysRequiredByAdminCheckForms:$daysRequiredByAdminCheckForms")
            log("if lastDaysReading > daysRequiredByAdminCheckForms:${lastDaysReading < daysRequiredByAdminCheckForms}")
            if (lastDaysReading < daysRequiredByAdminCheckForms) {
                isDueCheckFormAfterDaysPassed = true
            }
        }
        catch (e: Exception) {
            log("isDueCheckFormAfterDaysPassed_exception:${e.localizedMessage}")
        }
        
        return isDueCheckFormAfterDaysPassed
    }
    
    /**
     * Check if CheckForm is Due after Machine Hours completed.
     */
    fun isDueCheckFormAfterMachineHoursCompleted(adminCheckForm: MyData, adminCheckFormsCompleted: MyData?): Boolean {
        var isDueCheckFormAfterMachineHoursCompleted = false
        var lastMachineHourReading: Double = 0.0
//        log("lastMachineHourReading:$lastMachineHourReading")
        try {
            if (adminCheckFormsCompleted != null) {
                lastMachineHourReading = adminCheckFormsCompleted.admin_checkforms_schedules_value!!.toDouble()
            }
//            log("lastMachineHourReading:$lastMachineHourReading")
            val currentMachineHourReading = getMeterTimeForFinish().toDouble()
//            log("currentMachineHourReading:$currentMachineHourReading")
            val machineHoursForDueCheckForm = currentMachineHourReading - lastMachineHourReading
//            log("machineHoursForDueCheckForm:$machineHoursForDueCheckForm")
            val machineHoursRequiredByAdminCheckForms = adminCheckForm.admin_checkforms_schedules_value!!.toDouble()
//            log("machineHoursRequiredByAdminCheckForms:$machineHoursRequiredByAdminCheckForms")
//            log("if:${machineHoursForDueCheckForm > machineHoursRequiredByAdminCheckForms}")
            if (machineHoursForDueCheckForm > machineHoursRequiredByAdminCheckForms) {
                isDueCheckFormAfterMachineHoursCompleted = true
            }
        }
        catch (e: Exception) {
            log("isDueCheckFormAfterMachineHoursCompletedException:${e.localizedMessage}")
        }
        
        return isDueCheckFormAfterMachineHoursCompleted
    }
    
    fun checkDueCheckForms(dueCheckForms: java.util.ArrayList<MyData>) {
        // TODO show due Checkfroms at start
//        if (dueCheckForms.size > 0) {
//            val intent = Intent(context, CheckFormsActivity::class.java)
//            context.startActivity(intent)
//        } else {
        startHomeActivityByType(MyData())
//        }
    }
    
    fun getMachineDetails(): String {
        var machineDetails = ""
        when (getMachineTypeID()) {
            MyEnum.EXCAVATOR -> {
                machineDetails = "Excavator # ${getMachineNumber()}"
            }
            MyEnum.SCRAPER -> {
                machineDetails = "Scraper # ${getMachineNumber()}"
            }
            MyEnum.TRUCK -> {
                machineDetails = "Truck # ${getMachineNumber()}"
            }
        }
        return machineDetails
    }
/*
    fun isValidUsername(target: String): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else target.length >= 5
    }



    fun setDefaultLayout(view: com.google.android.material.floatingactionbutton.FloatingActionButton) {
        val width = context.resources.getDimensionPixelSize(R.dimen._80sdp)
        val height = context.resources.getDimensionPixelSize(R.dimen._80sdp)
        val layoutParams = FrameLayout.LayoutParams(width, height)
        view.setLayoutParams(layoutParams)
    }
    */
    
    /**
     * There are issue on Lollipop version devices due to SSL checking. Currently SSL are not utilized and neither
     * we have purchased premium SSL. So SSL checking in current scenario and not implemented. So we are skipping SSL
     * checking to support Lollipop devices.
     */
    fun skipSSLOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        
        okHttpClient
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false)
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request().newBuilder().addHeader("Connection", "close").build()
                    return chain.proceed(request)
                }
            })
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? = arrayOf()
            })
            
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
//                okHttpClient.hostnameVerifier { _, _ -> true }
                okHttpClient.hostnameVerifier(object : HostnameVerifier {
                    override fun verify(hostname: String?, session: SSLSession?): Boolean {
                        return true
                    }
                })
            }
            
            return okHttpClient
        }
        catch (e: Exception) {
            return okHttpClient
        }
    }
    
    fun getKMLFileName(awsPath: String): String {
        return context.getExternalFilesDir(null).toString() + "/kml_maps/" + awsPath.substringAfterLast("kml_maps")
    }
    
    /**
     * This method will do following actions.
     * 1. Delete Operator Login PIN and end session.
     * 2. Clear Last Journey Data and End Trip Loop.
     * 3. Clear App previous Activities.
     * 4. Redirect to Operator Login Activity.
     */
    fun clearLoginData() {
        setOperatorAPI(MyData())
        val data = MyData()
        setLastJourney(data)
        
        val intent = Intent(context, OperatorLoginActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
//        context.finishAffinity()
    }
    
    fun autoLogout() {
    
    }
    
    fun getTypeName(type: Int): String {
        var typeName = ""
        when (type) {
            1 -> typeName = context.getString(R.string.operators_hours)
            2 -> typeName = context.getString(R.string.trucks_trips)
            3 -> typeName = context.getString(R.string.scrapers_trips)
            4 -> typeName = context.getString(R.string.scrapers_trimmings)
            5 -> typeName = context.getString(R.string.excavators_prod_digging)
            6 -> typeName = context.getString(R.string.excavators_trenching)
            7 -> typeName = context.getString(R.string.excavators_gen_digging)
            8 -> typeName = context.getString(R.string.machines_stops1)
            9 -> typeName = context.getString(R.string.machines_hours1)
            10 -> typeName = context.getString(R.string.operators_waitings)
            11 -> typeName = context.getString(R.string.checkforms_completed)
        }
        return typeName
    }
    
    fun installedApps(): java.util.ArrayList<String> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        
        var appList = java.util.ArrayList<String>()
//        appList = packageManager.queryIntentActivities(mainIntent, 0) as ArrayList<ResolveInfo>
//        Collections.sort(appList, ResolveInfo.DisplayNameComparator(packageManager))
        val packs: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in 0 until packs.size) {
            val p: PackageInfo = packs[i]
            val a: ApplicationInfo = p.applicationInfo
            // skip system apps if they shall not be included
//            if (a.flags and ApplicationInfo.FLAG_SYSTEM === 1) {
//                continue
//            }
            appList.add(p.packageName)
        }
        return appList
    }
    
    fun getCurrentRunningApp(): String {
        val mActivityManager: ActivityManager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        val runningTask: List<ActivityManager.RunningTaskInfo> = mActivityManager.getRunningTasks(1)
        val ar: ActivityManager.RunningTaskInfo = runningTask[0]
//        return ar.topActivity!!.className
        return ar.topActivity!!.packageName
    }
    
    fun killApp() {
        val startHomescreen = Intent(Intent.ACTION_MAIN)
        startHomescreen.addCategory(Intent.CATEGORY_HOME)
        startHomescreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(startHomescreen)
    }
    
    fun launchNtripClient() {
        try {
//            val mainIntent = Intent(Intent.ACTION_MAIN, null)
//            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//            val pkgAppsList: List<ResolveInfo> = context.applicationContext.packageManager.queryIntentActivities(mainIntent, 0)
//            log("pkgAppsList.toString()")
//            pkgAppsList.forEach { log(it.toString()) }
//            log("applicationContext.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)")
//            context.applicationContext.packageManager.getInstalledApplications(PackageManager.GET_META_DATA).forEach { log(it.toString()) }
            val intent = context.applicationContext.packageManager.getLaunchIntentForPackage("com.lefebure.ntripclient")!!
            context.applicationContext.startActivity(intent)
        }
        catch (e: Exception) {
            log("taptapuException:${e.localizedMessage}")
            showErrorDialog("No Ntrip Client installed", "Please install Lefebure Ntrip Client")
        }
    }
    
    fun lockOtherApps() {
        
        log("installedApps:${installedApps()}")
        log("getCurrentRunningApp:${getCurrentRunningApp()}")
        killApp()
    }
    
}




