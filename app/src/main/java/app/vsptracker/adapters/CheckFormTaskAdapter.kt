package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.CheckFormTaskActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import java.io.File
import java.util.*


class CheckFormTaskAdapter(
  val context: Activity, private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<CheckFormTaskAdapter.ViewHolder>() {
  
  private lateinit var myItemView: View
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  lateinit var cft_photo_attached_info: TextView
  lateinit var cft_photo_limit_info: TextView
  lateinit var cft_question: TextView
  lateinit var cft_item_acceptable: TextView
  lateinit var cft_item_unacceptable: TextView
  lateinit var cft_item_capture: TextView
  lateinit var cft_item_attachment: TextView
  lateinit var cft_comment: EditText
  lateinit var cft_row: LinearLayout
  lateinit var cft_photo_layout: LinearLayout
  lateinit var cft_photo_info_layout: LinearLayout
  lateinit var photo_layout: LinearLayout
  lateinit var photo_layout_main: HorizontalScrollView
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_check_form_task, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    
    cft_photo_attached_info = v.findViewById<TextView>(R.id.cft_photo_attached_info)
    cft_question = v.findViewById<TextView>(R.id.cft_question)
    cft_item_acceptable = v.findViewById<TextView>(R.id.cft_item_acceptable)
    cft_item_unacceptable = v.findViewById<TextView>(R.id.cft_item_unacceptable)
    cft_photo_limit_info = v.findViewById<TextView>(R.id.cft_photo_limit_info)
    cft_item_capture = v.findViewById<TextView>(R.id.cft_item_capture)
    cft_item_attachment = v.findViewById<TextView>(R.id.cft_item_attachment)
    cft_comment = v.findViewById<EditText>(R.id.cft_comment)
    cft_row = v.findViewById<LinearLayout>(R.id.cft_row)
    cft_photo_layout = v.findViewById<LinearLayout>(R.id.cft_photo_layout)
    cft_photo_info_layout = v.findViewById<LinearLayout>(R.id.cft_photo_info_layout)
    photo_layout = v.findViewById<LinearLayout>(R.id.photo_layout)
    photo_layout_main = v.findViewById<HorizontalScrollView>(R.id.photo_layout_main)
    
    
    val datum = dataList[position]
    myItemView = holder.itemView
    cft_question.text = "${position + 1}. ${datum.name}"
    
    resetItemView(holder, datum)
    
    cft_item_acceptable.setOnClickListener {
      myHelper.log("position:$position")
      myHelper.log(datum.toString())
      (context as CheckFormTaskActivity).addCheckFormData(datum.id, MyEnum.ACCEPTED, cft_comment.text.toString())
      
      cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_background)
      cft_item_acceptable.setTextColor(ContextCompat.getColor(context, R.color.white))
      datum.acceptableChecked = true
      
      hideCommentsPhotoLayout(holder.itemView)
      
      when (datum.unacceptableChecked) {
        true -> {
          cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_border)
          cft_item_unacceptable.setTextColor(ContextCompat.getColor(context, R.color.light_red))
          datum.unacceptableChecked = false
        }
        else -> {}
      }
      resetItemView(holder, datum)
    }
    
    cft_comment.setOnClickListener {
      myHelper.showKeyboard(cft_comment)
      (context as CheckFormTaskActivity).hideSaveLayout()
      resetItemView(holder, datum)
    }
    
    
    cft_comment.addTextChangedListener(object : TextWatcher {
      override fun onTextChanged(
        s: CharSequence, start: Int, before: Int, count: Int
      ) {
        myHelper.showKeyboard(cft_comment)
        (context as CheckFormTaskActivity).addCheckFormData(datum.id, MyEnum.UNDEFINED, cft_comment.text.toString())
        context.hideSaveLayout()
      }
      
      override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int, after: Int
      ) {
      }
      
      override fun afterTextChanged(s: Editable) {}
    })
    
    cft_row.setOnClickListener {
      myHelper.hideKeyboard(it)
      hideCommentsPhotoLayout(it)
      (context as CheckFormTaskActivity).showSaveLayout()
    }
    
    cft_item_unacceptable.setOnClickListener {
      myHelper.log(datum.toString())
      (context as CheckFormTaskActivity).addCheckFormData(datum.id, MyEnum.UNACCEPTED, cft_comment.text.toString())
      
      cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_background)
      cft_item_unacceptable.setTextColor(ContextCompat.getColor(context, R.color.white))
      datum.unacceptableChecked = true
      
      myHelper.hideKeyboard(cft_item_unacceptable)
      
      when (datum.admin_questions_types_id) {
        2 -> {
          //show comments section
          showCommentsLayout(holder.itemView)
        }
        3 -> {
          //show photo section
          showPhotoLayout(holder.itemView, datum.images_limit)
          context.showSaveLayout()
          myHelper.hideKeyboard(holder.itemView)
        }
        4 -> {
          //show photo, comments section
          showCommentsLayout(holder.itemView)
          showPhotoLayout(holder.itemView, datum.images_limit)
        }
      }
      
      when (datum.acceptableChecked) {
        true -> {
          cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_border)
          cft_item_acceptable.setTextColor(ContextCompat.getColor(context, R.color.light_colorPrimary))
          datum.acceptableChecked = true
        }
        else -> {}
      }
      resetItemView(holder, datum)
      
    }
    
    cft_item_capture.setOnClickListener {
      
      if (datum.images_limit <= (context as CheckFormTaskActivity).getAttachedImagesSize(datum.id)) {
        myHelper.toast("Attachments limit reached.")
      } else {
        myHelper.hideKeyboard(cft_item_capture)
        context.selectedQuestionID = datum.id
        takePhoto(position)
      }
      resetItemView(holder, datum)
      context.showSaveLayout()
      
    }
    
    cft_item_attachment.setOnClickListener {
      
      if (datum.images_limit <= (context as CheckFormTaskActivity).getAttachedImagesSize(datum.id)) {
        myHelper.toast("Attachments limit reached.")
      } else {
        showFileChooser(position)
        myHelper.hideKeyboard(cft_item_capture)
        context.selectedQuestionID = datum.id
      }
      resetItemView(holder, datum)
      context.showSaveLayout()
    }
  }
  
  
  fun takePhoto(position: Int) {
    myHelper.log("takePhoto:$position")
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    
    if (cameraIntent.resolveActivity(context.packageManager) != null) {
      (context as CheckFormTaskActivity).mAdapterPosition = position
      
      val photoFile: File?
      try {
        photoFile = context.createImageFile()
        if (photoFile != null) {
          myHelper.log("photoFile:${photoFile.path}")
          context.imageToUploadUri = FileProvider.getUriForFile(context, "app.vsptracker.provider", photoFile)
          cameraIntent.putExtra(
            MediaStore.EXTRA_OUTPUT, context.imageToUploadUri
          )
          context.startActivityForResult(
            cameraIntent, context.CAMERA_REQUEST
          )
        }
      }
      catch (e: Exception) {
        myHelper.log(e.message.toString())
      }
      
    } else {
      myHelper.toast("Camera not available.")
    }
  }
  
  private fun showFileChooser(position: Int) {
    myHelper.log("showFileChooser:$position")
    (context as CheckFormTaskActivity).mAdapterPosition = position
    val selectFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    selectFileIntent.type = "image/*"
    context.startActivityForResult(Intent.createChooser(selectFileIntent, "SelectFile"), context.PICK_FILE_REQUEST)
  }
  
  private fun showCommentsLayout(itemView: View) {
    cft_comment.visibility = View.VISIBLE
    myHelper.showKeyboard(cft_comment)
    (context as CheckFormTaskActivity).hideSaveLayout()
  }
  
  private fun hideCommentsPhotoLayout(itemView: View) {
    
    cft_comment.visibility = View.GONE
    cft_photo_layout.visibility = View.GONE
    photo_layout_main.visibility = View.GONE
    cft_photo_info_layout.visibility = View.GONE
    myHelper.hideKeyboard(cft_item_acceptable)
    (context as CheckFormTaskActivity).showSaveLayout()
  }
  
  private fun showPhotoLayout(itemView: View, imagesLimit: Int) {
    cft_photo_layout.visibility = View.VISIBLE
    photo_layout_main.visibility = View.VISIBLE
    photo_layout.visibility = View.VISIBLE
    cft_photo_limit_info.text = context.resources.getString(R.string.images_limit, imagesLimit)
    cft_photo_attached_info.text = context.resources.getString(R.string.images_attached_info)
    cft_photo_info_layout.visibility = View.VISIBLE
    
  }
  
  /**
   * This function is used when scrolling RecyclerView, It will highlight Acceptable / Unacceptable Option when
   * any option is already selected for Question.
   */
  private fun resetItemView(holder: ViewHolder, datum: MyData) {
    holder.setIsRecyclable(false)
    
    val data = (context as CheckFormTaskActivity).checkFormDataList.find { it.admin_questions_id == datum.id }
    
    when (data) {
      null -> {
        
        cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_border)
        cft_item_acceptable.setTextColor(ContextCompat.getColor(context, R.color.light_colorPrimary))
        cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_border)
        cft_item_unacceptable.setTextColor(ContextCompat.getColor(context, R.color.light_red))
        cft_comment.setText("")
        hideCommentsPhotoLayout(holder.itemView)
        context.showSaveLayout()
        
      }
      else -> {
        when (data.answer) {
          MyEnum.UNACCEPTED -> {
            cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_border)
            cft_item_acceptable.setTextColor(ContextCompat.getColor(context, R.color.light_colorPrimary))
            cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_background)
            cft_item_unacceptable.setTextColor(ContextCompat.getColor(context, R.color.white))
            cft_comment.setText(data.answerDataObj.comment)
            when (datum.admin_questions_types_id) {
              2 -> {
                //show comments section
//                                showCommentsLayout(holder.itemView)
              }
              3 -> {
                //show photo section
//                                showPhotoLayout(holder.itemView, datum.images_limit)
                context.showSaveLayout()
                myHelper.hideKeyboard(cft_item_acceptable)
                
                photo_layout.removeAllViews()
                data.answerDataObj.imagesList.forEach {
                  photo_layout.addView(
                    myHelper.addImageToPhotoLayout(
                      context, null, Uri.parse(it.localImagePath), data.answerDataObj.imagesList
                    )
                  )
                }
//                                holder.itemView.photo_layout.visibility = View.VISIBLE
              }
              4 -> {
                //show photo, comments section
//                                showCommentsLayout(holder.itemView)
//                                showPhotoLayout(holder.itemView, datum.images_limit)
                photo_layout.removeAllViews()
                data.answerDataObj.imagesList.forEach {
                  photo_layout.addView(
                    myHelper.addImageToPhotoLayout(
                      context, null, Uri.parse(it.localImagePath), data.answerDataObj.imagesList
                    )
                  )
                }
//                                holder.itemView.photo_layout.visibility = View.VISIBLE
              }
            }
            
          }
          MyEnum.ACCEPTED -> {
            cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_background)
            cft_item_acceptable.setTextColor(ContextCompat.getColor(context, R.color.white))
            cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_border)
            cft_item_unacceptable.setTextColor(ContextCompat.getColor(context, R.color.light_red))
            cft_comment.setText("")
            context.showSaveLayout()
            myHelper.hideKeyboard(cft_item_acceptable)
            hideCommentsPhotoLayout(holder.itemView)
          }
        }
      }
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

