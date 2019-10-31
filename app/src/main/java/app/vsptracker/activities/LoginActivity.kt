package app.vsptracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.vsptracker.AppRater
import app.vsptracker.BuildConfig
import app.vsptracker.MyHelper
import app.vsptracker.R
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.login.LoginResponse
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.operators.OperatorResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    protected lateinit var db: DatabaseAdapter

    internal lateinit var retrofit: Retrofit
    internal lateinit var retrofitAPI: RetrofitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_login)
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

        myHelper.hideKeybaordOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        signin_forgot_pass.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {

                var email = signin_email.text.toString()
                var pass = signin_pass.text.toString()

                // myHelper.log("email:" + email)
                // myHelper.log("Pass" + pass)

                if (!myHelper.isValidEmail(email)) {
                    myHelper.toast("Please Provide valid Email Address.")
                } else if (pass.length < 8) {
                    myHelper.toast("Password Minimum Length should be 8 Characters")
                } else {
                    orgLogin(email, pass)
                }
            }
        }
    }


    fun orgLogin(email: String, pass: String) {
        myHelper.showProgressBar()

        val call = this.retrofitAPI.getLogin(email, pass)
        call.enqueue(object : retrofit2.Callback<LoginResponse> {

            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
//                myHelper.hideProgressBar()
                myHelper.log("RetrofitResponse:${response.body()}")
                val loginResponse = response.body()
                if (loginResponse!!.success) {
                    loginResponse.data.pass = pass
                    myHelper.setLoginAPI(loginResponse.data)

                    var versionCode = BuildConfig.VERSION_CODE
                    myHelper.log("App:${loginResponse.app}")
                    myHelper.log("versionCode:$versionCode")
                    if (loginResponse.app.version_code > versionCode && loginResponse.app.is_critical > 0) {
                        myHelper.log("Update App")
                        val appRater = AppRater()
                        appRater.rateNow(this@LoginActivity)

                    } else {
                        fetchOrgData()
                    }

                } else {
                    myHelper.hideProgressBar()
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
        // myHelper.log("fetchJson")

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
                // myHelper.log("body:$Jobject")
            }

            override fun onFailure(call: Call, e: IOException) {
                myHelper.hideProgressBar()
                Log.d("OKHttp", "Failed to execute request ${e.printStackTrace()}")
            }
        })
    }

    fun fetchOrgData() {
        myHelper.toast("Fetching Company Data.\nPlease wait...")
        fetchMachinesHours()
//        fetchMachinesTasks()
    }

    private fun fetchMachinesHours() {
        val call = this.retrofitAPI.getMachinesHours(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<MyDataListResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataListResponse>,
                response: retrofit2.Response<MyDataListResponse>
            ) {
                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachinesHours(response.data as ArrayList<MyData>)
                    fetchMachinesTasks()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<MyDataListResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
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
                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachinesTasks(response.data as ArrayList<OperatorAPI>)
                    fetchMaterials()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
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
                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMaterials(response.data as ArrayList<OperatorAPI>)
                    fetchLocations()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
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

                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertLocations(response.data as ArrayList<OperatorAPI>)
                    fetchMachines()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
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

                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertMachines(response.data as ArrayList<OperatorAPI>)
                    fetchStopReasons()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
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

                myHelper.log("Response:$response")
                val response = response.body()
                if (response!!.success && response.data != null) {
                    db.insertStopReasons(response.data as ArrayList<OperatorAPI>)
                    fetchMachinesPlants()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(response.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
    }

    private fun fetchMachinesPlants() {

        val call = this.retrofitAPI.getMachinesPlants(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("MachinesPlants:${operatorResponse.data}.")
                    db.insertMachinesPlants(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesBrands()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchMachinesBrands() {

        val call = this.retrofitAPI.getMachinesBrands(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("MachinesBrands:${operatorResponse.data}.")
                    db.insertMachinesBrands(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesTypes()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchMachinesTypes() {

        val call = this.retrofitAPI.getMachinesTypes(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("MachinesTypes:${operatorResponse.data}.")
                    db.insertMachinesTypes(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchSites()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchSites() {

        val call = this.retrofitAPI.getSites(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("Sites:${operatorResponse.data}.")
                    db.insertSites(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchOperators()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }

    private fun fetchOperators() {

        val call = this.retrofitAPI.getOperators(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("RetrofitResponse:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    myHelper.log("Operators:${operatorResponse.data}.")
                    db.insertOperators(operatorResponse.data as ArrayList<OperatorAPI>)
                    val intent = Intent(this@LoginActivity, OperatorLoginActivity::class.java)
                    startActivity(intent)
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(operatorResponse.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.toast(t.message.toString())
                Log.e(TAG, "FailureResponse:" + t.message)
            }
        })
    }
}
