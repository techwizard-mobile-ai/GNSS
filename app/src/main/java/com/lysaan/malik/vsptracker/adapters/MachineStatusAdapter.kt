package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.classes.Material
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import kotlinx.android.synthetic.main.list_row_machine_status.view.*


class MachineStatusAdapter(
    val myContext: Activity,
    val dataList: MutableList<Material>
) : RecyclerView.Adapter<MachineStatusAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName
    private lateinit var helper: Helper
    private lateinit var db : DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MachineStatusAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_machine_status, parent, false)
        helper = Helper(TAG, myContext)
        db = DatabaseAdapter(myContext)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: MachineStatusAdapter.ViewHolder, position: Int) {

        val material = dataList.get(position)

        holder.itemView.machine_status_rv_title.setText(material.name)
        holder.itemView.machine_status_rv_title.setOnClickListener {
            helper.toast("Machine Stopped due to : " + material.name)


            val data = Data()
            data.machineStoppedReason = material.name
            data.loadingGPSLocation  = (myContext as MachineStatus1Activity).gpsLocation




            val insertID = db.insertMachineStatus(data)

            if(insertID > 0) {
                helper.toast("Record Saved in Database Successfully.")
                helper.setIsMachineStopped(true, material.name)
                helper.stopMachine(insertID)

                helper.startHomeActivityByType(Data())

            }else{
                helper.toast("Machine Not Stopped. Please try again.")
            }



        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

