package app.vsptracker.classes

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
    var locationName: String = ""
    override fun toString(): String {
        return "GPSLocation(longitude=$longitude, latitude=$latitude)"
    }
}