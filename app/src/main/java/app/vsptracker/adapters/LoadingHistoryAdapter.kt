package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.others.MyHelper
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.list_row_loading_history.view.*


class LoadingHistoryAdapter(
    val context: Activity,
    private val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<LoadingHistoryAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {


        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_loading_history, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myData = myDataList[position]

        when (myData.tripType) {
            0 -> {
                holder.itemView.lhr_trip_number.text = ":  ${myData.id}"
                holder.itemView.lhr_trip_type.text = ": Simple Load"
            }
            1 -> {
                holder.itemView.lhr_trip_number.text = ":  ${myData.id}"
                holder.itemView.lhr_trip_type.text = ": Back Load"
            }
        }


        holder.itemView.lhr_trip0_id.text = ": ${myData.trip0ID}"
        holder.itemView.lhr_operator.text = ": ${db.getOperatorByID(myData.operatorId.toString()).name}"
        holder.itemView.lhr_sync.text = ": ${if(myData.isSync == 1) "Yes" else "No"}"

        when (myData.loadedMachineType) {
            2 -> {
                holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
                holder.itemView.lhr_task_layout.visibility = View.GONE
            }
            3 -> {
                holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
                holder.itemView.lhr_task_layout.visibility = View.VISIBLE
            }
        }
        holder.itemView.lhr_loading_machine.text = ":  " + myData.loadingMachine

        holder.itemView.lhr_loaded_material.text = ":  ${myData.loadingMaterial} / ${myData.unloadingMaterial}"
        holder.itemView.lhr_loading_location.text = ":  ${myData.loadingLocation} / ${myData.unloadingLocation}"
        holder.itemView.lhr_loading_weight.text = ":  " + myData.unloadingWeight
        holder.itemView.lhr_task.text = ":  " + myData.unloadingTask

        holder.itemView.lhr_time.text = ": ${myHelper.getTime(myData.startTime)} / ${myHelper.getTime(
            myData.stopTime
        )} Hrs"

        holder.itemView.lhr_duration.text = ": ${myHelper.getFormattedTime(myData.totalTime)} Hrs"
        holder.itemView.lhr_workmode.text = ": ${myData.workMode}"

        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(myData.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        myData.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${myHelper.getRoundedDecimal(myData.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        myData.unloadingGPSLocation.longitude
                )} "

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {
            when (myData.tripType) {
                0 -> {
                    myHelper.showOnMap(myData.loadingGPSLocation, "Loading Location (${myData.loadingLocation})")
                }
                1 -> {
                    myHelper.showOnMap(myData.loadingGPSLocation, "Back Loading Location (${myData.loadingLocation})")
                }
            }

        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
            when (myData.tripType) {
                0 -> {
                    myHelper.showOnMap(myData.unloadingGPSLocation, "Unloading Location (${myData.unloadingLocation})")
                }
                1 -> {
                    myHelper.showOnMap(myData.unloadingGPSLocation, "Back Unloading Location (${myData.unloadingLocation})")
                }
            }

        }


    }


    override fun getItemCount(): Int {
        return myDataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

