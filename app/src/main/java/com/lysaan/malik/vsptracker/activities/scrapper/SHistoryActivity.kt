package com.lysaan.malik.vsptracker.activities.scrapper

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.fragments.common.DelayHistoryFragment
import com.lysaan.malik.vsptracker.fragments.excavator.EDiggingHistoryFragment
import com.lysaan.malik.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import com.lysaan.malik.vsptracker.fragments.truck.LoadingHistoryFragment
import kotlinx.android.synthetic.main.activity_shistory.*

class SHistoryActivity : BaseActivity(), View.OnClickListener,
    LoadingHistoryFragment.OnFragmentInteractionListener,
    EDiggingHistoryFragment.OnFragmentInteractionListener,
    EOffloadingLoadsFragment.OnFragmentInteractionListener,
    DelayHistoryFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {}

    private val LOADING_HISTORY: String = "LOADING_HISTORY"
    private val E_TRENCHINGHISTORY: String = "E_TRENCHINGHISTORY"
    private val E_DIGGINGHISTORY: String = "E_DIGGINGHISTORY"
    private val DELAY_HISTORY: String = "DELAY_HISTORY"
    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_shistory, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(5).isChecked = true

        myHelper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle!!.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }


        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            myHelper.log("menuFragment:${menuFragment.toString()}")
            if (menuFragment.equals("loadingHistoryFragment")) {
                val tFragment = LoadingHistoryFragment.newInstance(this)
                openFragment(tFragment, LOADING_HISTORY)
            } else if (menuFragment.equals("delayHistoryFragment")) {
                val delayFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
                openFragment(delayFragment, DELAY_HISTORY)
            } else {
                val homeFragment = EDiggingHistoryFragment.newInstance(this, E_DIGGINGHISTORY, 3)
                openFragment(homeFragment, E_DIGGINGHISTORY)
            }
        } else {

            val homeFragment = LoadingHistoryFragment.newInstance(this)
            openFragment(homeFragment, LOADING_HISTORY)
        }

        val loadingHistoryFragment = LoadingHistoryFragment.newInstance(this)
        openFragment(loadingHistoryFragment, LOADING_HISTORY)
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
                    val loadingHistoryFragment = LoadingHistoryFragment.newInstance(this)
                    openFragment(loadingHistoryFragment, LOADING_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navf_digging -> {
                    val eDiggingHistoryFragment = EDiggingHistoryFragment.newInstance(
                        this,
                        E_DIGGINGHISTORY,
                        3
                    )
                    openFragment(eDiggingHistoryFragment, E_DIGGINGHISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navf_delay -> {
                    val delayHistoryFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
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
        when (view!!.id) {
        }
    }

    fun setNavItem(currentFragmentTag: String?) {
        when (currentFragmentTag) {
            LOADING_HISTORY -> navigationView.selectedItemId = R.id.navf_loading
            E_DIGGINGHISTORY -> navigationView.selectedItemId = R.id.navf_digging
            DELAY_HISTORY -> navigationView.selectedItemId = R.id.nav_delay
        }

    }

    override fun onBackPressed() {

        super.onBackPressed()
//        val count = supportFragmentManager.backStackEntryCount
//        myHelper.log("count--$count")
//        if (count < 2) {
//            finish()
//        } else {
//            supportFragmentManager.popBackStack()
//            val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(count - 2).name
//
//            if (supportFragmentManager.getBackStackEntryAt(count - 2).id != navigationView.selectedItemId) {
//                setNavItem(currentFragmentTag)
//                supportFragmentManager.popBackStack()
//            } else {
//                supportFragmentManager.popBackStack()
//            }
//        }
    }

}