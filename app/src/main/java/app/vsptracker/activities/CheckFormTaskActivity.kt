package app.vsptracker.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CheckFormTaskAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_check_form_task.*

class CheckFormTaskActivity : BaseActivity(), View.OnClickListener {
    private val tag = this::class.java.simpleName
    var checkform_id = 0
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
        myHelper.log("Questions:${db.getQuestionsByIDs(myHelper.toCommaSeparatedString(checkForm.questions_data))}")
        val questionsList = db.getQuestionsByIDs(myHelper.toCommaSeparatedString(checkForm.questions_data))
    
        val mAdapter = CheckFormTaskAdapter(this , questionsList)
        cft_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cft_rv.adapter = mAdapter
    
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
        }
    }
}
