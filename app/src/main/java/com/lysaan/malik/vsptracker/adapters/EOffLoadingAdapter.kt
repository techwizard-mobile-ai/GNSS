package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.EWork
import kotlinx.android.synthetic.main.list_row_eloading.view.*

class EOffLoadingAdapter(
    val context: Activity,
    val dataList: MutableList<EWork>
) : RecyclerView.Adapter<EOffLoadingAdapter
.ViewHolder>() {


    private val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EOffLoadingAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_eoffloading, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: EOffLoadingAdapter.ViewHolder, position: Int) {

        val myData = dataList.get(position)

        holder.itemView.elhr_number.setText("Load # " + (dataList.size - position))
        holder.itemView.elhr_time.setText(":  " + myData.time + " Hrs")
    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}

