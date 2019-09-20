package com.lysaan.malik.vsptracker.apis.login

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : LoginAPI,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "LoginResponse(success=$success, data=$data, message='$message')"
    }
}