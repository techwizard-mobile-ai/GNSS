package app.vsptracker.classes

import java.io.Serializable

class AnswerData : Serializable {
    
    // 0 Unacceptable, 1 Acceptable
    var answer: Int = -1
    var comment: String = ""
    var imagesPath = ArrayList<String>()
    override fun toString(): String {
        return "AnswerData(answer=$answer, comment='$comment', imagesPath=$imagesPath)"
    }
    
    
}