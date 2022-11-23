package app.vsptracker.apis.trip

import app.vsptracker.classes.CheckFormData
import app.vsptracker.classes.GPSLocation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MyData : Serializable {
  
  @SerializedName("size")
  @Expose
  var size: Int = 0
  
  @SerializedName("security_level")
  @Expose
  var security_level: Int = 0
  
  @SerializedName("file_level")
  @Expose
  var file_level: Int = 0
  
  @SerializedName("upload_status")
  @Expose
  var upload_status: Int = 0
  
  @SerializedName("file_description")
  @Expose
  var file_description: String = ""
  
  @SerializedName("file_details")
  @Expose
  var file_details: String = "{ \"size\": 0 }"
  
  @SerializedName("relative_path")
  @Expose
  var relative_path: String = ""
  
  @SerializedName("processing_status")
  @Expose
  var processing_status: Int = -1
  
  @SerializedName("admin_file_type_id")
  @Expose
  var admin_file_type_id: Int = -1
  
  var isDownloaded: Int = 0
  
  @SerializedName("updated_at")
  @Expose
  var updated_at: String = ""
  
  @SerializedName("created_at")
  @Expose
  var created_at: String = ""
  
  @SerializedName("id")
  @Expose
  var id: Int = 0
  
  @SerializedName("org_id")
  @Expose
  var org_id: Int = 0
  
  @SerializedName("user_id")
  @Expose
  var user_id: Int = 0
  
  @SerializedName("site_id")
  @Expose
  var siteId: Int = 0
  
  @SerializedName("operator_id")
  @Expose
  var operatorId: Int = 0
  
  @SerializedName("name")
  @Expose
  var name: String? = null
  
  @SerializedName("is_running")
  @Expose
  var isRunning: Int = 0
  
  @SerializedName("machine_type_id")
  @Expose
  var machineTypeId: Int = 0
  
  /**
   * This ID will be useful when we are saving different loads in OrgsLoads Table.
   * Like we can save Each Load Detail for Trimming, Production Digging, Trenching Loads, General Digging Loads
   */
  @SerializedName("load_type_id")
  @Expose
  var loadTypeId: Int = 0
  
  @SerializedName("machine_id")
  @Expose
  var machineId: Int = 0
  
  @SerializedName("loading_gps")
  @Expose
  var loadingGPSLocationString = ""
  
  @SerializedName("unloading_gps")
  @Expose
  var unloadingGPSLocationString = ""
  
  @SerializedName("start_time")
  @Expose
  var startTime = 0L
  
  @SerializedName("end_time")
  @Expose
  var stopTime = 0L
  
  @SerializedName("start_hours")
  @Expose
  var startHours = ""
  
  @SerializedName("total_hours")
  @Expose
  var totalHours = ""
  
  
  @SerializedName("is_start_hours_custom")
  @Expose
  var isStartHoursCustom = 0
  
  @SerializedName("is_total_hours_custom")
  @Expose
  var isTotalHoursCustom = 0
  
  @SerializedName("total_time")
  @Expose
  var totalTime = 0L
  
  @SerializedName("time")
  @Expose
  var time = ""
  
  @SerializedName("date")
  @Expose
  var date = ""
  
  @SerializedName("is_days_work")
  @Expose
  var isDayWorks: Int = 0
  
  @SerializedName("loading_machine_id")
  @Expose
  var loading_machine_id: Int = 0
  var back_loading_machine_id: Int = 0
  
  @SerializedName("loading_material_id")
  @Expose
  var loading_material_id: Int = 0
  var back_loading_material_id: Int = 0
  
  @SerializedName("loading_location_id")
  @Expose
  var loading_location_id: Int = 0
  var back_loading_location_id: Int = 0
  
  @SerializedName("unloading_task_id")
  @Expose
  var unloading_task_id: Int = 0
  var back_unloading_task_id: Int = 0
  
  @SerializedName("unloading_machine_id")
  @Expose
  var unloading_machine_id: Int = 0
  var back_unloading_machine_id: Int = 0
  
  @SerializedName("unloading_material_id")
  @Expose
  var unloading_material_id: Int = 0
  var back_unloading_material_id: Int = 0
  
  
  @SerializedName("unloading_location_id")
  @Expose
  var unloading_location_id: Int = 0
  var back_unloading_location_id: Int = 0
  
  
  @SerializedName("unloading_weight")
  @Expose
  var unloadingWeight: Double = 0.0
  
  
  //    Operator Logout = -1
  //    Machine Changed = -2
  //    Machine AutoLogout = -3
  @SerializedName("machine_stop_reason_id")
  @Expose
  var machine_stop_reason_id: Int = 0
  
  var recordID: Long = 0L
  var isStartMachine: Boolean = false
  
  var isForLoadResult: Boolean = false
  var isForUnloadResult: Boolean = false
  
  var isForBackLoadResult: Boolean = false
  var isForBackUnloadResult: Boolean = false
  
  //    eWorkType 1 = General Digging
  //    eWorkType 2 = Trenching
  //    eWorkType 3 = Scraper Trimming
  var eWorkType: Int = 0
  
  //    eWorkActionType 1 = Side Casting
  //    eWorkActionType 2 = Off Loading
  var eWorkActionType: Int = 0
  
  //    nextAction 0 = Do Loading
  //    nextAction 1 = Do Unloading
  //    nextAction 2 = Do Back Loading
  //    nextAction 3 = Do Back Unloading
  var nextAction: Int = 0
  
  //    repeatJourney 0 = No Repeat Journey
  //    repeatJourney 1 = Repeat Journey without Back Load
  //    repeatJourney 2 = Repeat Journey with Back Load
  var repeatJourney: Int = 0
  
  //    tripType 0 = Simple Trip
  //    tripType 1 = Trip for Back Load
  @SerializedName("trip_type")
  @Expose
  var tripType: Int = 0
  
  @SerializedName("trip0_id")
  @Expose
  var trip0ID: String = ""
  
  var loadingGPSLocation = GPSLocation()
  var backLoadingGPSLocation = GPSLocation()
  
  var unloadingGPSLocation = GPSLocation()
  var backUnloadingGPSLocation = GPSLocation()
  
  //    isSync 0 = Not Uploaded to Server
  //    isSync 1 = Uploaded to Server
  //    isSync 2 = Uploaded to Server by Export
  @SerializedName("server_sync")
  @Expose
  var isSync: Int = 0
  
  @SerializedName("status")
  @Expose
  var status: Int = 1
  
  @SerializedName("is_deleted")
  @Expose
  var isDeleted: Int = 0
  
  @SerializedName("answers_options")
  @Expose
  var answers_options: Int = 0
  
  @SerializedName("total_correct_answers")
  @Expose
  var total_correct_answers: Int = 0
  
  @SerializedName("admin_questions_types_id")
  @Expose
  var admin_questions_types_id: Int = 0
  
  @SerializedName("questions_data")
  @Expose
  var questions_data: String = ""
  
  @SerializedName("admin_checkforms_schedules_id")
  @Expose
  var admin_checkforms_schedules_id: Int = 0
  
  @SerializedName("admin_checkforms_schedules_value")
  @Expose
  var admin_checkforms_schedules_value: String? = null
  
  var acceptableChecked = false
  var unacceptableChecked = false
  
  @SerializedName("admin_checkforms_id")
  @Expose
  var admin_checkforms_id: Int = 0
  
  @SerializedName("checkform_data")
  @Expose
  var checkFormData = ArrayList<CheckFormData>()
  
  @SerializedName("images_limit")
  @Expose
  var images_limit: Int = 0
  
  @SerializedName("images_quality")
  @Expose
  var images_quality: Int = 0
  
  @SerializedName("entry_type")
  @Expose
  var entry_type: Int = 0
  
  @SerializedName("attempted_questions")
  @Expose
  var attempted_questions: Int = 0
  
  @SerializedName("aws_path")
  @Expose
  var aws_path: String = ""
  
  var isMapOpened: Boolean = false
  var type: Int = 0
  var timer_interval: Long = 1000
  
  @SerializedName("details")
  @Expose
  var details = ""
  
  @SerializedName("project_id")
  @Expose
  var project_id: Int = 0
  
  @SerializedName("mvp_orgs_files_id")
  @Expose
  var mvp_orgs_files_id: Int = 0
  
  @SerializedName("admin_surveys_labels_type_id")
  @Expose
  var admin_surveys_labels_type_id: Int = 0
  
  @SerializedName("is_favorite")
  @Expose
  var is_favorite: Int = 0
  
  @SerializedName("presignedUrl")
  @Expose
  var presignedUrl: String = ""
  var image_path: String = ""
  
  var mvp_orgs_project_name: String = ""
  var mvp_orgs_files_name: String = ""
  
  override fun toString(): String {
    return "MyData(" +
            "id=$id, " +
            "org_id=$org_id, " +
            "user_id=$user_id, " +
            "name=$name, " +
            "aws_path='$aws_path', " +
            "relative_path='$relative_path', " +
            "file_description='$file_description', " +
            "is_favorite='$is_favorite', " +
            "loadingGPSLocationString='$loadingGPSLocationString', " +
            "status=$status, " +
            "isDeleted=$isDeleted, " +
            ")"
  }
  
}
