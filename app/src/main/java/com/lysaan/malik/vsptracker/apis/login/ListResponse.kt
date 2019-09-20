package com.lysaan.malik.vsptracker.apis.login

import com.google.gson.annotations.SerializedName
import com.lysaan.malik.vsptracker.classes.Material

data class ListResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : ArrayList<Material>,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "LoginResponse(success=$success, data=$data, message='$message')"
    }
}