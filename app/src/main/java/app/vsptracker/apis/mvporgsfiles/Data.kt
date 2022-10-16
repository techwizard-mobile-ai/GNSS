package app.vsptracker.apis.mvporgsfiles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
  @SerializedName("id")
  @Expose
  var id: Int? = null
  
  @SerializedName("db_type")
  @Expose
  var dbType: Int? = null
  
  @SerializedName("org_id")
  @Expose
  var orgId: Int? = null
  
  @SerializedName("name")
  @Expose
  var name: String? = null
  
  @SerializedName("clients")
  @Expose
  var clients: Any? = null
  
  @SerializedName("details")
  @Expose
  var details: String? = null
  
  @SerializedName("status")
  @Expose
  var status: Int? = null
  
  @SerializedName("is_deleted")
  @Expose
  var isDeleted: Int? = null
  
  @SerializedName("remember_token")
  @Expose
  var rememberToken: Any? = null
  
  @SerializedName("created_at")
  @Expose
  var createdAt: String? = null
  
  @SerializedName("updated_at")
  @Expose
  var updatedAt: String? = null
  
  @SerializedName("files")
  @Expose
  var files: List<File>? = null
  override fun toString(): String {
    return "Data(" +
            "id=$id, " +
            "dbType=$dbType, " +
            "orgId=$orgId, " +
            "name=$name, " +
            "clients=$clients, " +
            "details=$details, " +
            "status=$status, " +
            "isDeleted=$isDeleted, " +
            "rememberToken=$rememberToken, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "files=$files" +
            ")"
  }
  
}
