package app.vsptracker.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CheckFormTaskAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.CheckFormData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_check_form_task.*
import kotlinx.android.synthetic.main.dialog_save_checkform.view.*
import kotlinx.android.synthetic.main.list_row_check_form_task.view.*
import java.io.ByteArrayOutputStream
import java.io.File

class CheckFormTaskActivity : BaseActivity(), View.OnClickListener {
    internal var mAdapterPostion  = 0
    private lateinit var mAdapter: CheckFormTaskAdapter
    private var questionsList = ArrayList<MyData> ()
    private val tag = this::class.java.simpleName
    var checkform_id = 0
    var checkFormDataList = ArrayList<CheckFormData>()
    lateinit var checkFormCompleted : MyData
    
    val PICK_FILE_REQUEST = 1
    val CAMERA_REQUEST = 1888
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_check_form_task, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(10).isChecked = true
        
        myHelper.setTag(tag)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            checkform_id = bundle.getInt("checkform_id")
            myHelper.log("checkform_id:$checkform_id")
        }
    
        initCheckFormCompleted()
        
        val checkForm = db.getAdminCheckFormByID(checkform_id)
        cft_title.text = checkForm.name
        
        myHelper.log("checkForm:$checkForm")
        myHelper.log("questionsIDs:${myHelper.getQuestionsIDsList(checkForm.questions_data)}")
        myHelper.log(
            "Questions:${db.getQuestionsByIDs(
                myHelper.toCommaSeparatedString(checkForm.questions_data),
                myHelper.getQuestionsIDsList(checkForm.questions_data)
            )}"
        )
        
        questionsList = db.getQuestionsByIDs(
            myHelper.toCommaSeparatedString(checkForm.questions_data),
            myHelper.getQuestionsIDsList(checkForm.questions_data)
        )
        
        mAdapter = CheckFormTaskAdapter(this, questionsList)
        cft_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cft_rv.adapter = mAdapter
        cft_finish.setOnClickListener(this)
        cft_skip.setOnClickListener(this)
        
    }

    
    private fun initCheckFormCompleted() {
        checkFormCompleted = MyData()
        checkFormCompleted.orgId = myHelper.getLoginAPI().org_id
        checkFormCompleted.siteId = myHelper.getMachineSettings().siteId
        checkFormCompleted.operatorId = myHelper.getOperatorAPI().id
        checkFormCompleted.machineTypeId = myHelper.getMachineTypeID()
        checkFormCompleted.machineId = myHelper.getMachineID()
        checkFormCompleted.admin_checkforms_id = checkform_id
        checkFormCompleted.loadingGPSLocation = gpsLocation
        checkFormCompleted.loadingGPSLocationString = myHelper.getGPSLocationToString(gpsLocation)
        checkFormCompleted.startTime = System.currentTimeMillis()
        if (myHelper.isDailyModeStarted()) checkFormCompleted.isDayWorks = 1 else checkFormCompleted.isDayWorks = 0
        
    }
    
    fun hideSaveLayout() {
        cft_finish_layout.visibility = View.GONE
    }
    
    fun showSaveLayout() {
        cft_finish_layout.visibility = View.VISIBLE
    }
    
    override fun onClick(view: View?) {
        myHelper.log(checkFormDataList.toString())
        when (view!!.id) {
            R.id.cft_finish -> saveCheckFormDialog()
            R.id.cft_skip -> skipCheckFormDialog()
        }
    }
    
    @SuppressLint("InflateParams")
    private fun saveCheckFormDialog() {
    
        
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_checkform, null)
        
        if(questionsList.size > checkFormDataList.size){
            val txt = "Total Question:${questionsList.size}\nRemaining Questions: ${questionsList.size - checkFormDataList.size}\n\nPlease complete all questions to save checkform."
            mDialogView.cftd_sub_title.text = txt
            mDialogView.save_checkform_yes.visibility = View.GONE
            mDialogView.cftd_save_bottom.visibility = View.GONE
            
            myHelper.log("Checkform not completed.")
        }else{
            mDialogView.cftd_title_layout.visibility = View.GONE
            mDialogView.cftd_title_layout_bottom.visibility = View.GONE
        }
        
        mDialogView.save_checkform_yes.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(true)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        
        mDialogView.save_checkform_yes.setOnClickListener {
            mAlertDialog.dismiss()
            saveCompletedCheckForm()
            finish()
        }
        mDialogView.save_checkform_no.setOnClickListener {
            mAlertDialog.dismiss()
        }
        
    }
    
    private fun saveCompletedCheckForm() {
        checkFormCompleted.unloadingGPSLocation = gpsLocation
        checkFormCompleted.unloadingGPSLocationString = myHelper.getGPSLocationToString(gpsLocation)
        checkFormCompleted.stopTime = System.currentTimeMillis()
        checkFormCompleted.totalTime = checkFormCompleted.stopTime - checkFormCompleted.startTime
        
        val checkFormCompletedLocalID = db.insertAdminCheckFormsCompleted(checkFormCompleted)
        myHelper.log("checkFormCompletedLocalID:$checkFormCompletedLocalID")
        db.insertAdminCheckFormsData(checkFormDataList, checkFormCompletedLocalID)
    }
    
    @SuppressLint("InflateParams")
    private fun skipCheckFormDialog() {
        
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_checkform, null)
        if(questionsList.size > checkFormDataList.size){
            val txt = "Total Question:${questionsList.size}\nRemaining Questions: ${questionsList.size - checkFormDataList.size}\n\nPlease complete all questions."
            mDialogView.cftd_sub_title.text = txt
            mDialogView.save_checkform_yes.text = getString(R.string.save_unfinished_form)
            myHelper.log("Checkform not completed.")
        }else{
            mDialogView.save_checkform_yes.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            mDialogView.cftd_title_layout.visibility = View.GONE
            mDialogView.cftd_title_layout_bottom.visibility = View.GONE
        }
        
        
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCancelable(true)
        
        val window = mAlertDialog.window
        val wlp = window!!.attributes
        
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        
        
        mDialogView.save_checkform_yes.setOnClickListener {
            mAlertDialog.dismiss()
            saveCompletedCheckForm()
            finish()
        }
        mDialogView.save_checkform_no.setOnClickListener {
            mAlertDialog.dismiss()
        }
        
    }
    
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var filePath: Uri? = null
//        attachment = Attachment()
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            val takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            if (filePath != null) {
                contentResolver.takePersistableUriPermission(filePath, takeFlags)
            }
            val file = File(filePath!!.path)
            val cr: ContentResolver = contentResolver
            val mime = cr.getType(filePath)
            myHelper.log("mime:$mime")
            val types = mime!!.split("/").toTypedArray()
            if (types[0].equals("image", ignoreCase = true)) {
                
                cft_rv.findViewHolderForAdapterPosition(mAdapterPostion)!!.itemView.image_view.setImageURI(filePath)
                cft_rv.findViewHolderForAdapterPosition(mAdapterPostion)!!.itemView.photo_layout.visibility = View.VISIBLE
//                val attachment1 = Attachment()
//                attachment1.setFilePath(filePath)
//                attachment1.setAttachmentType(1)
//                attachmentList.add(attachment1)
//                mAdapter.notifyDataSetChanged()
                myHelper.toast("Image attached successfully.")
                
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            if (extras != null) {
                val photo = extras.getParcelable<Bitmap>("data")
                val baos = ByteArrayOutputStream()
                photo!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val b = baos.toByteArray()
                val temp = Base64.encodeToString(b, Base64.DEFAULT)
    
                cft_rv.findViewHolderForAdapterPosition(mAdapterPostion)!!.itemView.image_view.setImageBitmap(photo)
                cft_rv.findViewHolderForAdapterPosition(mAdapterPostion)!!.itemView.photo_layout.visibility = View.VISIBLE
                
//                mAdapter.notifyDataSetChanged()
//                image_view.setImageBitmap(photo)
//                photo_layout.setVisibility(View.VISIBLE)
//                attachment.setBase64String(temp)
//                attachment.setDesc(attach_information.getText().toString())
//                attachment.setExtension("PNG")
//                captureList.add(attachment)
                myHelper.toast("Captured image attached successfully.")
            }
        } else {
            myHelper.toast("No image attached.")
        }
    }
}
