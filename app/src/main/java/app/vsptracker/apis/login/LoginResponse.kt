package app.vsptracker.apis.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
  
  @SerializedName("success") val success: Boolean,
  @SerializedName("data") val data: LoginAPI,
  @SerializedName("app") val app: AppAPI,
  @SerializedName("message") val message: String
) {
  
  override fun toString(): String {
    return "LoginResponse(success=$success, data=$data, app=$app, message='$message')"
  }
}