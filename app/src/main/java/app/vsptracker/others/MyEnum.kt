package app.vsptracker.others

class MyEnum {
    val ACCEPTED = 1
    
    companion object {
//        val user = ""
//        val pass = ""
//        val loginPin = ""
        
        val user = "zee.enterprises@mail.com"
        val pass = "user1@123"
        val loginPin = "OP1"
        
        
//        val user = "kamil.vspt@gmail.com"
//        val pass = "VSPTuser1@123"
//        val loginPin = "AA0101"

//        val user = "vsptCambridgeConstruction@gmail.com"
//        val pass = "VsptCC123#"
//        val loginPin = "AA0101"
//        val loginPin = "AA9999"

        
        const val BASE_URL: String = "https://vsptracker.app/api/v1/"
        const val LOGIN_URL: String = "${BASE_URL}org/users/login1"
        const val TTL: String = "43800"
        const val OPERATOR_ROLE: String = "1"
        const val ACCEPTED: String = "1"
        const val UNACCEPTED: String = "0"
        const val UNDEFINED: String = "-1"
    
        const val REQUEST_ACCESS_FINE_LOCATION: Int = 1
        const val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 7
    }
}