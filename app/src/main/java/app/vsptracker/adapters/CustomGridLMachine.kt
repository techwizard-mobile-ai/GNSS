package app.vsptracker.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.mvp.activities.MvpSurveySurveyActivity
import app.mvp.activities.MvpSurveysLabelsSettingsActivity
import app.vsptracker.R
import app.vsptracker.classes.Material
import app.vsptracker.others.MyHelper

class CustomGridLMachine(private val mContext: Context, private var arrayList: ArrayList<Material>, private val type: Int = 0) : BaseAdapter(), Filterable {
  
  private val tag = this::class.java.simpleName
  var mOriginalValues: ArrayList<Material> = arrayList
  var inflater: LayoutInflater? = null
  var myHelper = MyHelper(tag, mContext)
  
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
        0 -> inflater.inflate(R.layout.list_row_grid, null)
        1 -> inflater.inflate(R.layout.list_row_grid_survey, null)
        2 -> inflater.inflate(R.layout.list_row_grid_survey_labels, null)
        3 -> inflater.inflate(R.layout.list_row_grid_survey_labels_favorite, null)
        4 -> inflater.inflate(R.layout.list_row_grid_folders, null)
        else -> inflater.inflate(R.layout.list_row_grid, null)
      }
    } else {
      grid = (convertView as View?)!!
    }
    
    val textView = grid.findViewById(R.id.grid_text) as TextView
    textView.visibility = View.VISIBLE
    textView.text = arrayList[position].number
    
    when (type) {
      0 -> {}
      1 -> {
        val minus = grid.findViewById(R.id.grid_minus) as ImageView
        val plus = grid.findViewById(R.id.grid_image) as ImageView
        val grid_row = grid.findViewById(R.id.grid_row) as RelativeLayout
        minus.setColorFilter(Color.parseColor(arrayList[position].color_hex))
        plus.setColorFilter(Color.parseColor(arrayList[position].color_hex))
        minus.setOnClickListener { (mContext as MvpSurveySurveyActivity).minus(position) }
        plus.setOnClickListener { (mContext as MvpSurveySurveyActivity).plus(position) }
        if (arrayList[position].id == (mContext as MvpSurveySurveyActivity).selectedLabel.id) {
          grid_row.setBackgroundResource(R.drawable.bdue_border)
        } else {
          grid_row.setBackgroundResource(R.drawable.disabled_spinner_border)
        }
      }
      
      2 -> {
        val grid_make_favorite = grid.findViewById(R.id.grid_make_favorite) as ImageView
        grid_make_favorite.setColorFilter(Color.parseColor(arrayList[position].color_hex))
        val myData = arrayList[position]
        myData.is_favorite = 1
        grid_make_favorite.setOnClickListener { (mContext as MvpSurveysLabelsSettingsActivity).updateFavoriteUnfavorite(myData) }
      }
      
      3 -> {
        val grid_make_unfavorite = grid.findViewById(R.id.grid_make_unfavorite) as ImageView
        grid_make_unfavorite.setColorFilter(Color.parseColor(arrayList[position].color_hex))
        val myData = arrayList[position]
        myData.is_favorite = 0
        grid_make_unfavorite.setOnClickListener { (mContext as MvpSurveysLabelsSettingsActivity).updateFavoriteUnfavorite(myData) }
      }
      
      4 -> { // add created_at date to sites/folders
        val grid_text_created_at = grid.findViewById(R.id.grid_text_created_at) as TextView
        grid_text_created_at.visibility = View.VISIBLE
        grid_text_created_at.text = arrayList[position].created_at
      }
      
    }
    return grid
  }
  
  override fun getFilter(): Filter? {
    return object : Filter() {
      override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        arrayList = results.values as ArrayList<Material> // has the filtered values
        notifyDataSetChanged() // notifies the data with new filtered values
      }
      
      override fun performFiltering(constraint: CharSequence): FilterResults? {
        var constraint = constraint
        val results = FilterResults()
        
        if (constraint.isEmpty()) {
          results.values = mOriginalValues
        } else {
          results.values = mOriginalValues.filter { p -> p.number.contains(constraint.toString(), true) }
        }
        return results
      }
    }
  }
}