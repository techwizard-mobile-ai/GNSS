package com.lysaan.malik.vsptracker.others

import java.io.Serializable

class Data : Serializable {

    var recordID: String =""
    var isUnload: Boolean = false
    var isStartMachine : Boolean = false
    var isRepeatJourney : Boolean = false
    var isForLoadResult : Boolean = false
    var isForUnloadResult : Boolean = false
    var machineType : Int = 0
    var loadingMachine : String = ""
    var loadingMaterial : String = ""
    var loadingLocation : String = ""
    var unloadingTask : String = ""
    var unloadingMachine : String = ""
    var unloadingMaterial : String = ""
    var unloadingLocation : String = ""
    var loadedWeight: Double = 0.0
    var loadedMachine: String = ""
    var time: String = ""
    var date: String = ""
    override fun toString(): String {
        return "Data(recordID='$recordID', isUnload=$isUnload, isStartMachine=$isStartMachine, isRepeatJourney=$isRepeatJourney, isForLoadResult=$isForLoadResult, isForUnloadResult=$isForUnloadResult, machineType=$machineType, loadingMachine='$loadingMachine', loadingMaterial='$loadingMaterial', loadingLocation='$loadingLocation', unloadingTask='$unloadingTask', unloadingMachine='$unloadingMachine', unloadingMaterial='$unloadingMaterial', unloadingLocation='$unloadingLocation', loadedWeight=$loadedWeight, loadedMachine='$loadedMachine', time='$time', date='$date')"
    }


}
