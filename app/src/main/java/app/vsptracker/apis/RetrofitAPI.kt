package app.vsptracker.apis
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.delay.EWorkResponse
import app.vsptracker.apis.login.LoginResponse
import app.vsptracker.apis.operators.OperatorResponse
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
//        const val BASE_URL = "https://vsptracker.app/api/v1/"
        const val BASE_URL = MyEnum.BASE_URL
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

    @POST(ORGS_PUSH_OPERATORS_HOURS)
    fun pushOperatorHour(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>
    
    /**
     * Operator Device details are saved in Portal. This will be helpful if there is any GPS issue for any Device.
     */
    @GET(ORGS_MACHINES_AUTO_LOGOUTS)
    fun getMachinesAutoLogouts(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?,
        @Query("operator_id") id: Int,
        @Query("device_details")deviceDetailsString: String
    ): Call<OperatorResponse>

    @PUT(ORGS_MACHINES_UPDATE)
    fun pushIsMachineRunning(
        @Query("token") token: String?,
        @Query("id") machineID: Int?,
        @Query("is_running") isRunning: Int?,
        @Query("machines_stops_db_id") machines_stops_db_id: Int?
    ): Call<MyDataResponse>


    @POST(ORGS_PUSH_MACHINES_HOURS)
    fun pushMachinesHours(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>


    @GET(ORGS_MACHINES_HOURS)
    fun getMachinesHours(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<MyDataListResponse>

    //    load_type_id 1 = Production Digging Load
    @POST(ORGS_LOADS)
    fun pushLoads(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>
    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
    // eWorkType 3 = Scraper Trimming

    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    @POST(ORGS_SIDECASTINGS)
    fun pushSideCastings(@Query("token") token: String?, @Body eWork: EWork): Call<EWorkResponse>


    @GET(ORGS_MACHINES_TASKS)
    fun getMachinesTasks(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MACHINES_PLANTS)
    fun getMachinesPlants(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_MACHINE_BRANDS)
    fun getMachinesBrands(
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_MACHINE_TYPES)
    fun getMachinesTypes(
        @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ADMIN_ORGS_SITES)
    fun getSites(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_OPERATORS)
    fun getOperators(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @POST(ORGS_MACHINE_STOPS)
    fun pushMachinesStops(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>

    @POST(ORGS_TRIP)
    fun pushTrip(@Query("token") token: String?, @Body myData: MyData): Call<MyDataResponse>

    @POST(ORGS_DELAY)
    fun pushDelay(@Query("token") token: String?, @Body eWork: EWork): Call<EWorkResponse>

    @GET(ORGS_STOP_REASONS)
    fun getStopReasons(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MATERIALS)
    fun getMaterials(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_MACHINES)
    fun getMachines(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

    @GET(ORGS_LOCATIONS)
    fun getLocations(
        @Query("org_id") org_id: Int?, @Query("token") token: String?
    ): Call<OperatorResponse>

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
    
    @GET(ORGS_SERVER_SYNC_LIST)
    fun getServerSyncOrg(
        @Query("org_id") org_id: Int?,
        @Query("token") token: String?
    ): Call<ServerSyncResponse>
}
