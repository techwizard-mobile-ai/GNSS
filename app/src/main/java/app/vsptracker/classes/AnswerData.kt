package app.vsptracker.classes

import java.io.Serializable

class AnswerData : Serializable {
  
  var comment: String = ""
  var imagesList = ArrayList<Images>()
  override fun toString(): String {
    return "AnswerData(comment='$comment', imagesList=$imagesList)"
  }
  
}