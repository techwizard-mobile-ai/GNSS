package com.lysaan.malik.vsptracker.apis

import com.lysaan.malik.vsptracker.apis.delay.DelayResponse
import com.lysaan.malik.vsptracker.apis.delay.EWork
import com.lysaan.malik.vsptracker.apis.login.ListResponse
import com.lysaan.malik.vsptracker.apis.login.LoginResponse
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.apis.trip.TripResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPI {

    companion object {

        val BASE_URL = "https://vsptracker.app/api/v1/"
        const val LOGIN = "org/users/login"
        const val OPERATOR_LOGIN ="orgsoperators/operator"
        const val ORGS_LOCATIONS = "orgslocations/list"
        const val ORGS_MACHINES = "orgsmachines/list"
        const val ORGS_MATERIALS = "orgsmaterials/list"
        const val ORGS_STOP_REASONS = "orgsmachinesstopsreasons/list"
        const val ORGS_DELAY = "orgsdelays/store"
        const val ORGS_TRIP = "orgstrips/store"
        const val ORGS_MACHINE_STOPS = "orgsmachinesstops/store"
    }

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
    ): Call<ListResponse>

    @GET(ORGS_MATERIALS)
    fun getMaterials(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<ListResponse>

    @GET(ORGS_MACHINES)
    fun getMachines(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<ListResponse>

    @GET(ORGS_LOCATIONS)
    fun getLocations(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<ListResponse>

    @GET(OPERATOR_LOGIN)
    fun getOperatorLogin(
        @Query("org_id") org_id: Int?,
        @Query("pin") pin: String?,
        @Query("token") token: String?
        ): Call<LoginResponse>

    @POST(LOGIN)
    @FormUrlEncoded
    fun getLogin(@Field("email") email: String,
                 @Field("password") password: String): Call<LoginResponse>
}
