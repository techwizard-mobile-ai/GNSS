package app.vsptracker.others

//import android.app.ProgressDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import app.vsptracker.BuildConfig
import app.vsptracker.R
import app.vsptracker.activities.HourMeterStopActivity
import app.vsptracker.activities.LoginActivity
import app.vsptracker.activities.Map1Activity
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
import app.vsptracker.apis.serverSync.ServerSyncAPI
import app.vsptracker.apis.serverSync.ServerSyncDataAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.DeviceDetails
import app.vsptracker.classes.GPSLocation
import app.vsptracker.classes.Material
import app.vsptracker.classes.Meter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
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
        log(getDeviceDetailsString())
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("email", getLoginAPI().email)
            .add("password", getLoginAPI().pass)
            .add("role", "1")
            .add("ttl", "43800")
            .build()
        val request = Request.Builder()
            .url("https://vsptracker.app/api/v1/org/users/login1")
            .post(formBody)
            .build()
//        val deviceDetails = getDeviceDetails()
//        log(deviceDetails)
        
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body()!!.string()
                val responseJObject = JSONObject(responseString)
                log("RefreshToken:$responseString")
                val success = responseJObject.getBoolean("success")
                if (success) {
                    val gson = GsonBuilder().create()
                    val loginAPI = gson.fromJson(responseJObject.getString("data"), LoginAPI::class.java)
                    loginAPI.pass = getLoginAPI().pass
                    
                    val app = gson.fromJson(responseJObject.getString("app"), AppAPI::class.java)
                    log("app:$app")
                    
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
            
            override fun onFailure(call: Call, e: IOException) {
                log("Failed to execute request ${e.printStackTrace()}")
            }
        })
    }
    
    fun getServerSyncDataAPIString(serverSyncList: ArrayList<ServerSyncAPI>): String {
        val serverSyncDataAPI = ServerSyncDataAPI()
        serverSyncDataAPI.data = serverSyncList
        return gson.toJson(serverSyncDataAPI, ServerSyncDataAPI::class.java)
    }
    
    fun getDeviceDetailsString(): String {
        return gson.toJson(DeviceDetails(), DeviceDetails::class.java)
//        return ("AppVersionCode:$versionCode--Device:$device--Build:$build--Manufacturer:$manufacturer--Model:$model--AndroidOS:$andoridOS")
    }
/*
    
    fun pushIsMachineRunning(isRunning: Int, machinesStopsDbID: Int) {

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val call = this.retrofitAPI.pushIsMachineRunning(getLoginAPI().auth_token, getMachineID(), isRunning, machinesStopsDbID)
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {

            override fun onResponse(call: retrofit2.Call<MyDataResponse>, response: retrofit2.Response<MyDataResponse>) {
                log("updateMachineStatus:$response")
                val loginResponse = response.body()
                log("updatebody:$loginResponse")
//                if(loginResponse!!.success){
//                    log("SendReponse:${loginResponse.data}.")
//                    setLoginAPI(loginResponse.data)
//                    val intent = Intent(context, context.javaClass)
//                    intent.putExtra("myData", MyData())
//                    context.startActivity(intent)
//                    toast("Please Try Again.")
//                }else{
//                    toast(loginResponse.message)
//                    val intent = Intent(context, LoginActivity::class.java)
//                    context.startActivity(intent)
//                }
//                myHelper.setLoginAPI(loginResponse.data)
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                log("API Failure:$t")
            }
        })
    }
*/
    
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
        view.layoutParams = layoutParams
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
        log("MeterStopBefore:$meter")
        meter.isDelayStarted = false
        meter.delayStopTime = System.currentTimeMillis()
        meter.delayTotalTime = meter.delayStopTime - meter.delayStartTime
        meter.delayStopGPSLocation = gpsMaterial
        log("MeterStopAfter:$meter")
        setMeter(meter)
