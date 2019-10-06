package com.lysaan.malik.vsptracker

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lysaan.malik.vsptracker.activities.HourMeterStopActivity
import com.lysaan.malik.vsptracker.activities.LoginActivity
import com.lysaan.malik.vsptracker.activities.Map1Activity
import com.lysaan.malik.vsptracker.activities.TabHistoryActivity
import com.lysaan.malik.vsptracker.activities.excavator.EHistoryActivity
import com.lysaan.malik.vsptracker.activities.excavator.EHomeActivity
import com.lysaan.malik.vsptracker.activities.scrapper.SHistoryActivity
import com.lysaan.malik.vsptracker.activities.scrapper.SHomeActivity
import com.lysaan.malik.vsptracker.activities.scrapper.SUnloadAfterActivity
import com.lysaan.malik.vsptracker.activities.truck.THomeActivity
import com.lysaan.malik.vsptracker.activities.truck.TUnloadAfterActivity
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.login.LoginAPI
import com.lysaan.malik.vsptracker.apis.login.LoginResponse
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.classes.GPSLocation
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.classes.Meter
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
class MyHelper(var TAG: String, val context: Context) {

    private lateinit var dialog: ProgressDialog
    private lateinit var progressBar: ProgressBar
    private var sessionManager: SessionManager = SessionManager(context)
    private val gson = Gson()
    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    fun getMachineID() = sessionManager.getMachineID()
    fun setMachineID(id: Int) {
        sessionManager.setMachineID(id)
    }

    fun getOperatorAPI() = sessionManager.getOperatorAPI()
    fun setOperatorAPI(loginAPI: LoginAPI){sessionManager.setOperatorAPI(loginAPI)}

    fun getLoginAPI() = sessionManager.getLoginAPI()
    fun setLoginAPI(loginAPI: LoginAPI){sessionManager.setLoginAPI(loginAPI)}

