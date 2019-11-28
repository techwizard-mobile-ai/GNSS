package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.classes.ServerSyncModel
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_server_sync.view.*

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

        val datum = dataList[position]
        myHelper.log("serverSyncModel:$datum")

        holder.itemView.ssl_data_type.text = ":  ${datum.name}"
        holder.itemView.ssl_total.text = ":  ${datum.total}"
        holder.itemView.ssl_synced.text = ":  ${datum.synced}"
        holder.itemView.ssl_remaining.text = ":  ${datum.remaining}"

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

