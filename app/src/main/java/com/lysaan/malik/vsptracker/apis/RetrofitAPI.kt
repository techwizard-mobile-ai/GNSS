package com.lysaan.malik.vsptracker.apis

import com.lysaan.malik.vsptracker.apis.delay.DelayResponse
import com.lysaan.malik.vsptracker.apis.delay.EWork
import com.lysaan.malik.vsptracker.apis.login.LoginResponse
import com.lysaan.malik.vsptracker.apis.operators.OperatorResponse
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.TripResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPI {

    companion object {

        const val BASE_URL = "https://vsptracker.app/api/v1/"
        const val LOGIN = "org/users/login1"
        const val OPERATOR_LOGIN ="orgsoperators/operator"
        const val ORGS_LOCATIONS = "orgslocations/list"
        const val ORGS_MACHINES = "orgsmachines/list"
        const val ORGS_MATERIALS = "orgsmaterials/list"
        const val ORGS_STOP_REASONS = "orgsmachinesstopsreasons/list"
        const val ORGS_DELAY = "orgsdelays/store"
        const val ORGS_TRIP = "orgstrips/store"
        const val ORGS_MACHINE_STOPS = "orgsmachinesstops/store"
        const val ORGS_OPERATORS = "orgsoperators/list"
        const val ADMIN_SITES = "adminsites/get"
        const val ADMIN_MACHINE_TYPES = "adminmachinestypes/get"
        const val ADMIN_MACHINE_BRANDS = "adminmachinesbrands/get"
        const val ORGS_MACHINES_PLANTS = "orgsplants/list"
        const val ORGS_MACHINES_TASKS = "orgstasks/list"
        const val ORGS_SIDECASTINGS = "orgssidecastings/store"
    }

    @POST (ORGS_SIDECASTINGS)
    fun pushSideCasting(@Query("token") token: String?, @Body eWork: EWork):Call<DelayResponse>


    @GET(ORGS_MACHINES_TASKS)
    fun getMachinesTasks(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MACHINES_PLANTS)
    fun getMachinesPlants(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_MACHINE_BRANDS)
    fun getMachinesBrands(
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_MACHINE_TYPES)
    fun getMachinesTypes(
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_SITES)
    fun getSites(
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_OPERATORS)
    fun getOperators(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @POST (ORGS_MACHINE_STOPS)
    fun pushMachineStatus(@Query("token") token: String?, @Body myData: MyData):Call<TripResponse>

    @POST (ORGS_TRIP)
    fun pushTrip(@Query("token") token: String?, @Body myData: MyData):Call<TripResponse>

    @POST (ORGS_DELAY)
    fun pushDelay(@Query("token") token: String?, @Body eWork: EWork):Call<DelayResponse>

    @GET(ORGS_STOP_REASONS)
    fun getStopReasons(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MATERIALS)
    fun getMaterials(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MACHINES)
    fun getMachines(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_LOCATIONS)
    fun getLocations(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(OPERATOR_LOGIN)
    fun getOperatorLogin(
        @Query("org_id") org_id: Int?,
        @Query("pin") pin: String?,
        @Query("token") token: String?
        ): Call<LoginResponse>

    @POST(LOGIN)
    @FormUrlEncoded
    fun getLogin(@Field("email") email: String,
                 @Field("password") password: String,
                 @Field("role") role: Int = 1
    ): Call<LoginResponse>
}
