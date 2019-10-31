package app.vsptracker.adapters

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData

import kotlinx.android.synthetic.main.list_row_eloading.view.*

class ELoadingAdapter(
        val context: Activity,
        val myDataList: MutableList<MyData>
) : RecyclerView.Adapter<ELoadingAdapter
.ViewHolder>() {


    private val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ELoadingAdapter.ViewHolder {

        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.list_row_eloading, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ELoadingAdapter.ViewHolder, position: Int) {

        val myData = myDataList.get(position)

        holder.itemView.elhr_number.setText("Load # " + (myDataList.size - position))
        holder.itemView.elhr_material.setText(myData.loadingMaterial)
        holder.itemView.elhr_time.setText(myData.time + " Hrs")
    }


    override fun getItemCount(): Int {
        return myDataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

