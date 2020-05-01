package app.vsptracker.activities

import android.annotation.SuppressLint
import android.os.Bundle
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

class CheckFormTaskActivity : BaseActivity(), View.OnClickListener {
    private var questionsList = ArrayList<MyData> ()
    private val tag = this::class.java.simpleName
    var checkform_id = 0
    var checkFormDataList = ArrayList<CheckFormData>()
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
        
        val mAdapter = CheckFormTaskAdapter(this, questionsList)
        cft_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cft_rv.adapter = mAdapter
        cft_finish.setOnClickListener(this)
        cft_skip.setOnClickListener(this)
        
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
            finish()
        }
        mDialogView.save_checkform_no.setOnClickListener {
            mAlertDialog.dismiss()
        }
        
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
            finish()
        }
        mDialogView.save_checkform_no.setOnClickListener {
            mAlertDialog.dismiss()
        }
        
    }
}
