package app.vsptracker.apis.serverSync

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ServerSyncDataAPI : Serializable {
  
  @SerializedName("token")
  @Expose
  var token = ""
  
  @SerializedName("operator_id")
  @Expose
  var operator_id: Int = 0
  
  @SerializedName("device_details")
  @Expose
  var device_details: String = ""
  
  @SerializedName("data")
  @Expose
  var data = ArrayList<ServerSyncAPI>()
  
  override fun toString(): String {
    return "ServerSyncDataAPI(" +
            "token='$token', " +
            "operator_id=$operator_id, " +
            "device_details='$device_details', " +
            "data=$data" +
            ")"
  }
}