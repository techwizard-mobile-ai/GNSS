package app.vsptracker.apis.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginAPI : Serializable{

    @SerializedName("id") val id : Int = 0;
    @SerializedName("name") val name : String =""
    @SerializedName("email") val email : String =""
    @SerializedName("org_id") val org_id : Int = 0
    @SerializedName("auth_token") val auth_token : String =""
    var pass :String = ""
    override fun toString(): String {
        return "LoginAPI(id=$id, name='$name', email='$email', org_id=$org_id, auth_token='$auth_token', pass='$pass')"
    }


}