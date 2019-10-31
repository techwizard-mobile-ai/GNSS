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


//class CustomGrid(private val mContext: Context, private val web: Array<String>, private val Imageid: IntArray) :
class CustomGridLMachine(private val mContext: Context, private val arrayList: ArrayList<Material>) :
        BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var grid: View
        val inflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null) {

            grid = View(mContext)
            grid = inflater.inflate(R.layout.list_row_grid, null)
            val textView = grid.findViewById(R.id.grid_text) as TextView
            val imageView = grid.findViewById(R.id.grid_image) as ImageView
//            textView.text = web[position]
//            if(arrayList.get(position).id == 0){
//                arrayList.removeAt(0)
//            }
            textView.text = arrayList.get(position).number

//            imageView.setLayoutParams(AbsListView.LayoutParams(350, 350))
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
//            imageView.setPadding(8, 8, 8, 8)

//            imageView.setImageResource(Imageid[position])
        } else {
            grid = (convertView as View?)!!
        }

        return grid
    }
}