package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.classes.CheckFormData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class CheckFormsDataAdapter(
  val context: Activity,
  private val dataList: ArrayList<CheckFormData>
) : RecyclerView.Adapter<CheckFormsDataAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_check_forms_data, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    val cfd_question = v.findViewById<TextView>(R.id.cfd_question)
    val cfd_answer = v.findViewById<TextView>(R.id.cfd_answer)
    val cfd_comment = v.findViewById<TextView>(R.id.cfd_comment)
    val photo_layout_main = v.findViewById<HorizontalScrollView>(R.id.photo_layout_main)
    val photo_layout = v.findViewById<LinearLayout>(R.id.photo_layout)
    
    val datum = dataList[position]
    myHelper.log(datum.toString())
    cfd_question.text = "${db.getQuestionByID(datum.admin_questions_id).name}"
    
    holder.setIsRecyclable(false)
    
    when (datum.answer) {
      "1" -> cfd_answer.text = context.resources.getString(R.string.acceptable)
      
      "0" -> {
        cfd_answer.text = context.resources.getString(R.string.unacceptable)
        cfd_answer.setTextColor(ContextCompat.getColor(context, R.color.red))
        when (datum.answerDataObj.comment.length) {
          0 -> {
            cfd_comment.visibility = View.GONE
          }
          else -> {
            cfd_comment.text = datum.answerDataObj.comment
            cfd_comment.visibility = View.VISIBLE
            cfd_comment.setTextColor(ContextCompat.getColor(context, R.color.red))
          }
          
        }
        if (datum.answerDataObj.imagesList.size > 0) {
          photo_layout_main.visibility = View.VISIBLE
          photo_layout.visibility = View.VISIBLE
          datum.answerDataObj.imagesList.forEach {
            photo_layout.addView(
              myHelper.addImageToPhotoLayout(
                context,
                null,
                Uri.parse(it.localImagePath),
                datum.answerDataObj.imagesList,
                true
              )
            )
          }
        } else {
          photo_layout_main.visibility = View.GONE
          photo_layout.visibility = View.GONE
        }
      }
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

