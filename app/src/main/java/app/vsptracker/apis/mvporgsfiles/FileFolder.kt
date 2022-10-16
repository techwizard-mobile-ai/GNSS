package app.vsptracker.apis.mvporgsfiles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileFolder {
  @SerializedName("isFolder")
  @Expose
  var isFolder: Boolean? = null
  
  @SerializedName("name")
  @Expose
  var name: String? = null
  override fun toString(): String {
    return "FileFolder(" +
            "isFolder=$isFolder, " +
            "name=$name" +
            ")"
  }
  
}
