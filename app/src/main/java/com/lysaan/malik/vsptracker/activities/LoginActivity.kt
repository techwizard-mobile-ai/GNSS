package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var helper: Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        helper = Helper(TAG, this)
        helper.setProgressBar(signin_pb)


//        helper.imageLoad(resources.getDrawable(R.drawable.welcome))


        when (helper.getMachineType()) {
            1 -> {
                Glide.with(this).load(resources.getDrawable(R.drawable.excavator))
                        .into(signin_image)
            }
            2 -> {
                Glide.with(this).load(resources.getDrawable(R.drawable.scraper)).into(signin_image)
            }
            3 -> {
                Glide.with(this).load(resources.getDrawable(R.drawable.truck)).into(signin_image)
            }
            else -> {
                Glide.with(this).load(resources.getDrawable(R.drawable.welcomenew))
                        .into(signin_image)
            }
        }

//        keyboard.showKeyBoard(signin_email)

        if (helper.getMachineNumber().isNullOrBlank()) {
            helper.log("No machine Number is entered.")
            val intent = Intent(this, MachineTypeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

//        val intent = Intent(this, HourMeterStartActivity::class.java)
//        startActivity(intent)


        helper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        signin_forgot_pass.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {

                var email = signin_email.text.toString()
                var pass = signin_pass.text.toString()

                helper.log("email:" + email)
                helper.log("Pass" + pass)

                signIn(email, pass)

//                if (!helper.isValidEmail(email)) {
//                    helper.toast("Please Provide valid Email Address.")
//                } else if (pass.length < 6) {
//                    helper.toast("Password Minimum Length should be 6 Characters")
//                } else {
//                    signIn(email, pass)
//                }
            }
        }
    }

    private fun signIn(email: String, pass: String) {

        helper.showProgressBar()

        val intent = Intent(this, HourMeterStartActivity::class.java)
        startActivity(intent)
        helper.hideProgressBar()
//        helper.startHomeActivityByType()
    }
}
