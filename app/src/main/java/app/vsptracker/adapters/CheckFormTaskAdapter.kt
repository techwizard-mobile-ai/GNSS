package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.CheckFormTaskActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.AnswerData
import app.vsptracker.classes.CheckFormData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_check_form_task.view.*
import java.util.*


class CheckFormTaskAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<CheckFormTaskAdapter
.ViewHolder>() {
    
    private lateinit var myItemView: View
    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_check_form_task, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }
    
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val datum = dataList[position]
        
        myItemView = holder.itemView
//        myPosition = position
        holder.itemView.cft_question.text = "${datum.name}"
        
        holder.itemView.cft_item_acceptable.setOnClickListener {
            myHelper.log(datum.toString())
            
            addCheckFormData(datum.id, "1", holder.itemView.cft_comment.text.toString())
            
            holder.itemView.cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_background)
            holder.itemView.cft_item_acceptable.setTextColor(context.resources.getColor(R.color.white))
            datum.acceptableChecked = true
            
            holder.itemView.cft_comment.visibility = View.GONE
            holder.itemView.cft_photo_layout.visibility = View.GONE
            myHelper.hideKeyboard(holder.itemView.cft_item_acceptable)
            (context as CheckFormTaskActivity).showSaveLayout()
            
            when (datum.unacceptableChecked) {
                true -> {
                    holder.itemView.cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_border)
                    holder.itemView.cft_item_unacceptable.setTextColor(context.resources.getColor(R.color.light_red))
                    datum.unacceptableChecked = false
                }
            }
        }
        
        holder.itemView.cft_comment.setOnClickListener {
            myHelper.showKeyboard(holder.itemView.cft_comment)
            (context as CheckFormTaskActivity).hideSaveLayout()
        }
        
        
        holder.itemView.cft_comment.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                myHelper.showKeyboard(holder.itemView.cft_comment)
                addCheckFormData(datum.id, "-1", holder.itemView.cft_comment.text.toString())
                (context as CheckFormTaskActivity).hideSaveLayout()
            }
            
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }
            
            override fun afterTextChanged(s: Editable) {}
        })
        
        
        
        holder.itemView.cft_row.setOnClickListener {
            myHelper.hideKeyboard(holder.itemView.cft_row)
            (context as CheckFormTaskActivity).showSaveLayout()
        }
        holder.itemView.cft_item_unacceptable.setOnClickListener {
            myHelper.log(datum.toString())
            addCheckFormData(datum.id, "0", holder.itemView.cft_comment.text.toString())
            
            holder.itemView.cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_background)
            holder.itemView.cft_item_unacceptable.setTextColor(context.resources.getColor(R.color.white))
            datum.unacceptableChecked = true
            
            myHelper.hideKeyboard(holder.itemView.cft_item_unacceptable)
            (context as CheckFormTaskActivity).showSaveLayout()
            when (datum.admin_questions_types_id) {
                2 -> {
                    //show comments section
                    holder.itemView.cft_comment.visibility = View.VISIBLE
                    myHelper.showKeyboard(holder.itemView.cft_comment)
                    context.hideSaveLayout()
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                    
                }
                3 -> {
                    //show photo section
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                    context.showSaveLayout()
                }
                4 -> {
                    //show comments section
                    holder.itemView.cft_comment.visibility = View.VISIBLE
                    myHelper.showKeyboard(holder.itemView.cft_comment)
                    context.hideSaveLayout()
                    
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                }
            }
            
            when (datum.acceptableChecked) {
                true -> {
                    holder.itemView.cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_border)
                    holder.itemView.cft_item_acceptable.setTextColor(context.resources.getColor(R.color.light_colorPrimary))
                    datum.acceptableChecked = true
                }
            }
            
        }
        
        holder.itemView.cft_item_capture.setOnClickListener {
            myHelper.log("capture photo")
//            myHelper.toast("Under development")
            takePhoto(position)
            myHelper.hideKeyboard(holder.itemView.cft_item_capture)
            (context as CheckFormTaskActivity).showSaveLayout()
        }
        holder.itemView.cft_item_attachment.setOnClickListener {
            myHelper.log("photo attachment")
//            myHelper.toast("Under development")
            showFileChooser(position)
            myHelper.hideKeyboard(holder.itemView.cft_item_capture)
            (context as CheckFormTaskActivity).showSaveLayout()
            
        }
    }
    fun takePhoto(position: Int) {
        myHelper.log("takePhoto:$position")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) != null) {
            (context as CheckFormTaskActivity).mAdapterPostion = position
            context.startActivityForResult(cameraIntent, (context as CheckFormTaskActivity).CAMERA_REQUEST)
        } else {
            myHelper.toast("Camera not available.")
        }
    }
    
    private fun showFileChooser(position: Int) {
        myHelper.log("showFileChooser:$position")
        (context as CheckFormTaskActivity).mAdapterPostion = position
        val selectFileIntent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectFileIntent.type = "image/*"
        context.startActivityForResult(Intent.createChooser(selectFileIntent, "SelectFile"), (context as CheckFormTaskActivity).PICK_FILE_REQUEST)
    }
 
    fun addCheckFormData(questionID: Int, answer: String, comment: String) {
        
        val checkFormData = CheckFormData()
        checkFormData.time = System.currentTimeMillis().toString()
        checkFormData.admin_questions_id = questionID
        val answerData = AnswerData()
        answerData.comment = comment
        
        
        val data = (context as CheckFormTaskActivity).checkFormDataList.find { it.admin_questions_id == questionID }
        myHelper.log("data:$data")
        if (data == null) {
            checkFormData.answer = answer
            checkFormData.answerDataObj = answerData
            // question data is not already saved in list. Add data to list
            (context as CheckFormTaskActivity).checkFormDataList.add(checkFormData)
        } else {
            if (!answer.isBlank())
                checkFormData.answer = answer
            else
                checkFormData.answer = data.answer
            // questions data is already in the list. Update data to list
            checkFormData.answerDataObj = answerData
            val index: Int = (context as CheckFormTaskActivity).checkFormDataList.indexOf(data)
            (context as CheckFormTaskActivity).checkFormDataList[index] = checkFormData
        }
    }
    
    override fun getItemCount(): Int {
        return dataList.size
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}

