package com.lysaan.malik.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.R
import com.lysaan.malik.vsptracker.activities.common.MachineStatus1Activity
import com.lysaan.malik.vsptracker.apis.RetrofitAPI
import com.lysaan.malik.vsptracker.apis.operators.OperatorAPI
import com.lysaan.malik.vsptracker.apis.operators.OperatorResponse
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

        when (myHelper.getMachineTypeID()) {
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

        if(myHelper.isOnline()){
            fetchOrgData()
        }

        if(myHelper.getOperatorAPI().id > 0){
            launchHome()
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
                    myHelper.log("OperatorPIN")
                    if(db.getOperator(pin).id > 0){
                        myHelper.setOperatorAPI(db.getOperator(pin))
                        launchHome()
                    }else{
                        myHelper.toast("Invalid PIN.\nPlease Enter Correct PIN.")
                    }
                }
            }
        }
    }

/*    fun operatorLogin(pin: String) {
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
    }*/
    fun launchHome(){
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

    fun fetchOrgData(){
        myHelper.toast("Fetching Company Data in background.")
        fetchMachinesTasks()
    }

    private fun fetchMachinesTasks() {
        val call = this.retrofitAPI.getMachinesTasks(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachinesTasks(response.data as ArrayList<OperatorAPI>)
                    fetchMaterials()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun fetchMaterials() {
        val call = this.retrofitAPI.getMaterials(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMaterials(response.data as ArrayList<OperatorAPI>)
                    fetchLocations()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun fetchLocations() {
        val call = this.retrofitAPI.getLocations(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertLocations(response.data as ArrayList<OperatorAPI>)
                    fetchMachines()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun fetchMachines() {
        val call = this.retrofitAPI.getMachines(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachines(response.data as ArrayList<OperatorAPI>)
                    fetchStopReasons()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun fetchStopReasons() {
        val call = this.retrofitAPI.getStopReasons(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {

                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertStopReasons(response.data as ArrayList<OperatorAPI>)
                    fetchMachinesPlants()
                } else {

                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.log("Failure" + t.message)
            }
        })
    }
    private fun fetchMachinesPlants(){

        val call = this.retrofitAPI.getMachinesPlants(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if(operatorResponse!!.success && operatorResponse.data != null){
                    myHelper.log("MachinesPlants:${operatorResponse.data}.")
                    db.insertMachinesPlants(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesBrands()
                }else{

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
    private fun fetchMachinesBrands(){

        val call = this.retrofitAPI.getMachinesBrands(
            myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if(operatorResponse!!.success && operatorResponse.data != null){
                    myHelper.log("MachinesBrands:${operatorResponse.data}.")
                    db.insertMachinesBrands(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesTypes()
                }else{

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
    private fun fetchMachinesTypes(){

        val call = this.retrofitAPI.getMachinesTypes(
            myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if(operatorResponse!!.success && operatorResponse.data != null){
                    myHelper.log("MachinesTypes:${operatorResponse.data}.")
                    db.insertMachinesTypes(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchSites()
                }else{

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
    private fun fetchSites(){

        val call = this.retrofitAPI.getSites(
            myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if(operatorResponse!!.success && operatorResponse.data != null){
                    myHelper.log("Sites:${operatorResponse.data}.")
                    db.insertSites(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchOperators()
                }else{

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
    private fun fetchOperators(){

        val call = this.retrofitAPI.getOperators(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token)
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if(operatorResponse!!.success && operatorResponse.data != null){
                    myHelper.log("Operators:${operatorResponse.data}.")
                    db.insertOperators(operatorResponse.data as ArrayList<OperatorAPI>)
                }else{

                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {

                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
}
