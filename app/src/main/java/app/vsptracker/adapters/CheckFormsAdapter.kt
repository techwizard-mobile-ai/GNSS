package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.CheckFormTaskActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_check_forms.view.*

class CheckFormsAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>,
    private val type: Int
) : RecyclerView.Adapter<CheckFormsAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_check_forms, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val datum = dataList[position]
        myHelper.log(datum.toString())
        
        when(type){
            0 -> {
                // Due checkforms
                holder.itemView.cf_start.background = context.getDrawable(R.drawable.bdue_background)
            }
            1 -> {
                // All checkforms
                holder.itemView.cf_start.background = context.getDrawable(R.drawable.bnext_background)
            }
        }
        
        holder.itemView.cf_id.text = ":  ${datum.id}"
        holder.itemView.cf_name.text = ":  ${datum.name}"
        
        var applicable = ""
        
        if(datum.siteId <  1){
            applicable = "All Sites"
        }else{
            val siteName = db.getSiteByID(datum.siteId).name
            if(datum.machineTypeId < 1){
                applicable = "$siteName\n  [ All Machines Types ]"
            }else{
                val machineTypeName = db.getMachineTypeByID(datum.machineTypeId).name
                if(datum.machineId < 1){
                    applicable = "$siteName\n  [All $machineTypeName ]"
                }else{
                    myHelper.log("All Machines: ${db.getMachines()}")
                    myHelper.log("Machine:${db.getMachineByID(datum.machineId)}")
                    val machineNumber = db.getMachineByID(datum.machineId).number
                    applicable = "$siteName\n  [ $machineTypeName# $machineNumber ]"
                }
            }
        }
        holder.itemView.cf_applicable.text = ":  $applicable"
        
        val checkFormSchedule = db.getAdminCheckFormScheduleByID(datum.admin_checkforms_schedules_id)
        holder.itemView.cf_schedule.text = ":  ${checkFormSchedule.name}[${datum.admin_checkforms_schedules_value}]"
        holder.itemView.cf_questions.text = ":  ${myHelper.getQuestionsIDsList(datum.questions_data).size}"
        
        holder.itemView.cf_start.setOnClickListener {
            val intent = Intent(context, CheckFormTaskActivity::class.java)
            intent.putExtra("checkform_id", datum.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}

