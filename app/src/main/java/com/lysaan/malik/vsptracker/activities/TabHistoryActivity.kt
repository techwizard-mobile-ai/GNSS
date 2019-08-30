package com.lysaan.malik.vsptracker.activities

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.fragments.common.DelayHistoryFragment
import com.lysaan.malik.vsptracker.fragments.truck.LoadingHistoryFragment
import kotlinx.android.synthetic.main.activity_tab_history.*

class TabHistoryActivity : BaseActivity(), View.OnClickListener,
    LoadingHistoryFragment.OnFragmentInteractionListener,
    DelayHistoryFragment.OnFragmentInteractionListener {


    override fun onFragmentInteraction(uri: Uri) {}

    private val LOADING_HISTORY: String = "LOADING_HISTORY"
    private val DELAY_HISTORY: String = "DELAY_HISTORY"
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_tab_history, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(5).isChecked = true

        helper.setTag(TAG)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }


        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            helper.log("menuFragment:${menuFragment.toString()}")
            if (menuFragment.equals("delayHistoryFragment")) {
                val delayFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
                openFragment(delayFragment, DELAY_HISTORY)
            } else {
                val homeFragment = LoadingHistoryFragment.newInstance(this)
                openFragment(homeFragment, LOADING_HISTORY)
            }
        } else {
            val homeFragment = LoadingHistoryFragment.newInstance(this)
            openFragment(homeFragment, LOADING_HISTORY)
        }

        val eLoadingHistoryFragment = LoadingHistoryFragment.newInstance(this)
        openFragment(eLoadingHistoryFragment, LOADING_HISTORY)
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
                        LoadingHistoryFragment.newInstance(this)
                    openFragment(loadingHistoryFragment, LOADING_HISTORY)
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
            DELAY_HISTORY -> navigationView.selectedItemId = R.id.nav_delay
        }
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        helper.log("count--$count")
        if (count < 2) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
            val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(count - 2).name

            if (supportFragmentManager.getBackStackEntryAt(count - 2).id != navigationView.selectedItemId) {
                setNavItem(currentFragmentTag)
                supportFragmentManager.popBackStack()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }
}