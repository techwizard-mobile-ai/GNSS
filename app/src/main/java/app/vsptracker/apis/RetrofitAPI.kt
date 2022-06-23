package app.vsptracker.apis
import app.vsptracker.apis.login.LoginResponse
import app.vsptracker.apis.mvporgsfiles.MvpOrgsFilesResponse
import app.vsptracker.apis.serverSync.ServerSyncDataAPI
import app.vsptracker.apis.serverSync.ServerSyncResponse
import app.vsptracker.apis.trip.MyData
import app.vsptracker.apis.trip.MyDataListResponse
import app.vsptracker.apis.trip.MyDataResponse
import app.vsptracker.others.MyEnum
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPI {
    companion object {
//        const val BASE_URL = MyEnum.BASE_URL
        const val LOGIN = "org/users/login1"
        const val ORGS_LOCATIONS = "orgslocations/list"
        const val ORGS_MACHINES = "orgsmachines/list"
        const val ORGS_MATERIALS = "orgsmaterials/list"
        const val ORGS_STOP_REASONS = "orgsmachinesstopsreasons/list"
        const val ORGS_OPERATORS = "orgsoperators/list"
        const val ADMIN_ORGS_SITES = "adminorgssites/list"
        const val ADMIN_MACHINE_TYPES = "adminmachinestypes/get"
        const val ADMIN_MACHINE_BRANDS = "adminmachinesbrands/get"
        const val ORGS_MACHINES_PLANTS = "orgsplants/list"
        const val ORGS_MACHINES_TASKS = "orgstasks/list"

        const val ORGS_MACHINE_STOPS = "orgsmachinesstops/store"
        const val ORGS_DELAY = "orgsdelays/store"
        const val ORGS_TRIP = "orgstrips/store"
        const val ORGS_SIDECASTINGS = "orgssidecastings/store"
        const val ORGS_LOADS = "orgsloads/store"

//        ORGS_MACHINE_MAX_HOUR is replaced with max_hours to get Max Total Hours for a Machine in Single Entry
//        const val ORGS_MACHINES_HOURS = "orgsmachineshours/list"
        const val ORGS_MACHINES_HOURS = "orgsmachineshours/max_hours"
        const val ORGS_PUSH_MACHINES_HOURS = "orgsmachineshours/store"
        const val ORGS_MACHINES_UPDATE = "orgsmachines/update"
        const val ORGS_MACHINES_AUTO_LOGOUTS = "orgsmachinesautologouts/list"
        const val ORGS_PUSH_OPERATORS_HOURS = "orgsoperatorshours/store"
        const val ORGS_MACHINE_MAX_HOUR ="orgsmachineshours/machine_max_hour"
        const val ORGS_SERVER_SYNC = "orgsserversync/store"
        const val ORGS_SERVER_SYNC_LIST = "orgsserversync/list"
        const val MVP_ORGS_FILES_LIST = "mvporgsprojects/show"
    }
    

    
    @POST(ORGS_SERVER_SYNC)
    fun pushServerSync(
        @Body serverSyncDataAPI: ServerSyncDataAPI
    ): Call<ServerSyncResponse>
    
    /**
     * This API call will be Made in Start Hour Meter and this Total Hours will be compared with App Machine Max Hour
     * Greater value will be placed in Hour Meter And Operator will see Max Hour for Start of Machine.
     * Now Either Operator will Correct Start Hours Manually OR use already displayed Hours if that reading is correct.
     */
    @GET(ORGS_MACHINE_MAX_HOUR)
    fun getMachineMaxHour(
        @Query("machine_id") machine_id: Int?, @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<MyDataListResponse>
    
    @POST(ORGS_PUSH_MACHINES_HOURS)
    fun pushMachinesHours(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>

    
    @POST(LOGIN)
    @FormUrlEncoded
    fun getLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: Int = 1,
        @Field("ttl") ttl: String = MyEnum.TTL
    ): Call<LoginResponse>
    
    @GET(ORGS_SERVER_SYNC_LIST)
    fun getServerSync(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?,
        @Query("operator_id") id: Int,
        @Query("device_details")deviceDetailsString: String
    ): Call<ServerSyncResponse>
    
    @GET(MVP_ORGS_FILES_LIST)
    fun getMvpOrgsFiles(
        @Query("id") id: Int?,
        @Query("prefix") prefix: String = "",
        @Query("role") role: Int?,
        @Query("isAdminLoggedIn") isAdminLoggedIn: Boolean = false,
        @Query("token") token: String?
    ): Call<MvpOrgsFilesResponse>
    
}
