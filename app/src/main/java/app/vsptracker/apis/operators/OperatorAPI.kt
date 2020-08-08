package app.vsptracker.apis.operators

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OperatorAPI : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("org_id")
    @Expose
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

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("site_name")
    @Expose
    var siteName: String? = null

    @SerializedName("number")
    @Expose
    var number: String? = null

    @SerializedName("total_hours")
    @Expose
    var totalHours: String? = null

    @SerializedName("auto_logout_time")
    @Expose
    var autoLogoutTime: String? = null

    @SerializedName("pin")
    @Expose
    var pin: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Int? = null
    @SerializedName("auth_token")
    @Expose
    var authToken: Any? = null

    @SerializedName("remember_token")
    @Expose
    var rememberToken: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    
    @SerializedName("priority")
    @Expose
    var priority: Int = -1
    
    override fun toString(): String {
        return "OperatorAPI(" +
                "id=$id, " +
                "name=$name, " +
                "priority=$priority, " +
                "status=$status, " +
                "isDeleted=$isDeleted, " +
                ")"
    }
    
    
}