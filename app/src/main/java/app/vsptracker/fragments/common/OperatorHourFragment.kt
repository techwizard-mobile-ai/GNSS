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
import app.vsptracker.adapters.OperatorHourAdapter
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.fragment_operator_hour.view.*


class OperatorHourFragment : Fragment() {
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
        root = inflater.inflate(R.layout.fragment_operator_hour, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = db.getOperatorsHours()
        val mAdapter = OperatorHourAdapter(context as Activity, dataList)
        root!!.oh_rv.layoutManager = LinearLayoutManager(context as Activity, RecyclerView.VERTICAL, false)
        root!!.oh_rv!!.adapter = mAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        myContext =  context as Activity
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

        @JvmStatic
        fun newInstance(
            FRAGMENT_TG: String
        ) =
            OperatorHourFragment().apply {
                arguments = Bundle().apply {
                    //                        myContext = eHistoryActivity
                    FRAGMENT_TAG = FRAGMENT_TG
                }
            }
    }
}

