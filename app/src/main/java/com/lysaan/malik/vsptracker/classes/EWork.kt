package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class EWork() : Serializable {

    var ID = 0
    var eWorkID = 0
    var workType = 0
    var workActionType = 0
    var startTime = 0L
    var stopTime = 0L
    var totalTime = 0L
    var date = ""
    var time = ""
    var workMode = ""
    var machineType = 0
    var machineNumber = ""

    var loadingGPSLocation = GPSLocation()
    var unloadingGPSLocation = GPSLocation()
    var userID = ""

    // isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
    var isSync: Int = 0

    override fun toString(): String {
        return "EWork(ID=$ID, eWorkID=$eWorkID, workType=$workType, workActionType=$workActionType, startTime=$startTime, stopTime=$stopTime, totalTime=$totalTime, date='$date', time='$time', workMode='$workMode', machineType=$machineType, machineNumber='$machineNumber', loadingGPSLocation=$loadingGPSLocation, unloadingGPSLocation=$unloadingGPSLocation, userID='$userID', isSync=$isSync)"
    }


}
