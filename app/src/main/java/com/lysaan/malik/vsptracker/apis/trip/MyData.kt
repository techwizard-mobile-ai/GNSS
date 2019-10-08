package com.lysaan.malik.vsptracker.apis.trip

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lysaan.malik.vsptracker.classes.GPSLocation
import java.io.Serializable

class MyData : Serializable {

    @SerializedName("org_id")
    @Expose
    var orgID : Int = 0

    @SerializedName("site_id")
    @Expose
    var siteId: Int = 0

    @SerializedName("operator_id")
    @Expose
    var operatorID :Int = 0

    @SerializedName("machine_type_id")
    @Expose
    var machineTypeID : Int = 0

    @SerializedName("machine_id")
    @Expose
    var machineID : Int = 0

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
    var isDaysWork : Int = 0


    @SerializedName("loading_machine_id")
    @Expose
    var loading_machine_id : Int = 0


    @SerializedName("loading_material_id")
    @Expose
    var loading_material_id : Int = 0


    @SerializedName("loading_location_id")
    @Expose
    var loading_location_id : Int = 0

//    Stockpile = 1
//    Fill = 2
//    Respread = 3
//    Off site = 4
    @SerializedName("unloading_task_id")
    @Expose
    var unloading_task_id : Int = 0


    @SerializedName("unloading_machine_id")
    @Expose
    var unloading_machine_id : Int = 0


    @SerializedName("unloading_material_id")
    @Expose
    var unloading_material_id : Int = 0


    @SerializedName("unloading_location_id")
    @Expose
    var unloading_location_id : Int = 0


    @SerializedName("unloading_weight")
    @Expose
    var unloadingWeight: Double = 0.0


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
//    var loadingMachine: String = ""
    var loadingMaterial: String = ""
//    var loadingMaterial: String = ""
    var loadingLocation: String = ""
//    var loadingLocation: String = ""
    var unloadingTask: String = ""
//    var unloadingTask: String = ""
    var unloadingMachine: String = ""
//    var unloadingMachine: String = ""
    var unloadingMaterial: String = ""
//    var unloadingMaterial: String = ""
    var unloadingLocation: String = ""
//    var unloadingLocation: String = ""

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
//    repeatJourney 2 = No Repeat Journey with Back Load
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
    var trip0ID: Int = 0

    var loadingGPSLocation = GPSLocation()
    var unloadingGPSLocation = GPSLocation()
//    var userID = ""

// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
    var isSync: Int = 0

    var machineStoppedReason = ""

    var isMapOpened: Boolean = false
    override fun toString(): String {
        return "MyData(orgID=$orgID, siteId=$siteId, operatorID=$operatorID, machineTypeID=$machineTypeID, machineID=$machineID, loadingGPSLocationString='$loadingGPSLocationString', unloadingGPSLocationString='$unloadingGPSLocationString', startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, time='$time', date='$date', isDaysWork=$isDaysWork, loading_machine_id=$loading_machine_id, loading_material_id=$loading_material_id, loading_location_id=$loading_location_id, unloading_task_id=$unloading_task_id, unloading_machine_id=$unloading_machine_id, unloading_material_id=$unloading_material_id, unloading_location_id=$unloading_location_id, unloadingWeight=$unloadingWeight, machine_stop_reason_id=$machine_stop_reason_id, recordID=$recordID, isStartMachine=$isStartMachine, isForLoadResult=$isForLoadResult, isForUnloadResult=$isForUnloadResult, loadedMachineType=$loadedMachineType, loadedMachineNumber='$loadedMachineNumber', isForBackLoadResult=$isForBackLoadResult, isForBackUnloadResult=$isForBackUnloadResult, loadingMachine='$loadingMachine', loadingMaterial='$loadingMaterial', loadingLocation='$loadingLocation', unloadingTask='$unloadingTask', unloadingMachine='$unloadingMachine', unloadingMaterial='$unloadingMaterial', unloadingLocation='$unloadingLocation', loadedMachine='$loadedMachine', eWorkType=$eWorkType, eWorkActionType=$eWorkActionType, workMode='$workMode', nextAction=$nextAction, repeatJourney=$repeatJourney, tripType=$tripType, trip0ID=$trip0ID, loadingGPSLocation=$loadingGPSLocation, unloadingGPSLocation=$unloadingGPSLocation, isSync=$isSync, machineStoppedReason='$machineStoppedReason', isMapOpened=$isMapOpened)"
    }


}
