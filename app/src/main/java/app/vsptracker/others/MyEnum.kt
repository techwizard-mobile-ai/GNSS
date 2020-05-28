package app.vsptracker.others

class MyEnum {
    val ACCEPTED = 1
    companion object {
        const val BASE_URL: String = "https://vsptracker.app/api/v1/"
        const val LOGIN_URL: String = "${BASE_URL}org/users/login1"
        const val TTL: String = "43800"
        const val OPERATOR_ROLE: String = "1"
        const val ACCEPTED : String = "1"
        const val UNACCEPTED : String = "0"
        const val UNDEFINED : String = "-1"
    }
}