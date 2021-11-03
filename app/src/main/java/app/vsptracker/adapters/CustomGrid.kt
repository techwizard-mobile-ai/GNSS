package app.vsptracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.vsptracker.R
import app.vsptracker.classes.Material
import app.vsptracker.others.MyHelper

class CustomGrid(
    private val mContext: Context,
    private val arrayList: ArrayList<Material> = ArrayList(),
    private val isTaskLayout: Boolean = false
    ) :
        BaseAdapter() {
    private val tag = "CustomGrid"
    private val myHelper = MyHelper(tag, mContext)

    override fun getCount(): Int {
        return arrayList.size
    }
    
    override fun getItem(position: Int): Any? {
        return arrayList[position]
    }
    
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val grid: View
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        
        if (convertView == null) {
            grid = inflater.inflate(R.layout.list_row_grid, null)
        } else {
            grid = (convertView as View?)!!
        }
        val textView = grid.findViewById(R.id.grid_text) as TextView
        val imageView = grid.findViewById(R.id.grid_image) as ImageView
        
        when (isTaskLayout) {
            false -> {
                textView.text = arrayList[position].name
                textView.visibility = View.VISIBLE
            }
            else -> {
                imageView.visibility = View.VISIBLE
                when(arrayList[position].machineTaskId){
                    1 -> {
                        myHelper.imageLoad(R.drawable.task_stockpile, imageView)
                    }
                    2 -> {
                        myHelper.imageLoad(R.drawable.task_fill, imageView)
                    }
                    3 -> {
                        myHelper.imageLoad(R.drawable.task_respread, imageView)
                    }
                    4 -> {
                        myHelper.imageLoad(R.drawable.task_off_site, imageView)
                    }
                    5 -> {
                        myHelper.imageLoad(R.drawable.task_crushing_plant, imageView)
                    }
                    else -> {
                        textView.visibility = View.VISIBLE
                        textView.text = arrayList[position].name
                    }
                }
            }
        }
        
        return grid
    }
}