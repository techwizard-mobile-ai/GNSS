package com.lysaan.malik.vsptracker.others

import java.io.Serializable

class EWork : Serializable {

    var ID  = 0
    var eWorkID  = 0
    var workType  = 0
    var workActionType  = 0
    var startTime = 0L
    var stopTime = 0L
    var totalTime = 0L
    var date = ""
    var time = ""
    var workMode = ""
    var machineType = 0
    var machineNumber =""
    override fun toString(): String {
        return "EWork(ID=$ID, eWorkID=$eWorkID, workType=$workType, workActionType=$workActionType, startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, date='$date', time='$time', workMode='$workMode', machineType=$machineType, machineNumber='$machineNumber')"
    }


}
