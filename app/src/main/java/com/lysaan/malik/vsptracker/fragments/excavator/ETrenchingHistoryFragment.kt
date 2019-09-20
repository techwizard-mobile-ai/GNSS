package com.lysaan.malik.vsptracker.fragments.excavator

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
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.adapters.ETHistoryAdapter
import com.lysaan.malik.vsptracker.classes.EWork
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.fragment_etrenching_history.view.*


class ETrenchingHistoryFragment : Fragment() {

    private val TAG = this::class.java.simpleName

    private lateinit var myHelper: MyHelper
    private var root: View? = null

    private lateinit var trenchingHistory: MutableList<EWork>
    private lateinit var db: DatabaseAdapter

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myHelper = MyHelper(
                    TAG,
                    myContext
            )
            db = DatabaseAdapter(myContext)
            trenchingHistory = db.getEWorks(2)
            myHelper.log("Trenching:$trenchingHistory")

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_etrenching_history, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workType = 2
        val mAdapter = ETHistoryAdapter(
                myContext, trenchingHistory,
                FRAGMENT_TAG, workType
        )
        root!!.eth_rv.layoutManager = LinearLayoutManager(myContext, RecyclerView.VERTICAL, false)
        root!!.eth_rv!!.setAdapter(mAdapter)
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
        private lateinit var FRAGMENT_TAG: String

        @JvmStatic
        fun newInstance(
            eHistoryActivity: Activity,
            FRAGMENT_TG: String
        ) =
                ETrenchingHistoryFragment().apply {
                    arguments = Bundle().apply {
                        myContext = eHistoryActivity
                        FRAGMENT_TAG = FRAGMENT_TG
                    }
                }
    }
}
