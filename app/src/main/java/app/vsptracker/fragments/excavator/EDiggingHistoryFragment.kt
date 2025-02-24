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
import app.vsptracker.adapters.ETHistoryAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper


class EDiggingHistoryFragment : Fragment() {
  
  
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
      diggingHistory = db.getEWorks(workType)
      myHelper.log("Digging:$diggingHistory ")
    }
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.fragment_edigging_history, container, false)
    return root
  }
  
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    val v = view
    val edh_rv = root!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.edh_rv)
    val e_digging_f_title = root!!.findViewById<TextView>(R.id.e_digging_f_title)
    
    when (workType) {
      1 -> e_digging_f_title.text = getString(R.string.general_digging_history)
      3 -> e_digging_f_title.text = getString(R.string.scraper_trimming)
    }
    val mAdapter = ETHistoryAdapter(
      context as Activity, diggingHistory as ArrayList<EWork>,
      FRAGMENT_TAG, workType
    )
    edh_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
    edh_rv!!.adapter = mAdapter
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
    
    private lateinit var FRAGMENT_TAG: String
    private var workType = 0
    
    @JvmStatic
    fun newInstance(
      FRAGMENT_TG: String,
      workType1: Int
    ) =
      EDiggingHistoryFragment().apply {
        arguments = Bundle().apply {
          FRAGMENT_TAG = FRAGMENT_TG
          workType = workType1
        }
      }
  }
}
