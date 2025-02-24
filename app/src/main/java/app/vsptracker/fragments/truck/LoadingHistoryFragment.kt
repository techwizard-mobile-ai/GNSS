package app.vsptracker.fragments.truck

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
import app.vsptracker.adapters.LoadingHistoryAdapter
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper


class LoadingHistoryFragment : Fragment() {
  
  private lateinit var loadingHistory: MutableList<MyData>
  private val tag1 = this::class.java.simpleName
  
  private lateinit var myHelper: MyHelper
  private var root: View? = null
  
  private lateinit var db: DatabaseAdapter
  private var listener: OnFragmentInteractionListener? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      myHelper = MyHelper(tag1, context as Activity)
      db = DatabaseAdapter(context as Activity)
      loadingHistory = db.getTrips()
      myHelper.log("loadingHistory:$loadingHistory")
    }
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.fragment_loading_history, container, false)
    return root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val v = view
    val flh_rv = root!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.flh_rv)
    
    val mAdapter = LoadingHistoryAdapter(context as Activity, loadingHistory as ArrayList<MyData>)
    flh_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
    flh_rv!!.adapter = mAdapter
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
    @JvmStatic
    fun newInstance() =
      LoadingHistoryFragment().apply {
        arguments = Bundle().apply {
//                        myContext = activity
        }
      }
  }
}
