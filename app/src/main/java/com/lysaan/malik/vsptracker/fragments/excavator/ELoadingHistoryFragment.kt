package com.lysaan.malik.vsptracker.fragments.excavator

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.ELoadingHistoryAdapter
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.fragment_eloading_history.*


class ELoadingHistoryFragment : Fragment() {

    private lateinit var loadingHistory: MutableList<Data>
    private val TAG = this::class.java.simpleName

    private lateinit var helper: Helper
    private var root: View? = null

    private lateinit var db: DatabaseAdapter


    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            helper = Helper(
                    TAG,
                    myContext
            )
            db = DatabaseAdapter(myContext)
            loadingHistory = db.getELoadHistroy()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_eloading_history, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAdapter = ELoadingHistoryAdapter(myContext, loadingHistory)
        elh_rv.layoutManager = LinearLayoutManager(myContext, LinearLayout.VERTICAL, false)
        elh_rv!!.setAdapter(mAdapter)
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
        fun newInstance(
                eHistoryActivity: Activity
        ) =
                ELoadingHistoryFragment().apply {
                    arguments = Bundle().apply {
                        myContext = eHistoryActivity
                    }
                }
    }
}
