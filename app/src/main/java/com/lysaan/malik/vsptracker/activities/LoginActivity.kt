package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        myHelper = MyHelper(TAG, this)
        myHelper.setProgressBar(signin_pb)


//        myHelper.imageLoad(resources.getDrawable(R.drawable.welcome))


        when(myHelper.getMachineType()){
            1 -> {Glide.with(this).load(resources.getDrawable(R.drawable.excavator)).into(signin_image)}
            2 -> {Glide.with(this).load(resources.getDrawable(R.drawable.scraper)).into(signin_image)}
            3 -> {Glide.with(this).load(resources.getDrawable(R.drawable.truck)).into(signin_image)}
            else -> {Glide.with(this).load(resources.getDrawable(R.drawable.welcomenew)).into(signin_image)}
        }

//        keyboard.showKeyBoard(signin_email)

        if(myHelper.getMachineNumber().isNullOrBlank()){
            myHelper.log("No machine Number is entered.")
            val intent = Intent(this, MachineTypeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val intent = Intent(this, HourMeterStartActivity::class.java)
        startActivity(intent)


        myHelper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        signin_forgot_pass.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.signin_signin ->{

                var email = signin_email.text.toString()
                var pass = signin_pass.text.toString()

                myHelper.log("email:" + email)
                myHelper.log("Pass" + pass)

                signIn(email, pass)

//                if (!myHelper.isValidEmail(email)) {
//                    myHelper.toast("Please Provide valid Email Address.")
//                } else if (pass.length < 6) {
//                    myHelper.toast("Password Minimum Length should be 6 Characters")
//                } else {
//                    signIn(email, pass)
//                }
            }
        }
    }

    private fun signIn(email :String, pass:String){

        myHelper.showProgressBar()

        val intent = Intent(this, HourMeterStartActivity::class.java)
        startActivity(intent)
//        myHelper.startHomeActivityByType()
    }
}
