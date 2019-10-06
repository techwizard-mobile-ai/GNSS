package com.lysaan.malik.vsptracker.apis.operators

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
}