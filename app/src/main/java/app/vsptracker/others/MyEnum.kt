package app.vsptracker.others

class MyEnum {
  companion object {
    const val user = ""
    const val pass = ""
    const val loginPin = ""

//        val user = "zee.enterprises@mail.com"
//        val pass = "user1@123"
//        val loginPin = "OP1"


//        val user = "kamil.vspt@gmail.com"
//        val pass = "VSPTuser1@123"
//        val loginPin = "AA0101"

//        val user = "vsptCambridgeConstruction@gmail.com"
//        val pass = "VsptCC123#"
//        val loginPin = "AA0101"
//        val loginPin = "AA9999"
    
    
    //        const val BASE_URL: String = "https://vsptracker.app/api/v1/"
//        const val BASE_URL: String = app.vsptracker.BuildConfig.BASE_URL
//        const val LOGIN_URL: String = "${BASE_URL}org/users/login1"
    const val TTL: String = "43800" // 43200 minutes = 30 days
    const val ROLE_OPERATOR: String = "1"
    const val ACCEPTED: String = "1"
    const val UNACCEPTED: String = "0"
    const val UNDEFINED: String = "-1"
    
    const val REQUEST_ACCESS_FINE_LOCATION: Int = 1
    const val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 7
    
    const val CHECKFORMS_IMAGES = "checkforms/images"
    
    // constant should be declared here for any value and used in app
    const val ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START = 3
    const val ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START_ONE_TIME = 4
    
    const val ADMIN_CHECKFORMS_DUE = 0
    const val ADMIN_CHECKFORMS_ALL = 1
    const val ADMIN_CHECKFORMS_COMPLETED = 2
    const val ADMIN_CHECKFORMS_COMPLETED_DETAILS = 3
    const val ADMIN_CHECKFORMS_COMPLETED_SERVER = 4
    
    const val ONE_DAY = 86400000 // Milliseconds in a day
    
    const val EXCAVATOR = 1
    const val SCRAPER = 2
    const val TRUCK = 3
    
    const val EXCAVATOR_GEN_DIGGING = 1
    const val EXCAVATOR_TRENCHING = 2
    const val SCRAPER_TRIMMING = 3
    
    const val TASK_STOCKPILE = "Stockpile"
    const val TASK_FILL = "Fill"
    const val TASK_RESPREAD = "Respread"
    const val TASK_OFFSITE = "Off site"
    const val TASK_CRUSHINGPLANT = "Crushing Plant"
    
    const val STOP_REASON_WEATHER = "Weather"
    const val STOP_REASON_OTHER1 = "Other 1"
    
    const val WORKER_TAG_AUTO_LOGOUT = "vspt_machine_auto_logout"
    const val WORKER_TAG_SERVER_SYNC = "vspt_server_sync"
    const val WORKER_TAG_KILL_OTHER_APPS = "vspt_kill_other_apps"
    
    const val SERVER_SYNC_DATA_BACKGROUND = 1 // Do all tasks in background
    const val SERVER_SYNC_DATA_LOGOUT = 2 // Do all tasks in background but don't exclude CheckFormsCompleted as it is logout call
    const val SERVER_SYNC_DATA_DIALOG = 3 // Do all tasks and Show Dialog as user has to wait for response
    const val SERVER_SYNC_UPDATE_MACHINE_STATUS = 4 // To update machine status call must make API request to server
    
    const val LOGOUT_TYPE_OPERATOR = -1
    const val LOGOUT_TYPE_MACHINE_CHANGED = -2
    const val LOGOUT_TYPE_AUTO = -3
    const val WORKER_TYPE_AUTO_LOGOUT = 1
    const val WORKER_TYPE_APP_LOCKS = 2
    
    const val MVP = "app.mvp"
    const val VSPT = "app.vsptracker"
    
    const val MOBILE_API_TYPE_GET_ALL: Int = 0
    const val MOBILE_API_TYPE_GET_VSPT_ALL: Int = 1
    const val MOBILE_API_TYPE_GET_MVP_ALL: Int = 2
    const val MOBILE_API_TYPE_GET_VSPT_DATA: Int = 3
    const val MOBILE_API_TYPE_GET_MVP_DATA: Int = 4
    
    const val SETTINGS_TYPE_MVP_SCAN = 1
    const val SETTINGS_TYPE_MVP_SURVEY = 2
    const val SETTINGS_TYPE_MVP_RECORD_CHECKPOINT = 3
    
    const val ADMIN_FILE_TYPE_TAPU_CHECKPOINT = 1
    const val ADMIN_FILE_TYPE_TAPU_SCAN = 2
    const val ADMIN_FILE_TYPE_TAPU_SURVEY_POINT = 3
    const val ADMIN_FILE_TYPE_TAPU_SURVEY_LINE = 4
    
    const val APP_SETTINGS_PICTURES_UPLOAD_AUTOMATICALLY = 1
    const val APP_SETTINGS_PICTURES_UPLOAD_MANUALLY = 2
    
    //    const val MVP_ZOOM_LEVEL: Float = 19.0f
    const val MVP_ZOOM_LEVEL: Float = 21.0f
    
    const val USER_ROLE_MAIN_SUPER_ADMIN = 21 // Not implemented as of yet, might be used in Future
    const val USER_ROLE_VSPT_SUPER_ADMIN = 15 // Admin Portal Login for VSPT
    const val USER_ROLE_MVP_SUPER_ADMIN = 45 // Admin Portal Login for MVP
    const val USER_ROLE_VSPT_COMPANY_SUPER_ADMIN = 1 // Company Admin Login for VSPT companies
    const val USER_ROLE_VSPT_COMPANY_ADMIN = 6 // Company Admin Login for VSPT companies
    const val USER_ROLE_VSPT_COMPANY_REFUELLING_COMPANY_ADMIN = 12  // Refuelling Company Admin which will be used for refuelling to VSPT Companies
    const val USER_ROLE_MVP_COMPANY_SUPER_ADMIN = 31 // Company Admin Login for MVP companies
    const val USER_ROLE_MVP_COMPANY_ADMIN = 36  // MVP Company Admin for MVP companies. Changing it from 44 to 36 as we might need extra roles between company admin and standard users.
    const val USER_ROLE_MVP_COMPANY_STANDARD_USER = 46 // MVP Standard User
    const val USER_ROLE_MVP_COMPANY_CONTRACTOR = 47 // MVP Contractor
  }
}