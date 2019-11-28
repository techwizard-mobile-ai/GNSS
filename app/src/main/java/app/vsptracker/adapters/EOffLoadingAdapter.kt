package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import kotlinx.android.synthetic.main.list_row_eloading.view.*

class EOffLoadingAdapter(
    val context: Activity,
    private val dataList: ArrayList<EWork>
) : RecyclerView
.Adapter<EOffLoadingAdapter
.ViewHolder>() {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_eoffloading, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myData = dataList[position]

        holder.itemView.elhr_number.text = "Load # " + (dataList.size - position)
        holder.itemView.elhr_time.text = ":  " + myData.time + " Hrs"
    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

