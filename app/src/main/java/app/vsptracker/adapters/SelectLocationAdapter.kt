package app.vsptracker.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import app.vsptracker.R
import app.vsptracker.classes.Material
import kotlinx.android.synthetic.main.list_row_select_location.view.*

class SelectLocationAdapter(context: Activity, internal var locationList: List<Material>) :
        BaseAdapter() {

    internal var context: Context
    internal var flater: LayoutInflater

    init {
        this.context = context
        flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return locationList.size!!
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

        val row = flater.inflate(R.layout.list_row_select_location, null)
        row.list_row_select_location_name.setText(locationList[i].name)
        return row

    }
}

