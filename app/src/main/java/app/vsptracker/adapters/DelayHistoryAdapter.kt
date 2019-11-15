package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.list_row_delay_history.view.*


class DelayHistoryAdapter(
    val context: Activity,
    private val dataList: MutableList<EWork>
) : RecyclerView.Adapter<DelayHistoryAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_delay_history, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val eWork = dataList[position]

        when (eWork.machineTypeId) {
            1 -> {
                holder.itemView.eth_machine_type.text = ":  Excavator"
            }
            2 -> {
                holder.itemView.eth_machine_type.text = ":  Scraper"
            }
            3 -> {
                holder.itemView.eth_machine_type.text = ":  Truck"
            }
        }



        holder.itemView.eth_sync.text = ": ${if(eWork.isSync == 1) "Yes" else "No"}"
        holder.itemView.eth_record_number.text = ":  " + (eWork.ID)

        holder.itemView.eth_operator.text = ":  " + (db.getOperatorByID(eWork.operatorId.toString()).name)

        holder.itemView.eth_machine_number.text = ":  " + eWork.machineNumber
        holder.itemView.eth_start_time.text = ":  " + myHelper.getTime(eWork.startTime) + " Hrs"
        holder.itemView.eth_end_time.text = ":  " + myHelper.getTime(eWork.stopTime) + " Hrs"
        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
        holder.itemView.eth_date.text = ":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs"
        holder.itemView.eth_mode.text = ":  ${eWork.workMode}"

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

