package app.vsptracker.others

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import app.vsptracker.apis.login.LoginAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.classes.Meter
import com.google.gson.Gson

private var PRIVATE_MODE = 0
private const val PREF_NAME = "VSPTracker"
private const val KEY_MACHINE_TYPE_ID = "machine_type_id"
private const val KEY_MACHINE_NUMBER = "machine_number"
private const val KEY_MACHINE_ID = "machine_id"
private const val KEY_IS_MACHINE_STOPPED = "is_machine_stopped"
private const val KEY_MACHINE_STOPPED_REASON = "machine_stopped_reason"
private const val KEY_MACHINE_STOPPED_REASON_ID = "machine_stopped_reason_id"
private const val KEY_NIGHT_MODE = "night_mode"
private const val KEY_LAST_JOURNEY = "last_journey"
private const val KEY_METER = "meter"
private const val KEY_LOGINAPI = "login_api"
private const val KEY_OPERATORAPI = "operator_api"
private const val KEY_MACHINE_SETTINGS = "machine_settings"
private const val KEY_DISABLE_NAV = "disable_navigation"

class SessionManager(_context: Context) {

    private var pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private var editor: Editor

    init {
        editor = pref.run { edit() }
    }

    fun getNav() = pref.getBoolean(KEY_DISABLE_NAV, true)
    /**
     * Disable OR Enable Side Menu and Bottom Navigation.
     * This is useful when we want to restrict user from entering into app by using Map Activity OR Bottom Nav OR Menu Nav.
     */
    fun setNav(status: Boolean) {
        editor.putBoolean(KEY_DISABLE_NAV, status)
        editor.commit()
    }

    fun getMachineSettings(): Material {
        val gson = Gson()
        val json = pref.getString(KEY_MACHINE_SETTINGS, "")
        return when (val obj = gson.fromJson<Material>(json, Material::class.java)) {
            null -> Material()
            else -> obj
        }
    }

    fun setMachineSettings(material: Material) {
        val gson = Gson()
        val json = gson.toJson(material)
        editor.putString(KEY_MACHINE_SETTINGS, json)
        editor.commit()
    }

    fun getLastJourney(): MyData {
        val gson = Gson()
        val json = pref.getString(KEY_LAST_JOURNEY, "")
        return when (val obj = gson.fromJson<MyData>(json, MyData::class.java)) {
            null -> MyData()
            else -> obj
        }
    }

    fun setLastJourney(myData: MyData) {
        val gson = Gson()
        val json = gson.toJson(myData)
        editor.putString(KEY_LAST_JOURNEY, json)
        editor.commit()
    }

    fun getOperatorAPI(): MyData {
        val gson = Gson()
        val json = pref.getString(KEY_OPERATORAPI, "")
        return when (val obj = gson.fromJson<MyData>(json, MyData::class.java)) {
            null -> MyData()
            else -> obj
        }
    }

    fun getLoginAPI(): LoginAPI {
        val gson = Gson()
        val json = pref.getString(KEY_LOGINAPI, "")
        return when (val obj = gson.fromJson<LoginAPI>(json, LoginAPI::class.java)) {
            null -> LoginAPI()
            else -> obj
        }
    }

    fun getMeter(): Meter {
        val gson = Gson()
        val json = pref.getString(KEY_METER, "")
        return when (val obj = gson.fromJson<Meter>(json, Meter::class.java)) {
            null -> Meter()
            else -> obj
        }
    }

    fun setOperatorAPI(loginAPI: MyData) {
        val gson = Gson()
        val json = gson.toJson(loginAPI)
        editor.putString(KEY_OPERATORAPI, json)
        editor.commit()
    }

    fun setLoginAPI(loginAPI: LoginAPI) {
        val gson = Gson()
        val json = gson.toJson(loginAPI)
        editor.putString(KEY_LOGINAPI, json)
        editor.commit()
    }

    fun setMeter(meter: Meter) {
        val gson = Gson()
        val json = gson.toJson(meter)
        editor.putString(KEY_METER, json)
        editor.commit()
    }

    fun isNightMode() = pref.getBoolean(KEY_NIGHT_MODE, false)
    fun setNightMode(mode: Boolean) {
        editor.putBoolean(KEY_NIGHT_MODE, mode)
        editor.commit()
    }

    fun getMachineStoppedReasonID() = pref.getInt(KEY_MACHINE_STOPPED_REASON_ID, 0)
    fun getMachineStoppedReason() = pref.getString(KEY_MACHINE_STOPPED_REASON, "")
    fun getIsMachineStopped() = pref.getBoolean(KEY_IS_MACHINE_STOPPED, false)
    fun setMachineStopped(status: Boolean, reason: String, id: Int) {
        editor.putBoolean(KEY_IS_MACHINE_STOPPED, status)
        editor.putString(KEY_MACHINE_STOPPED_REASON, reason)
        editor.putInt(KEY_MACHINE_STOPPED_REASON_ID, id)
        editor.commit()
    }

    fun getMachineID() = pref.getInt(KEY_MACHINE_ID, 0)
    fun getMachineNumber() = pref.getString(KEY_MACHINE_NUMBER, "").toString()
    fun setMachineID(id: Int) {
        editor.putInt(KEY_MACHINE_ID, id)
        editor.commit()
    }

    fun setMachineNumber(number: String) {
        editor.putString(KEY_MACHINE_NUMBER, number)
        editor.commit()
    }

    fun getMachineTypeID() = pref.getInt(KEY_MACHINE_TYPE_ID, 0)
    
    fun setMachineTypeID(type: Int) {
        editor.putInt(KEY_MACHINE_TYPE_ID, type)
        editor.commit()
    }
}