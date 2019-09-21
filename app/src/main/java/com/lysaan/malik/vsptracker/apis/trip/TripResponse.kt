package com.lysaan.malik.vsptracker.apis.trip

import com.google.gson.annotations.SerializedName

data class TripResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : MyData,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "DelayResponse(success=$success, data=$data, message='$message')"
    }
}