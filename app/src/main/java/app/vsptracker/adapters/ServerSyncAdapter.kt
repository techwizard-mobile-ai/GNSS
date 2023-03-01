package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper

class ServerSyncAdapter(
  val context: Activity,
  private val dataList: ArrayList<ServerSyncModel>
) : RecyclerView.Adapter<ServerSyncAdapter
.ViewHolder>() {
  
  private val tag = this::class.java.simpleName
  lateinit var myHelper: MyHelper
  private lateinit var db: DatabaseAdapter
  
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_row_server_sync, parent, false)
    myHelper = MyHelper(tag, context)
    db = DatabaseAdapter(context)
    return ViewHolder(v)
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    
    val v = holder.itemView
    val ssl_data_type = v.findViewById<TextView>(R.id.ssl_data_type)
    val ssl_total = v.findViewById<TextView>(R.id.ssl_total)
    val ssl_synced = v.findViewById<TextView>(R.id.ssl_synced)
    val ssl_remaining = v.findViewById<TextView>(R.id.ssl_remaining)
    val ssl_remaining_size = v.findViewById<TextView>(R.id.ssl_remaining_size)
    val ssl_remaining_size_layout = v.findViewById<LinearLayout>(R.id.ssl_remaining_size_layout)
    
    val datum = dataList[position]
    myHelper.log("serverSyncModel:$datum")
    
    ssl_data_type.text = ":  ${datum.name}"
    ssl_total.text = ":  ${datum.total}"
    ssl_synced.text = ":  ${datum.synced}"
    ssl_remaining.text = ":  ${datum.remaining}"
    ssl_remaining_size_layout.visibility = View.GONE
    if (datum.size > 0) {
      ssl_remaining_size.text = ":  ${myHelper.formatSizeUnits(datum.size.toLong())}"
      ssl_remaining_size_layout.visibility = View.VISIBLE
    }
    
  }
  
  override fun getItemCount(): Int {
    return dataList.size
  }
  
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  
}

