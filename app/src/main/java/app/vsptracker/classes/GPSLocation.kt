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
  var antenna_height: Double = 0.0
  var locationName: String = ""
  var verticalAccuracyMeters: Float = 0F
  var speedAccuracyMetersPerSecond: Float = 0F
  var bearingAccuracyDegrees: Float = 0F
  var hasVerticalAccuracy = false
  var hasSpeedAccuracy = false
  var hasBearingAccuracy = false
  override fun toString(): String {
    return "GPSLocation(" +
            "time=$time, " +
            "hasVerticalAccuracy=$hasVerticalAccuracy, " +
            "hasSpeedAccuracy=$hasSpeedAccuracy, " +
            "hasBearingAccuracy=$hasBearingAccuracy, " +
            "bearingAccuracyDegrees=$bearingAccuracyDegrees, " +
            "speedAccuracyMetersPerSecond=$speedAccuracyMetersPerSecond, " +
            "verticalAccuracyMeters=$verticalAccuracyMeters, " +
            "elapsedRealtimeNanos=$elapsedRealtimeNanos, " +
            "provider='$provider', " +
            "speed=$speed, " +
            "bearing=$bearing, " +
            "accuracy=$accuracy, " +
            "altitude=$altitude, " +
            "longitude=$longitude, " +
            "latitude=$latitude, " +
            "antenna_height=$antenna_height, " +
            "locationName='$locationName'" +
            ")"
  }
  
}