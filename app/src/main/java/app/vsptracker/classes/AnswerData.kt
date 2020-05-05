package app.vsptracker.classes

import java.io.Serializable

class AnswerData : Serializable {
    
    var comment: String = ""
    var imagesPath = ArrayList<String>()
    override fun toString(): String {
        return "AnswerData(comment='$comment', imagesPath=$imagesPath)"
    }
    
    
}