package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_delay_history.view.*

class DelayHistoryAdapter(
    val context: Activity,
    private val dataList: ArrayList<EWork>
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
        myHelper.log("waiting:$eWork")
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
        holder.itemView.eth_record_number.text = ":  " + (eWork.id)
        holder.itemView.eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"

        val operatorName =  (db.getOperatorByID(eWork.operatorId)).name
        holder.itemView.eth_operator.text = ":  $operatorName"

        holder.itemView.eth_machine_number.text = ":  " + eWork.machineNumber
        holder.itemView.eth_start_time.text = ":  " + myHelper.getTime(eWork.startTime) + " Hrs"
        holder.itemView.eth_end_time.text = ":  " + myHelper.getTime(eWork.stopTime) + " Hrs"
        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
        val date = myHelper.getDateTime(eWork.stopTime)
        holder.itemView.eth_date.text = ":  $date Hrs"

        when {
            eWork.isDayWorks > 0 -> { holder.itemView.eth_mode.text = context.getString(R.string.day_works_text) }
            else -> holder.itemView.eth_mode.text = context.getString(R.string.standard_mode_text)
        }
//        holder.itemView.eth_mode.text = ":  ${eWork.workMode}"

        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${myHelper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.loadingGPSLocation, "$operatorName using Machine ${eWork.machineNumber } Started Waiting")
        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.unloadingGPSLocation, "$operatorName using Machine ${eWork.machineNumber } Stopped Waiting")
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

