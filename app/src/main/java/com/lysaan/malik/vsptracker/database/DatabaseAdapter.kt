package com.lysaan.malik.vsptracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lysaan.malik.vsptracker.MyHelper
import com.lysaan.malik.vsptracker.apis.delay.EWork
import com.lysaan.malik.vsptracker.apis.operators.OperatorAPI
import com.lysaan.malik.vsptracker.apis.trip.MyData
import com.lysaan.malik.vsptracker.classes.Material

const val DATABASE_NAME = "vsptracker"
const val TABLE_E_LOAD_HISTORY = "e_load_history"
const val TABLE_E_WORK = "ework"
const val TABLE_E_WORK_ACTION_OFFLOADING = "ework_action_loading"
const val TABLE_DELAY = "t_wait"
const val TABLE_TRIP = "ttrip_simple"
const val TABLE_LOCATIONS = "locations"
const val TABLE_MACHINES = "machines"
const val TABLE_MATERIALS = "materials"
const val TABLE_MACHINE_STATUS = "machine_status"
const val TABLE_STOP_REASONS = "stop_reasons"
const val TABLE_OPERATORS = "operators"
const val TABLE_SITES = "sites"
const val TABLE_MACHINES_TYPES = "machines_types"
const val TABLE_MACHINES_BRANDS = "machines_brands"
const val TABLE_MACHINES_PLANTS = "machines_plants"
const val TABLE_MACHINES_TASKS = "machines_tasks"

const val COL_TIME = "time"
const val COL_DATE = "date"
const val COL_LOADED_MACHINE = "loaded_machine"
const val COL_LOADING_MACHINE = "loading_machine"
const val COL_LOADING_MATERIAL = "loading_material"
const val COL_LOADING_LOCATION = "loading_location"
const val COL_UNLOADING_WEIGHT = "loading_weight"
const val COL_UNLOADING_TASK = "unloading_task"
const val COL_UNLOADING_MATERIAL = "unloading_material"
const val COL_UNLOADING_MACHINE = "unloading_machine"
const val COL_UNLOADING_LOCATION = "unloading_location"
const val COL_MACHINE_TYPE_ID = "machine_type_id"
const val COL_MACHINE_BRAND_ID = "machine_brand_id"
const val COL_MACHINE_TASK_ID = "machine_task_id"

const val COL_LOADING_GPS_LOCATION = "loading_gps_location"
const val COL_UNLOADING_GPS_LOCATION = "unloading_gps_location"

const val COL_ID = "id"
const val COL_USER_ID = "user_id"
const val COL_DB_ID = "db_id"
const val COL_ORG_ID = "org_id"

const val COL_START_TIME = "start_time"
const val COL_END_TIME = "end_time"
const val COL_TOTAL_TIME = "total_time"

const val COL_EWORK_TYPE = "ework_type"
const val COL_EWORK_ACTION_TYPE = "ework_action_type"
const val COL_EWORK_ID = "ework_id"

// tripType 0 = Simple Trip
// tripType 1 = Trip For Back Load
const val COL_TRIP_TYPE = "trip_type"
const val COL_TRIP0_ID = "trip0_id"

const val COL_WORK_MODE = "work_mode"
const val COL_MACHINE_NUMBER = "machine_number"
const val COL_MACHINE_STOPPED_REASON = "machine_stop_reason"

const val COL_NAME = "name"
const val COL_IS_DELETED = "is_deleted"
const val COL_STATUS = "status"

const val COL_NUMBER = "number"
// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
const val COL_IS_SYNC = "is_sync"

const val COL_PIN = "pin"
const val COL_SITE_ID = "site_id"
const val COL_MACHINE_PLANT_ID = "machine_plant_id"


class DatabaseAdapter(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 4) {

    val TAG = "DatabaseAdapter"
    private var myHelper: MyHelper

