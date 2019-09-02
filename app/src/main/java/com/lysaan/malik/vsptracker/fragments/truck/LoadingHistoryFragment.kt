package com.lysaan.malik.vsptracker.fragments.truck

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
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.LoadingHistoryAdapter
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.fragment_loading_history.*


class LoadingHistoryFragment : Fragment() {

    private lateinit var loadingHistory: MutableList<Data>
    private val TAG = this::class.java.simpleName

    private lateinit var helper: Helper
    private var root: View? = null

    private lateinit var db: DatabaseAdapter
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            helper = Helper(TAG, myContext)
            db = DatabaseAdapter(myContext)
            loadingHistory = db.getTrips()
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

        val mAdapter = LoadingHistoryAdapter(myContext, loadingHistory)
        flh_rv.layoutManager = LinearLayoutManager(myContext, RecyclerView.VERTICAL, false)
        flh_rv!!.setAdapter(mAdapter)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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

        private lateinit var myContext: Activity
        @JvmStatic
        fun newInstance(activity: Activity) =
                LoadingHistoryFragment().apply {
                    arguments = Bundle().apply {
                        myContext = activity
                    }
                }
    }
}
