package app.vsptracker.fragments.excavator

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
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.adapters.ETHistoryAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.fragment_edigging_history.*
import kotlinx.android.synthetic.main.fragment_edigging_history.view.*


class EDiggingHistoryFragment : Fragment() {


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

        when(workType){
            1 -> e_digging_f_title.text = "General Digging History"
            3 -> e_digging_f_title.text = "Scraper Trimming"
        }
        val mAdapter = ETHistoryAdapter(
                myContext, diggingHistory,
                FRAGMENT_TAG, workType
        )
        root!!.edh_rv.layoutManager = LinearLayoutManager(myContext, RecyclerView.VERTICAL, false)
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

        private lateinit var myContext: Activity
        private lateinit var FRAGMENT_TAG: String
        private var workType = 0

        @JvmStatic
        fun newInstance(
            eHistoryActivity: Activity,
            FRAGMENT_TG: String,
            workType1: Int
        ) =
                EDiggingHistoryFragment().apply {
                    arguments = Bundle().apply {
                        myContext = eHistoryActivity
                        FRAGMENT_TAG = FRAGMENT_TG
                        workType = workType1
                    }
                }
    }
}
