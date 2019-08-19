package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.list_row_eload_history.view.*

class ELoadingHistoryAdapter(
    val context: Activity,
    val dataList: MutableList<Data>
) : RecyclerView.Adapter<ELoadingHistoryAdapter
.ViewHolder>() {

    val TAG = "ELoadingHistoryAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ELoadingHistoryAdapter.ViewHolder {


        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_eload_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ELoadingHistoryAdapter.ViewHolder, position: Int) {

        val myData = dataList.get(position)

//        holder.itemView.elhr_number.setText("Truck "+data.recordID)
        holder.itemView.elhr_number.setText("Truck "+(dataList.size - position))
        holder.itemView.elhr_material.setText(myData.loadingMaterial)
        holder.itemView.elhr_time.setText(myData.time)

//        holder.itemView.delete_product.setOnClickListener {
//            showAlertDialog(holder.itemView.context, product , position)
//        }
    }



    override fun getItemCount(): Int {
        return dataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

