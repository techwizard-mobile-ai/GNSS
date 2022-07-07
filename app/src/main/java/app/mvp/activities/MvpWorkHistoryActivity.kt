package app.mvp.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.fragments.common.DelayHistoryFragment
import app.vsptracker.fragments.truck.LoadingHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val LOADING_HISTORY: String = "LOADING_HISTORY"
private const val DELAY_HISTORY: String = "DELAY_HISTORY"

class MvpWorkHistoryActivity : BaseActivity(), View.OnClickListener,
                         LoadingHistoryFragment.OnFragmentInteractionListener,
                         DelayHistoryFragment.OnFragmentInteractionListener {
    
    override fun onFragmentInteraction(uri: Uri) {}
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_mvp_work_history, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(7).isChecked = true
        
        myHelper.setTag(tag)
        
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
        }
        
        
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        
        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            if (menuFragment == "delayHistoryFragment") {
                val delayFragment = DelayHistoryFragment.newInstance(DELAY_HISTORY)
                openFragment(delayFragment, DELAY_HISTORY)
            } else {
                val homeFragment = LoadingHistoryFragment.newInstance()
                openFragment(homeFragment, LOADING_HISTORY)
            }
        } else {
            val homeFragment = LoadingHistoryFragment.newInstance()
            openFragment(homeFragment, LOADING_HISTORY)
        }
        
        val eLoadingHistoryFragment = LoadingHistoryFragment.newInstance()
        openFragment(eLoadingHistoryFragment, LOADING_HISTORY)
        
        myHelper.log("onCreate:${this::class.java.simpleName}")
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
                R.id.navf_loading -> {
                    val loadingHistoryFragment =
                        LoadingHistoryFragment.newInstance()
                    openFragment(loadingHistoryFragment, LOADING_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                
                R.id.navf_delay -> {
                    val delayHistoryFragment = DelayHistoryFragment.newInstance(DELAY_HISTORY)
                    openFragment(delayHistoryFragment, DELAY_HISTORY)
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
