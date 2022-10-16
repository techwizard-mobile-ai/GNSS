package app.vsptracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import app.vsptracker.R
import app.vsptracker.classes.Material

class CustomGridLMachine(private val mContext: Context, private val arrayList: ArrayList<Material>) :
        BaseAdapter() {
  
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
    textView.visibility = View.VISIBLE
    textView.text = arrayList[position].number
    return grid
  }
}