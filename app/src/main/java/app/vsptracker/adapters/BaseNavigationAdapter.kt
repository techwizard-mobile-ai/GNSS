package app.vsptracker.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.MyHelper
import app.vsptracker.R
import app.vsptracker.classes.Material
import kotlinx.android.synthetic.main.list_row_base_navigation.view.*

class BaseNavigationAdapter(
        val context: Activity,
        private val myMaterialList: MutableList<Material>
) : RecyclerView.Adapter<BaseNavigationAdapter
.ViewHolder>() {

    private val TAG = this::class.java.simpleName

    lateinit var myHelper: MyHelper

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): BaseNavigationAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_base_navigation, parent, false)
        myHelper = MyHelper(TAG, context)
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
        myHelper.log("myMaterialList.lastIndex:${myMaterialList.lastIndex}")
        holder.itemView.base_navigation_text.setOnClickListener {
            myHelper.toast("${myData.name}:$position")
        }
    }


    override fun getItemCount(): Int {
        return myMaterialList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

