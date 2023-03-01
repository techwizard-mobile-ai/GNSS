package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter

class ELoadingAdapter(
  val context: Activity,
  private val myDataList: ArrayList<MyData>
) : RecyclerView.Adapter<ELoadingAdapter
.ViewHolder>() {
  
  private val tag1 = this::class.java.simpleName
  private lateinit var db: DatabaseAdapter
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
    db = DatabaseAdapter(context)
    val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_eloading, parent, false)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val v = holder.itemView
    val elhr_number = v.findViewById<TextView>(R.id.elhr_number)
    val elhr_material = v.findViewById<TextView>(R.id.elhr_material)
    val elhr_time = v.findViewById<TextView>(R.id.elhr_time)
    
    val myData = myDataList[position]
    
    elhr_number.text = "Load # " + (myDataList.size - position)
    elhr_material.text = db.getMaterialByID(myData.loading_material_id).name
    elhr_time.text = "${myData.date} Hrs"
  }
  
  
  override fun getItemCount(): Int {
    return myDataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

