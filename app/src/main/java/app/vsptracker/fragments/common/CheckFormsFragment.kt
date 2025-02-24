package app.vsptracker.fragments.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper


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
    
    val no_cf = root!!.findViewById<TextView>(R.id.no_cf)
    val cf_title = root!!.findViewById<TextView>(R.id.cf_title)
    val checkforms_upload = root!!.findViewById<Button>(R.id.checkforms_upload)
    val cf_rv = root!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.cf_rv)
    
    myHelper.log("type: $type")
    
    var title = ""
//        when (type) {
//            MyEnum.ADMIN_CHECKFORMS_DUE -> title = getString(R.string.due_checkforms)
//            MyEnum.ADMIN_CHECKFORMS_ALL -> title = getString(R.string.all_checkforms)
//            MyEnum.ADMIN_CHECKFORMS_COMPLETED -> title = getString(R.string.completed_checkforms)
//            MyEnum.ADMIN_CHECKFORMS_COMPLETED_DETAILS -> title = "${db.getAdminCheckFormByID(checkFormCompleted.admin_checkforms_id).name} Details"
//            MyEnum.ADMIN_CHECKFORMS_COMPLETED_SERVER -> title = getString(R.string.server_checkforms)
//        }
//
//        cf_title.text = title
    
    when (type) {
      MyEnum.ADMIN_CHECKFORMS_DUE -> {
        title = getString(R.string.due_checkforms)
        val dueCheckForms = db.getAdminCheckFormsDue()
        when (dueCheckForms.size) {
          0 -> {
            no_cf.text = getString(R.string.no_due_checkforms)
            no_cf.visibility = View.VISIBLE
          }
          else -> {
            val mAdapter = CheckFormsAdapter(context as Activity, dueCheckForms, type)
            cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
            cf_rv!!.adapter = mAdapter
          }
        }
      }
      MyEnum.ADMIN_CHECKFORMS_ALL -> {
        title = getString(R.string.all_checkforms)
        val allCheckForms = db.getAdminCheckForms()
        when (allCheckForms.size) {
          0 -> {
            no_cf.text = getString(R.string.no_checkforms)
            no_cf.visibility = View.VISIBLE
          }
          else -> {
            val mAdapter = CheckFormsAdapter(context as Activity, allCheckForms, type)
            cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
            cf_rv!!.adapter = mAdapter
          }
        }
      }
      MyEnum.ADMIN_CHECKFORMS_COMPLETED -> {
        title = getString(R.string.completed_checkforms)
        val completedCheckForms = db.getAdminCheckFormsCompleted()
        when (completedCheckForms.size) {
          0 -> {
            no_cf.text = getString(R.string.no_completed_checkforms)
            no_cf.visibility = View.VISIBLE
          }
          else -> {
            val mAdapter = CheckFormsCompletedAdapter(context as Activity, completedCheckForms, type, supportFragmentManager1)
            cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
            cf_rv!!.adapter = mAdapter
            checkforms_upload.visibility = View.GONE
/*                        root!!.checkforms_upload.setOnClickListener {
                            
                            completedCheckForms.forEach { completedCheckForm ->
                                completedCheckForm.checkFormData.forEach { checkFormDatum ->
                                    checkFormDatum.answerDataObj.imagesList.forEach { images ->
                                        if (images.localImagePath.isNotBlank() && images.awsImagePath.isBlank()) {
                                            try {
                                                val file = myHelper.readContentToFile(Uri.parse(images.localImagePath))
                                                val filePath = myHelper.getAWSFilePath(MyEnum.CHECKFORMS_IMAGES)
                                                myHelper.awsFileUpload(filePath, file)
                                                images.awsImagePath = filePath + file.name
                                                myHelper.log("fileAdded:${checkFormDatum.answerDataObj}")
                                            }
                                            catch (e: Exception) {
                                                myHelper.log("uploadException:${e.localizedMessage}")
                                            }
                                        }
                                    }
                                }
                                db.updateAdminCheckFormsData(completedCheckForm.checkFormData)
                            }
                            val myDataPushSave = MyDataPushSave(context as Activity)
                            myDataPushSave.checkUpdateServerSyncData(true)
                        }*/
          }
        }
      }
      MyEnum.ADMIN_CHECKFORMS_COMPLETED_DETAILS -> {
        title = "${db.getAdminCheckFormByID(checkFormCompleted.admin_checkforms_id).name} Details"
        val checkFormData = db.getAdminCheckFormsDataByLocalID(checkFormCompleted.id)
        when (checkFormData.size) {
          0 -> {
            no_cf.text = getString(R.string.no_questions_to_show)
            no_cf.visibility = View.VISIBLE
          }
          else -> {
            val mAdapter = CheckFormsDataAdapter(context as Activity, checkFormData)
            cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
            cf_rv!!.adapter = mAdapter
          }
        }
      }
      MyEnum.ADMIN_CHECKFORMS_COMPLETED_SERVER -> {
        title = getString(R.string.server_checkforms)
        val completedServerCheckForms = db.getAdminCheckFormsCompletedServer()
        when (completedServerCheckForms.size) {
          0 -> {
            no_cf.text = getString(R.string.no_completed_server_checkforms)
            no_cf.visibility = View.VISIBLE
          }
          else -> {
            val mAdapter = CheckFormsCompletedAdapter(context as Activity, completedServerCheckForms, type, supportFragmentManager1)
            cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
            cf_rv!!.adapter = mAdapter
            checkforms_upload.visibility = View.GONE
          }
        }
      }
    }
    
    cf_title.text = title
    
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
