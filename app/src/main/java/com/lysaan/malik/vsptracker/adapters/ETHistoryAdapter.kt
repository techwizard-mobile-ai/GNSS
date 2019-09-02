package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.EWork
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import kotlinx.android.synthetic.main.list_row_et_history.view.*


class ETHistoryAdapter(
        val context: Activity,
        val dataList: MutableList<EWork>,
        val FRAGMENT_TAG: String,
        val workType: Int
) : RecyclerView.Adapter<ETHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ETHistoryAdapter.ViewHolder {
        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.list_row_et_history, parent, false)
        myHelper = MyHelper(TAG, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ETHistoryAdapter.ViewHolder, position: Int) {

        val eWork = dataList.get(position)

        holder.itemView.eth_record_number.setText(":  " + (dataList.size - position))
        if (eWork.workActionType == 1) {
            holder.itemView.eth_action.setText(":  Side Casting")
            holder.itemView.eth_totalloads_layout.visibility = View.GONE
        } else {
            holder.itemView.eth_action.setText(":  Loading")
            holder.itemView.eth_totalloads_layout.visibility = View.VISIBLE
            holder.itemView.eth_totalloads.text =
                    ":  " + db.getEWorksOffLoads(eWork.ID).size.toString()
        }

        holder.itemView.eth_start_time.setText(":  " + myHelper.getTime(eWork.startTime) + " Hrs")
        holder.itemView.eth_end_time.setText(":  " + myHelper.getTime(eWork.stopTime) + " Hrs")
        holder.itemView.eth_duration.setText(":  " + myHelper.getFormatedTime(eWork.totalTime) + " Hrs")
        holder.itemView.eth_date.setText(":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs")
        holder.itemView.eth_mode.setText(":  ${eWork.workMode}")


        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${myHelper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.loadingGPSLocation, "GPS Location")
        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.unloadingGPSLocation, "GPS Location")
        }

        holder.itemView.eth_row.setOnClickListener {
            if (eWork.workActionType == 2) {
                val activity = holder.itemView.getContext() as AppCompatActivity
                val eOffloadingLoadsFragment = EOffloadingLoadsFragment.newInstance(
                        context, FRAGMENT_TAG, eWork
                )
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

