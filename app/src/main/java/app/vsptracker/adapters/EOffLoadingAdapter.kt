package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork

class EOffLoadingAdapter(
  val context: Activity,
  private val dataList: ArrayList<EWork>
) : RecyclerView
    .Adapter<EOffLoadingAdapter
.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_eoffloading, parent, false)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    val elhr_number = v.findViewById<TextView>(R.id.elhr_number)
    val elhr_time = v.findViewById<TextView>(R.id.elhr_time)
    val myData = dataList[position]
    
    elhr_number.text = "Load # " + (dataList.size - position)
    elhr_time.text = ":  " + myData.date + " Hrs"
  }
  
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

