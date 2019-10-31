package app.vsptracker.activities.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.adapters.CustomGridLMachine
import app.vsptracker.apis.trip.MyData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_lmachine1.*

class LMachine1Activity : BaseActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_lmachine1, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
        when (myData.nextAction) {
            0 -> {
                lm_title.text = "Select Loading Machine"
            }
            2 -> {
                lm_title.text = "Select Back Loading Machine"
            }
        }

        val gv = findViewById(R.id.tlm_gridview) as GridView
        val machines = db.getMachines(1)
//        myHelper.log("machines:$machines")

        val adapter = CustomGridLMachine(this@LMachine1Activity, machines)

        gv.setAdapter(adapter)
        gv.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            myHelper.toast("Selected Machine: " + machines.get(position).number)

            if (myData.isForLoadResult) {
                val intent = intent
                myData.loadingMachine = machines.get(position).number
                myData.loading_machine_id = machines.get(position).id
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (myData.isForBackLoadResult) {
                val intent = intent
                myData.backLoadingMachine = machines.get(position).number
                myData.back_loading_machine_id = machines.get(position).id
                intent.putExtra("myData", myData)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else {
                when (myData.nextAction) {
                    0 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        myData.loadingMachine = machines.get(position).number
                        myData.loading_machine_id = machines.get(position).id
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this, Material1Activity::class.java)
                        myData.backLoadingMachine = machines.get(position).number
                        myData.back_loading_machine_id = machines.get(position).id
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }
            }
        })

        lmachine_back.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        base_nav_view.setCheckedItem(base_nav_view.menu.getItem(0))
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.lmachine_back -> {
                finish()
            }
        }
    }
}
