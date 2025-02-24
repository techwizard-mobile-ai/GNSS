package app.vsptracker.fragments.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.adapters.DelayHistoryAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class DelayHistoryFragment : Fragment() {
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
    root = inflater.inflate(R.layout.fragment_delay_history, container, false)
    return root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val v = view
    val dh_rv = root!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.dh_rv)
    
    val dataList = db.getWaits()
    val mAdapter = DelayHistoryAdapter(context as Activity, dataList)
    dh_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
    dh_rv!!.adapter = mAdapter
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
    
    @JvmStatic
    fun newInstance(
      FRAGMENT_TG: String
    ) =
      DelayHistoryFragment().apply {
        arguments = Bundle().apply {
          FRAGMENT_TAG = FRAGMENT_TG
        }
      }
  }
}
