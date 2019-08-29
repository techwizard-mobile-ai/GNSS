package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class Data : Serializable {

    var recordID: Long = 0L

    // to be deleted; replaced by nextAction
    var isUnload: Boolean = false
    var isForBackLoad: Boolean = false
    var isForBackUnload: Boolean = false

    var isStartMachine: Boolean = false

    // to be deleted; replaced by repeatJourney
    var isRepeatJourney: Boolean = false

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

    var eWorkType: Int = 0
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
    override fun toString(): String {
        return "Data(recordID=$recordID, isUnload=$isUnload, isForBackLoad=$isForBackLoad, isForBackUnload=$isForBackUnload, isStartMachine=$isStartMachine, isRepeatJourney=$isRepeatJourney, isForLoadResult=$isForLoadResult, isForUnloadResult=$isForUnloadResult, loadedMachineType=$loadedMachineType, loadedMachineNumber='$loadedMachineNumber', isForBackLoadResult=$isForBackLoadResult, isForBackUnloadResult=$isForBackUnloadResult, loadingMachine='$loadingMachine', backLoadingMachine='$backLoadingMachine', loadingMaterial='$loadingMaterial', backLoadingMaterial='$backLoadingMaterial', loadingLocation='$loadingLocation', backLoadingLocation='$backLoadingLocation', unloadingTask='$unloadingTask', backUnloadingTask='$backUnloadingTask', unloadingMachine='$unloadingMachine', backUnloadingMachine='$backUnloadingMachine', unloadingMaterial='$unloadingMaterial', backUnloadingMaterial='$backUnloadingMaterial', unloadingLocation='$unloadingLocation', backUnloadingLocation='$backUnloadingLocation', unloadingWeight=$unloadingWeight, backUnloadedWeight=$backUnloadedWeight, loadedMachine='$loadedMachine', backLoadedMachine='$backLoadedMachine', time='$time', date='$date', eWorkType=$eWorkType, eWorkActionType=$eWorkActionType, workMode='$workMode', nextAction=$nextAction, repeatJourney=$repeatJourney, startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, tripType=$tripType, trip0ID=$trip0ID, loadingGPSLocation=$loadingGPSLocation, unloadingGPSLocation=$unloadingGPSLocation, userID='$userID', isSync=$isSync, machineStoppedReason='$machineStoppedReason')"
    }


}
