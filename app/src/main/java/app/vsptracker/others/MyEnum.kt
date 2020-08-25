package app.vsptracker.others

class MyEnum {
    val ACCEPTED = 1
    
    companion object {
        val user = ""
        val pass = ""
        val loginPin = ""
        
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

        
        const val BASE_URL: String = "https://vsptracker.app/api/v1/"
        const val LOGIN_URL: String = "${BASE_URL}org/users/login1"
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
        
    
        
        
        
        
    }
}