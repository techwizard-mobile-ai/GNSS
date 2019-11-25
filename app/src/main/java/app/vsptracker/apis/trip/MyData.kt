package app.vsptracker.apis.trip

import app.vsptracker.classes.GPSLocation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MyData : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("org_id")
    @Expose
    var orgId: Int = 0

    @SerializedName("site_id")
    @Expose
    var siteId: Int = 0

    @SerializedName("operator_id")
    @Expose
    var operatorId: Int = 0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("is_running")
    @Expose
    var isRunning: Int = 0

    @SerializedName("machine_type_id")
    @Expose
    var machineTypeId: Int = 0


    @SerializedName("load_type_id")
    @Expose
    var loadTypeId: Int = 0

    @SerializedName("machine_id")
    @Expose
    var machineId: Int = 0

    @SerializedName("loading_gps")
    @Expose
    var loadingGPSLocationString = ""

    @SerializedName("unloading_gps")
    @Expose
    var unloadingGPSLocationString = ""

    @SerializedName("start_time")
    @Expose
    var startTime = 0L

    @SerializedName("end_time")
    @Expose
    var stopTime = 0L

    var machineNumber = ""

    @SerializedName("start_hours")
    @Expose
    var startHours = ""

    @SerializedName("total_hours")
    @Expose
    var totalHours = ""


    @SerializedName("is_start_hours_custom")
    @Expose
    var isStartHoursCustom = 0

    @SerializedName("is_total_hours_custom")
    @Expose
    var isTotalHoursCustom = 0


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
    var isDaysWork: Int = 0


    @SerializedName("loading_machine_id")
    @Expose
    var loading_machine_id: Int = 0
    var back_loading_machine_id: Int = 0


    @SerializedName("loading_material_id")
    @Expose
    var loading_material_id: Int = 0
    var back_loading_material_id: Int = 0


    @SerializedName("loading_location_id")
    @Expose
    var loading_location_id: Int = 0
    var back_loading_location_id: Int = 0

    //    Stockpile = 1
//    Fill = 2
//    Respread = 3
//    Off site = 4
    @SerializedName("unloading_task_id")
    @Expose
    var unloading_task_id: Int = 0
    var back_unloading_task_id: Int = 0


    @SerializedName("unloading_machine_id")
    @Expose
    var unloading_machine_id: Int = 0
    var back_unloading_machine_id: Int = 0


    @SerializedName("unloading_material_id")
    @Expose
    var unloading_material_id: Int = 0
    var back_unloading_material_id: Int = 0


    @SerializedName("unloading_location_id")
    @Expose
    var unloading_location_id: Int = 0
    var back_unloading_location_id: Int = 0


    @SerializedName("unloading_weight")
    @Expose
    var unloadingWeight: Double = 0.0


//    Operator Logout = -1
//    Machine Changed = -2
    @SerializedName("machine_stop_reason_id")
    @Expose
    var machine_stop_reason_id: Int = 0


    var recordID: Long = 0L
    var isStartMachine: Boolean = false

    var isForLoadResult: Boolean = false
    var isForUnloadResult: Boolean = false

    //    machineTypeId = 1 excavator
    //    machineTypeId = 2 scrapper
    //    machineTypeId = 3 truck
    var loadedMachineType: Int = 0

    var loadedMachineNumber: String = ""
    var isForBackLoadResult: Boolean = false
    var isForBackUnloadResult: Boolean = false

    var loadingMachine: String = ""
    var backLoadingMachine: String = ""

    var loadingMaterial: String = ""
    var backLoadingMaterial: String = ""

    var loadingLocation: String = ""
    var backLoadingLocation: String = ""

    var unloadingTask: String = ""
    var backUnloadingTask: String = ""

    var unloadingMachine: String = ""
    //    var unloadingMachine: String = ""

    var unloadingMaterial: String = ""
    var backUnloadingMaterial: String = ""

    var unloadingLocation: String = ""
    var backUnloadingLocation: String = ""

    //    var unloadingWeight: Double = 0.0
    var loadedMachine: String = ""
//    var loadedMachine: String = ""
//    var time: String = ""
//    var date: String = ""

    //    eWorkType 1 = General Digging
//    eWorkType 2 = Trenching
//    eWorkType 3 = Scraper Trimming
    var eWorkType: Int = 0

    //    eWorkActionType 1 = Side Casting
//    eWorkActionType 2 = Off Loading
    var eWorkActionType: Int = 0

    var workMode: String = ""

    // nextAction 0 = Do Loading
    // nextAction 1 = Do Unloading
    // nextAction 2 = Do Back Loading
    // nextAction 3 = Do Back Unloading
    var nextAction: Int = 0

    //    repeatJourney 0 = No Repeat Journey
//    repeatJourney 1 = Repeat Journey without Back Load
//    repeatJourney 2 = Repeat Journey with Back Load
    var repeatJourney: Int = 0

//    var startTime = 0L
//    var stopTime = 0L
//    var totalTime = 0L

    //    tripType 0 = Simple Trip
//    tripType 1 = Trip for Back Load
    @SerializedName("trip_type")
    @Expose
    var tripType: Int = 0

    @SerializedName("trip0_id")
    @Expose
    var trip0ID: String = ""

    var loadingGPSLocation = GPSLocation()
    var backLoadingGPSLocation = GPSLocation()

    var unloadingGPSLocation = GPSLocation()
    var backUnloadingGPSLocation = GPSLocation()
//    var userID = ""

//    isSync 0 = Not Uploaded to Server
//    isSync 1 = Uploaded to Server
//    isSync 2 = Uploaded to Server by Export
    @SerializedName("server_sync")
    @Expose
    var isSync: Int = 0

    var machineStoppedReason = ""

    var isMapOpened: Boolean = false
    override fun toString(): String {
        return "MyData(" +
                "siteId=$siteId, " +
                "machineTypeId=$machineTypeId, " +
                "machineId=$machineId, " +
                "machineStoppedReason='$machineStoppedReason', " +
                "startTime='$startTime', " +
                "stopTime=$stopTime" +
                "totalTime=$totalTime" +
                "time=$time" +
                "date=$date" +
                "getWorkMode=$workMode" +
                "totalTime=$totalTime" +

                "loadingGPSLocation=$loadingGPSLocation" +
                "unloadingGPSLocation=$unloadingGPSLocation" +
                "userID=$operatorId" +
                "isSync=$isSync" +
                ")"
    }
}
