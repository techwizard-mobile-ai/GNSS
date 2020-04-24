package app.vsptracker.fragments.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import app.vsptracker.R
import app.vsptracker.adapters.CheckFormsAdapter
import app.vsptracker.adapters.MachineStopAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.fragment_check_forms.*
import kotlinx.android.synthetic.main.fragment_check_forms.view.*
import kotlinx.android.synthetic.main.fragment_check_forms.view.cf_title
import kotlinx.android.synthetic.main.fragment_machine_stop.view.*

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
        
        val dataList = db.getAdminCheckForms()
        myHelper.log("type: $type")
        myHelper.log("CheckForms:${dataList.size}")
        myHelper.log("CheckForms:$dataList")
        
        
        val mAdapter = CheckFormsAdapter(context as Activity, dataList)
        root!!.cf_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
        root!!.cf_rv!!.adapter = mAdapter
        
        var title = ""
        when(type){
            0-> title = getString(R.string.due_checkforms)
            1-> title = getString(R.string.all_checkforms)
            2-> title = getString(R.string.completed_checkforms)
        }
    
        cf_title.text = title
        when(dataList.size){
            0 ->{
                when(type){
                    1 -> no_cf.text = getString(R.string.no_checkforms)
                    else -> no_cf.text = "No $title."
                }
                no_cf.visibility= View.VISIBLE
            }
            else ->{
                no_cf.visibility= View.GONE
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
        
        @JvmStatic
        fun newInstance(
            FRAGMENT_TYPE: Int
        ) =
            CheckFormsFragment().apply {
                arguments = Bundle().apply {
                    type = FRAGMENT_TYPE
                }
            }
    }
}
