package app.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.MyHelper
import kotlinx.android.synthetic.main.list_row_delay_history.view.*


class DelayHistoryAdapter(
        val context: Activity,
        val dataList: MutableList<EWork>
) : RecyclerView.Adapter<DelayHistoryAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): DelayHistoryAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_delay_history, parent, false)
        myHelper = MyHelper(TAG, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DelayHistoryAdapter.ViewHolder, position: Int) {

        val eWork = dataList.get(position)

        when (eWork.machineTypeId) {
            1 -> {
                holder.itemView.eth_machine_type.setText(":  Excavator")
            }
            2 -> {
                holder.itemView.eth_machine_type.setText(":  Scraper")
            }
            3 -> {
                holder.itemView.eth_machine_type.setText(":  Truck")
            }
        }



        holder.itemView.eth_sync.text = ": ${if(eWork.isSync == 1) "Yes" else "No"}"
        holder.itemView.eth_record_number.setText(":  " + (eWork.ID))

        holder.itemView.eth_operator.setText(":  " + (db.getOperatorByID(eWork.operatorId.toString()).name))

        holder.itemView.eth_machine_number.setText(":  " + eWork.machineNumber)
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
            myHelper.showOnMap(eWork.loadingGPSLocation, "Delay Location")
        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.unloadingGPSLocation, "Delay Location")
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

