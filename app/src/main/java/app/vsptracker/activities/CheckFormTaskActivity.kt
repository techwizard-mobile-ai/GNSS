package app.vsptracker.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import app.vsptracker.classes.AnswerData
import app.vsptracker.classes.CheckFormData
import app.vsptracker.classes.GPSLocation
import app.vsptracker.classes.Images
import app.vsptracker.others.MyDataPushSave
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_check_form_task.*
import kotlinx.android.synthetic.main.dialog_save_checkform.view.*
import kotlinx.android.synthetic.main.list_row_check_form_task.view.*
import java.io.File
import java.io.IOException


class CheckFormTaskActivity : BaseActivity(), View.OnClickListener {
    private lateinit var checkForm: MyData
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: Int = 7
    internal lateinit var imageToUploadUri: Uri
    private lateinit var imageFilePath: String
    internal var selectedQuestionID = 0
    internal var mAdapterPosition = 0
    private lateinit var mAdapter: CheckFormTaskAdapter
    private var questionsList = ArrayList<MyData>()
    private val tag = this::class.java.simpleName
    var checkform_id = 0
    var entry_type = 0
    var checkFormDataList = ArrayList<CheckFormData>()
    lateinit var checkFormCompleted: MyData
    
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
            entry_type = bundle.getInt("entry_type")
            gpsLocation = bundle.getSerializable("gpsLocation") as GPSLocation
            myHelper.log("checkform_id:$checkform_id")
        }
        
        checkForm = db.getAdminCheckFormByID(checkform_id)
        cft_title.text = checkForm.name
        
        questionsList = db.getQuestionsByIDs(
            myHelper.toCommaSeparatedString(checkForm.questions_data),
            myHelper.getQuestionsIDsList(checkForm.questions_data)
        )
        mAdapter = CheckFormTaskAdapter(this, questionsList)
        cft_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cft_rv.isNestedScrollingEnabled = true
        cft_rv.adapter = mAdapter
        cft_finish.setOnClickListener(this)
        cft_skip.setOnClickListener(this)
        
        initCheckFormCompleted()
    }
    
    private fun initCheckFormCompleted() {
        checkFormCompleted = MyData()
        
        checkFormCompleted.orgId = myHelper.getLoginAPI().org_id
        checkFormCompleted.siteId = myHelper.getMachineSettings().siteId
        checkFormCompleted.operatorId = myHelper.getOperatorAPI().id
        checkFormCompleted.machineTypeId = myHelper.getMachineTypeID()
        checkFormCompleted.machineId = myHelper.getMachineID()
        checkFormCompleted.admin_checkforms_id = checkform_id
        checkFormCompleted.admin_checkforms_schedules_id = checkForm.admin_checkforms_schedules_id
        checkFormCompleted.admin_checkforms_schedules_value = checkForm.admin_checkforms_schedules_value
        checkFormCompleted.entry_type = entry_type
        checkFormCompleted.loadingGPSLocation = gpsLocation
        checkFormCompleted.loadingGPSLocationString = myHelper.getGPSLocationToString(gpsLocation)
        myHelper.log("loadingGPSLocationString:${checkFormCompleted.loadingGPSLocationString}")
        checkFormCompleted.startTime = System.currentTimeMillis()
        if (myHelper.isDailyModeStarted()) checkFormCompleted.isDayWorks = 1 else checkFormCompleted.isDayWorks = 0
        
    }
  
    fun hideSaveLayout() {
        myHelper.log("hideSaveLayout")
        cft_finish_layout.visibility = View.GONE
    }
    
    fun showSaveLayout() {
        myHelper.log("showSaveLayout")
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
        
        if (questionsList.size > checkFormDataList.size) {
            val txt =
                "Total Questions:${questionsList.size}\nRemaining Questions: ${questionsList.size - checkFormDataList.size}\n\nPlease complete all questions to save checkform."
            mDialogView.cftd_sub_title.text = txt
            mDialogView.save_checkform_yes.visibility = View.GONE
            mDialogView.cftd_save_bottom.visibility = View.GONE
            
            myHelper.log("Checkform not completed.")
        } else {
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
        myHelper.log("checkFormDataList:$checkFormDataList")
        val checkFormCompletedLocalID = db.insertAdminCheckFormsCompleted(checkFormCompleted)
        myHelper.log("checkFormCompletedLocalID:$checkFormCompletedLocalID")
        db.insertAdminCheckFormsData(checkFormDataList, checkFormCompletedLocalID)
        
        val myDataPushSave = MyDataPushSave(this)
        myDataPushSave.checkUpdateServerSyncData()
        val intent = Intent(this@CheckFormTaskActivity, CheckFormsActivity::class.java)
        startActivity(intent)
        finishAffinity()
        
    }
    
    @SuppressLint("InflateParams")
    private fun skipCheckFormDialog() {
        
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_checkform, null)
        if (questionsList.size > checkFormDataList.size) {
            val txt =
                "Total Questions:${questionsList.size}\nRemaining Questions: ${questionsList.size - checkFormDataList.size}\n\nPlease complete all questions."
            mDialogView.cftd_sub_title.text = txt
            mDialogView.save_checkform_yes.text = getString(R.string.save_unfinished_form)
            myHelper.log("Checkform not completed.")
        } else {
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
        }
        mDialogView.save_checkform_no.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var filePath: Uri? = null
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
                
                cft_rv.findViewHolderForAdapterPosition(mAdapterPosition)!!.itemView.photo_layout.addView(myHelper.addImageToPhotoLayout(this, null, filePath))
                val imagePath = filePath.toString()
                addImageToCheckFormData(selectedQuestionID, imagePath)
                cft_rv.findViewHolderForAdapterPosition(mAdapterPosition)!!.itemView.photo_layout.visibility = View.VISIBLE
                myHelper.toast("Image attached successfully.")
                
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            
            myHelper.log("onResult:$imageToUploadUri")
            addImageToCheckFormData(selectedQuestionID, imageToUploadUri.toString())
            cft_rv.findViewHolderForAdapterPosition(mAdapterPosition)!!.itemView.photo_layout.addView(
                myHelper.addImageToPhotoLayout(
                    this,
                    null,
                    imageToUploadUri
                )
            )
            cft_rv.findViewHolderForAdapterPosition(mAdapterPosition)!!.itemView.photo_layout.visibility = View.VISIBLE
            myHelper.toast("Captured image attached successfully.")
        } else {
            myHelper.toast("No image attached.")
        }
    }
    
    fun addCheckFormData(questionID: Int, answer: String, comment: String) {
        
        val checkFormData = CheckFormData()
        checkFormData.time = System.currentTimeMillis().toString()
        checkFormData.admin_questions_id = questionID
        
        val data = this.checkFormDataList.find { it.admin_questions_id == questionID }
        myHelper.log("data:$data")
        if (data == null) {
            checkFormData.answer = answer
            val answerData = AnswerData()
            answerData.comment = comment
            
            checkFormData.answerDataObj = answerData
            // question data is not already saved in list. Add data to list
            checkFormDataList.add(checkFormData)
        } else {
            if (!answer.equals("-1", true))
                checkFormData.answer = answer
            else
                checkFormData.answer = data.answer
            // questions data is already in the list. Update data to list
            checkFormData.answerDataObj = data.answerDataObj
            if (comment.isNotEmpty()) {
                checkFormData.answerDataObj.comment = comment
            }
            val index: Int = checkFormDataList.indexOf(data)
            checkFormDataList[index] = checkFormData
        }
    }
    
    fun addImageToCheckFormData(questionID: Int, imagePath: String) {
        
        val checkFormData = CheckFormData()
        checkFormData.time = System.currentTimeMillis().toString()
        checkFormData.admin_questions_id = questionID
        
        val data = this.checkFormDataList.find { it.admin_questions_id == questionID }
        
        if (data == null) {
            val answerData = AnswerData()
            answerData.imagesList.add(Images(imagePath, ""))
            checkFormData.answerDataObj = answerData
            // question data is not already saved in list. Add data to list
            checkFormDataList.add(checkFormData)
        } else {
            checkFormData.answer = data.answer
            // questions data is already in the list. Update data to list
            checkFormData.answerDataObj = data.answerDataObj
            checkFormData.answerDataObj.imagesList.add(Images(imagePath, ""))
            val index: Int = checkFormDataList.indexOf(data)
            checkFormDataList[index] = checkFormData
        }
        myHelper.log("addImageToCheckFormData:$checkFormData")
    }
    
    fun getAttachedImagesSize(questionID: Int): Int {
        val data = this.checkFormDataList.find { it.admin_questions_id == questionID }
        return if (data == null) {
            0
        } else {
            data.answerDataObj.imagesList.size
        }
    }
    
    @Throws(IOException::class)
    fun createImageFile(): File? {
        
        val imageFileName = myHelper.getFileName(checkform_id, selectedQuestionID)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        myHelper.log("imageFilePath:$imageFilePath")
        galleryAddPic(imageFilePath)
        return image
    }
    
    private fun galleryAddPic(imageFilePath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(imageFilePath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
    
}
