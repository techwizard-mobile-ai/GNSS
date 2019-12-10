package app.vsptracker.adapters
import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_et_history.view.*
class ETHistoryAdapter(
    val context: Activity,
    private val dataList: ArrayList<EWork>,
    private val FRAGMENT_TAG: String,
    val workType: Int
) : RecyclerView.Adapter<ETHistoryAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.list_row_et_history, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val eWork = dataList[position]

        myHelper.log(eWork.toString())
        holder.itemView.eth_record_number.text = ":  ${eWork.id}"
        if (eWork.workActionType == 1) {
            when(eWork.workType){
                3 -> {
                    holder.itemView.eth_action.text = ":  Trimming"
                    holder.itemView.eth_meterial_layout.visibility = View.GONE
                }
                else -> {
                    holder.itemView.eth_action.text = ":  Side Casting"
                    holder.itemView.eth_meterial_layout.visibility = View.GONE
                }
            }
//            holder.itemView.eth_action.setText(":  Side Casting")
            holder.itemView.eth_totalloads_layout.visibility = View.GONE
        } else {
            holder.itemView.eth_action.text = ":  Loading"
            if(eWork.workType == 2)
            holder.itemView.eth_meterial_layout.visibility = View.VISIBLE
            holder.itemView.eth_totalloads_layout.visibility = View.VISIBLE
            holder.itemView.eth_totalloads.text =
                    ":  " + db.getEWorksOffLoads(eWork.id).size.toString()
        }

        holder.itemView.eth_is_sync.text = if (eWork.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)

        when (eWork.isDayWorks) {
            1 -> holder.itemView.eth_mode.text = context.getString(R.string.day_works_text)
            else -> holder.itemView.eth_mode.text = context.getString(R.string.standard_mode_text)
        }

        holder.itemView.eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"
        holder.itemView.eth_material.text = ":  ${db.getMaterialByID(eWork.materialId).name}"
        holder.itemView.eth_operator.text = ":  ${db.getOperatorByID(eWork.operatorId).name}"
        holder.itemView.eth_start_time.text = ":  " + myHelper.getTime(eWork.startTime) + " Hrs"
        holder.itemView.eth_end_time.text = ":  " + myHelper.getTime(eWork.stopTime) + " Hrs"
        holder.itemView.eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
        holder.itemView.eth_date.text = ":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs"

        holder.itemView.lhr_gps_loading.text =
                ": ${myHelper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.loadingGPSLocation.longitude
                )} "
        holder.itemView.lhr_gps_unloading.text =
                ": ${myHelper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${myHelper.getRoundedDecimal(
                        eWork.unloadingGPSLocation.longitude
                )} "

        // eWorkType 2 = Trenching
        // eWorkType 1 = General Digging

        // eWorkActionType 1 = Side Casting
        // eWorkActionType 2 = Off Loading

        holder.itemView.lhr_gps_loading_layout.setOnClickListener {

            when(eWork.workType){
                3 -> {
                    myHelper.showOnMap(eWork.loadingGPSLocation, "Scraper Trimming Start")
                }
                2 -> {
//                    Trenching
                    when(eWork.workActionType){
                        1 ->{
//                            Side Casting
                            myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Start (Side Casting)")
                        }
                        2 ->{
//                            Off Loading
                            myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Start (Off Loading)")
                        }
                    }
                }
                1 -> {
//                    General Digging
                    when(eWork.workActionType){
                        1 ->{
//                            Side Casting
                            myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Start (Side Casting)")
                        }
                        2 ->{
//                            Off Loading
                            myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Start (Off Loading)")
                        }
                    }
                }
            }


        }
        holder.itemView.lhr_gps_unloading_layout.setOnClickListener {
//            myHelper.showOnMap(eWork.unloadingGPSLocation, "GPS Location")
            when(eWork.workType){
                3 -> {
                    myHelper.showOnMap(eWork.loadingGPSLocation, "Scraper Trimming Stop")
                }
                2 -> {
//                    Trenching
                    when(eWork.workActionType){
                        1 ->{
//                            Side Casting
                            myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Stop (Side Casting)")
                        }
                        2 ->{
//                            Off Loading
                            myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Stop (Off Loading)")
                        }
                    }
                }
                1 -> {
//                    General Digging
                    when(eWork.workActionType){
                        1 ->{
//                            Side Casting
                            myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Stop (Side Casting)")
                        }
                        2 ->{
//                            Off Loading
                            myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Stop (Off Loading)")
                        }
                    }
                }
            }
        }

        holder.itemView.eth_row.setOnClickListener {
            if (eWork.workActionType == 2) {
                val activity = holder.itemView.context as AppCompatActivity
                val eOffloadingLoadsFragment = EOffloadingLoadsFragment.newInstance(
                    FRAGMENT_TAG, eWork
                )
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, eOffloadingLoadsFragment, FRAGMENT_TAG)
                transaction.addToBackStack(FRAGMENT_TAG)
                transaction.commit()
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

