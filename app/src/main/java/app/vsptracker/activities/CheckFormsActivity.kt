package app.vsptracker.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import app.vsptracker.BaseActivity
import app.vsptracker.R
import com.google.android.material.navigation.NavigationView

class CheckFormsActivity : BaseActivity(), View.OnClickListener {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val contentFrameLayout = findViewById<FrameLayout>(R.id.base_content_frame)
        layoutInflater.inflate(R.layout.activity_check_forms, contentFrameLayout)
        val navigationView = findViewById<NavigationView>(R.id.base_nav_view)
        navigationView.menu.getItem(10).isChecked = true
    }
    override fun onClick(view: View?) {
        when (view!!.id) {
        }
    }
}