//            toast("Delay Stopped.\nStart Time: ${getTime(getMeter().dailyModeStartTime)}Hrs.\nTotal Time: ${getFormattedTime(meter.delayTotalTime)}")
    }
    
    fun stopDailyMode() {
        val currentTime = System.currentTimeMillis()
        if (isDailyModeStarted()) {
            val meter = getMeter()
            log("MeterStopBefore:$meter")
            meter.isDailyModeStarted = false
            val startTime = meter.dailyModeStartTime
            val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime
            meter.dailyModeTotalTime = 0L
            log("MeterStopAfter:$meter")
//            Reset Meter as we are not counting Daily Mode as whole but in entries
            setMeter(meter)
//            setMeter(Meter())
            toast("Day Works Stopped.\nStart Time: ${getTime(getMeter().dailyModeStartTime)}.\nTotal Time: ${getMinutesFromMillisec(totalTime)}")
        }
//        else {
//            toast(
//                "Day Works Already Stopped." +
//                        "\nTotal Time: ${getMinutesFromMillisec(getMeter().dailyModeTotalTime)}"
//            )
//        }
    }
    
    fun startDelay(gpsMaterial: GPSLocation) {
        val currentTime = System.currentTimeMillis()
        if (!isDelayStarted()) {
            val meter = getMeter()
            log("MeterStartBefore:$meter")
            meter.isDelayStarted = true
            meter.delayStartTime = currentTime
            meter.delayStartGPSLocation = gpsMaterial
            toast("Waiting Started.")
            log("MeterStartAfter:$meter")
            setMeter(meter)
        } else {
            toast("Waiting is already Started.")
        }
    }
    
    fun startDailyMode() {
        
        val currentTime = System.currentTimeMillis()
        if (!isDailyModeStarted()) {
            val meter = getMeter()
            log("MeterStartBefore:$meter")
            meter.isDailyModeStarted = true
            meter.dailyModeStartTime = currentTime
            toast("Day Works Started.")
            log("MeterStartAfter:$meter")
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
    
    private fun getMinutesFromMillisec(totalTime: Long): Long {
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
    
    fun getDateTime(s: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
            //            val sdf = SimpleDateFormat("HH:mm")
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
        log("setMeter:$meter")
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
    
    private fun getMachineStartTime(): Long {
        val meter = sessionManager.getMeter()
        
        log("meter:$meter")
        
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
        if(resetJourney){
            val data = MyData()
            setLastJourney(data)
        }
        
//            toast("Machine is Stopped.\n Machine Total Time : $meterONTime (mins)")
//        toast("Machine is Stopped.")
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
        view.setOnTouchListener { _, _ ->
            hideKeyboard(view)
            false
        }
    }
    
    fun hideKeyboard(view: View) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
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
            val v = toast.view.findViewById(android.R.id.message) as TextView
            v.gravity = Gravity.CENTER
            toast.show()
        }
        catch (e: Exception) {
            log("toastException:${e.message}")
        }
        
    }
    
    fun log(message: String) {
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
            1 -> {
                val intent = Intent(context, EHistoryActivity::class.java)
                context.startActivity(intent)
            }
            2 -> {
                val intent = Intent(context, SHistoryActivity::class.java)
                context.startActivity(intent)
            }
            3 -> {
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
        when (getMachineTypeID()) {
            1 -> {
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
            
            2 -> {
                val intent : Intent = if(lastJourney.repeatJourney >0 && (lastJourney.nextAction == 0 || lastJourney.nextAction == 2)){
                    // Launch Load Screen
                    Intent(context, RLoadActivity::class.java)
                }else if (lastJourney.repeatJourney >0 && (lastJourney.nextAction == 1 || lastJourney.nextAction == 3)){
                    // Launch Unload Screen
                    Intent(context, RUnloadActivity::class.java)
                }else{
                    // No settings so Start Home Activity
                    Intent(context, SHomeActivity::class.java)
                }
                context.startActivity(intent)
            }
            3 -> {
//                val intent = Intent(context, THomeActivity::class.java)
                val intent : Intent = if(lastJourney.repeatJourney >0 && (lastJourney.nextAction == 0 || lastJourney.nextAction == 2)){
                    // Launch Load Screen
                    Intent(context, RLoadActivity::class.java)
                }else if (lastJourney.repeatJourney >0 && (lastJourney.nextAction == 1 || lastJourney.nextAction == 3)){
                    // Launch Unload Screen
                    Intent(context, RUnloadActivity::class.java)
                }else{
                    // No settings so Start Home Activity
                    Intent(context, THomeActivity::class.java)
                }
                context.startActivity(intent)
                
            }
        }
    }
    
    fun startLoadAfterActivityByType(myData: MyData) {
        
        when (getMachineTypeID()) {
            1 -> {
                val intent = Intent(context, EHomeActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            2 -> {
                val intent = Intent(context, SUnloadAfterActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            3 -> {
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
    fun showDialog() {
        try {
            dialog = ProgressDialog.show(
                context, "", "VSP Tracker. Loading Please wait...", true, false
            )
            
        }
        catch (exception: Exception) {
            log("showDialogException:$exception")
        }
    }


/*
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
    fun imageLoad(filePath: Uri?, coach_user_image: ImageView) {
        try {
            Glide.with(context).load(filePath).into(coach_user_image)
            toast("Image Attached Successfully.")
        } catch (exception: Exception) {
            toast("$exception")
        }
    }

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
/*
    fun refreshToken1() {

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val call = this.retrofitAPI.getLogin(getLoginAPI().email, "user@123")
        call.enqueue(object : retrofit2.Callback<LoginResponse> {

            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                log("RetrofitResponse:$response")
//                val loginResponse = response.body()
//                if(loginResponse!!.success){
//                    log("SendReponse:${loginResponse.data}.")
//                    setLoginAPI(loginResponse.data)
//                    val intent = Intent(context, context.javaClass)
//                    intent.putExtra("myData", MyData())
//                    context.startActivity(intent)
//                    toast("Please Try Again.")
//                }else{
//                    toast(loginResponse.message)
//                    val intent = Intent(context, LoginActivity::class.java)
//                    context.startActivity(intent)
//                }
//                myHelper.setLoginAPI(loginResponse.data)
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                log("API Failure:" + t)
            }
        })
    }
*/

}




