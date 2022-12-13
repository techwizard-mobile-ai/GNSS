package app.vsptracker.apis.serverSync

import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.ServerSyncModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ServerSyncAPI : Serializable {
  
  @SerializedName("type")
  @Expose
  var type = 0
  
  @SerializedName("name")
  @Expose
  var name = ""
  
  @SerializedName("mydata_list")
  @Expose
  var myDataList = ArrayList<MyData>()
  
  @SerializedName("ework_list")
  @Expose
  var myEWorkList = ArrayList<EWork>()
  
  @SerializedName("operatorapi_list")
  @Expose
  var operatorAPIList = ArrayList<OperatorAPI>()
  
  var serverSyncModel: ServerSyncModel? = null
  
  override fun toString(): String {
    return "\n\nServerSyncAPI(" +
            "type=$type, " +
            "name='$name', " +
            "myDataList=$myDataList, " +
            "myEWorkList=$myEWorkList, " +
            "operatorAPIList=$operatorAPIList" +
            ")"
  }
}