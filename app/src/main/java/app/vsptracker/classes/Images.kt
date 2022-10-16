package app.vsptracker.classes

import java.io.Serializable

class Images(var localImagePath: String, var awsImagePath: String) : Serializable {
  
  override fun toString(): String {
    return "Images(localImagePath='$localImagePath', awsImagePath='$awsImagePath')"
  }
  
}