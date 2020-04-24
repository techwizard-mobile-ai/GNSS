package app.vsptracker.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.MyHelper
import kotlinx.android.synthetic.main.list_row_check_form_task.view.*

class CheckFormTaskAdapter(
    val context: Activity,
    private val dataList: ArrayList<MyData>
) : RecyclerView.Adapter<CheckFormTaskAdapter
.ViewHolder>() {

    private val tag = this::class.java.simpleName
    lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_check_form_task, parent, false)
        myHelper = MyHelper(tag, context)
        db = DatabaseAdapter(context)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val datum = dataList[position]
        
     
        holder.itemView.cft_question.text = "${datum.name}"
        
        holder.itemView.cft_item_acceptable.setOnClickListener {
            myHelper.log(datum.toString())
            holder.itemView.cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_background)
            holder.itemView.cft_item_acceptable.setTextColor(context.resources.getColor(R.color.white))
            datum.acceptableChecked = true
            
            holder.itemView.cft_comment.visibility = View.GONE
            holder.itemView.cft_photo_layout.visibility = View.GONE
            myHelper.hideKeyboard(holder.itemView.cft_item_acceptable)
            
            when(datum.unacceptableChecked){
                true -> {
                    holder.itemView.cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_border)
                    holder.itemView.cft_item_unacceptable.setTextColor(context.resources.getColor(R.color.light_red))
                    datum.unacceptableChecked = false
                }
            }
            
        }
        
        holder.itemView.cft_item_unacceptable.setOnClickListener {
            myHelper.log(datum.toString())
            holder.itemView.cft_item_unacceptable.background = context.getDrawable(R.drawable.bdue_background)
            holder.itemView.cft_item_unacceptable.setTextColor(context.resources.getColor(R.color.white))
            datum.unacceptableChecked = true
    
            myHelper.hideKeyboard(holder.itemView.cft_item_acceptable)
            
            when(datum.admin_questions_types_id){
                2 ->{
                    //show comments section
                    holder.itemView.cft_comment.visibility = View.VISIBLE
                    myHelper.showKeyboard(holder.itemView.cft_comment)
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                }
                3 ->{
                    //show photo section
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                }
                4 ->{
                    //show comments section
                    holder.itemView.cft_comment.visibility = View.VISIBLE
                    myHelper.showKeyboard(holder.itemView.cft_comment)
                    holder.itemView.cft_photo_layout.visibility = View.VISIBLE
                }
            }
            
            when(datum.acceptableChecked){
                true -> {
                    holder.itemView.cft_item_acceptable.background = context.getDrawable(R.drawable.bnext_border)
                    holder.itemView.cft_item_acceptable.setTextColor(context.resources.getColor(R.color.light_colorPrimary))
                    datum.acceptableChecked = true
                }
            }
            
        }
    
        holder.itemView.cft_item_capture.setOnClickListener {
            myHelper.log("capture photo")
        }
        holder.itemView.cft_item_attachment.setOnClickListener {
            myHelper.log("photo attachment")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}

