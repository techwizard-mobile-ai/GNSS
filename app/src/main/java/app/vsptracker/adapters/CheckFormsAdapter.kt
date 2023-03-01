package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.activities.CheckFormTaskActivity
import app.vsptracker.activities.CheckFormsActivity
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper

class CheckFormsAdapter(
  val context: Activity,
  private val dataList: ArrayList<MyData>,
  private val type: Int
) : RecyclerView.Adapter<CheckFormsAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_check_forms, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val v = holder.itemView
    val cf_start = v.findViewById<Button>(R.id.cf_start)
    val cf_id = v.findViewById<TextView>(R.id.cf_id)
    val cf_name = v.findViewById<TextView>(R.id.cf_name)
    val cf_applicable = v.findViewById<TextView>(R.id.cf_applicable)
    val cf_schedule = v.findViewById<TextView>(R.id.cf_schedule)
    val cf_questions = v.findViewById<TextView>(R.id.cf_questions)
    
    val datum = dataList[position]
    myHelper.log(datum.toString())
    
    when (type) {
      MyEnum.ADMIN_CHECKFORMS_DUE -> {
        // Due checkforms
        cf_start.background = context.getDrawable(R.drawable.bdue_background)
      }
      MyEnum.ADMIN_CHECKFORMS_ALL -> {
        // All checkforms
        cf_start.background = context.getDrawable(R.drawable.bnext_background)
      }
    }
    
    cf_id.text = ":  ${datum.id}"
    cf_name.text = ":  ${datum.name}"
    
    var applicable = ""
    
    if (datum.siteId < 1) {
      applicable = "All Sites"
    } else {
      val siteName = db.getSiteByID(datum.siteId).name
      if (datum.machineTypeId < 1) {
        applicable = "$siteName\n  [ All Machines Types ]"
      } else {
        val machineTypeName = db.getMachineTypeByID(datum.machineTypeId).name
        if (datum.machineId < 1) {
          applicable = "$siteName\n  [All $machineTypeName ]"
        } else {
          myHelper.log("All Machines: ${db.getMachines()}")
          myHelper.log("Machine:${db.getMachineByID(datum.machineId)}")
          val machineNumber = db.getMachineByID(datum.machineId).number
          applicable = "$siteName\n  [ $machineTypeName# $machineNumber ]"
        }
      }
    }
    cf_applicable.text = ":  $applicable"
    
    val checkFormSchedule = db.getAdminCheckFormScheduleByID(datum.admin_checkforms_schedules_id)
    var schedules = ""
    if (datum.admin_checkforms_schedules_id == MyEnum.ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START || datum.admin_checkforms_schedules_id == MyEnum.ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START_ONE_TIME) {
      schedules = "${checkFormSchedule.name}"
    } else {
      schedules = "${checkFormSchedule.name}[${datum.admin_checkforms_schedules_value}]"
    }
    
    cf_schedule.text = ":  $schedules"
    
    val totalQuestions = myHelper.getQuestionsIDsList(datum.questions_data).size
    cf_questions.text = ":  $totalQuestions"
    
    cf_start.setOnClickListener {
      when (totalQuestions) {
        0 -> {
          myHelper.showErrorDialog("There are no questions in this checkform.")
        }
        else -> {
          val intent = Intent(context, CheckFormTaskActivity::class.java)
          intent.putExtra("checkform_id", datum.id)
          intent.putExtra("entry_type", type)
          intent.putExtra("gpsLocation", (context as CheckFormsActivity).gpsLocation)
          myHelper.log("sentGPSLocation:${(context as CheckFormsActivity).gpsLocation}")
          context.startActivity(intent)
        }
      }
      
    }
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

