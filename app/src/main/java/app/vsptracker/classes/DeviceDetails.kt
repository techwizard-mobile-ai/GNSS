package app.vsptracker.classes

import android.os.Build
import app.vsptracker.BuildConfig
import java.io.Serializable

class DeviceDetails : Serializable {
    
    val VSPT_VERSION_CODE = BuildConfig.VERSION_CODE
    val MANUFACTURER: String = Build.MANUFACTURER
    val DEVICE: String = Build.DEVICE
    val BRAND: String = Build.BRAND
    val MODEL: String = Build.MODEL
    val ANDROID_SDK_API = Build.VERSION.SDK_INT
    override fun toString(): String {
        return "DeviceDetails(" +
                "VSPT_VERSION_CODE=$VSPT_VERSION_CODE, " +
                "MANUFACTURER='$MANUFACTURER', " +
                "DEVICE='$DEVICE', " +
                "BRAND='$BRAND', " +
                "MODEL='$MODEL', " +
                "ANDROID_SDK_API=$ANDROID_SDK_API" +
                ")"
    }
    
    
}