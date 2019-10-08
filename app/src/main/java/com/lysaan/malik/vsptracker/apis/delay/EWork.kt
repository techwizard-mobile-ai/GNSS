package com.lysaan.malik.vsptracker.apis.delay

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lysaan.malik.vsptracker.classes.GPSLocation
import java.io.Serializable

class EWork() : Serializable {


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

    @SerializedName("material_id")
    @Expose
    var materialID : Int = 0

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
    var workMode = ""

//    @SerializedName("id")
//    @Expose
    var ID = 0

    var eWorkID = 0

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




//    var machineTypeID = 0
    var machineNumber = ""



// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
    var isSync: Int = 0

    override fun toString(): String {
        return "EWork(orgID=$orgID, siteId=$siteId, operatorID=$operatorID, machineTypeID=$machineTypeID, machineID=$machineID, loadingGPSLocationString='$loadingGPSLocationString', loadingGPSLocation=$loadingGPSLocation, unloadingGPSLocationString='$unloadingGPSLocationString', unloadingGPSLocation=$unloadingGPSLocation, startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, time='$time', date='$date', isDaysWork=$isDaysWork, workMode='$workMode', ID=$ID, eWorkID=$eWorkID, workType=$workType, workActionType=$workActionType, machineNumber='$machineNumber', isSync=$isSync)"
    }


}
