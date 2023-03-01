package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import app.vsptracker.others.MyHelper

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
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_et_history, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val eWork = dataList[position]
    val v = holder.itemView
    val eth_row = v.findViewById<LinearLayout>(R.id.eth_row)
    val eth_record_number = v.findViewById<TextView>(R.id.eth_record_number)
    val eth_action = v.findViewById<TextView>(R.id.eth_action)
    val eth_totalloads = v.findViewById<TextView>(R.id.eth_totalloads)
    val eth_is_sync = v.findViewById<TextView>(R.id.eth_is_sync)
    val eth_mode = v.findViewById<TextView>(R.id.eth_mode)
    
    val eth_site = v.findViewById<TextView>(R.id.eth_site)
    val eth_material = v.findViewById<TextView>(R.id.eth_material)
    val eth_operator = v.findViewById<TextView>(R.id.eth_operator)
    val eth_start_time = v.findViewById<TextView>(R.id.eth_start_time)
    val eth_end_time = v.findViewById<TextView>(R.id.eth_end_time)
    val eth_duration = v.findViewById<TextView>(R.id.eth_duration)
    val eth_date = v.findViewById<TextView>(R.id.eth_date)
    val lhr_gps_loading = v.findViewById<TextView>(R.id.lhr_gps_loading)
    val lhr_gps_unloading = v.findViewById<TextView>(R.id.lhr_gps_unloading)
    
    val eth_meterial_layout = v.findViewById<LinearLayout>(R.id.eth_meterial_layout)
    val eth_totalloads_layout = v.findViewById<LinearLayout>(R.id.eth_totalloads_layout)
    val lhr_gps_loading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_loading_layout)
    val lhr_gps_unloading_layout = v.findViewById<LinearLayout>(R.id.lhr_gps_unloading_layout)
    
    myHelper.log(eWork.toString())
    eth_record_number.text = ":  ${eWork.id}"
    if (eWork.workActionType == 1) {
      when (eWork.workType) {
        3 -> {
          eth_action.text = ":  Trimming"
          eth_meterial_layout.visibility = View.GONE
        }
        else -> {
          eth_action.text = ":  Side Casting"
          eth_meterial_layout.visibility = View.GONE
        }
      }
//            holder.itemView.eth_action.setText(":  Side Casting")
      eth_totalloads_layout.visibility = View.GONE
    } else {
      eth_action.text = ":  Loading"
      if (eWork.workType == 2)
        eth_meterial_layout.visibility = View.VISIBLE
      eth_totalloads_layout.visibility = View.VISIBLE
      eth_totalloads.text =
        ":  " + db.getEWorksOffLoads(eWork.id).size.toString()
    }
    
    eth_is_sync.text = if (eWork.isSync == 1) context.getString(R.string.yes) else context.getString(R.string.no)
    
    when (eWork.isDayWorks) {
      1 -> eth_mode.text = context.getString(R.string.day_works_text)
      else -> eth_mode.text = context.getString(R.string.standard_mode_text)
    }
    
    eth_site.text = ":  ${db.getSiteByID(eWork.siteId).name}"
    eth_material.text = ":  ${db.getMaterialByID(eWork.materialId).name}"
    eth_operator.text = ":  ${db.getOperatorByID(eWork.operatorId).name}"
    eth_start_time.text = ":  " + myHelper.getTime(eWork.startTime) + " Hrs"
    eth_end_time.text = ":  " + myHelper.getTime(eWork.stopTime) + " Hrs"
    eth_duration.text = ":  " + myHelper.getFormattedTime(eWork.totalTime) + " Hrs"
    eth_date.text = ":  " + myHelper.getDateTime(eWork.stopTime) + " Hrs"
    
    lhr_gps_loading.text =
      ": ${myHelper.getRoundedDecimal(eWork.loadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          eWork.loadingGPSLocation.longitude
        )
      } "
    lhr_gps_unloading.text =
      ": ${myHelper.getRoundedDecimal(eWork.unloadingGPSLocation.latitude)} / ${
        myHelper.getRoundedDecimal(
          eWork.unloadingGPSLocation.longitude
        )
      } "
    
    // eWorkType 2 = Trenching
    // eWorkType 1 = General Digging
    
    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    
    lhr_gps_loading_layout.setOnClickListener {
      
      when (eWork.workType) {
        3 -> {
          myHelper.showOnMap(eWork.loadingGPSLocation, "Scraper Trimming Start")
        }
        2 -> {
//                    Trenching
          when (eWork.workActionType) {
            1 -> {
//                            Side Casting
              myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Start (Side Casting)")
            }
            2 -> {
//                            Off Loading
              myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Start (Off Loading)")
            }
          }
        }
        1 -> {
//                    General Digging
          when (eWork.workActionType) {
            1 -> {
//                            Side Casting
              myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Start (Side Casting)")
            }
            2 -> {
//                            Off Loading
              myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Start (Off Loading)")
            }
          }
        }
      }
      
      
    }
    lhr_gps_unloading_layout.setOnClickListener {
//            myHelper.showOnMap(eWork.unloadingGPSLocation, "GPS Location")
      when (eWork.workType) {
        3 -> {
          myHelper.showOnMap(eWork.loadingGPSLocation, "Scraper Trimming Stop")
        }
        2 -> {
//                    Trenching
          when (eWork.workActionType) {
            1 -> {
//                            Side Casting
              myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Stop (Side Casting)")
            }
            2 -> {
//                            Off Loading
              myHelper.showOnMap(eWork.loadingGPSLocation, "Trenching Stop (Off Loading)")
            }
          }
        }
        1 -> {
//                    General Digging
          when (eWork.workActionType) {
            1 -> {
//                            Side Casting
              myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Stop (Side Casting)")
            }
            2 -> {
//                            Off Loading
              myHelper.showOnMap(eWork.loadingGPSLocation, "General Digging Stop (Off Loading)")
            }
          }
        }
      }
    }
    
    eth_row.setOnClickListener {
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

