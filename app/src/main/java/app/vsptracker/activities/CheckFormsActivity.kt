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
import app.vsptracker.others.MyEnum
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val DUE_CHECKFORMS: String = "DUE_CHECKFORMS"
private const val ALL_CHECKFORMS: String = "ALL_CHECKFORMS"
private const val COMPLETED_CHECKFORMS: String = "COMPLETED_CHECKFORMS"
private const val COMPLETED_SERVER_CHECKFORMS: String = "COMPLETED_SERVER_CHECKFORMS"

class CheckFormsActivity : BaseActivity(), View.OnClickListener,
                           CheckFormsFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {}
    private val tag = this::class.java.simpleName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_check_forms, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(9).isChecked = true
        
        myHelper.setTag(tag)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
        }
        
        val bottomNavigation: BottomNavigationView = findViewById(R.id.cf_navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        
        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            when (menuFragment) {
                "dueCheckFormsFragment" -> {
                    val dueCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_DUE, supportFragmentManager, MyData())
                    openFragment(dueCheckFormsFragment, DUE_CHECKFORMS)
                }
                "allCheckFormsFragment" -> {
                    val allCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_ALL, supportFragmentManager, MyData())
                    openFragment(allCheckFormsFragment, ALL_CHECKFORMS)
                }
                "completedServerCheckFormsFragment" -> {
                    val completedServerCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_COMPLETED_SERVER, supportFragmentManager, MyData())
                    openFragment(completedServerCheckFormsFragment, COMPLETED_SERVER_CHECKFORMS)
                }
                else -> {
                    val completedCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_COMPLETED, supportFragmentManager, MyData())
                    openFragment(completedCheckFormsFragment, COMPLETED_CHECKFORMS)
                }
            }
        } else {
            val dueCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_DUE, supportFragmentManager, MyData())
            openFragment(dueCheckFormsFragment, DUE_CHECKFORMS)
        }
        val dueCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_DUE, supportFragmentManager, MyData())
        openFragment(dueCheckFormsFragment, DUE_CHECKFORMS)
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
                    val dueCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_DUE, supportFragmentManager, MyData())
                    openFragment(dueCheckFormsFragment, DUE_CHECKFORMS)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cf_all -> {
                    val allCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_ALL, supportFragmentManager, MyData())
                    openFragment(allCheckFormsFragment, ALL_CHECKFORMS)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cf_completed -> {
                    val completedCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_COMPLETED, supportFragmentManager, MyData())
                    openFragment(completedCheckFormsFragment, COMPLETED_CHECKFORMS)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cf_completed_server -> {
                    
                    val completedServerCheckFormsFragment = CheckFormsFragment.newInstance(MyEnum.ADMIN_CHECKFORMS_COMPLETED_SERVER, supportFragmentManager, MyData())
                    openFragment(completedServerCheckFormsFragment, COMPLETED_SERVER_CHECKFORMS)
                    
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
