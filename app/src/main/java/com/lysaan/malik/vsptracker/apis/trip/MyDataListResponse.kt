package com.lysaan.malik.vsptracker.apis.trip

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MyDataListResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean = false
    @SerializedName("message")
    @Expose
    var message: String = ""
    @SerializedName("data")
    @Expose
    var data: List<MyData>? = null
}