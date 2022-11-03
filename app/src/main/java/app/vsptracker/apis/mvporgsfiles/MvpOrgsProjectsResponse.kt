package app.vsptracker.apis.mvporgsfiles

import app.vsptracker.apis.trip.MyData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MvpOrgsProjectsResponse {
  @SerializedName("success")
  @Expose
  var success: Boolean = false
  
  @SerializedName("message")
  @Expose
  var message: String = "Unsuccessful API call"
  
  @SerializedName("separateFolders")
  @Expose
  var separateFolders: List<SeparateFolders>? = null
  
  @SerializedName("data")
  @Expose
  var data: List<MyData>? = null
  
  @SerializedName("role")
  @Expose
  var role: String? = null
  
  @SerializedName("isAdminLoggedIn")
  @Expose
  var isAdminLoggedIn: Boolean? = null
  override fun toString(): String {
    return "MvpOrgsFilesResponse(" +
            "success=$success, " +
            "message=$message, " +
            "separateFolders=$separateFolders, " +
            "data=$data, " +
            "role=$role, " +
            "isAdminLoggedIn=$isAdminLoggedIn" +
            ")"
  }
}
