package app.vsptracker.classes

import java.io.Serializable

class Meter : Serializable {
  
  var machineStartTime: Long = 0
  var hourStartTime: Long = 0
  var isMachineStopped: Boolean = true
  var isMachineStartTimeCustom: Boolean = false
  var isMachineStopTimeCustom: Boolean = false
  var machineTotalTime: Long = 0
  
  var startHours = ""
  var totalHours = ""
  var dailyModeStartTime: Long = 0
  var dailyModeTotalTime: Long = 0
  var isDailyModeStarted: Boolean = false
  var machineDbID: Long = 0L
  
  var delayStartTime: Long = 0
  var delayStopTime: Long = 0
  var delayTotalTime: Long = 0
  var delayStartGPSLocation = GPSLocation()
  var delayStopGPSLocation = GPSLocation()
  var hourStartGPSLocation = GPSLocation()
  var hourStopGPSLocation = GPSLocation()
  var isDelayStarted: Boolean = false
  var machineHourDbID: Long = 0L
  var delayDbID: Long = 0L
  override fun toString(): String {
    return "Meter(machineStartTime=$machineStartTime, hourStartGPSLocation=$hourStartGPSLocation, delayStartGPSLocation=$delayStartGPSLocation hourStartTime=$hourStartTime, isMachineStartTimeCustom=$isMachineStartTimeCustom, isMachineStopTimeCustom=$isMachineStopTimeCustom, machineTotalTime=$machineTotalTime)"
  }
  
  
}
