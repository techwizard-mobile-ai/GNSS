package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class GPSLocation : Serializable {

    var time: Long = 0L
    //    var extras: Bundle = Bundle.EMPTY
    var elapsedRealtimeNanos: Long = 0L
    var provider: String = ""
    var speed: Float = 0f
    var bearing: Float = 0f
    var accuracy: Float = 0f
    var altitude: Double = 0.0
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    override fun toString(): String {
        return "GPSLocation(time=$time, elapsedRealtimeNanos=$elapsedRealtimeNanos, provider='$provider', speed=$speed, bearing=$bearing, accuracy=$accuracy, altitude=$altitude, longitude=$longitude, latitude=$latitude)"
    }


}
