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
    
    /**
     * This ID will be useful when we are saving different loads in OrgsLoads Table.
     * Like we can save Each Load Detail for Trimming, Production Digging, Trenching Loads, General Digging Loads
     */
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
    var isDayWorks: Int = 0
    
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
    
    var isForBackLoadResult: Boolean = false
    var isForBackUnloadResult: Boolean = false
    
    //    eWorkType 1 = General Digging
    //    eWorkType 2 = Trenching
    //    eWorkType 3 = Scraper Trimming
    var eWorkType: Int = 0
    
    //    eWorkActionType 1 = Side Casting
    //    eWorkActionType 2 = Off Loading
    var eWorkActionType: Int = 0
    
    //    nextAction 0 = Do Loading
    //    nextAction 1 = Do Unloading
    //    nextAction 2 = Do Back Loading
    //    nextAction 3 = Do Back Unloading
    var nextAction: Int = 0
    
    //    repeatJourney 0 = No Repeat Journey
    //    repeatJourney 1 = Repeat Journey without Back Load
    //    repeatJourney 2 = Repeat Journey with Back Load
    var repeatJourney: Int = 0
    
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
    //    isSync 0 = Not Uploaded to Server
    //    isSync 1 = Uploaded to Server
    //    isSync 2 = Uploaded to Server by Export
    @SerializedName("server_sync")
    @Expose
    var isSync: Int = 0
    
    var isMapOpened: Boolean = false
    override fun toString(): String {
        return "MyData(" +
                "id=$id, " +
                "orgId=$orgId, " +
                "siteId=$siteId, " +
                "loading_machine_id=$loading_machine_id, " +
                "loading_material_id=$loading_material_id, " +
                "back_loading_material_id=$back_loading_material_id, " +
                "loading_location_id=$loading_location_id, " +
                "back_loading_location_id=$back_loading_location_id, " +
                "unloading_task_id=$unloading_task_id, " +
                "back_unloading_task_id=$back_unloading_task_id" +
                ")"
    }
    
    
}
