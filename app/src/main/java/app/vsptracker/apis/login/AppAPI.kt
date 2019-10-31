package app.vsptracker.apis.login

import com.google.gson.annotations.SerializedName

class AppAPI (){
    @SerializedName("version_code") val version_code = 0
    @SerializedName("is_critical") val is_critical = 0
    override fun toString(): String {
        return "AppAPI(version_code=$version_code, is_critical=$is_critical)"
    }


}