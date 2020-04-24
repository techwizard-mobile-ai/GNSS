package app.vsptracker.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.fragments.common.MachineHourFragment
import app.vsptracker.fragments.common.MachineStopFragment
import app.vsptracker.fragments.common.OperatorHourFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val LOADING_HISTORY: String = "LOADING_HISTORY"
private const val E_DIGGINGHISTORY: String = "E_DIGGINGHISTORY"
private const val DELAY_HISTORY: String = "DELAY_HISTORY"
private const val MACHINE_HOUR: String = "MACHINE_HOUR"

class HoursReportingActivity : BaseActivity(), View.OnClickListener,
    MachineHourFragment.OnFragmentInteractionListener,
    MachineStopFragment.OnFragmentInteractionListener,
    OperatorHourFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {}
    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_hours_reporting, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(8).isChecked = true
        myHelper.setTag(tag)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.ss_navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            when (menuFragment) {
                "machineStopFragment" -> {
                    val machineStopFragment = MachineStopFragment.newInstance(MACHINE_HOUR)
                    openFragment(machineStopFragment, LOADING_HISTORY)
                }
                "operatorHourFragment" -> {
                    val operatorHourFragment = OperatorHourFragment.newInstance(MACHINE_HOUR)
                    openFragment(operatorHourFragment, LOADING_HISTORY)
                }
                else -> {
                    val machineHourFragment = MachineHourFragment.newInstance(MACHINE_HOUR)
                    openFragment(machineHourFragment, E_DIGGINGHISTORY)
                }
            }
        } else {
            val machineHourFragment = MachineHourFragment.newInstance(MACHINE_HOUR)
            openFragment(machineHourFragment, LOADING_HISTORY)
        }
        val machineHourFragment = MachineHourFragment.newInstance(MACHINE_HOUR)
        openFragment(machineHourFragment, LOADING_HISTORY)
    }

    private fun openFragment(fragment: Fragment, FRAGMENT_TAG: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
        transaction.addToBackStack(FRAGMENT_TAG)
        transaction.commit()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ss_machine_hour -> {
                    val machineHourFragment = MachineHourFragment.newInstance(MACHINE_HOUR)
                    openFragment(machineHourFragment, LOADING_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.ss_machine_stop -> {
                    val machineStopFragment = MachineStopFragment.newInstance(MACHINE_HOUR)
                    openFragment(machineStopFragment, LOADING_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.ss_operator_hour -> {
                    val operatorHourFragment = OperatorHourFragment.newInstance(MACHINE_HOUR)
                    openFragment(operatorHourFragment, LOADING_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navf_finish -> {
                    finish()
                }
            }
            false
        }

    override fun onClick(view: View?) {
    }
}