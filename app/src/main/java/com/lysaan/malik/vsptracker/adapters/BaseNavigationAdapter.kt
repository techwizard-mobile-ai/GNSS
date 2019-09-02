package com.lysaan.malik.vsptracker.adapters

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.list_row_base_navigation.view.*

class BaseNavigationAdapter(
        val context: Activity,
        private val myMaterialList: MutableList<Material>
) : RecyclerView.Adapter<BaseNavigationAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName

    lateinit var helper: Helper

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): BaseNavigationAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_base_navigation, parent, false)
        helper = Helper(TAG, context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: BaseNavigationAdapter.ViewHolder, position: Int) {

        val myData = myMaterialList.get(position)
        holder.itemView.base_navigation_text.setText(myData.name)

        if (position == 0) {
            holder.itemView.base_navigation_arrow.visibility = View.GONE
        } else {
            holder.itemView.base_navigation_arrow.visibility = View.VISIBLE
        }
        helper.log("myMaterialList.lastIndex:${myMaterialList.lastIndex}")
        holder.itemView.base_navigation_text.setOnClickListener {
            helper.toast("${myData.name}:$position")
        }
    }


    override fun getItemCount(): Int {
        return myMaterialList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