    fun refreshToken(){
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("email", getLoginAPI().email)
            .add("password", getLoginAPI().pass)
            .add("role", "1")
            .build()
        val request = Request.Builder()
            .url("https://vsptracker.app/api/v1/org/users/login")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val respontString = response.body()!!.string()
                val responeJObject = JSONObject(respontString)
                log("RefreshToken:$respontString")
                val success =responeJObject.getBoolean("success")
                if(success){
                    val gson = GsonBuilder().create()
                    var loginAPI = gson.fromJson(responeJObject.getString("data"),LoginAPI::class.java)
                    loginAPI.pass = getLoginAPI().pass
                    log("body:$responeJObject")
                    log("Success:$success")
                    log("LoginAPI:$loginAPI")
                    setLoginAPI(loginAPI)
//                    val intent = Intent(context, LoginActivity::class.java)
//                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }


            }

            override fun onFailure(call: Call, e: IOException) {
                log("Failed to execute request ${e.printStackTrace()}")
            }
        })
    }

    fun refreshToken1(){

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val call = this.retrofitAPI.getLogin(getLoginAPI().email,"user@123")
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

    fun getIsMapOpened() = sessionManager.getLastJourney().isMapOpened

    fun setIsMapOpened(isMapOpened: Boolean) {
        val lastJourney = sessionManager.getLastJourney()
        lastJourney.isMapOpened = isMapOpened
        sessionManager.setLastJourney(lastJourney)
    }


    fun setUserID(userID: String) {}
    fun getUserID() = getOperatorAPI().id

    fun getStringToGPSLocation(stringGPSLocation: String): GPSLocation {
        return gson.fromJson(stringGPSLocation, GPSLocation::class.java)
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
        val width = context.resources.getDimensionPixelSize(R.dimen._100sdp)
        val height = context.resources.getDimensionPixelSize(R.dimen._100sdp)
        val layoutParams = FrameLayout.LayoutParams(width, height)
        view.setLayoutParams(layoutParams)
    }

    fun setDefaultLayout(view: com.google.android.material.floatingactionbutton.FloatingActionButton) {
        val width = context.resources.getDimensionPixelSize(R.dimen._80sdp)
        val height = context.resources.getDimensionPixelSize(R.dimen._80sdp)
        val layoutParams = FrameLayout.LayoutParams(width, height)
        view.setLayoutParams(layoutParams)
    }

    fun getWorkMode(): String {
        if (isDailyModeStarted()) {
            return "Day Works"
        } else {
            return "Standard Mode"
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
//            toast("Delay Stopped.\nStart Time: ${getTime(getMeter().dailyModeStartTime)}Hrs.\nTotal Time: ${getFormatedTime(meter.delayTotalTime)}")
    }

    fun stopDailyMode() {
        val currentTime = System.currentTimeMillis()
        if (isDailyModeStarted()) {
            val meter = getMeter()
            log("MeterStopBefore:$meter")
            meter.isDailyModeStarted = false
            val startTime = meter.dailyModeStartTime
            val totalTime = (currentTime - startTime) + meter.dailyModeTotalTime
            meter.dailyModeTotalTime = totalTime
            log("MeterStopAfter:$meter")
            setMeter(meter)
            toast("Day Works Stopped.\nStart Time: ${getTime(getMeter().dailyModeStartTime)}.\nTotal Time: ${getMinutesFromMillisec(totalTime)}")
        } else {
//            toast("Day Works Already Stopped." +
//                    "\nTotal Time: ${getMinutesFromMillisec(getMeter().dailyModeTotalTime)}")
        }
    }

        fun startDelay(gpsMaterial: GPSLocation) {
        val currentTime = System.currentTimeMillis()
        if(!isDelayStarted()){
            val meter = getMeter()
            log("MeterStartBefore:$meter")
            meter.isDelayStarted = true
            meter.delayStartTime = currentTime
            meter.delayStartGPSLocation = gpsMaterial
            toast("Waiting Started.")
            log("MeterStartAfter:$meter")
            setMeter(meter)
        }else{
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
                        "Work Duration : ${getTotalTimeMintues(startTime)} (Minutes)"
        )
    }

    fun getFormatedTime(millis: Long): String {
        val hms = String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )

        return hms
    }

    fun getMinutesFromMillisec(totalTime: Long): Long {
        return (totalTime / 1000 / 60)
    }

    fun getTotalTimeVSP(startTime: Long): String {
        val minutes = getTotalTimeMintues(startTime)
        return getRoundedDecimal(minutes / 60.0).toString()
    }

    fun getTotalTimeMintues(startTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        val ONTime = currentTime - startTime
        val minutes = (ONTime / 1000 / 60) as Long
        log("StartTime: $startTime, CurrentTime:$currentTime, TotalMinutes: $minutes")
        return minutes
    }

    fun getDateTime(s: Long): String {
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
//            val sdf = SimpleDateFormat("HH:mm")
            val netDate = Date(s)
            return sdf.format(netDate)
        } catch (e: Exception) {
            log("getDatetime:${e}")
            return s.toString()
        }
    }

    fun getTime(s: Long): String {

        if (s > 0) {
            try {
//            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
                val sdf = SimpleDateFormat("HH:mm")
                val netDate = Date(s)
                return sdf.format(netDate)
            } catch (e: Exception) {
                log("getDatetime:${e}")
                return s.toString()
            }
        } else {
            return ""
        }

    }

    fun getDate(s: String): String {
        try {
//            val sdf = SimpleDateFormat("MM/dd/yy")
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val netDate = Date(s.toLong())
            return sdf.format(netDate)
        } catch (e: Exception) {
            log("getDateString:${e}")
            return s.toString()
        }
    }

    fun getDate(s: Long): String {
        try {
//            val sdf = SimpleDateFormat("MM/dd/yy")
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val netDate = Date(s)
            return sdf.format(netDate)
        } catch (e: Exception) {
            log("getDate:${e}")
            return s.toString()
        }
    }

    fun getLastJourney() = sessionManager.getLastJourney()
    fun setLastJourney(myData: MyData) = sessionManager.setLastJourney(myData)

    fun getRoundedDecimal(minutes: Double): Double {
        val number3digits: Double = Math.round(minutes * 1000.0) / 1000.0
        val number2digits: Double = Math.round(number3digits * 100.0) / 100.0
        val solution: Double = Math.round(number2digits * 10.0) / 10.0
        return solution
    }

    fun getRoundedInt(minutes: Double): Long {
        val newMinutes = Math.round(getRoundedDecimal(minutes))
        return newMinutes
    }



    fun getMeter() = sessionManager.getMeter()
    fun setMeter(meter: Meter) {
        sessionManager.setMeter(meter)
    }

    fun getMeterTimeForFinish(): String {
        val meterONTime = getMachineTotalTime() + getMachineStartTime()
        return getRoundedDecimal(meterONTime / 60.0).toString()
    }

    fun getMeterTimeForStart(): String {
        return getRoundedDecimal(getMachineTotalTime() / 60.0).toString()
    }

    fun getMachineTotalTime() = sessionManager.getMeter().machineTotalTime


    fun setMachineTotalTime(time: Long) {
        val meter = sessionManager.getMeter()
        meter.machineTotalTime = time
        sessionManager.setMeter(meter)
//        sessionManager.setMachineTotalTime( time)
    }

    fun getMachineStartTime(): Long {
//        val startTime = sessionManager.getMachineStartTime()
        val meter = sessionManager.getMeter()

        log("meter:$meter")

        if (meter.isMachineStopped) {
            return 0
        } else {
            val startTime = meter.machineStartTime
            val currentTime = System.currentTimeMillis()
            val ONTime = currentTime - startTime
            val minutes = (ONTime / 1000 / 60) as Long
            return minutes
        }

    }

    fun stopMachine(insertID: Long) {
        val meter = sessionManager.getMeter()
            val meterONTime = getMachineTotalTime() + getMachineStartTime()
            meter.machineTotalTime = meterONTime
            meter.isMachineStopped = true
            meter.machineDbID = insertID
            sessionManager.setMeter(meter)
            stopDailyMode()
//            toast("Machine is Stopped.\n Machine Total Time : $meterONTime (mins)")
            toast("Machine is Stopped.")

    }

    fun startMachine() {
        val currentTime = System.currentTimeMillis()
        val meter = sessionManager.getMeter()
        meter.machineStartTime = currentTime
        meter.isMachineStopped = false
        meter.machineDbID = 0
        sessionManager.setMeter(meter)
//        sessionManager.setMeterStartTime(currentTime)

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

    fun getMachines(): ArrayList<Material> {
        val states = ArrayList<Material>()

        states.add(Material(0, "Select Machine"))
        states.add(Material(1, "Excavator 1"))
        states.add(Material(2, "Excavator 2"))
        states.add(Material(3, "Other 1"))

        return states
    }

    fun getMachineStopReasons(): ArrayList<Material> {
        val states = ArrayList<Material>()
        states.add(Material(0, "Select Stop Reason"))
        states.add(Material(1, "Machine Breakdown"))
        states.add(Material(2, "Weather"))
        states.add(Material(3, "Other 1"))
        return states
    }

    fun getMachineLocations(): ArrayList<Material> {
        val states = ArrayList<Material>()
        states.add(Material(0, "Select Machine Location"))
        states.add(Material(1, "Auckland"))
        states.add(Material(2, "Drury"))
        states.add(Material(3, "Te Kauwhata"))
        return states
    }

    fun getMachineTypes(): ArrayList<Material> {
        val states = ArrayList<Material>()
        states.add(Material(0, "Select Machine Type"))
        states.add(Material(1, "Excavator"))
        states.add(Material(2, "Scraper"))
        states.add(Material(3, "Truck"))
        return states
    }

    fun getScraperMaterials(): ArrayList<Material> {
        val states = ArrayList<Material>()

        states.add(Material(0, "Select Material"))

        states.add(Material(1, "Topsoil"))
        states.add(Material(2, "Clay"))

        return states
    }

    fun getMaterials(): ArrayList<Material> {
        val states = ArrayList<Material>()

        states.add(Material(0, "Select Material"))

        states.add(Material(1, "Topsoil"))
        states.add(Material(2, "Clay"))
        states.add(Material(3, "Fire Clay"))
        states.add(Material(4, "SPR"))
        states.add(Material(5, "Unsuitable"))
        states.add(Material(6, "Overburden"))

        return states
    }

    fun getMaterials1(): ArrayList<Material> {
        val locations = ArrayList<Material>()

        locations.add(Material(0, "Select Material"))
        locations.add(Material(1, "Material 1"))
        locations.add(Material(2, "Material 2"))
        locations.add(Material(3, "Material 3"))
        locations.add(Material(4, "Material 4"))
        locations.add(Material(5, "Material 5"))
        locations.add(Material(6, "Other 1"))
        return locations
    }

    fun getLocations(): ArrayList<Material> {

        val locations = ArrayList<Material>()

        locations.add(Material(0, "Select Location"))
        locations.add(Material(1, "Location 1"))
        locations.add(Material(2, "Location 2"))
        locations.add(Material(3, "Location 3"))
        locations.add(Material(4, "Location 4"))
        locations.add(Material(5, "Location 5"))
        locations.add(Material(6, "Other 1"))
        return locations
    }


    fun getMachineNumber() = sessionManager.getMachineNumber()
    fun setMachineNumber(number: String) {
        sessionManager.setMachineNumber(number)
    }


    //    type = 1 excavator
    //    type = 2 scrapper
    //    type = 3 truck
    fun getMachineType() = sessionManager.getMachineType()

    fun setMachineType(type: Int) {
        sessionManager.setMachineType(type)
    }

    fun isOnline(): Boolean {
        return !(!this.isNetworkAvailable()!!)!!
    }

    fun isNetworkAvailable(): Boolean? {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun addLoginNumber() {
        sessionManager.addLoginNumber()
    }

    fun getLoginNumber(): Int {
        return sessionManager.getLoginNumber()
    }


    fun hideKeybaordOnClick(view: View) {
        view.setOnTouchListener(View.OnTouchListener { v, event ->
            hideKeyboard(view)
            false
        })
    }

    fun hideKeyboard(view: View) {
        val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    fun imageLoadFromURL(url: String, imageView: ImageView, myContext: Context) {

//        log("imageLoadFromURL:$url")
        if (!url.isNullOrBlank()) {

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

    fun getPass(): String {
        return sessionManager.getPass()
    }

    fun setPass(pass: String) {
        sessionManager.setPass(pass)
    }

    fun getEmail(): String {
        return sessionManager.getEmail()
    }

    fun setEmail(email: String) {
        sessionManager.setEmail(email)
    }

    fun hideDialog() {
        if (dialog.isShowing)
            dialog.dismiss()
    }

    fun showDialog() {
        try {
            dialog = ProgressDialog.show(
                    context, "", "Loading. Please wait...", true
            )
        } catch (exception: Exception) {
            log("showDialogException:$exception")
        }
    }

    fun showProgressBar() {
        if (progressBar != null) {
            if (progressBar.visibility == View.GONE)
                progressBar.visibility = View.VISIBLE
        }
    }

    fun hideProgressBar() {
        if (progressBar != null) {
            if (progressBar.visibility == View.VISIBLE)
                progressBar.visibility = View.GONE
        }
    }

    fun toast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        val v = toast.view.findViewById(android.R.id.message) as TextView
        if (v != null) v.gravity = Gravity.CENTER
//            toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

    fun log(message: String) {
        Log.e(TAG, message)
    }

    fun isValidUsername(target: String): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        } else return target.length >= 5
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
        when (getMachineType()) {
            1 -> {
                val intent = Intent(context, EHistoryActivity::class.java)
                context.startActivity(intent)
            }
            2->{
                val intent = Intent(context, SHistoryActivity::class.java)
                context.startActivity(intent)
            }
            3 -> {
//                val intent = Intent(myContext, HistoryActivity::class.java)
                val intent = Intent(context, TabHistoryActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    //    type = 1 excavator
    //    type = 2 scrapper
    //    type = 3 truckf
    fun startHomeActivityByType(myData: MyData) {
        when (getMachineType()) {
            1 -> {
//                val intent = Intent(myContext, Material1Activity::class.java)
                val intent = Intent(context, EHomeActivity::class.java)
                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            2 -> {
                val intent = Intent(context, SHomeActivity::class.java)
//                intent.putExtra("myData", myData)
                context.startActivity(intent)
            }
            3 -> {
                val intent = Intent(context, THomeActivity::class.java)
//                intent.putExtra("myData", myData)
                context.startActivity(intent)

            }
        }
    }

    fun startLoadAfterActivityByType(myData: MyData) {

        when (getMachineType()) {
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

    fun restartActivity(
            intent: Intent,
            activity: Activity
    ) {
        var bundle: Bundle? = intent.extras
        var data = MyData()
        if (bundle != null) {
            data = bundle!!.getSerializable("myData") as MyData
            log("myData:$data")
        }
        activity.finish()
        val intent = Intent(activity, activity.javaClass)
        intent.putExtra("myData", MyData())
        activity.startActivity(intent)

    }

    fun logout(activity: Activity) {
        val intent = Intent(activity, HourMeterStopActivity::class.java)
        activity.startActivity(intent)
    }


}



