package app.vsptracker.classes

import java.io.Serializable

class AnswerData : Serializable {
    
    var comment: String = ""
    var imagesPaths = ArrayList<String>()
    var awsImagesPaths = ArrayList<String>()
    
    override fun toString(): String {
        return "AnswerData(comment='$comment', imagesPaths=$imagesPaths, awsImagesPaths=$awsImagesPaths)"
    }
    
}