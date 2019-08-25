package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import com.lysaan.malik.vsptracker.others.EWork
import kotlinx.android.synthetic.main.list_row_et_history.view.*


class ETHistoryAdapter(
    val context: Activity,
    val dataList: MutableList<EWork>,
    val FRAGMENT_TAG: String,
    val workType: Int
) : RecyclerView.Adapter<ETHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    lateinit var helper: Helper
    lateinit var db : DatabaseAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ETHistoryAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_et_history, parent, false)
        helper = Helper(TAG,context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ETHistoryAdapter.ViewHolder, position: Int) {

        val eWork= dataList.get(position)

        holder.itemView.eth_record_number.setText(":  "+(dataList.size - position))
        if(eWork.workActionType == 1){
            holder.itemView.eth_action.setText(":  Side Casting")
            holder.itemView.eth_totalloads_layout.visibility = View.GONE
        }else{
            holder.itemView.eth_action.setText(":  Loading")
            holder.itemView.eth_totalloads_layout.visibility = View.VISIBLE
            holder.itemView.eth_totalloads.text = ":  "+db.getEWorksOffLoads(eWork.ID).size.toString()
        }

        holder.itemView.eth_start_time.setText(":  "+helper.getTime(eWork.startTime)+" Hrs")
        holder.itemView.eth_end_time.setText(":  "+helper.getTime(eWork.stopTime)+" Hrs")
        holder.itemView.eth_duration.setText(":  "+helper.getFormatedTime(eWork.totalTime)+" Hrs")
        holder.itemView.eth_date.setText(":  "+helper.getDateTime(eWork.stopTime)+" Hrs")
        holder.itemView.eth_mode.setText(":  ${eWork.workMode}")


        holder.itemView.eth_row.setOnClickListener {
            if(eWork.workActionType == 2){
                val activity = holder.itemView.getContext() as AppCompatActivity
                val eOffloadingLoadsFragment = EOffloadingLoadsFragment.newInstance(
                    context,  FRAGMENT_TAG, eWork )
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, eOffloadingLoadsFragment, FRAGMENT_TAG)
                transaction.addToBackStack(FRAGMENT_TAG)
                transaction.commit()
            }



        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

