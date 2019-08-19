package com.lysaan.malik.vsptracker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.LoadHistoryActivity
import com.lysaan.malik.vsptracker.others.Data
import kotlinx.android.synthetic.main.list_row_load_history.view.*

class LoadingHistoryAdapter(
    val context: LoadHistoryActivity,
    val dataList: MutableList<Data>
) : RecyclerView.Adapter<LoadingHistoryAdapter
.ViewHolder>() {

    val TAG = "ProductsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingHistoryAdapter.ViewHolder {


        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_load_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LoadingHistoryAdapter.ViewHolder, position: Int) {

        val myData = dataList.get(position)

        if(!myData.recordID.isNullOrBlank()){
            holder.itemView.lhr_record_number_layout.visibility = View.VISIBLE
//            holder.itemView.lhr_record_number.setText(":  "+data.recordID)
            holder.itemView.lhr_record_number.setText(":  "+(dataList.size - position))
        }else{
            holder.itemView.lhr_record_number_layout.visibility = View.GONE
        }

        if(!myData.loadingMachine.isNullOrBlank()){
            holder.itemView.lhr_loading_machine_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_machine.setText(":  "+myData.loadingMachine)
        }else{
            holder.itemView.lhr_loading_machine_layout.visibility = View.GONE
        }

        if(!myData.loadedMachine.isNullOrBlank()){
            holder.itemView.lhr_loaded_machine_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loaded_machine.setText(":  "+myData.loadedMachine)
        }else{
            holder.itemView.lhr_loaded_machine_layout.visibility = View.GONE
        }

        if(!myData.loadingMaterial.isNullOrBlank()){
            holder.itemView.lhr_loaded_material_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loaded_material.setText(":  "+myData.loadingMaterial)
        }else{
            holder.itemView.lhr_loaded_material_layout.visibility = View.GONE
        }

        if(!myData.loadingLocation.isNullOrBlank()){
            holder.itemView.lhr_loading_location_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_location.setText(":  "+myData.loadingLocation)
        }else{
            holder.itemView.lhr_loading_location_layout.visibility = View.GONE
        }

        if(myData.loadedWeight != 0.0){
            holder.itemView.lhr_loading_weight_layout.visibility = View.VISIBLE
            holder.itemView.lhr_loading_weight.setText(":  "+myData.loadedWeight)
        }else{
            holder.itemView.lhr_loading_weight_layout.visibility = View.GONE
        }

        if(!myData.date.isNullOrBlank()){
            holder.itemView.lhr_time_layout.visibility = View.VISIBLE
            holder.itemView.lhr_time.setText(": "+myData.date)
        }else{
            holder.itemView.lhr_time_layout.visibility = View.GONE
        }

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

