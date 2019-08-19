package com.lysaan.malik.vsptracker.others

import java.io.Serializable

class Meter : Serializable {

    var machineStartTime : Long = 0
    var isMachineStopped : Boolean = true
    var isMachineTimeCustom : Boolean = false
    var machineTotalTime : Long = 0

    override fun toString(): String {
        return "Meter(machineStartTime=$machineStartTime, isMachineStopped=$isMachineStopped, isMachineTimeCustom=$isMachineTimeCustom, machineTotalTime=$machineTotalTime)"
    }


}
