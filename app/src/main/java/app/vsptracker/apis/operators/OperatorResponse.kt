package app.vsptracker.apis.operators

import app.vsptracker.apis.login.AppAPI
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OperatorResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean = false
    @SerializedName("message")
    @Expose
    var message: String = ""
    @SerializedName("data")
    @Expose
    var data: List<OperatorAPI>? = null
    @SerializedName("app")
    @Expose
    var app : AppAPI = AppAPI()

    override fun toString(): String {
        return "OperatorResponse(success=$success, message='$message', data=$data, app=$app)"
    }


}