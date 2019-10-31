package app.vsptracker.activities.notused

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import kotlinx.android.synthetic.main.activity_load_unload.*

class LoadUnloadActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById(R.id.base_content_frame) as FrameLayout
        layoutInflater.inflate(R.layout.activity_load_unload, contentFrameLayout)
        val navigationView = findViewById(R.id.base_nav_view) as NavigationView
        navigationView.menu.getItem(0).isChecked = true

        lu_load.setOnClickListener(this)
        lu_unload.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.lu_load -> {
            }
            R.id.lu_unload -> {
            }
        }
    }
}
