package com.lysaan.malik.vsptracker.apis.delay

import com.google.gson.annotations.SerializedName

data class DelayResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : EWork,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "DelayResponse(success=$success, data=$data, message='$message')"
    }
}