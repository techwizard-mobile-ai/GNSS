package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class MyData : Serializable {

    var recordID: Long = 0L
    var isStartMachine: Boolean = false

    var isForLoadResult: Boolean = false
    var isForUnloadResult: Boolean = false

    //    type = 1 excavator
    //    type = 2 scrapper
    //    type = 3 truck
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
    var backUnloadingMachine: String = ""
    var unloadingMaterial: String = ""
    var backUnloadingMaterial: String = ""
    var unloadingLocation: String = ""
    var backUnloadingLocation: String = ""
    var unloadingWeight: Double = 0.0
    var backUnloadedWeight: Double = 0.0
    var loadedMachine: String = ""
    var backLoadedMachine: String = ""
    var time: String = ""
    var date: String = ""

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

    var startTime = 0L
    var stopTime = 0L
    var totalTime = 0L

//    tripType 0 = Simple Trip
//    tripType 1 = Trip for Back Load
    var tripType: Int = 0
    var trip0ID: Int = 0

    var loadingGPSLocation = GPSLocation()
    var unloadingGPSLocation = GPSLocation()
    var userID = ""

// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
    var isSync: Int = 0

    var machineStoppedReason = ""

    var isMapOpened: Boolean = false
    override fun toString(): String {
        return "MyData(loadingMachine='$loadingMachine', backLoadingMachine='$backLoadingMachine', loadingMaterial='$loadingMaterial', backLoadingMaterial='$backLoadingMaterial', loadingLocation='$loadingLocation', backLoadingLocation='$backLoadingLocation', unloadingTask='$unloadingTask', backUnloadingTask='$backUnloadingTask', unloadingMachine='$unloadingMachine', backUnloadingMachine='$backUnloadingMachine', unloadingMaterial='$unloadingMaterial', backUnloadingMaterial='$backUnloadingMaterial', unloadingLocation='$unloadingLocation', backUnloadingLocation='$backUnloadingLocation',  workMode='$workMode', nextAction=$nextAction, repeatJourney=$repeatJourney)"
    }


}
