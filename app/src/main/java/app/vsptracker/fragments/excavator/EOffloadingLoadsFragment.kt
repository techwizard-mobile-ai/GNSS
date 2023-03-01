package app.vsptracker.fragments.excavator

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.adapters.EOffLoadingAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper


class EOffloadingLoadsFragment : Fragment() {
  private val tag1 = this::class.java.simpleName
  
  private lateinit var myHelper: MyHelper
  private var root: View? = null
  
  
  private lateinit var diggingHistory: MutableList<EWork>
  private lateinit var db: DatabaseAdapter
  
  private var listener: OnFragmentInteractionListener? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      myHelper = MyHelper(
        tag1,
        context as Activity
      )
      db = DatabaseAdapter(context as Activity)
      myHelper.log("eWork:$eWork ")
    }
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.fragment_eoffloading_loads, container, false)
    return root
  }
  
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val v = view
    val eoff_fragment_rv = root!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.eoff_fragment_rv)
    val offloading_loads_title = root!!.findViewById<TextView>(R.id.offloading_loads_title)
    
    if (eWork.workType == 1) {
      offloading_loads_title.text = getString(R.string.general_digging_loads_history)
    } else {
      offloading_loads_title.text = getString(R.string.trenching_loads_history)
    }
    
    val mAdapter = EOffLoadingAdapter(
      context as Activity, db.getEWorksOffLoads(
        eWork.id
      )
    )
    eoff_fragment_rv.layoutManager =
      LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
    eoff_fragment_rv!!.adapter = mAdapter
  }
  
  fun onButtonPressed(uri: Uri) {
    listener?.onFragmentInteraction(uri)
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
    
    
    //        private lateinit var myContext: Activity
    private lateinit var FRAGMENT_TAG: String
    private lateinit var eWork: EWork
    
    @JvmStatic
    fun newInstance(fragmentTag: String, eWork1: EWork) =
      EOffloadingLoadsFragment().apply {
        arguments = Bundle().apply {
//                        myContext = mContext
          FRAGMENT_TAG = fragmentTag
          eWork = eWork1
        }
      }
  }
}
