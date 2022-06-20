package app.vsptracker.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.vsptracker.BuildConfig
import app.vsptracker.R
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.login.LoginResponse
import app.vsptracker.database.DatabaseAdapter
import app.vsptracker.others.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    
    private val tag = this::class.java.simpleName
    private lateinit var myHelper: MyHelper
    private lateinit var db: DatabaseAdapter
    
    private lateinit var retrofit: Retrofit
    private lateinit var retrofitAPI: RetrofitAPI
    private lateinit var myDataPushSave: MyDataPushSave
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Utils.onActivityCreateSetTheme(this)
        
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        
        myHelper = MyHelper(tag, this)
        myHelper.setProgressBar(signin_pb)
        
        db = DatabaseAdapter(this)
        
        myDataPushSave = MyDataPushSave(this)
        
        this.retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.api_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(myHelper.skipSSLOkHttpClient().build())
            .build()
        this.retrofitAPI = retrofit.create(RetrofitAPI::class.java)
        
        when (myHelper.getMachineTypeID()) {
            1 -> {
                Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.excavator))
                    .into(signin_image)
            }
            2 -> {
                Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.scraper)).into(signin_image)
            }
            3 -> {
                Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.truck)).into(signin_image)
            }
            else -> {
                Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.welcomenew))
                    .into(signin_image)
            }
        }
        
        
        
        signin_email.setText(MyEnum.user)
        signin_pass.setText(MyEnum.pass)
        
        myHelper.hideKeyboardOnClick(login_main_layout)
        signin_signin.setOnClickListener(this)
        signin_forgot_pass.setOnClickListener(this)
        
        
    }
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signin_signin -> {
                val email = signin_email.text.toString()
                val pass = signin_pass.text.toString()
                when {
                    !myHelper.isValidEmail(email) -> myHelper.toast(getString(R.string.invalid_email))
                    pass.length < 8 -> myHelper.toast(getString(R.string.minimum_password_length))
                    else -> orgLogin(email, pass)
                }
            }
        }
    }
    
    private fun orgLogin(email: String, pass: String) {
        myHelper.log("email:$email")
        myHelper.log("pass:$pass")
        if (!myHelper.isOnline()) {
            myHelper.showErrorDialog(getString(R.string.no_internet_connection), getString(R.string.no_internet_explanation))
        } else {
            myHelper.showProgressBar()
            val call = this.retrofitAPI.getLogin(email, pass)
            call.enqueue(object : retrofit2.Callback<LoginResponse> {
    
                override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
        
                    myHelper.log("headers:${response.headers()}")
                    myHelper.log("code:${response.code()}")
                    myHelper.log("RetrofitResponse:${response.body()}")
                    val loginResponse = response.body()
                    if (loginResponse!!.success) {
                        loginResponse.data.pass = pass
                        myHelper.setLoginAPI(loginResponse.data)
            
                        val versionCode = BuildConfig.VERSION_CODE
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
                }
            })
        }
        
    }
    
    fun fetchOrgData() {
        Toast.makeText(this, R.string.login_successful_fetching_data, Toast.LENGTH_LONG).show()
        myDataPushSave.fetchOrgData(false)
    }
}
