package app.vsptracker.apis.login

import com.google.gson.annotations.SerializedName

class AppAPI {
  @SerializedName("version_code")
  val version_code = 0
  @SerializedName("version_name")
  val version_name = ""
  @SerializedName("is_critical")
  val is_critical = 0
  @SerializedName("ttl")
  val ttl = 0
  override fun toString(): String {
    return "AppAPI(" +
            "version_code=$version_code, " +
            "version_name='$version_name', " +
            "is_critical=$is_critical, " +
            "ttl=$ttl" +
            ")"
  }
  
  
}