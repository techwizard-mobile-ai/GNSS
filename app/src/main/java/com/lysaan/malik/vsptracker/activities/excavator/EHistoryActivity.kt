package com.lysaan.malik.vsptracker.activities.excavator


import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.view.View
import android.widget.FrameLayout
import com.lysaan.malik.vsptracker.BaseActivity
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.fragments.excavator.*
import com.lysaan.malik.vsptracker.fragments.excavator.ELoadingHistoryFragment.Companion.newInstance
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.fragments.common.DelayHistoryFragment
import kotlinx.android.synthetic.main.activity_ehistory.*

class EHistoryActivity : BaseActivity(), View.OnClickListener,
    ELoadingHistoryFragment.OnFragmentInteractionListener,
    ETrenchingHistoryFragment.OnFragmentInteractionListener,
    EDiggingHistoryFragment.OnFragmentInteractionListener,
    EOffloadingLoadsFragment.OnFragmentInteractionListener,
    DelayHistoryFragment.OnFragmentInteractionListener{

    override fun onFragmentInteraction(uri: Uri) {}

    private val E_LOADHISTORY: String = "E_LOADHISTORY"
    private val E_TRENCHINGHISTORY: String = "E_TRENCHINGHISTORY"
    private val E_DIGGINGHISTORY: String = "E_DIGGINGHISTORY"
    private val DELAY_HISTORY : String = "DELAY_HISTORY"
    private val TAG = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_ehistory, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(5).isChecked = true

        helper.setTag(TAG)

        var bundle :Bundle ?=intent.extras
        if(bundle != null){
            data = bundle!!.getSerializable("data") as Data
            helper.log("data:$data")
        }


        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val menuFragment = bundle?.getString("menuFragment")
        if(menuFragment !=null){
            helper.log("menuFragment:${menuFragment.toString()}")
            if (menuFragment.equals("eTrenchingHistoryFragment")) {
                val tFragment = ETrenchingHistoryFragment.newInstance(this, E_TRENCHINGHISTORY)
                openFragment(tFragment , E_TRENCHINGHISTORY)
            }else if (menuFragment.equals("delayHistoryFragment")){
                val delayFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
                openFragment(delayFragment, DELAY_HISTORY)
            }else{
                val homeFragment = EDiggingHistoryFragment.newInstance(this, E_DIGGINGHISTORY)
                openFragment(homeFragment,E_DIGGINGHISTORY)
            }
        }else{

            val homeFragment = newInstance(this)
            openFragment(homeFragment,E_LOADHISTORY)
        }

        val eLoadingHistoryFragment = newInstance(this)
        openFragment(eLoadingHistoryFragment,E_LOADHISTORY)
    }
    private fun openFragment(fragment: Fragment, FRAGMENT_TAG: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
        transaction.addToBackStack(FRAGMENT_TAG)
        transaction.commit()
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navf_loading -> {
                val eLoadingHistoryFragment = newInstance(this@EHistoryActivity)
                openFragment(eLoadingHistoryFragment,E_LOADHISTORY)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navf_digging -> {
                val eDiggingHistoryFragment = EDiggingHistoryFragment.newInstance(
                    this@EHistoryActivity,
                    E_DIGGINGHISTORY
                )
                openFragment(eDiggingHistoryFragment,E_DIGGINGHISTORY)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navf_trenching-> {
                val eTrenchingHistoryFragment = ETrenchingHistoryFragment.newInstance(this, E_TRENCHINGHISTORY)
                openFragment(eTrenchingHistoryFragment,E_TRENCHINGHISTORY)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navf_delay ->{
                val delayHistoryFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
                openFragment(delayHistoryFragment, DELAY_HISTORY)
                return@OnNavigationItemSelectedListener  true
            }
            R.id.navf_finish -> {finish()}
        }
        false
    }
    override fun onClick(view: View?) {
        when(view!!.id){
        }
    }
    fun setNavItem(currentFragmentTag: String?) {
        when(currentFragmentTag){
            E_LOADHISTORY -> navigationView.selectedItemId = R.id.navf_loading
            E_TRENCHINGHISTORY -> navigationView.selectedItemId = R.id.navf_trenching
            E_DIGGINGHISTORY -> navigationView.selectedItemId = R.id.navf_digging
            DELAY_HISTORY -> navigationView.selectedItemId = R.id.nav_delay }

    }
    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        helper.log("count--$count")
        if(count < 2 ){
            finish()
        }else{
            supportFragmentManager.popBackStack()
            val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(count - 2).name

            if(supportFragmentManager.getBackStackEntryAt(count -2).id != navigationView.selectedItemId){
                setNavItem(currentFragmentTag)
                supportFragmentManager.popBackStack()
            }else{
                supportFragmentManager.popBackStack()
            }
        }
    }

}
