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
import com.lysaan.malik.vsptracker.adapters.EOffLoadingAdapter
import com.lysaan.malik.vsptracker.classes.EWork
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.fragment_eoffloading_loads.*
import kotlinx.android.synthetic.main.fragment_eoffloading_loads.view.*


class EOffloadingLoadsFragment : Fragment() {
    private val TAG = this::class.java.simpleName

    private lateinit var myHelper: MyHelper
    private var root: View? = null


    private lateinit var diggingHistory: MutableList<EWork>
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

        if (eWork.workType == 1) {
            offloading_loads_title.text = "General Digging Loads History"
        } else {
            offloading_loads_title.text = "Trenching Loads History"
        }

        val mAdapter = EOffLoadingAdapter(
                myContext, db.getEWorksOffLoads(
                eWork.ID
        )
        )
        root!!.eoff_fragment_rv.layoutManager =
                LinearLayoutManager(myContext, RecyclerView.VERTICAL, false)
        root!!.eoff_fragment_rv!!.setAdapter(mAdapter)
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
        private lateinit var eWork: EWork

        @JvmStatic
        fun newInstance(mContext: Activity, fragmentTag: String, eWork1: EWork) =
                EOffloadingLoadsFragment().apply {
                    arguments = Bundle().apply {
                        myContext = mContext
                        FRAGMENT_TAG = fragmentTag
                        eWork = eWork1
                    }
                }
    }
}
