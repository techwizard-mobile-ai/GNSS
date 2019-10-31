package app.vsptracker.apis.delay

import com.google.gson.annotations.SerializedName

data class EWorkResponse (

    @SerializedName("success") val success : Boolean,
    @SerializedName("data") val data : EWork,
    @SerializedName("message") val message: String
){
    override fun toString(): String {
        return "EWorkResponse(success=$success, data=$data, message='$message')"
    }
}