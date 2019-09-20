package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.login.LoginResponse
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper

    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(com.lysaan.malik.vsptracker.R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        myHelper = MyHelper(TAG, this)
        myHelper.setProgressBar(signin_pb)

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

//        myHelper.imageLoad(resources.getDrawable(R.drawable.welcome))

        when (myHelper.getMachineType()) {
            1 -> {
                Glide.with(this).load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.excavator))
                        .into(signin_image)
            }
            2 -> {
                Glide.with(this).load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.scraper)).into(signin_image)
            }
            3 -> {
                Glide.with(this).load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.truck)).into(signin_image)
            }
            else -> {
                Glide.with(this).load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.welcomenew))
                        .into(signin_image)
            }
        }

//        keyboard.showKeyBoard(signin_email)



//        val intent = Intent(this, HourMeterStartActivity::class.java)
//        startActivity(intent)


        myHelper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        signin_forgot_pass.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            com.lysaan.malik.vsptracker.R.id.signin_signin -> {

                var email = signin_email.text.toString()
                var pass = signin_pass.text.toString()

                myHelper.log("email:" + email)
                myHelper.log("Pass" + pass)
//                email = "zee.enterprises@mail.com"
//                pass = "user1@123"
//                signIn(email, pass)

                if (!myHelper.isValidEmail(email)) {
                    myHelper.toast("Please Provide valid Email Address.")
                }
                else if (pass.length < 6) {
                    myHelper.toast("Password Minimum Length should be 6 Characters")
                }
                else {
                    signIn(email, pass)
                }
            }
        }
    }

    private fun signIn(email: String, pass: String) {

//        operatorLogin()
        orgLogin(email, pass)

//        if(myHelper.getIsMachineStopped()){
//            val intent = Intent(this, MachineStatus1Activity::class.java)
//            startActivity(intent)
//        }else{
//            val intent = Intent(this, HourMeterStartActivity::class.java)
//            startActivity(intent)
//        }

//        myHelper.hideProgressBar()
    }


    fun operatorLogin(){
        val call = this.retrofitAPI.getOperatorLogin(myHelper.getLoginAPI().org_id, "RJ12314", myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<LoginResponse>{
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                val loginResponse = response.body()
                myHelper.log("Operator:$loginResponse")
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                myHelper.log("Failure"+t.message)
            }
        }
        )
    }
    fun orgLogin(email: String, pass: String){
        myHelper.showProgressBar()

        val call = this.retrofitAPI.getLogin(email, pass)
        call.enqueue(object : retrofit2.Callback<LoginResponse> {

            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                myHelper.hideProgressBar()
                myHelper.log("RetrofitResponse:$response")
                val loginResponse = response.body()
                if(loginResponse!!.success){
                    myHelper.log("SendReponse:${loginResponse.data}.")
                    myHelper.log("Start OperatorLoginActivity")
                    loginResponse.data.pass = pass
                    myHelper.setLoginAPI(loginResponse.data)
                    myHelper.toast("Organizaion Login Successful.")
                    val intent = Intent(this@LoginActivity, OperatorLoginActivity::class.java)
                    startActivity(intent)
                }else{
                    myHelper.toast(loginResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
    fun fetchJSON() {
        myHelper.log("fetchJson")

        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("email", "zee.enterprises@mail.com")
            .add("password", "user1@123")
            .build()
        val request = Request.Builder()
            .url("https://vsptracker.app/api/v1/org/users/login")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                myHelper.hideProgressBar()
                val jsonData = response.body()!!.string()
                val Jobject = JSONObject(jsonData)
                myHelper.log("body:$Jobject")
            }

            override fun onFailure(call: Call, e: IOException) {
                myHelper.hideProgressBar()
                Log.d("OKHttp", "Failed to execute request ${e.printStackTrace()}")
            }
        })
    }
}
