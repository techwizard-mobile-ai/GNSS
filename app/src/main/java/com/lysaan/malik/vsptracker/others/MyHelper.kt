package com.lysaan.malik.vsptracker

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.lysaan.malik.vsptracker.activities.excavator.EHome1Activity
import com.lysaan.malik.vsptracker.activities.scrapper.SHomeActivity
import com.lysaan.malik.vsptracker.activities.excavator.EHomeActivity
import com.lysaan.malik.vsptracker.activities.truck.THomeActivity
import com.lysaan.malik.vsptracker.classes.Location
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.activity_tload.*

class MyHelper(val TAG: String, val context: Context) {


    private lateinit var dialog: ProgressDialog
    private lateinit var progressBar: ProgressBar
    private var  sessionManager :SessionManager = SessionManager(context)


    fun isNightMode() = sessionManager.isNightMode()
    fun setNightMode(mode : Boolean) { sessionManager.setNightMode(mode)}

    fun getIsMachineStopped () = sessionManager.getIsMachineStopped()
    fun setIsMachineStopped (status : Boolean, reason:String ){ sessionManager.setMachineStopped(status, reason)}
    fun getMachineStoppedReason () = sessionManager.getMachineStoppedReason()

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
        states.add(Material(2, "Scrapper"))
        states.add(Material(3, "Truck"))
        return states
    }

    fun getMaterials(): ArrayList<Material> {
        val states = ArrayList<Material>()

        states.add(Material(0, "Select Material"))
        states.add(Material(1, "Material 1"))
        states.add(Material(2, "Material 2"))
        states.add(Material(3, "Material 3"))
        states.add(Material(4, "Material 4"))
        states.add(Material(5, "Material 5"))
        states.add(Material(6, "Other 1"))

        return states
    }

    fun getLocations1(): ArrayList<Material> {
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

    fun getLocations(): ArrayList<Location> {
        val locations = ArrayList<Location>()

        locations.add(Location(0, "Select Location"))
        locations.add(Location(1, "Location 1"))
        locations.add(Location(2, "Location 2"))
        locations.add(Location(3, "Location 3"))
        locations.add(Location(4, "Location 4"))
        locations.add(Location(5, "Location 5"))
        locations.add(Location(6, "Other 1"))
        return locations
    }

    fun getMachineNumber() = sessionManager.getMachineNumber()
    fun setMachineNumber(number : String) { sessionManager.setMachineNumber(number)}

    fun getMachineType() = sessionManager.getMachineType()
    fun setMachineType(type : Int){
        sessionManager.setMachineType(type)
    }

    fun isOnline(): Boolean {
        return !(!this.isNetworkAvailable()!!)!!
    }

    fun isNetworkAvailable(): Boolean? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun addLoginNumber () { sessionManager.addLoginNumber()}
    fun getLoginNumber(): Int{ return sessionManager.getLoginNumber()}


    fun hideKeybaordOnClick(view : View){
        view.setOnTouchListener(View.OnTouchListener { v, event ->
            hideKeyboard(view)
            false
        })
    }
    fun hideKeyboard(view :View){
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    fun imageLoadFromURL(url:String, imageView: ImageView, myContext:Context){

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
        }else{
//                    myHelper.hideDialog()
            log("else$url")
            imageView.setImageResource(R.drawable.user_img)
        }

    }
    fun imageLoad(filePath: Uri?, coach_user_image: ImageView) {
        try {
            Glide.with(context).load(filePath).into(coach_user_image)
            toast("Image Attached Successfully.")
        }catch (exception: Exception){
            toast("$exception")
        }
    }

    fun getPass():String{ return  sessionManager.getPass()}
    fun setPass(pass:String){ sessionManager.setPass(pass) }

    fun getEmail():String{ return  sessionManager.getEmail()}
    fun setEmail(email: String){ sessionManager.setEmail(email)}

    fun hideDialog(){
        if(dialog.isShowing)
        dialog.dismiss()}

    fun showDialog(){
        try {
            dialog = ProgressDialog.show(
                context, "","Loading. Please wait...", true
            )
        }catch (exception: Exception){
            log("showDialogException:$exception")
        }
    }

    fun showProgressBar(){
        if(progressBar != null){
            if(progressBar.visibility == View.GONE)
                progressBar.visibility = View.VISIBLE
        }
    }
    fun hideProgressBar(){
        if(progressBar != null){
            if(progressBar.visibility == View.VISIBLE)
                progressBar.visibility = View.GONE
        }
    }
    fun toast(message:String){
        Toast.makeText(context, message,Toast.LENGTH_LONG).show()
    }
    fun log(message: String){
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

    fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar!!
    }

    //    type = 1 excavator
    //    type = 2 scrapper
    //    type = 3 truck
    fun startHomeActivityByType() {
        when(getMachineType()){
            1 -> {
                val intent = Intent(context, EHome1Activity::class.java)

                context.startActivity(intent)

            }
            2 -> {
                val intent = Intent(context, SHomeActivity::class.java)
                context.startActivity(intent)

            }
            3 -> {
                val intent = Intent(context, THomeActivity::class.java)
                context.startActivity(intent)

            }
        }
    }


}