package com.lysaan.malik.vsptracker.adapters
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Material
import kotlinx.android.synthetic.main.list_row_select_material.view.*

class SelectStateAdapter(context: Activity, internal var materialList: List<Material>) :
    BaseAdapter() {

    internal var context: Context
    internal var flater: LayoutInflater

    init {
        this.context = context
        flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return materialList.size!!
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

        val row = flater.inflate(R.layout.list_row_select_material, null)
        row.list_row_select_material_name.setText(materialList[i].name)
        return row

    }
}

