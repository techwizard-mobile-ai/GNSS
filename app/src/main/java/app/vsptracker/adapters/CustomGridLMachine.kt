package app.vsptracker.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.mvp.activities.MvpSurveySurveyActivity
import app.vsptracker.R
import app.vsptracker.classes.Material

class CustomGridLMachine(private val mContext: Context, private val arrayList: ArrayList<Material>, private val type: Int = 0) :
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
    val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    
    if (convertView == null) {
      grid = when (type) {
        1 -> inflater.inflate(R.layout.list_row_grid_survey, null)
        else -> inflater.inflate(R.layout.list_row_grid, null)
      }
    } else {
      grid = (convertView as View?)!!
    }
    
    if (type == 1) {
      val minus = grid.findViewById(R.id.grid_minus) as ImageView
      val plus = grid.findViewById(R.id.grid_image) as ImageView
      minus.setColorFilter(Color.parseColor(arrayList[position].color_hex))
      plus.setColorFilter(Color.parseColor(arrayList[position].color_hex))
      minus.setOnClickListener { (mContext as MvpSurveySurveyActivity).minus(position) }
      plus.setOnClickListener { (mContext as MvpSurveySurveyActivity).plus(position) }
    }
    
    val textView = grid.findViewById(R.id.grid_text) as TextView
    textView.visibility = View.VISIBLE
    textView.text = arrayList[position].number
    return grid
  }
}