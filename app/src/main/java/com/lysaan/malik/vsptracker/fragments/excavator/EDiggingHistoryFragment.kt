package com.lysaan.malik.vsptracker.fragments.excavator

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

import com.lysaan.malik.vsptracker.activities.excavator.EHistoryActivity
import com.lysaan.malik.vsptracker.adapters.ETHistoryAdapter
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.EWork
import kotlinx.android.synthetic.main.fragment_edigging_history.view.*


class EDiggingHistoryFragment : Fragment() {


    private val TAG = this::class.java.simpleName
    private lateinit var helper: Helper
    private var root: View? = null
    private lateinit var diggingHistory : MutableList<EWork>
    private lateinit var db : DatabaseAdapter

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            helper = Helper(TAG,
                myContext
            )
            db = DatabaseAdapter(myContext)
            diggingHistory = db.getEWorks(1)
            helper.log("Digging:$diggingHistory ")
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

        val workType = 1
        val mAdapter = ETHistoryAdapter(
            myContext, diggingHistory,
            FRAGMENT_TAG, workType)
        root!!.edh_rv.layoutManager = LinearLayoutManager(myContext, LinearLayout.VERTICAL, false)
        root!!.edh_rv!!.setAdapter(mAdapter)
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

        private lateinit var myContext : EHistoryActivity
        private lateinit var FRAGMENT_TAG: String

        @JvmStatic
        fun newInstance(
            eHistoryActivity: EHistoryActivity,
            FRAGMENT_TG: String
        ) =
            EDiggingHistoryFragment().apply {
                arguments = Bundle().apply {
                    myContext = eHistoryActivity
                    FRAGMENT_TAG = FRAGMENT_TG
                }
            }
    }
}
