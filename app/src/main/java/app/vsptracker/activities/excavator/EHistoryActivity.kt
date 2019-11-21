package app.vsptracker.activities.excavator
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
import app.vsptracker.fragments.excavator.ELoadingHistoryFragment
import app.vsptracker.fragments.excavator.EOffloadingLoadsFragment
import app.vsptracker.fragments.excavator.ETrenchingHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private const val E_LOADHISTORY: String = "E_LOADHISTORY"
private const val E_TRENCHINGHISTORY: String = "E_TRENCHINGHISTORY"
private const val E_DIGGINGHISTORY: String = "E_DIGGINGHISTORY"
private const val DELAY_HISTORY: String = "DELAY_HISTORY"

class EHistoryActivity : BaseActivity(), View.OnClickListener,
        ELoadingHistoryFragment.OnFragmentInteractionListener,
        ETrenchingHistoryFragment.OnFragmentInteractionListener,
        EDiggingHistoryFragment.OnFragmentInteractionListener,
        EOffloadingLoadsFragment.OnFragmentInteractionListener,
        DelayHistoryFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {}


    private val tag = this::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_ehistory, contentFrameLayout)
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
                "eTrenchingHistoryFragment" -> {
                    val tFragment = ETrenchingHistoryFragment.newInstance(E_TRENCHINGHISTORY)
                    openFragment(tFragment, E_TRENCHINGHISTORY)
                }
                "delayHistoryFragment" -> {
                    val delayFragment = DelayHistoryFragment.newInstance(DELAY_HISTORY)
                    openFragment(delayFragment, DELAY_HISTORY)
                }
                else -> {
                    val homeFragment = EDiggingHistoryFragment.newInstance(E_DIGGINGHISTORY, 1)
                    openFragment(homeFragment, E_DIGGINGHISTORY)
                }
            }
        } else {

            val homeFragment = ELoadingHistoryFragment.newInstance()
            openFragment(homeFragment, E_LOADHISTORY)
        }

        val eLoadingHistoryFragment = ELoadingHistoryFragment.newInstance()
        openFragment(eLoadingHistoryFragment, E_LOADHISTORY)
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
                        val eLoadingHistoryFragment = ELoadingHistoryFragment.newInstance()
                        openFragment(eLoadingHistoryFragment, E_LOADHISTORY)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navf_digging -> {
                        val eDiggingHistoryFragment = EDiggingHistoryFragment.newInstance(
                            E_DIGGINGHISTORY,
                            1
                        )
                        openFragment(eDiggingHistoryFragment, E_DIGGINGHISTORY)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navf_trenching -> {
                        val eTrenchingHistoryFragment =
                                ETrenchingHistoryFragment.newInstance(E_TRENCHINGHISTORY)
                        openFragment(eTrenchingHistoryFragment, E_TRENCHINGHISTORY)
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
