package app.vsptracker.others

import android.content.Context
import android.content.Intent
import app.vsptracker.activities.OperatorLoginActivity
import app.vsptracker.apis.RetrofitAPI
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.delay.EWorkResponse
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.operators.OperatorResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.apis.trip.MyDataResponse
import app.vsptracker.database.DatabaseAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This class will be used for All APIs Calls and Database Actions. It will do following Actions.
 * 1. Insert / Update will be used for Inserting / Updating  data after API call in Database.
 * 2. Push will be used for sending data to Server.
 * 3. PushInsert will be used for interacting with others activities and this method will be protected.
 * 4. fetch will be used for Getting Data by APIs and Saving in Database.
 * 5. All Methods Parameters will be provided by calling Activity and this class will be used
 * only for APIs and Database Calls.
 * 6. Internet Connection will be checked in this Class. All Actions for Database Save and API
 * Calls will be made only using this Class for Background Actions.
 * 7. Save Data in App Database.
 * 8. Get Data from Server using API Calls and Update in App Database.
 * 9. Push Data to Server using API Calls.
 * 10. After Successful Push Call save data with isSync = 1 in App Database.
 * 11. If API call is not Successful, save data with isSync = 0 in App Database.
 * 12. Stop Time, Unloading GPS Location could be set in This Class as this class will be used in BaseActivity children
 * and Other Activities.
 */


private const val REQUEST_ACCESS_FINE_LOCATION = 1

class MyDataPushSave(private val context: Context) {
    private val tag = this::class.java.simpleName
    private val myHelper = MyHelper(tag, context)
    private val db = DatabaseAdapter(context)
    private val retrofit = Retrofit.Builder()
        .baseUrl(RetrofitAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    var isBackgroundCall = true

    /**
     * All Company Data will be fetched from Server using API Calls by this Class only.
     * Old data will be replaced with new one.
     */
    fun fetchOrgData() {
        fetchMachinesAutoLogouts()
    }

    /**
     * Fetch Machines Logout Times and Save in Database.
     * This Logout Time is different for each Site and each Machine Type.
     * After inactivity of Operator, Logout function will be called and required actions will be taken.
     */
    private fun fetchMachinesAutoLogouts() {
        val call = this.retrofitAPI.getMachinesAutoLogouts(
            myHelper.getLoginAPI().org_id,
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(
                call: retrofit2.Call<OperatorResponse>,
                response: retrofit2.Response<OperatorResponse>
            ) {
                myHelper.log("Response:$response")
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesAutoLogouts(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMachinesHours()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.hideProgressBar()
                myHelper.log("Failure" + t.message)
            }
        })
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesHours(responseBody.data as ArrayList<MyData>)
                    fetchMachinesTasks()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachinesTasks(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMaterials()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMaterials(responseBody.data as ArrayList<OperatorAPI>)
                    fetchLocations()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertLocations(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMachines()
                } else {
                    myHelper.hideProgressBar()
                    myHelper.toast(responseBody.message)
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertMachines(responseBody.data as ArrayList<OperatorAPI>)
                    fetchStopReasons()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
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
                val responseBody = response.body()
                if (responseBody!!.success && responseBody.data != null) {
                    db.insertStopReasons(responseBody.data as ArrayList<OperatorAPI>)
                    fetchMachinesPlants()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(responseBody.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    log("Failure" + t.message)
                }
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
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesPlants(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesBrands()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }

    private fun fetchMachinesBrands() {

        val call = this.retrofitAPI.getMachinesBrands(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesBrands(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchMachinesTypes()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }

    private fun fetchMachinesTypes() {

        val call = this.retrofitAPI.getMachinesTypes(
            myHelper.getLoginAPI().auth_token
        )
        call.enqueue(object : retrofit2.Callback<OperatorResponse> {
            override fun onResponse(call: retrofit2.Call<OperatorResponse>, response: retrofit2.Response<OperatorResponse>) {
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertMachinesTypes(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchSites()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
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
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    // myHelper.log("Sites:${operatorResponse.data}.")
                    db.insertSites(operatorResponse.data as ArrayList<OperatorAPI>)
                    fetchOperators()
                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
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
                myHelper.log("response:$response")
                val operatorResponse = response.body()
                if (operatorResponse!!.success && operatorResponse.data != null) {
                    db.insertOperators(operatorResponse.data as ArrayList<OperatorAPI>)
                    when {
                        !isBackgroundCall -> {
                            val intent = Intent(context, OperatorLoginActivity::class.java)
                            context.startActivity(intent)
                        }
                    }

                } else {
                    myHelper.run {
                        hideProgressBar()
                        toast(operatorResponse.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<OperatorResponse>, t: Throwable) {
                myHelper.run {
                    hideProgressBar()
                    toast(t.message.toString())
                    log("FailureResponse: ${t.message}")
                }
            }
        })
    }

    fun pushInsertDelay(eWork: EWork) {
        when {
            myHelper.isOnline() -> pushDelay(eWork)
            else -> insertDelay(eWork)
        }
    }

    private fun pushDelay(eWork: EWork) {
        val call = this.retrofitAPI.pushDelay(myHelper.getLoginAPI().auth_token, eWork)
        call.enqueue(object : retrofit2.Callback<EWorkResponse> {
            override fun onResponse(
                call: retrofit2.Call<EWorkResponse>,
                response: retrofit2.Response<EWorkResponse>
            ) {
                val responseBody = response.body()
                myHelper.run {
                    log("Response:$response")
                    log("ResponseBody:$responseBody")
                }
                when {
                    responseBody!!.success -> eWork.isSync = 1
                    else -> when {
                        responseBody.message == "Token has expired" -> {
                            myHelper.run {
                                log("Token Expired:$response")
                                refreshToken()
                            }
                        }
                        else -> myHelper.toast(responseBody.message)
                    }
                }
                insertDelay(eWork)
            }

            override fun onFailure(call: retrofit2.Call<EWorkResponse>, t: Throwable) {
                insertDelay(eWork)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }

    private fun insertDelay(eWork: EWork) {
        val insertID = db.insertDelay(eWork)
        myHelper.log("saveDelayID: $insertID")
    }

    fun pushInsertMachineHour(myData: MyData) {
        myHelper.log("pushInsertMachineHour:$myData")
        when {
            myHelper.isOnline() -> pushMachineHour(myData)
            else -> insertMachineHour(myData)
        }
    }

    private fun pushMachineHour(myData: MyData) {

        val call = this.retrofitAPI.pushMachinesHours(
            myHelper.getLoginAPI().auth_token,
            myData
        )
        call.enqueue(object : retrofit2.Callback<MyDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<MyDataResponse>,
                response: retrofit2.Response<MyDataResponse>
            ) {
                val responseBody = response.body()
                myHelper.run {
                    log("response:$response")
                    log("responseBody:$responseBody")
                }
                if (responseBody!!.success) {
                    myData.isSync = 1
                } else {
                    if (responseBody.message == "Token has expired") {
                        myHelper.log("Token Expired:$response")
                        myHelper.refreshToken()
                    } else {
                        myHelper.toast(responseBody.message)
                    }
                }
                insertMachineHour(myData)
            }

            override fun onFailure(call: retrofit2.Call<MyDataResponse>, t: Throwable) {
                insertMachineHour(myData)
                myHelper.run {
                    toast(t.message.toString())
                    log("Failure" + t.message)
                }
            }
        })
    }

    private fun insertMachineHour(myData: MyData) {
        db.insertMachineHours(myData)
    }
}
