package app.vsptracker.apis.mvporgsfiles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeparateFolders {
  @SerializedName("id")
  @Expose
  var id: Int? = null
  
  @SerializedName("db_type")
  @Expose
  var dbType: Int? = null
  
  @SerializedName("org_id")
  @Expose
  var orgId: Int? = null
  
  @SerializedName("admin_file_type_id")
  @Expose
  var adminFileTypeId: Int? = null
  
  @SerializedName("processing_status")
  @Expose
  var processingStatus: Int? = null
  
  @SerializedName("file_level")
  @Expose
  var fileLevel: Int? = null
  
  @SerializedName("security_level")
  @Expose
  var securityLevel: Int? = null
  
  @SerializedName("aws_path")
  @Expose
  var awsPath: String? = null
  
  @SerializedName("relative_path")
  @Expose
  var relativePath: String? = null
  
  @SerializedName("size")
  @Expose
  var size: Int? = null
  
  @SerializedName("file_details")
  @Expose
  var fileDetails: String? = null
  
  @SerializedName("file_description")
  @Expose
  var fileDescription: String? = null
  
  @SerializedName("loading_gps")
  @Expose
  var loadingGps: Any? = null
  
  @SerializedName("upload_status")
  @Expose
  var uploadStatus: Int? = null
  
  @SerializedName("status")
  @Expose
  var status: Int? = null
  
  @SerializedName("is_deleted")
  @Expose
  var isDeleted: Int? = null
  
  @SerializedName("remember_token")
  @Expose
  var rememberToken: Any? = null
  
  @SerializedName("created_at")
  @Expose
  var createdAt: String? = null
  
  @SerializedName("updated_at")
  @Expose
  var updatedAt: String? = null
  
  @SerializedName("fileFolder")
  @Expose
  var fileFolder: FileFolder? = null
  
  @SerializedName("download_percent_completed")
  @Expose
  var downloadPercentCompleted: Int? = null
  override fun toString(): String {
    return "SeparateFolders(" +
            "id=$id, " +
            "dbType=$dbType, " +
            "orgId=$orgId, " +
            "adminFileTypeId=$adminFileTypeId, " +
            "processingStatus=$processingStatus, " +
            "fileLevel=$fileLevel, " +
            "securityLevel=$securityLevel, " +
            "awsPath=$awsPath, " +
            "relativePath=$relativePath, " +
            "size=$size, " +
            "fileDetails=$fileDetails, " +
            "fileDescription=$fileDescription, " +
            "loadingGps=$loadingGps, " +
            "uploadStatus=$uploadStatus, " +
            "status=$status, " +
            "isDeleted=$isDeleted, " +
            "rememberToken=$rememberToken, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "fileFolder=$fileFolder, " +
            "downloadPercentCompleted=$downloadPercentCompleted" +
            ")"
  }
  
}
