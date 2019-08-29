package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class Meter : Serializable {

    var machineStartTime : Long = 0
    var isMachineStopped : Boolean = true
    var isMachineTimeCustom : Boolean = false
    var machineTotalTime : Long = 0

    var dailyModeStartTime : Long = 0
    var dailyModeTotalTime : Long = 0
    var isDailyModeStarted : Boolean = false

    override fun toString(): String {
        return "Meter(machineStartTime=$machineStartTime, isMachineStopped=$isMachineStopped, isMachineTimeCustom=$isMachineTimeCustom, machineTotalTime=$machineTotalTime, dailyModeStartTime=$dailyModeStartTime, dailyModeTotalTime=$dailyModeTotalTime, isDailyModeStarted=$isDailyModeStarted)"
    }


}
