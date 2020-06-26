package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.fragments.common.CheckFormsFragment
import app.vsptracker.others.MyEnum
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_check_forms.view.cf_applicable
import kotlinx.android.synthetic.main.list_row_check_forms.view.cf_id
import kotlinx.android.synthetic.main.list_row_check_forms.view.cf_name
import kotlinx.android.synthetic.main.list_row_check_forms.view.cf_questions
import kotlinx.android.synthetic.main.list_row_check_forms.view.cf_schedule
import kotlinx.android.synthetic.main.list_row_check_forms_completed.view.*

class CheckFormsCompletedAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>,
    private val type: Int,
    private val supportFragmentManager1: FragmentManager
) : RecyclerView.Adapter<CheckFormsCompletedAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_check_forms_completed, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val datum = dataList[position]
        myHelper.log(datum.toString())
        val adminCheckForm = db.getAdminCheckFormByID(datum.admin_checkforms_id)
        holder.itemView.cf_id.text = ":  ${datum.id}"
        holder.itemView.cf_checkform_id.text = ":  ${adminCheckForm.id}"
        holder.itemView.cf_entry_type.text = ":  ${if (datum.entry_type == 0) "Automatic" else "Manual" }"
        holder.itemView.cf_name.text = ":  ${adminCheckForm.name}"
        
        var applicable = ""
        
        if(adminCheckForm.siteId <  1){
            applicable = "All Sites"
        }else{
            val siteName = db.getSiteByID(adminCheckForm.siteId).name
            if(adminCheckForm.machineTypeId < 1){
                applicable = "$siteName\n  [ All Machines Types ]"
            }else{
                val machineTypeName = db.getMachineTypeByID(adminCheckForm.machineTypeId).name
                if(adminCheckForm.machineId < 1){
                    applicable = "$siteName\n  [All $machineTypeName ]"
                }else{
                    myHelper.log("All Machines: ${db.getMachines()}")
                    myHelper.log("Machine:${db.getMachineByID(adminCheckForm.machineId)}")
                    val machineNumber = db.getMachineByID(adminCheckForm.machineId).number
                    applicable = "$siteName\n  [ $machineTypeName# $machineNumber ]"
                }
            }
        }
        holder.itemView.cf_applicable.text = ":  $applicable"
        
        val checkFormSchedule = db.getAdminCheckFormScheduleByID(adminCheckForm.admin_checkforms_schedules_id)
        var schedules = ""
        if (datum.admin_checkforms_schedules_id == MyEnum.ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START || datum.admin_checkforms_schedules_id == MyEnum.ADMIN_CHECKFORMS_SCHEDULES_ID_MACHINE_START_ONE_TIME) {
            schedules = "${checkFormSchedule.name}"
        } else {
            schedules = "${checkFormSchedule.name}[${datum.admin_checkforms_schedules_value}]"
        }
    
        holder.itemView.cf_schedule.text = ":  $schedules"
        holder.itemView.cfc_operator.text = ":  ${db.getOperatorByID(datum.operatorId).name}"
        val details = "${db.getMachineTypeByID(datum.machineTypeId).name}#${db.getMachineByID(datum.machineId).number} [${db.getSiteByID(datum.siteId).name}]"
        holder.itemView.cfc_details.text = ":  $details"
        
        if (datum.startTime > 0)
            holder.itemView.cfc_start_time.text = ":  " + myHelper.getDateTime(datum.startTime) + " Hrs"
        if (datum.stopTime > 0)
            holder.itemView.cfc_end_time.text = ":  " + myHelper.getDateTime(datum.stopTime) + " Hrs"
        holder.itemView.cfc_duration.text = ":  " + myHelper.getFormattedTime(datum.totalTime) + " Hrs"
    
        holder.itemView.cfc_gps_loading.text =
            ": ${myHelper.getRoundedDecimal(datum.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                datum.loadingGPSLocation.longitude
            )} "
        holder.itemView.cfc_gps_unloading.text =
            ": ${myHelper.getRoundedDecimal(datum.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                datum.unloadingGPSLocation.longitude
            )} "
    
        holder.itemView.cfc_gps_loading_layout.setOnClickListener {
            myHelper.showOnMap(datum.loadingGPSLocation, "Checkform started location.")
        }
        holder.itemView.cfc_gps_unloading_layout.setOnClickListener {
            myHelper.showOnMap(datum.unloadingGPSLocation, "Checkform completed Location")
        }
    
        when {
            datum.isDayWorks > 0 -> {
                holder.itemView.cfc_mode.text = context.getString(R.string.day_works_text)
            }
            else -> holder.itemView.cfc_mode.text = context.getString(R.string.standard_mode_text)
        }
    
        var attemptedQuestions = 0
        when(type){
            3 ->{
                holder.itemView.cfc_sync.text = if (datum.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
                holder.itemView.cfc_sync_layout.visibility = View.VISIBLE
                holder.itemView.cf_details.visibility = View.VISIBLE
                holder.itemView.cf_details.setOnClickListener {
                    val machineStopFragment = CheckFormsFragment.newInstance(3, supportFragmentManager1, datum )
                    openFragment(machineStopFragment, "COMPLETED_CHECKFORMS_DETAILS")
                    myHelper.log(datum.toString())
                }
                attemptedQuestions = datum.checkFormData.size
            }
            
            4-> {
                holder.itemView.cfc_sync_layout.visibility = View.GONE
                holder.itemView.cf_details.visibility = View.GONE
                attemptedQuestions = datum.attempted_questions
            }

        }
    
        val totalQuestions = myHelper.getQuestionsIDsList(adminCheckForm.questions_data).size
    
        holder.itemView.cf_questions.text = ":  $totalQuestions"
        if( totalQuestions > attemptedQuestions){
            holder.itemView.cf_done_questions_text.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.itemView.cf_done_questions.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        holder.itemView.cf_done_questions.text = ":  $attemptedQuestions"

    }
    private fun openFragment(fragment: Fragment, FRAGMENT_TAG: String?) {
        val transaction = supportFragmentManager1.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
        transaction.addToBackStack(FRAGMENT_TAG)
        transaction.commit()
    }
    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}

