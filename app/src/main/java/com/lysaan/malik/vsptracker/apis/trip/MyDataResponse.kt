package com.lysaan.malik.vsptracker.apis.trip

import com.google.gson.annotations.SerializedName

data class MyDataResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : MyData,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "MyDataResponse(success=$success, data=$data, message='$message')"
    }
}