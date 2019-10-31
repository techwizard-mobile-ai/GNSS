package app.vsptracker.apis.operators

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import app.vsptracker.apis.login.AppAPI


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
}