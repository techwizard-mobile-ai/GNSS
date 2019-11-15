package app.vsptracker.activities.scrapper

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import app.vsptracker.BaseActivity
import app.vsptracker.R
import app.vsptracker.apis.trip.MyData
import app.vsptracker.fragments.common.DelayHistoryFragment
import app.vsptracker.fragments.excavator.EDiggingHistoryFragment
import app.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import app.vsptracker.fragments.truck.LoadingHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val LOADING_HISTORY: String = "LOADING_HISTORY"
private const val E_DIGGINGHISTORY: String = "E_DIGGINGHISTORY"
private const val DELAY_HISTORY: String = "DELAY_HISTORY"

class SHistoryActivity : BaseActivity(), View.OnClickListener,
    LoadingHistoryFragment.OnFragmentInteractionListener,
    EDiggingHistoryFragment.OnFragmentInteractionListener,
    EOffloadingLoadsFragment.OnFragmentInteractionListener,
    DelayHistoryFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {}
    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_shistory, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(6).isChecked = true

        myHelper.setTag(tag)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            myData = bundle.getSerializable("myData") as MyData
            myHelper.log("myData:$myData")
        }


        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val menuFragment = bundle?.getString("menuFragment")
        if (menuFragment != null) {
            myHelper.log("menuFragment:${menuFragment}")
            when (menuFragment) {
                "loadingHistoryFragment" -> {
                    val tFragment = LoadingHistoryFragment.newInstance(this)
                    openFragment(tFragment, LOADING_HISTORY)
                }
                "delayHistoryFragment" -> {
                    val delayFragment = DelayHistoryFragment.newInstance(this, DELAY_HISTORY)
                    openFragment(delayFragment, DELAY_HISTORY)
                }
                else -> {
                    val homeFragment = EDiggingHistoryFragment.newInstance(this, E_DIGGINGHISTORY, 3)
                    openFragment(homeFragment, E_DIGGINGHISTORY)
                }
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
    }


}