    init {
        this.myHelper = MyHelper(TAG, context)
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createMachinesTasksTable = "CREATE TABLE $TABLE_MACHINES_TASKS ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_MACHINE_TASK_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createMachinesPlantsTable = "CREATE TABLE $TABLE_MACHINES_PLANTS ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_MACHINE_BRAND_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createMachinesBrandsTable = "CREATE TABLE $TABLE_MACHINES_BRANDS ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createMachinesTypesTable = "CREATE TABLE $TABLE_MACHINES_TYPES ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createSitesTable = "CREATE TABLE $TABLE_SITES ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createOperatorsTable = "CREATE TABLE $TABLE_OPERATORS ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_PIN TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createStopReasonsTable = "CREATE TABLE $TABLE_STOP_REASONS (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"

        val createMaterialsTable = "CREATE TABLE $TABLE_MATERIALS (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"


        val createLocationsTable = "CREATE TABLE $TABLE_LOCATIONS (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_NAME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_IS_DELETED INTEGER" +
                " )"


        val createMachineStatusTable = "CREATE TABLE  $TABLE_MACHINE_STATUS (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_MACHINE_TYPE_ID  INTEGER," +
                "$COL_MACHINE_NUMBER TEXT, " +
                "$COL_MACHINE_STOPPED_REASON TEXT, " +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_START_TIME  INTEGER," +
                "$COL_END_TIME INTEGER," +
                "$COL_TOTAL_TIME INTEGER," +
                "$COL_DATE  TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"


        val createTripTable = "CREATE TABLE  $TABLE_TRIP (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TRIP_TYPE  INTEGER," +
                "$COL_TRIP0_ID  INTEGER," +
                "$COL_MACHINE_TYPE_ID  INTEGER," +
                "$COL_MACHINE_NUMBER TEXT, " +

                "$COL_LOADING_MACHINE  TEXT," +
                "$COL_LOADED_MACHINE  TEXT," +
                "$COL_LOADING_MATERIAL  TEXT," +
                "$COL_LOADING_LOCATION  TEXT," +

                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +

                "$COL_START_TIME  INTEGER," +

                "$COL_UNLOADING_TASK TEXT," +
                "$COL_UNLOADING_MATERIAL TEXT," +
                "$COL_UNLOADING_MACHINE TEXT," +
                "$COL_UNLOADING_LOCATION TEXT," +
                "$COL_UNLOADING_WEIGHT  REAL," +

                "$COL_END_TIME INTEGER," +
                "$COL_TOTAL_TIME INTEGER," +
                "$COL_DATE  TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createTWaitTable = "CREATE TABLE $TABLE_DELAY ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_MACHINE_NUMBER TEXT, " +
                "$COL_START_TIME INTEGER, " +
                "$COL_END_TIME INTEGER, " +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_TOTAL_TIME INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createEWorkActionOffloadingTable = "CREATE TABLE $TABLE_E_WORK_ACTION_OFFLOADING ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_EWORK_ID INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT, " +
                "FOREIGHT KEY $COL_EWORK_ID REFERENCES $TABLE_E_WORK($COL_ID)" +
                ")"

        val createEWorkTable = "CREATE TABLE $TABLE_E_WORK ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_EWORK_TYPE INTEGER, " +
                "$COL_EWORK_ACTION_TYPE INTEGER, " +
                "$COL_START_TIME INTEGER, " +
                "$COL_END_TIME INTEGER, " +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_TOTAL_TIME INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createLoadHistoryTable = "CREATE TABLE " + TABLE_E_LOAD_HISTORY + " (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_MACHINE_TYPE_ID  INTEGER," +
                "$COL_LOADING_MACHINE  TEXT," +
                "$COL_LOADED_MACHINE  TEXT," +
                "$COL_LOADING_MATERIAL  TEXT," +
                "$COL_LOADING_LOCATION  TEXT," +
                "$COL_UNLOADING_WEIGHT  REAL," +
                "$COL_DATE  TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_IS_SYNC  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createMachinesTable = "CREATE TABLE $TABLE_MACHINES ( " +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_MACHINE_TYPE_ID INTEGER, " +
                "$COL_MACHINE_BRAND_ID INTEGER, " +
                "$COL_MACHINE_PLANT_ID INTEGER, " +
                "$COL_NUMBER TEXT, " +
                "$COL_TOTAL_TIME INTEGER," +
                "$COL_STATUS INTEGER," +
                "$COL_IS_DELETED INTEGER" +
                " )"

        db?.execSQL(createMachinesTasksTable)
        db?.execSQL(createMachinesPlantsTable)
        db?.execSQL(createMachinesBrandsTable)
        db?.execSQL(createMachinesTypesTable)
        db?.execSQL(createSitesTable)
        db?.execSQL(createOperatorsTable)
        db?.execSQL(createStopReasonsTable)
        db?.execSQL(createMaterialsTable)
        db?.execSQL(createMachinesTable)
        db?.execSQL(createLocationsTable)
        db?.execSQL(createMachineStatusTable)
        db?.execSQL(createTripTable)
        db?.execSQL(createTWaitTable)
        db?.execSQL(createEWorkActionOffloadingTable)
        db?.execSQL(createEWorkTable)
        db?.execSQL(createLoadHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val DROP_TABLE_MACHINES_TASKS = "DROP TABLE IF EXISTS " + TABLE_MACHINES_TASKS;
        val DROP_TABLE_MACHINES_PLANTS = "DROP TABLE IF EXISTS " + TABLE_MACHINES_PLANTS;
        val DROP_TABLE_MACHINES_BRANDS = "DROP TABLE IF EXISTS " + TABLE_MACHINES_BRANDS;
        val DROP_TABLE_MACHINES_TYPES = "DROP TABLE IF EXISTS " + TABLE_MACHINES_TYPES;
        val DROP_TABLE_SITES = "DROP TABLE IF EXISTS " + TABLE_SITES;
        val DROP_TABLE_OPERATORS = "DROP TABLE IF EXISTS " + TABLE_OPERATORS;
        val DROP_TABLE_STOP_REASONS = "DROP TABLE IF EXISTS " + TABLE_STOP_REASONS;
        val DROP_TABLE_MATERIALS = "DROP TABLE IF EXISTS " + TABLE_MATERIALS;
        val DROP_TABLE_MACHINES = "DROP TABLE IF EXISTS " + TABLE_MACHINES;
        val DROP_TABLE_LOCATIONS = "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;
        val DROP_TABLE_TRIP = "DROP TABLE IF EXISTS " + TABLE_TRIP;
        val DROP_TABLE_WAIT = "DROP TABLE IF EXISTS " + TABLE_DELAY;
        val DROP_TABLE_E_WORK_ACTION_OFFLOADING = "DROP TABLE IF EXISTS " + TABLE_E_WORK_ACTION_OFFLOADING;
        val DROP_TABLE_E_WORK = "DROP TABLE IF EXISTS " + TABLE_E_WORK;
        val DROP_TABLE_E_LOAD_HISTORY = "DROP TABLE IF EXISTS " + TABLE_E_LOAD_HISTORY;

        db?.execSQL(DROP_TABLE_MACHINES_TASKS)
        db?.execSQL(DROP_TABLE_MACHINES_PLANTS)
        db?.execSQL(DROP_TABLE_MACHINES_BRANDS)
        db?.execSQL(DROP_TABLE_MACHINES_TYPES)
        db?.execSQL(DROP_TABLE_SITES)
        db?.execSQL(DROP_TABLE_OPERATORS)
        db?.execSQL(DROP_TABLE_STOP_REASONS)
        db?.execSQL(DROP_TABLE_MATERIALS)
        db?.execSQL(DROP_TABLE_MACHINES)
        db?.execSQL(DROP_TABLE_LOCATIONS)
        db?.execSQL(DROP_TABLE_TRIP)
        db?.execSQL(DROP_TABLE_WAIT)
        db?.execSQL(DROP_TABLE_E_WORK_ACTION_OFFLOADING)
        db?.execSQL(DROP_TABLE_E_WORK)
        db?.execSQL(DROP_TABLE_E_LOAD_HISTORY)
        onCreate(db)

    }


    fun insertMachinesTasks (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_MACHINE_TASK_ID, datum.machineTaskId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_TASKS, null, cv)
            myHelper.log("insertMachinesTasks-inserID:$insertID")
        }
    }
    fun insertMachinesPlants (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_MACHINE_BRAND_ID, datum.machineBrandId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_PLANTS, null, cv)
            myHelper.log("insertMachinesPlants-inserID:$insertID")
        }
    }
    fun insertMachinesBrands (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_BRANDS, null, cv)
            myHelper.log("insertMachinesBrands-inserID:$insertID")
        }
    }
    fun insertMachinesTypes (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_TYPES, null, cv)
            myHelper.log("insertMachinesTypes-inserID:$insertID")
        }
    }
    fun insertSites (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_SITES, null, cv)
            myHelper.log("insertSites-inserID:$insertID")
        }
    }
    fun insertOperators (data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_PIN, datum.pin)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_OPERATORS, null, cv)
            myHelper.log("insertOperators-inserID:$insertID")
        }
    }
    fun insertStopReasons(data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_STOP_REASONS, null, cv)
            myHelper.log("StopReasons-inserID:$insertID")
        }
    }
    fun insertMaterials(data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MATERIALS, null, cv)
            myHelper.log("Materials-inserID:$insertID")
        }
    }
    fun insertMachines(data: ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_MACHINE_BRAND_ID, datum.machineBrandId)
            cv.put(COL_MACHINE_PLANT_ID, datum.machinePlantId)
            cv.put(COL_NUMBER, datum.number)
            cv.put(COL_TOTAL_TIME, datum.totalHours)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES, null, cv)
            myHelper.log("Machines-inserID:$insertID")
        }
    }
    fun insertLocations(data : ArrayList<OperatorAPI>){

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data){
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_LOCATIONS, null, cv)
            myHelper.log("Locations-inserID:$insertID")
        }
    }
    fun insertMachineStatus(myData: MyData): Long {

        val currentTime = System.currentTimeMillis()
        myData.startTime = currentTime

        val time = System.currentTimeMillis()
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())
        myData.loadedMachineType = myHelper.getMachineTypeID()
        myData.loadedMachineNumber = myHelper.getMachineNumber()

        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_MACHINE_TYPE_ID, myData.loadedMachineType)
        cv.put(COL_MACHINE_NUMBER, myData.loadedMachineNumber)
        cv.put(COL_MACHINE_STOPPED_REASON, myData.machineStoppedReason)

        cv.put(COL_START_TIME, myData.startTime)
        cv.put(COL_END_TIME, myData.stopTime)
        cv.put(COL_TOTAL_TIME, myData.totalTime)

        cv.put(COL_TIME, myData.time.toLong())
        cv.put(COL_DATE, myData.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation))
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)


        val insertID = db.insert(TABLE_MACHINE_STATUS, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }
    fun insertTrip(myData: MyData): Long {


        val currentTime = System.currentTimeMillis()

        myData.startTime = currentTime
//        myData.stopTime = currentTime
//        myData.totalTime = currentTime - myData.startTime

        val time = System.currentTimeMillis()
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())
        myData.loadedMachineType = myHelper.getMachineTypeID()
        myData.loadedMachineNumber = myHelper.getMachineNumber()


        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE, myData.loadingMachine)
        cv.put(COL_TRIP_TYPE, myData.tripType)
        cv.put(COL_TRIP0_ID, myData.trip0ID)
        cv.put(COL_MACHINE_TYPE_ID, myData.loadedMachineType)
        cv.put(COL_MACHINE_NUMBER, myData.loadedMachineNumber)
        cv.put(COL_LOADED_MACHINE, myData.loadedMachine)
        cv.put(COL_LOADING_MATERIAL, myData.loadingMaterial)
        cv.put(COL_LOADING_LOCATION, myData.loadingLocation)

        cv.put(COL_UNLOADING_TASK, myData.unloadingTask)
        cv.put(COL_UNLOADING_MATERIAL, myData.unloadingMaterial)
        cv.put(COL_UNLOADING_LOCATION, myData.unloadingLocation)
        cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)

        cv.put(COL_START_TIME, myData.startTime)
        cv.put(COL_END_TIME, myData.stopTime)
        cv.put(COL_TOTAL_TIME, myData.totalTime)

        cv.put(COL_TIME, myData.time.toLong())
        cv.put(COL_DATE, myData.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation))
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)


        val insertID = db.insert(TABLE_TRIP, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }
    fun insertDelay(eWork: EWork): Long {

        val time = System.currentTimeMillis()
//        eWork.time = time.toString()

        eWork.stopTime = time
        eWork.totalTime = eWork.stopTime - eWork.startTime
        eWork.date = myHelper.getDate(time)


        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_MACHINE_TYPE_ID, myHelper.getMachineTypeID())
        cv.put(COL_MACHINE_NUMBER, myHelper.getMachineNumber())
        cv.put(COL_START_TIME, eWork.startTime)
        cv.put(COL_END_TIME, eWork.stopTime)
        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())

        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION,myHelper.getGPSLocationToString(eWork.unloadingGPSLocation))
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)

        val insertID = db.insert(TABLE_DELAY, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }
    fun insertEWorkOffLoad(eWork: EWork): Long {
        val time = System.currentTimeMillis()
        eWork.time = time.toString()
        eWork.date = myHelper.getDate(time)

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_EWORK_ID, eWork.eWorkID)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())

        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)

        val insertID = db.insert(TABLE_E_WORK_ACTION_OFFLOADING, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }

    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
    // eWorkType 3 = Scraper Trimming
    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    fun insertEWork(eWork: EWork): Long {

        val currentTime = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime - eWork.startTime

        eWork.time = currentTime.toString()
        eWork.date = myHelper.getDate(currentTime)


        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_EWORK_TYPE, eWork.workType)
        cv.put(COL_EWORK_ACTION_TYPE, eWork.workActionType)
        cv.put(COL_START_TIME, eWork.startTime)

        cv.put(COL_END_TIME, eWork.stopTime)

        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)
        val insertID = db.insert(TABLE_E_WORK, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID

    }
    fun insertELoad(myData: MyData): Long {

        val time = System.currentTimeMillis()
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())
        myData.loadedMachineType = myHelper.getMachineTypeID()


        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE, myData.loadingMachine)
        cv.put(COL_MACHINE_TYPE_ID, myData.loadedMachineType)
        cv.put(COL_LOADED_MACHINE, myData.loadedMachine)
        cv.put(COL_LOADING_MATERIAL, myData.loadingMaterial)
        cv.put(COL_LOADING_LOCATION, myData.loadingLocation)
        cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)

        cv.put(COL_TIME, myData.time.toLong())
        cv.put(COL_DATE, myData.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation))
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)
        val insertID = db.insert(TABLE_E_LOAD_HISTORY, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }

    fun getMachinesTasks(): ArrayList<OperatorAPI> {

        var list: ArrayList<OperatorAPI> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_MACHINES_TASKS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = OperatorAPI()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.machineTaskId = result.getInt(result.getColumnIndex(COL_MACHINE_TASK_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getMachinesTasks else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMachinesPlants(): ArrayList<OperatorAPI> {

        var list: ArrayList<OperatorAPI> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_MACHINES_PLANTS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = OperatorAPI()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.machineBrandId = result.getInt(result.getColumnIndex(COL_MACHINE_BRAND_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getMachinesPlants else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMachinesBrands(): ArrayList<OperatorAPI> {

        var list: ArrayList<OperatorAPI> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_MACHINES_BRANDS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = OperatorAPI()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getMachinesBrands else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMachinesTypes(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_MACHINES_TYPES} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getMachinesTypes else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getSites(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_SITES} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getSites else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getOperators(): ArrayList<OperatorAPI> {

        var list: ArrayList<OperatorAPI> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from ${TABLE_OPERATORS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var datum = OperatorAPI()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.pin = result.getString(result.getColumnIndex(COL_PIN))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("getOperators else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getOperator(pin: String ): OperatorAPI {

        var datum = OperatorAPI()

        val db = this.readableDatabase

        val query = "Select * from ${TABLE_OPERATORS} WHERE ${COL_PIN} =? AND ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, arrayOf(pin))


        if (result.moveToFirst()) {
            do {
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.pin = result.getString(result.getColumnIndex(COL_PIN))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))

            } while (result.moveToNext())
        } else {
            myHelper.log("getOperator else result:$result")
        }

        result.close()
        db.close()
        return datum
    }
    fun getStopReasons(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_STOP_REASONS  WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)

            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMaterials(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_MATERIALS  WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


//        val createMaterialsTable = "CREATE TABLE $TABLE_MATERIALS (" +
//                "$COL_ID INTEGER PRIMARY KEY, " +
//                "$COL_ORG_ID INTEGER, " +
//                "$COL_SITE_ID INTEGER, " +
//                "$COL_MACHINE_TYPE_ID INTEGER, " +
//                "$COL_NAME TEXT, " +
//                "$COL_STATUS INTEGER, " +
//                "$COL_IS_DELETED INTEGER" +
//                " )"


        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)

            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMachines(type:Int = 0): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase
        var query = ""
        if(type < 1){
            query = "Select * from $TABLE_MACHINES  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_ID DESC"
        }else{
            query = "Select * from $TABLE_MACHINES  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} AND $COL_MACHINE_TYPE_ID = ${type} ORDER BY $COL_ID DESC"
        }
        myHelper.log("rawQuery:$query")
        val result = db.rawQuery(query, null)


//        var datum = OperatorAPI()
//        datum.id = result.getInt(result.getColumnIndex(COL_ID))
//        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
//        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
//        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
//        datum.machineBrandId = result.getInt(result.getColumnIndex(COL_MACHINE_BRAND_ID))
//        datum.machinePlantId = result.getInt(result.getColumnIndex(COL_MACHINE_PLANT_ID))
//        datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
//        datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_TIME))
//        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
//        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
//        list.add(datum)


        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)

            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getLocations(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_LOCATIONS  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.name = result.getString(result.getColumnIndex(COL_NAME))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                list.add(datum)

            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getWaits(): MutableList<EWork> {

        var list: MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_DELAY  WHERE $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.machineTypeID = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                eWork.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

                eWork.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                eWork.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                eWork.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                eWork.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))

                list.add(eWork)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getEWorksOffLoads(eWorkID: Int): MutableList<EWork> {

        var list: MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_E_WORK_ACTION_OFFLOADING WHERE ${COL_EWORK_ID} = ${eWorkID} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.eWorkID = result.getInt(result.getColumnIndex(COL_EWORK_ID))

                eWork.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(eWork)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
    // eWorkType 3 = Scraper Trimming
    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    fun getEWorks(workType: Int): MutableList<EWork> {

        var list: MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_E_WORK WHERE ${COL_EWORK_TYPE} = ${workType} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.workType = result.getInt(result.getColumnIndex(COL_EWORK_TYPE))
                eWork.workActionType = result.getInt(result.getColumnIndex(COL_EWORK_ACTION_TYPE))
                eWork.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                eWork.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                eWork.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
                eWork.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(eWork)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getELoadHistroy(): MutableList<MyData> {

        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_E_LOAD_HISTORY WHERE $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var data = MyData()
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                data.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                data.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                data.loadingMaterial =
                        result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                data.loadingLocation =
                        result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                data.unloadingWeight =
                        result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                data.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))


                data.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(data)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getTrips(): MutableList<MyData> {

        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_TRIP  WHERE $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)



        if (result.moveToFirst()) {
            do {
                var data = MyData()
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
                data.trip0ID = result.getInt(result.getColumnIndex(COL_TRIP0_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                data.loadedMachineNumber =
                        result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
                data.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                data.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                data.loadingMaterial =
                        result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                data.loadingLocation =
                        result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                data.unloadingWeight =
                        result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                data.unloadingTask = result.getString(result.getColumnIndex(COL_UNLOADING_TASK))
                data.unloadingMaterial =
                        result.getString(result.getColumnIndex(COL_UNLOADING_MATERIAL))
                data.unloadingLocation =
                        result.getString(result.getColumnIndex(COL_UNLOADING_LOCATION))

                data.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                data.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                data.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                data.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                data.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(data)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }
    fun getMachineStatus(id:Long = 0): MyData {

//        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_MACHINE_STATUS  WHERE ${COL_ID} = ${id} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        var data = MyData()

        if (result.moveToFirst()) {
//            do {
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                data.loadedMachineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
                data.machineStoppedReason = result.getString(result.getColumnIndex(COL_MACHINE_STOPPED_REASON))


                data.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                data.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                data.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                data.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                data.loadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.operatorID = result.getInt(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
//                list.add(data)
//            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return data
    }

    fun updateDelay(eWork: EWork): Int {

        myHelper.log("updateDelay:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_IS_SYNC, eWork.isSync)
        val updatedID = db.update(TABLE_DELAY, cv, "$COL_ID = ${eWork.ID}", null)

        myHelper.log("updatedID :$updatedID ")
        return updatedID

    }
    fun updateEWork(eWork: EWork): Int {

        val currentTime = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime - eWork.startTime

        eWork.time = currentTime.toString()
        eWork.date = myHelper.getDate(currentTime)

        myHelper.log("updateEWork:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_END_TIME, eWork.stopTime)
        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, eWork.isSync)
        val updatedID = db.update(TABLE_E_WORK, cv, "$COL_ID = ${eWork.ID}", null)

        myHelper.log("updatedID :$updatedID ")
        return updatedID

    }
    fun updateTrip(myData: MyData): Int {
        val currentTime = System.currentTimeMillis()

        myData.stopTime = currentTime
        myData.totalTime = currentTime - myData.startTime

        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime)

        myHelper.log("updateTrip:$myData")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)
        cv.put(COL_UNLOADING_TASK, myData.unloadingTask)
        cv.put(COL_UNLOADING_MATERIAL, myData.unloadingMaterial)
        cv.put(COL_UNLOADING_LOCATION, myData.unloadingLocation)
        cv.put(COL_END_TIME, myData.stopTime)
        cv.put(COL_TOTAL_TIME, myData.totalTime)
        cv.put(COL_TIME, myData.time.toLong())
        cv.put(COL_DATE, myData.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, myData.isSync)

        val updatedID = db.update(TABLE_TRIP, cv, "$COL_ID = ${myData.recordID}", null)

        myHelper.log("updatedID :$updatedID ")
        return updatedID

    }
    fun updateMachineStatus(myData: MyData): Int {

        val currentTime = System.currentTimeMillis()

        myData.stopTime = currentTime
        myData.totalTime = currentTime - myData.startTime

        myData.time = currentTime.toString()
        myData.date = myHelper.getDate(currentTime)

        myHelper.log("updateTrip:$myData")

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_END_TIME, myData.stopTime)
        cv.put(COL_TOTAL_TIME, myData.totalTime)
        cv.put(COL_TIME, myData.time.toLong())
        cv.put(COL_DATE, myData.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())
        cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, myData.isSync)

        val updatedID = db.update(TABLE_MACHINE_STATUS, cv, "$COL_ID = ${myData.recordID}", null)

        myHelper.log("updatedID :$updatedID ")
        return updatedID

    }
}