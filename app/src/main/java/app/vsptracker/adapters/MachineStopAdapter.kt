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
import kotlinx.android.synthetic.main.list_row_machine_stop.view.*

class MachineStopAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<MachineStopAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_machine_stop, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val datum = dataList[position]

        myHelper.log(datum.toString())

        val siteName = db.getSiteByID(datum.siteId).name
        holder.itemView.oh_site.text = ":  $siteName"
        holder.itemView.oh_machine_type.text = ":  ${db.getMachineTypeByID(datum.machineTypeId).name}"
        holder.itemView.oh_machine_number.text = ":  ${db.getMachineByID(datum.machineId).number}"
        holder.itemView.eth_sync.text = ": ${if (datum.isSync == 1) "Yes" else "No"}"
        holder.itemView.eth_record_number.text = ":  " + (datum.id)
        val operatorName = db.getOperatorByID(datum.operatorId).name
        holder.itemView.eth_operator.text = ":  $operatorName"

        if(datum.startTime > 0)
        holder.itemView.eth_start_time.text = ":  " + myHelper.getDateTime(datum.startTime) + " Hrs"
        if(datum.stopTime > 0)
        holder.itemView.eth_end_time.text = ":  " + myHelper.getDateTime(datum.stopTime) + " Hrs"
        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(datum.totalTime) + " Hrs"
        holder.itemView.eth_date.text = ":  " + myHelper.getDateTime(datum.stopTime) + " Hrs"

        when (datum.isDayWorks) {
            1 -> holder.itemView.eth_mode.text = context.getString(R.string.day_works_text)
            else -> holder.itemView.eth_mode.text = context.getString(R.string.standard_mode_text)
        }

//        holder.itemView.eth_mode.text = ":  "+ if(datum.isDayWorks>0) "Day Works" else "Standard Mode"

        holder.itemView.mh_gps_loading.text =
            ": ${myHelper.getRoundedDecimal(datum.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                datum.loadingGPSLocation.longitude
            )} "
        holder.itemView.lhr_gps_unloading.text =
            ": ${myHelper.getRoundedDecimal(datum.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                datum.unloadingGPSLocation.longitude
            )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(datum.loadingGPSLocation, "$operatorName Started Work at $siteName")
        }
        holder.itemView.mh_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(datum.unloadingGPSLocation, "$operatorName Stopped Work at $siteName")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

