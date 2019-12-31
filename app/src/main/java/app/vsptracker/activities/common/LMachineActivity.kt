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
import kotlinx.android.synthetic.main.activity_lmachine.*

class LMachineActivity : BaseActivity(), View.OnClickListener {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_lmachine, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(0).isChecked = true

        myHelper.setTag(tag)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
        when (myData.nextAction) {
            0 -> {
                lm_title.text = getString(R.string.select_loading_machine)
            }
            2 -> {
                lm_title.text = getString(R.string.select_back_loading_machine)
            }
        }

        val gv = findViewById<GridView>(R.id.tlm_gridview)
        val machines = db.getMachines(1)
//        myHelper.log("machines:$machines")

        val adapter = CustomGridLMachine(this@LMachineActivity, machines)

        gv.adapter = adapter
        gv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            myHelper.toast("Selected Machine: " + machines[position].number)

            when {
                myData.isForLoadResult -> {
                    val intent = intent
                    myData.loading_machine_id = machines[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                myData.isForBackLoadResult -> {
                    val intent = intent
                    myData.back_loading_machine_id = machines[position].id
                    intent.putExtra("myData", myData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> when (myData.nextAction) {
                    0 -> {
                        val intent = Intent(this, MaterialActivity::class.java)
                        myData.loading_machine_id = machines[position].id
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this, MaterialActivity::class.java)
                        myData.back_loading_machine_id = machines[position].id
                        intent.putExtra("myData", myData)
                        startActivity(intent)
                    }
                }
            }
        }

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
