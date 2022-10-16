package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.classes.CheckFormData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_check_forms_data.view.*

class CheckFormsDataAdapter(
  val context: Activity,
  private val dataList: ArrayList<CheckFormData>
) : RecyclerView.Adapter<CheckFormsDataAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_row_check_forms_data, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val datum = dataList[position]
    myHelper.log(datum.toString())
    holder.itemView.cfd_question.text = "${db.getQuestionByID(datum.admin_questions_id).name}"
    
    holder.setIsRecyclable(false)
    
    when (datum.answer) {
      "1" -> {
        holder.itemView.cfd_answer.text = context.resources.getString(R.string.acceptable)
      }
      "0" -> {
        holder.itemView.cfd_answer.text = context.resources.getString(R.string.unacceptable)
        holder.itemView.cfd_answer.setTextColor(ContextCompat.getColor(context, R.color.red))
        when (datum.answerDataObj.comment.length) {
          0 -> {
            holder.itemView.cfd_comment.visibility = View.GONE
          }
          else -> {
            holder.itemView.cfd_comment.text = datum.answerDataObj.comment
            holder.itemView.cfd_comment.visibility = View.VISIBLE
            holder.itemView.cfd_comment.setTextColor(ContextCompat.getColor(context, R.color.red))
          }
          
        }
        if (datum.answerDataObj.imagesList.size > 0) {
          holder.itemView.photo_layout_main.visibility = View.VISIBLE
          holder.itemView.photo_layout.visibility = View.VISIBLE
          datum.answerDataObj.imagesList.forEach {
            holder.itemView.photo_layout.addView(
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
          holder.itemView.photo_layout_main.visibility = View.GONE
          holder.itemView.photo_layout.visibility = View.GONE
        }
      }
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

