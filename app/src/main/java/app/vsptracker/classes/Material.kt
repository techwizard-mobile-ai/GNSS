package app.vsptracker.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Material : Serializable {

    @SerializedName ("id")
    var id: Int = 0

    @SerializedName("org_id")
    var orgId:Int = 0

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

    constructor() {}

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    override fun toString(): String {
        return "Material(id=$id, orgId=$orgId, siteId=$siteId, machineTypeId=$machineTypeId, machineBrandId=$machineBrandId, machinePlantId=$machinePlantId, machineTaskId=$machineTaskId, isDeleted=$isDeleted, status=$status, totalHours=$totalHours, pin=$pin, name='$name', number='$number')"
    }


}
