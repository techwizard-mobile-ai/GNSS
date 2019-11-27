package app.vsptracker.apis.delay

import app.vsptracker.classes.GPSLocation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class EWork() : Serializable {


    @SerializedName("org_id")
    @Expose
    var orgId : Int = 0

    @SerializedName("site_id")
    @Expose
    var siteId: Int = 0

    @SerializedName("operator_id")
    @Expose
    var operatorId :Int = 0

    @SerializedName("machine_type_id")
    @Expose
    var machineTypeId : Int = 0

    @SerializedName("machine_id")
    @Expose
    var machineId : Int = 0

    @SerializedName("material_id")
    @Expose
    var materialId : Int = 0

    @SerializedName("total_loads")
    @Expose
    var totalLoads : Int = 0

    @SerializedName("loading_gps")
    @Expose
    var loadingGPSLocationString = ""

    var loadingGPSLocation = GPSLocation()

    @SerializedName("unloading_gps")
    @Expose
    var unloadingGPSLocationString = ""

    var unloadingGPSLocation = GPSLocation()


    @SerializedName("start_time")
    @Expose
    var startTime = 0L

    @SerializedName("end_time")
    @Expose
    var stopTime = 0L

    @SerializedName("start_hours")
    @Expose
    var startHours = ""

    @SerializedName("total_hours")
    @Expose
    var totalHours = ""

    @SerializedName("total_time")
    @Expose
    var totalTime = 0L


    @SerializedName("time")
    @Expose
    var time = ""

    @SerializedName("date")
    @Expose
    var date = ""


    @SerializedName("is_days_work")
    @Expose
    var isDayWorks : Int = 0
//    var workMode = ""

//    @SerializedName("id")
//    @Expose
    var id = 0

    var eWorkId = 0

    // eWorkType 3 = Scraper Trimming
    // eWorkType 2 = Trenching
    // eWorkType 1 = General Digging
    @SerializedName("work_type")
    @Expose
    var workType = 0


    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    @SerializedName("work_action_type")
    @Expose
    var workActionType = 0




//    var machineTypeId = 0
    var machineNumber = ""



// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
    var isSync: Int = 0

    override fun toString(): String {
        return "EWork(orgId=$orgId, siteId=$siteId, operatorId=$operatorId, machineTypeId=$machineTypeId, materialId=$materialId, machineId=$machineId, loadingGPSLocationString='$loadingGPSLocationString', loadingGPSLocation=$loadingGPSLocation, unloadingGPSLocationString='$unloadingGPSLocationString', unloadingGPSLocation=$unloadingGPSLocation, startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, time='$time', date='$date', isDaysWork=$isDayWorks, id=$id, eWorkId=$eWorkId, workType=$workType, workActionType=$workActionType, machineNumber='$machineNumber', isSync=$isSync)"
    }


}
