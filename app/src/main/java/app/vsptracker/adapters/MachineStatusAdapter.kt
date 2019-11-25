package app.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.common.MachineStatusActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.Material
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_machine_status.view.*
class MachineStatusAdapter(
    private val myContext: Activity,
    private val dataList: MutableList<Material>
) : RecyclerView.Adapter<MachineStatusAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_machine_status, parent, false)
        myHelper = MyHelper(tag, myContext)
        db = DatabaseAdapter(myContext)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val material = dataList[position]

        holder.itemView.machine_status_rv_title.text = material.name
        holder.itemView.machine_status_rv_title.setOnClickListener {
//            myHelper.toast("Machine Stopped due to : " + material.name)

            val data = MyData()
            data.machineStoppedReason = material.name
            data.loadingGPSLocation = (myContext as MachineStatusActivity).gpsLocation
//            val insertID = db.insertMachineStop(data)
            myContext.myDataPushSave.insertMachineStop(data, material)
//            if (insertID > 0) {
//                myHelper.toast("Record Saved in Database Successfully.")
//                myHelper.stopMachine(insertID, material)
//                myHelper.setIsMachineStopped(true, material.name, material.id)
                myHelper.logout(myContext)
                myContext.finishAffinity()
//            } else {
//                myHelper.toast("Machine Not Stopped. Please try again.")
//            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

