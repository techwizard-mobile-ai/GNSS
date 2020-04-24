package app.vsptracker.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.fragments.common.CheckFormsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val DUE_CHECKFORMS: String = "DUE_CHECKFORMS"
private const val ALL_CHECKFORMS: String = "ALL_CHECKFORMS"
private const val COMPLETED_CHECKFORMS: String = "COMPLETED_CHECKFORMS"

class CheckFormsActivity : BaseActivity(), View.OnClickListener,
                           CheckFormsFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {}
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_check_forms, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(10).isChecked = true
        
        myHelper.setTag(tag)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }
        
        val bottomNavigation: BottomNavigationView = findViewById(R.id.cf_navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        
        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            when (menuFragment) {
                "dueCheckFormsFragment" -> {
                    val machineStopFragment = CheckFormsFragment.newInstance(0)
                    openFragment(machineStopFragment, DUE_CHECKFORMS)
                }
                "allCheckFormsFragment" -> {
                    val operatorHourFragment = CheckFormsFragment.newInstance(1)
                    openFragment(operatorHourFragment, ALL_CHECKFORMS)
                }
                else -> {
                    val machineHourFragment = CheckFormsFragment.newInstance(2)
                    openFragment(machineHourFragment, COMPLETED_CHECKFORMS)
                }
            }
        } else {
            val machineHourFragment = CheckFormsFragment.newInstance(0)
            openFragment(machineHourFragment, DUE_CHECKFORMS)
        }
        val machineHourFragment = CheckFormsFragment.newInstance(0)
        openFragment(machineHourFragment, DUE_CHECKFORMS)
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
                R.id.cf_due -> {
                    val dueCheckFormsFragment = CheckFormsFragment.newInstance(0)
                    openFragment(dueCheckFormsFragment, DUE_CHECKFORMS)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cf_all -> {
                    val allCheckFormsFragment = CheckFormsFragment.newInstance(1)
                    openFragment(allCheckFormsFragment, ALL_CHECKFORMS)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cf_completed -> {
                    val completedCheckFormsFragment = CheckFormsFragment.newInstance(2)
                    openFragment(completedCheckFormsFragment, COMPLETED_CHECKFORMS)
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
