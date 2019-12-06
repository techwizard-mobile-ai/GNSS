package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_machine_hour.view.*

class MachineHourAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<MachineHourAdapter
.ViewHolder>() {
    
    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_machine_hour, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }
    
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val eWork = dataList[position]
        
        myHelper.log(eWork.toString())
        
        holder.itemView.eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"
        holder.itemView.eth_machine_type.text = ":  ${db.getMachineTypeByID(eWork.machineTypeId).name}"
        holder.itemView.eth_sync.text = ": ${if (eWork.isSync == 1) "Yes" else "No"}"
        holder.itemView.eth_record_number.text = ":  " + (dataList.size - position)
        val operatorName = db.getOperatorByID(eWork.operatorId).name
        holder.itemView.eth_operator.text = ":  $operatorName"
        
        val machineNumber = db.getMachineByID(eWork.machineId).number
        holder.itemView.eth_machine_number.text = ":  $machineNumber"
        
        if (eWork.startTime > 0)
            holder.itemView.eth_start_time.text = ":  " + myHelper.getDateTime(eWork.startTime) + " Hrs"
        if (eWork.stopTime > 0)
            holder.itemView.eth_end_time.text = ":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs"
//        val totalTime = eWork.stopTime - eWork.startTime
//        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(totalTime) + " Hrs"
        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
        
        when (eWork.machine_stop_reason_id) {
            -2 -> {
                holder.itemView.eth_remarks.text = ":  Machine Changed"
            }
            -1 -> {
                holder.itemView.eth_remarks.text = ":  Logout"
            }
            else -> {
                if (eWork.machine_stop_reason_id > 0)
                    holder.itemView.eth_remarks.text = ":  ${db.getStopReasonByID(eWork.machine_stop_reason_id)}"
                else holder.itemView.eth_remarks.text = ":  Unknown"
            }
        }
        
        
        holder.itemView.mh_start_hours.text = ":  ${eWork.startHours} Hrs"
        holder.itemView.mh_total_hours.text = ":  ${eWork.totalHours} Hrs"
        
        when {
            eWork.isDayWorks > 0 -> {
                holder.itemView.eth_mode.text = context.getString(R.string.day_works_text)
            }
            else -> holder.itemView.eth_mode.text = context.getString(R.string.standard_mode_text)
        }
        
        
        holder.itemView.mh_gps_loading.text =
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
        holder.itemView.mh_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(eWork.unloadingGPSLocation, "Delay Location")
        }
    }
    
    override fun getItemCount(): Int {
        return dataList.size
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}

