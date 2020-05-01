package app.vsptracker.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CheckFormData : Serializable {
    
    @SerializedName("id")
    @Expose
    var id: Int = 0
    
    @SerializedName("admin_checkform_completed_id")
    @Expose
    var admin_checkform_completed_id: Int = 0
    var admin_checkform_completed_local_id: Int = 0
    
    @SerializedName("admin_questions_id")
    @Expose
    var admin_questions_id: Int = 0
    
    @SerializedName("answer_data")
    @Expose
    var answerDataString  = ""
    
    var answerDataObj  = AnswerData()
    
    @SerializedName("time")
    @Expose
    var time = ""
    
    @SerializedName("server_sync")
    @Expose
    var isSync: Int = 0
    override fun toString(): String {
        return "CheckFormData(id=$id, admin_checkform_completed_id=$admin_checkform_completed_id, admin_checkform_completed_local_id=$admin_checkform_completed_local_id, admin_questions_id=$admin_questions_id, answer_dataString='$answerDataString', answer_data=$answerDataObj, time='$time', isSync=$isSync)"
    }
    
}