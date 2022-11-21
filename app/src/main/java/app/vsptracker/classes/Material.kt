package app.vsptracker.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Material : Serializable {
  
  @SerializedName("id")
  var id: Int = 0
  
  @SerializedName("org_id")
  var orgId: Int = 0
  
  @SerializedName("site_id")
  @Expose
  var siteId: Int = 0
  
  @SerializedName("machine_type_id")
  @Expose
  var machineTypeId: Int = 0
  
  @SerializedName("machine_brand_id")
  @Expose
  var machineBrandId: Int = 0
  
  @SerializedName("machine_plant_id")
  @Expose
  var machinePlantId: Int = 0
  
  @SerializedName("task_id")
  @Expose
  var machineTaskId: Int = 0
  
  @SerializedName("is_deleted")
  @Expose
  var isDeleted: Int = 0
  
  @SerializedName("status")
  @Expose
  var status: Int = 1
  
  @SerializedName("total_hours")
  @Expose
  var totalHours: String? = null
  
  
  @SerializedName("pin")
  @Expose
  var pin: String? = null
  
  
  @SerializedName("name")
  var name: String = ""
  
  @SerializedName("number")
  var number: String = ""
  
  
  @SerializedName("priority")
  @Expose
  var priority: Int = -1
  
  @SerializedName("relative_path")
  @Expose
  var relative_path: String = ""
  
  @SerializedName("aws_path")
  @Expose
  var aws_path: String = ""
  
  @SerializedName("admin_file_type_id")
  @Expose
  var admin_file_type_id: Int = -1
  
  @SerializedName("is_favorite")
  @Expose
  var is_favorite: Int = 0
  
  constructor()
  
  constructor(id: Int, name: String) {
    this.id = id
    this.number = name
  }
  
  constructor(id: Int, name: String, admin_file_type_id: Int) {
    this.id = id
    this.number = name
    this.admin_file_type_id = admin_file_type_id
  }
  
  override fun toString(): String {
    return "Material(" +
            "id=$id, " +
            "number='$number', " +
            "admin_file_type_id=$admin_file_type_id" +
            "is_favorite=$is_favorite" +
            ")"
  }
  
  
}
