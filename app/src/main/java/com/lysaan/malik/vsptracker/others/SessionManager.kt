package com.lysaan.malik.vsptracker

import android.content.SharedPreferences
import android.content.Context
import android.content.SharedPreferences.Editor
import android.util.Log

class SessionManager(internal var _context: Context) {

    internal var pref: SharedPreferences
    internal var editor: Editor
    internal var PRIVATE_MODE = 0
    private val PREF_NAME = "VSPTracker"
    private val KEY_UID = "_uid"
    private val KEY_FCM_TOKEN = "_fcm"
    private val KEY_EMAIL = "_email"
    private val KEY_PASS = "_pass"
    private val KEY_LOGIN_NUMBER = "isNewUser"

    private val KEY_MACHINE_TYPE = "machine_type"
    private val KEY_MACHINE_NUMBER = "machine_number"

    private val KEY_IS_MACHINE_STOPPED = "is_machine_stopped"
    private val KEY_MACHINE_STOPPED_REASON = "machine_stopped_reason"
    private val KEY_NIGHT_MODE= "night_mode"

    private val TAG = SessionManager::class.java.simpleName

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun isNightMode() = pref.getBoolean(KEY_NIGHT_MODE, false)
    fun setNightMode (mode : Boolean){
        editor.putBoolean(KEY_NIGHT_MODE, mode)
        editor.commit()
    }

    fun getMachineStoppedReason() = pref.getString(KEY_MACHINE_STOPPED_REASON, "")
    fun getIsMachineStopped () = pref.getBoolean(KEY_IS_MACHINE_STOPPED, false)
    fun setMachineStopped (status : Boolean, reason:String){
        editor.putBoolean(KEY_IS_MACHINE_STOPPED, status)
        editor.putString(KEY_MACHINE_STOPPED_REASON, reason)
        editor.commit()
    }

    fun getMachineNumber () = pref.getString(KEY_MACHINE_NUMBER, "")

    fun setMachineNumber (number : String){
        editor.putString(KEY_MACHINE_NUMBER, number)
        editor.commit()
    }

    fun getMachineType() = pref.getInt(KEY_MACHINE_TYPE, 0)

//    type = 1 excavator
//    type = 2 scrapper
//    type = 3 truck
    fun setMachineType (type :Int){
        editor.putInt(KEY_MACHINE_TYPE, type)
        editor.commit()
    }


    fun getLoginNumber(): Int {
        return pref.getInt(KEY_LOGIN_NUMBER, 0)
    }

    fun addLoginNumber() {

        editor.putInt(KEY_LOGIN_NUMBER, getLoginNumber() + 1)
        editor.commit()
    }

    fun getPass():String{ return pref.getString(KEY_PASS,"") }
    fun setPass(pass:String){
        editor.putString(KEY_PASS, pass)
        editor.commit()
        Log.e(TAG,"Pass:$pass")
    }

    fun getEmail():String{ return pref.getString(KEY_EMAIL,"") }
    fun setEmail(email:String){
        editor.putString(KEY_EMAIL,email)
        editor.commit()
        Log.e(TAG,"Email:$email")
    }

    fun getFcm() :String{ return  pref.getString(KEY_FCM_TOKEN,"")}
    fun setFcm(fcmToken : String){
        editor.putString(KEY_FCM_TOKEN, fcmToken)
        editor.commit()
        Log.e(TAG,"setFcm:$fcmToken")
    }

    fun getFanUid() : String{return pref.getString(KEY_UID,"")
    }
    fun setFanUid(uid: String){
        editor.putString(KEY_UID, uid)
        editor.commit()
        Log.e(TAG,"setUid:$uid")
    }

}