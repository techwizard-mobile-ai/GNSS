package app.vsptracker.fragments.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.adapters.CheckFormsAdapter
import app.vsptracker.adapters.CheckFormsCompletedAdapter
import app.vsptracker.adapters.CheckFormsDataAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.fragment_check_forms.*
import kotlinx.android.synthetic.main.fragment_check_forms.view.*


class CheckFormsFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    
    private val tag1 = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    private var root: View? = null
    private lateinit var diggingHistory: MutableList<EWork>
    private lateinit var db: DatabaseAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myHelper = MyHelper(
                tag1,
                context as Activity
            )
            db = DatabaseAdapter(context as Activity)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_check_forms, container, false)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        myHelper.log("type: $type")
        
        var title = ""
        when (type) {
            0 -> title = getString(R.string.due_checkforms)
            1 -> title = getString(R.string.all_checkforms)
            2 -> title = getString(R.string.completed_checkforms)
            3 -> title = "${db.getAdminCheckFormByID(checkFormCompleted.admin_checkforms_id).name} Details"
        }
        
        cf_title.text = title
        
        when (type) {
            0 -> {
                val dueCheckForms = db.getAdminCheckFormsDue()
                when (dueCheckForms.size) {
                    0 -> {
                        no_cf.text = getString(R.string.no_due_checkforms)
                        no_cf.visibility = View.VISIBLE
                    }
                    else -> {
                        val mAdapter = CheckFormsAdapter(context as Activity, dueCheckForms, type)
                        root!!.cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
                        root!!.cf_rv!!.adapter = mAdapter
                    }
                }
            }
            1 -> {
                val allCheckForms = db.getAdminCheckForms()
                when (allCheckForms.size) {
                    0 -> {
                        no_cf.text = getString(R.string.no_checkforms)
                        no_cf.visibility = View.VISIBLE
                    }
                    else -> {
                        val mAdapter = CheckFormsAdapter(context as Activity, allCheckForms, type)
                        root!!.cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
                        root!!.cf_rv!!.adapter = mAdapter
                    }
                }
            }
            2 -> {
                val completedCheckForms = db.getAdminCheckFormsCompleted()
                when (completedCheckForms.size) {
                    0 -> {
                        no_cf.text = getString(R.string.no_completed_checkforms)
                        no_cf.visibility = View.VISIBLE
                    }
                    else -> {
                        val mAdapter = CheckFormsCompletedAdapter(context as Activity, completedCheckForms, type, supportFragmentManager1)
                        root!!.cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
                        root!!.cf_rv!!.adapter = mAdapter
                        root!!.checkforms_upload.visibility = View.VISIBLE
                        root!!.checkforms_upload.setOnClickListener {
                            myHelper.log("upload checkforms:$completedCheckForms")
//                            val checkFormData = db.getAdminCheckFormsDataByLocalID(completedCheckForms[0].id)
//                            myHelper.log("checkFormData:$checkFormData")

                            completedCheckForms.forEach { completedCheckForm ->
                                val checkFormData = db.getAdminCheckFormsDataByLocalID(completedCheckForm.id)
                                checkFormData.forEach { checkFormDatum ->
                                    myHelper.log("checkFormDatum:$checkFormData")
                                    if (checkFormDatum.answerDataObj.imagesPaths.size > checkFormDatum.answerDataObj.awsImagesPaths.size) {
                                        checkFormDatum.answerDataObj.imagesPaths.forEach { imagePath ->
                                            try {
                                                val file = myHelper.readContentToFile(Uri.parse(imagePath))
                                                val filePath = myHelper.getAWSFilePath()
                                                myHelper.awsFileUpload(filePath, file)
                                                checkFormDatum.answerDataObj.awsImagesPaths.add(filePath + file.name)
                                                myHelper.log("fileAdded:${checkFormDatum.answerDataObj.awsImagesPaths}")
                                            }
                                            catch (e: Exception) {
                                                myHelper.log("uploadException:${e.localizedMessage}")
                                            }
                                        }
                                    }
                                }
                                db.updateAdminCheckFormsData(checkFormData)
                            }
                        }
                    }
                }
            }
            3 -> {
                val checkFormData = db.getAdminCheckFormsDataByLocalID(checkFormCompleted.id)
                when (checkFormData.size) {
                    0 -> {
                        no_cf.text = getString(R.string.no_questions_to_show)
                        no_cf.visibility = View.VISIBLE
                    }
                    else -> {
                        val mAdapter = CheckFormsDataAdapter(context as Activity, checkFormData)
                        root!!.cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
                        root!!.cf_rv!!.adapter = mAdapter
                    }
                }
            }
        }
        
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }
    
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
    
    companion object {
        private var type: Int = 0
        private var checkFormCompleted = MyData()
        private lateinit var supportFragmentManager1: FragmentManager
        
        @JvmStatic
        fun newInstance(
            FRAGMENT_TYPE: Int,
            supportFragmentManager: FragmentManager,
            checkFormCompleted1: MyData
        ) =
            CheckFormsFragment().apply {
                arguments = Bundle().apply {
                    type = FRAGMENT_TYPE
                    supportFragmentManager1 = supportFragmentManager
                    checkFormCompleted = checkFormCompleted1
                }
            }
    }
    
}
