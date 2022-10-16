package app.vsptracker.apis.serverSync

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ServerSyncResponse {
  @SerializedName("success")
  @Expose
  var success: Boolean = false
  
  @SerializedName("operator_id")
  @Expose
  var operatorID = 0
  
  @SerializedName("device_details")
  @Expose
  var deviceDetails = ""
  
  @SerializedName("data")
  @Expose
  val data = ServerSyncDataAPI()
  
  @SerializedName("get_data")
  @Expose
  val get_data = ArrayList<ServerSyncAPI>()
  
  @SerializedName("message")
  @Expose
  val message: String = ""
  
  override fun toString(): String {
    return "ServerSyncResponse(" +
            "success=$success, " +
            "operatorID=$operatorID, " +
            "deviceDetails='$deviceDetails', " +
            "data=$data, " +
            "get_data=$get_data, " +
            "message='$message'" +
            ")"
  }
}