package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.login.ListResponse
import com.lysaan.malik.vsptracker.apis.login.LoginResponse
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.database.DatabaseAdapter
import com.lysaan.malik.vsptracker.others.Utils
import kotlinx.android.synthetic.main.activity_operator_login.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OperatorLoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    protected lateinit var db: DatabaseAdapter

    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(com.lysaan.malik.vsptracker.R.layout.activity_operator_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        myHelper = MyHelper(TAG, this)
        myHelper.setProgressBar(signin_pb)
        db = DatabaseAdapter(this)

        this.retrofit = Retrofit.Builder()
            .baseUrl(RetrofitAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        when (myHelper.getMachineType()) {
            1 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.excavator))
                    .into(signin_image)
            }
            2 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.scraper))
                    .into(signin_image)
            }
            3 -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.truck))
                    .into(signin_image)
            }
            else -> {
                Glide.with(this)
                    .load(resources.getDrawable(com.lysaan.malik.vsptracker.R.drawable.welcomenew))
                    .into(signin_image)
            }
        }

        myHelper.refreshToken()

//        if (myHelper.getMachineNumber().isNullOrBlank()) {
//            myHelper.log("No machine Number is entered.")
//            val intent = Intent(this, MachineTypeActivity::class.java)
//            startActivity(intent)
//            finishAffinity()
//        }

        if(myHelper.getOperatorAPI().id > 0){
            syncData()
        }


        myHelper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {
                var pin = signin_pin.text.toString()
                myHelper.log("pin:" + pin)
                if (pin.length < 3) {
                    myHelper.toast("PIN Minimum Length should be 3 Characters")
                } else {
                    operatorLogin(pin)
                }
            }
        }
    }

    fun operatorLogin(pin: String) {
        myHelper.showProgressBar()
        val call = this.retrofitAPI.getOperatorLogin(
            myHelper.getLoginAPI().org_id,
            pin,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                val loginResponse = response.body()
                if (loginResponse!!.success && loginResponse.data != null) {
                    myHelper.toast("Operator Login Successful.")
                    myHelper.setOperatorAPI(loginResponse.data)
                    syncData()
                } else {
                    if (loginResponse.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$loginResponse")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(loginResponse.message)
                        myHelper.log("loginMessage:$loginResponse")
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

    fun syncData() {


        // Sync Following with Company
        // GET Calls
//        1. Locations URL: v1/orgslocations/list
        getLocations()
//        2. Machines URL: v1/orgsmachines/list
        getMachines()
//        3. Materials URL: v1/orgsmaterials/list
        getMaterials()
//        4. Machine Stop Reasons URL: v1/orgsmachinesstopsreasons/list
        getStopReasons()
        // POST Calls
//        1. Trips URL: v1/orgstrips/store
//        2. Waitings URL: v1/orgsdelays/store
//        3. Machine Stops URL: v1/orgsmachinesstops/store
//        getLocations()

        if (myHelper.getMachineID() < 1) {
            myHelper.log("No machine Number is entered.")
            val intent = Intent(this@OperatorLoginActivity, MachineTypeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }else if(myHelper.getIsMachineStopped()){
            val intent = Intent(this@OperatorLoginActivity, MachineStatus1Activity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            myHelper.startHomeActivityByType(MyData())
        }
    }

    fun getLocations() {
        val call = this.retrofitAPI.getLocations(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<ListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ListResponse>,
                response: retrofit2.Response<ListResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertLocations(response.data)
                    getMachines()
//                    syncData()
                } else {
                    if (response.message!!.equals("Token has expired")) {
                        // refresh token
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

    fun getMachines() {
        val call = this.retrofitAPI.getMachines(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<ListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ListResponse>,
                response: retrofit2.Response<ListResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachines(response.data)
                    getMaterials()
                } else {
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

    fun getMaterials() {
        val call = this.retrofitAPI.getMaterials(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<ListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ListResponse>,
                response: retrofit2.Response<ListResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMaterials(response.data)
                    getStopReasons()
                } else {
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

    fun getStopReasons() {
        val call = this.retrofitAPI.getStopReasons(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<ListResponse> {
            override fun onResponse(
                call: retrofit2.Call<ListResponse>,
                response: retrofit2.Response<ListResponse>
            ) {
                myHelper.hideProgressBar()
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertStopReasons(response.data)
                } else {
                    if (response.message!!.equals("Token has expired")) {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(response.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }
}
