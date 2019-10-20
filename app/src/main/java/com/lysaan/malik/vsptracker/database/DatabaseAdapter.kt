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
const val TABLE_MACHINES_HOURS = "machines_hours"

const val COL_TIME = "time"
const val COL_DATE = "date"
const val COL_LOADED_MACHINE = "loaded_machine"

const val COL_LOADING_MACHINE = "loading_machine"
const val COL_LOADING_MACHINE_ID = "loading_machine_id"

const val COL_LOADING_MATERIAL = "loading_material"
const val COL_LOADING_MATERIAL_ID = "loading_material_id"

const val COL_LOADING_LOCATION = "loading_location"
const val COL_LOADING_LOCATION_ID = "loading_location_id"

const val COL_UNLOADING_WEIGHT = "loading_weight"

const val COL_UNLOADING_TASK = "unloading_task"
const val COL_UNLOADING_TASK_ID = "unloading_task_id"

const val COL_UNLOADING_MATERIAL = "unloading_material"
const val COL_UNLOADING_MATERIAL_ID = "unloading_material_id"

const val COL_UNLOADING_MACHINE = "unloading_machine"

const val COL_UNLOADING_LOCATION = "unloading_location"
const val COL_UNLOADING_LOCATION_ID = "unloading_location_id"

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

const val COL_START_HOURS = "start_hours"
const val COL_TOTAL_HOURS = "total_hours"

const val COL_EWORK_TYPE = "ework_type"
const val COL_EWORK_ACTION_TYPE = "ework_action_type"
const val COL_EWORK_ID = "ework_id"

// tripType 0 = Simple Trip
// tripType 1 = Trip For Back Load
const val COL_TRIP_TYPE = "trip_type"
const val COL_TRIP0_ID = "trip0_id"

const val COL_WORK_MODE = "work_mode"
const val COL_MACHINE_NUMBER = "machine_number"
const val COL_MACHINE_ID = "machine_id"
const val COL_MACHINE_STOPPED_REASON = "machine_stop_reason"

const val COL_NAME = "name"
const val COL_IS_DELETED = "is_deleted"
const val COL_STATUS = "status"
const val COL_IS_START_HOURS_CUSTOM = "is_start_hours_custom"
const val COL_IS_TOTAL_HOURS_CUSTOM = "is_total_hours_custom"

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


        val createMachinesHoursTable = "CREATE TABLE  $TABLE_MACHINES_HOURS (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_MACHINE_TYPE_ID  INTEGER," +
                "$COL_MACHINE_ID  INTEGER," +
                "$COL_MACHINE_NUMBER TEXT, " +
                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +
                "$COL_START_TIME  INTEGER," +
                "$COL_END_TIME INTEGER," +
                "$COL_START_HOURS TEXT," +
                "$COL_IS_START_HOURS_CUSTOM INTEGER," +
                "$COL_TOTAL_HOURS TEXT," +
                "$COL_IS_TOTAL_HOURS_CUSTOM INTEGER," +
                "$COL_DATE  TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_IS_SYNC INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"


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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
                "$COL_TRIP_TYPE  INTEGER," +
                "$COL_TRIP0_ID  TEXT," +
                "$COL_MACHINE_TYPE_ID  INTEGER," +
                "$COL_MACHINE_NUMBER TEXT, " +

                "$COL_LOADING_MACHINE  TEXT," +
                "$COL_LOADING_MACHINE_ID  INTEGER," +

                "$COL_LOADED_MACHINE  TEXT," +

                "$COL_LOADING_MATERIAL  TEXT," +
                "$COL_LOADING_MATERIAL_ID  INTEGER," +

                "$COL_LOADING_LOCATION  TEXT," +
                "$COL_LOADING_LOCATION_ID  INTEGER," +

                "$COL_LOADING_GPS_LOCATION  TEXT," +
                "$COL_UNLOADING_GPS_LOCATION  TEXT," +
                "$COL_USER_ID  TEXT," +

                "$COL_START_TIME  INTEGER," +

                "$COL_UNLOADING_TASK TEXT," +
                "$COL_UNLOADING_TASK_ID INTEGER," +

                "$COL_UNLOADING_MATERIAL TEXT," +
                "$COL_UNLOADING_MATERIAL_ID INTEGER," +

                "$COL_UNLOADING_MACHINE TEXT," +
                "$COL_UNLOADING_LOCATION TEXT," +
                "$COL_UNLOADING_LOCATION_ID INTEGER," +

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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
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
                "$COL_ORG_ID INTEGER, " +
                "$COL_SITE_ID INTEGER, " +
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
                "$COL_TOTAL_TIME TEXT," +
                "$COL_STATUS INTEGER," +
                "$COL_IS_DELETED INTEGER" +
                " )"

        db?.execSQL(createMachinesHoursTable)
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

        val DROP_TABLE_MACHINES_HOURS = "DROP TABLE IF EXISTS " + TABLE_MACHINES_HOURS;
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
        val DROP_TABLE_E_WORK_ACTION_OFFLOADING =
            "DROP TABLE IF EXISTS " + TABLE_E_WORK_ACTION_OFFLOADING;
        val DROP_TABLE_E_WORK = "DROP TABLE IF EXISTS " + TABLE_E_WORK;
        val DROP_TABLE_E_LOAD_HISTORY = "DROP TABLE IF EXISTS " + TABLE_E_LOAD_HISTORY;

        db?.execSQL(DROP_TABLE_MACHINES_HOURS)
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

    fun insertMachineHours(datum: MyData): Long {


        val db = this.writableDatabase
        var cv = ContentValues()


        val currentTime = System.currentTimeMillis()
        datum.stopTime = currentTime

        val time = System.currentTimeMillis()
        datum.time = time.toString()
        datum.date = myHelper.getDate(time.toString())

        cv.put(COL_ORG_ID, datum.orgId)
        cv.put(COL_SITE_ID, datum.siteId)
        cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
        cv.put(COL_MACHINE_ID, datum.machineId)
        cv.put(COL_MACHINE_NUMBER, datum.loadedMachineNumber)



        cv.put(COL_START_TIME, datum.startTime)
        cv.put(COL_END_TIME, datum.stopTime)

        cv.put(COL_START_HOURS, datum.startHours)
        cv.put(COL_IS_START_HOURS_CUSTOM, datum.isStartHoursCustom)
        cv.put(COL_TOTAL_HOURS, datum.totalHours)
        cv.put(COL_IS_TOTAL_HOURS_CUSTOM, datum.isTotalHoursCustom)

        if (datum.time != null)
            cv.put(COL_TIME, datum.time.toLong())
        cv.put(COL_DATE, datum.date)
        cv.put(COL_WORK_MODE, myHelper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.loadingGPSLocation)
        )
        cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, datum.isSync)

        val insertID = db.insert(TABLE_MACHINES_HOURS, null, cv)
        myHelper.log("MachinesHour:$datum")
        myHelper.log("MachinesHour:insertID:$insertID")
        return insertID;
    }

    fun insertMachinesHours(data: ArrayList<MyData>) {


        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_MACHINE_ID, datum.machineId)
            cv.put(COL_MACHINE_NUMBER, datum.loadedMachineNumber)



            cv.put(COL_START_TIME, datum.startTime)
            cv.put(COL_END_TIME, datum.stopTime)

            cv.put(COL_START_HOURS, datum.startHours)
            cv.put(COL_TOTAL_HOURS, datum.totalHours)

            if (datum.time != null)
                cv.put(COL_TIME, datum.time.toLong())
            cv.put(COL_DATE, datum.date)
            cv.put(COL_WORK_MODE, myHelper.getWorkMode())


            cv.put(
                COL_LOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(datum.loadingGPSLocation)
            )
            cv.put(
                COL_UNLOADING_GPS_LOCATION,
                myHelper.getGPSLocationToString(datum.unloadingGPSLocation)
            )
            cv.put(COL_USER_ID, myHelper.getUserID())
            cv.put(COL_IS_SYNC, datum.isSync)


            val insertID = db.insert(TABLE_MACHINES_HOURS, null, cv)
            myHelper.log("MachinesHour:$datum")
            myHelper.log("MachinesHour:insertID:$insertID")
        }
    }

    fun insertMachinesTasks(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
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

    fun insertMachinesPlants(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data) {
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

    fun insertMachinesBrands(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data) {
            cv.put(COL_ID, datum.id)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_BRANDS, null, cv)
            myHelper.log("insertMachinesBrands-inserID:$insertID")
        }
    }

    fun insertMachinesTypes(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data) {
            cv.put(COL_ID, datum.id)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_MACHINES_TYPES, null, cv)
            myHelper.log("insertMachinesTypes-inserID:$insertID")
        }
    }

    fun insertSites(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data) {
            cv.put(COL_ID, datum.siteId)
            cv.put(COL_NAME, datum.siteName)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_SITES, null, cv)
            myHelper.log("insertSites-inserID:$insertID")
        }
    }

    fun insertOperators(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()


        for (datum in data) {
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

    fun insertStopReasons(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
            cv.put(COL_ID, datum.id)
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_NAME, datum.name)
            cv.put(COL_STATUS, datum.status)
            cv.put(COL_IS_DELETED, datum.isDeleted)

            val insertID = db.replace(TABLE_STOP_REASONS, null, cv)
            myHelper.log("StopReasons-inserID:$insertID")
        }
    }

    fun insertMaterials(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
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

    fun insertMachines(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
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
//            myHelper.log("Machines--datum:${datum}")
            myHelper.log("Machines-inserID:$insertID")
        }
    }

    fun insertLocations(data: ArrayList<OperatorAPI>) {

        val db = this.writableDatabase
        var cv = ContentValues()

        for (datum in data) {
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
        cv.put(COL_ORG_ID, myData.orgId)
        cv.put(COL_SITE_ID, myData.siteId)
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
        cv.put(
            COL_UNLOADING_GPS_LOCATION,
            myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)


        val insertID = db.insert(TABLE_MACHINE_STATUS, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }

    fun insertTrip(myData: MyData): Long {



        val currentTime = System.currentTimeMillis()

        myData.startTime = currentTime
        myData.stopTime = currentTime
        myData.totalTime = currentTime - myData.startTime

        val time = System.currentTimeMillis()
        myData.time = time.toString()
        myData.date = myHelper.getDate(time.toString())
        myData.loadedMachineType = myHelper.getMachineTypeID()
        myData.loadedMachineNumber = myHelper.getMachineNumber()


        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ORG_ID, myData.orgId)
        cv.put(COL_SITE_ID, myData.siteId)
        cv.put(COL_TRIP_TYPE, myData.tripType)
        cv.put(COL_TRIP0_ID, myData.trip0ID)
        cv.put(COL_MACHINE_TYPE_ID, myData.loadedMachineType)
        cv.put(COL_MACHINE_NUMBER, myData.loadedMachineNumber)
        cv.put(COL_LOADED_MACHINE, myData.loadedMachine)

//        Back Load saving as Loading item with Trip 0 ID
        if(myData.nextAction == 2){
            cv.put(COL_LOADING_MACHINE, myData.backLoadingMachine)
            cv.put(COL_LOADING_MACHINE_ID, myData.back_loading_machine_id)

            cv.put(COL_LOADING_MATERIAL, myData.backLoadingMaterial)
            cv.put(COL_LOADING_MATERIAL_ID, myData.back_loading_material_id)

            cv.put(COL_LOADING_LOCATION, myData.backLoadingLocation)
            cv.put(COL_LOADING_LOCATION_ID, myData.back_loading_location_id)

        }else{
            cv.put(COL_LOADING_MACHINE, myData.loadingMachine)
            cv.put(COL_LOADING_MACHINE_ID, myData.loading_machine_id)

            cv.put(COL_LOADING_MATERIAL, myData.loadingMaterial)
            cv.put(COL_LOADING_MATERIAL_ID, myData.loading_material_id)

            cv.put(COL_LOADING_LOCATION, myData.loadingLocation)
            cv.put(COL_LOADING_LOCATION_ID, myData.loading_location_id)
        }


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
        cv.put(
            COL_UNLOADING_GPS_LOCATION,
            myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)

        myHelper.log("BeforeInsertTrip--:$myData")

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
        cv.put(COL_ORG_ID, eWork.orgId)
        cv.put(COL_SITE_ID, eWork.siteId)
        cv.put(COL_MACHINE_TYPE_ID, myHelper.getMachineTypeID())
        cv.put(COL_MACHINE_NUMBER, myHelper.getMachineNumber())
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
        cv.put(COL_ORG_ID, eWork.orgId)
        cv.put(COL_SITE_ID, eWork.siteId)
        cv.put(COL_EWORK_ID, eWork.eWorkId)
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
        cv.put(COL_ORG_ID, eWork.orgId)
        cv.put(COL_SITE_ID, eWork.siteId)
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
        cv.put(COL_ORG_ID, myData.orgId)
        cv.put(COL_SITE_ID, myData.siteId)
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
        cv.put(
            COL_UNLOADING_GPS_LOCATION,
            myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, myHelper.getUserID())
        cv.put(COL_IS_SYNC, myData.isSync)
        val insertID = db.insert(TABLE_E_LOAD_HISTORY, null, cv)
        myHelper.log("insertID:$insertID")
        return insertID
    }

    fun getMachineHours(machineId: Int): MyData {


        val db = this.readableDatabase
        val query = "Select * from $TABLE_MACHINES_HOURS WHERE $COL_MACHINE_ID = $machineId ORDER BY $COL_ID DESC LIMIT 1"
        val result = db.rawQuery(query, null)
        var datum = MyData()
        if (result.moveToFirst()) {

            datum.id = result.getInt(result.getColumnIndex(COL_ID))
            datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
            datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))

            datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
            datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
            datum.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

            datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
            datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
//                myHelper.log("DB_startTime:${result.getLong(result.getColumnIndex(COL_START_TIME))}")
//                myHelper.log("DB_StartHour:${result.getString(result.getColumnIndex(COL_START_HOURS))}")
            if (result.getString(result.getColumnIndex(COL_START_HOURS)) != null)
                datum.startHours = result.getString(result.getColumnIndex(COL_START_HOURS))

            if (result.getString(result.getColumnIndex(COL_TOTAL_HOURS)) != null)
                datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_HOURS))

            datum.isStartHoursCustom = result.getInt(result.getColumnIndex(COL_IS_START_HOURS_CUSTOM))
            datum.isTotalHoursCustom = result.getInt(result.getColumnIndex(COL_IS_TOTAL_HOURS_CUSTOM))

            datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
            datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
            datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

            datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                result.getString(
                    result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                )
            )
            datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                result.getString(
                    result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                )
            )

            datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
            datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
            datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return datum
    }

    fun getMachinesHours(): ArrayList<MyData> {

        var list: ArrayList<MyData> = ArrayList()

        val db = this.readableDatabase

        val query =
            "Select * from $TABLE_MACHINES_HOURS  ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {

                var datum = MyData()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))

                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
                datum.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

                datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))

                if (result.getString(result.getColumnIndex(COL_START_HOURS)) != null)
                    datum.startHours = result.getString(result.getColumnIndex(COL_START_HOURS))

                if (result.getString(result.getColumnIndex(COL_TOTAL_HOURS)) != null)
                    datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_HOURS))


                datum.isStartHoursCustom = result.getInt(result.getColumnIndex(COL_IS_START_HOURS_CUSTOM))
                datum.isTotalHoursCustom = result.getInt(result.getColumnIndex(COL_IS_TOTAL_HOURS_CUSTOM))

                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )

                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getMachinesTasks(): ArrayList<OperatorAPI> {

        var list: ArrayList<OperatorAPI> = ArrayList()
        val db = this.readableDatabase

        val query =
            "Select * from ${TABLE_MACHINES_TASKS} WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_MACHINE_TYPE_ID} = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from ${TABLE_MACHINES_PLANTS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from ${TABLE_MACHINES_BRANDS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from ${TABLE_MACHINES_TYPES} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from ${TABLE_OPERATORS} WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
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

    fun getOperatorByID(id: String): OperatorAPI {

        var datum = OperatorAPI()

        val db = this.readableDatabase

        val query =
            "Select * from ${TABLE_OPERATORS} WHERE ${COL_ID} =? ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, arrayOf(id))


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

    fun getOperator(pin: String): OperatorAPI {

        var datum = OperatorAPI()

        val db = this.readableDatabase

        val query =
            "Select * from ${TABLE_OPERATORS} WHERE ${COL_PIN} =? AND ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from $TABLE_STOP_REASONS  WHERE ${COL_IS_DELETED} = 0 ORDER BY $COL_ID DESC"
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

        val query =
            "Select * from $TABLE_MATERIALS  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1  AND ${COL_MACHINE_TYPE_ID} = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
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
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
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

    fun getMachines(type: Int = 0): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase
        var query = ""
        if (type < 1) {
            query =
                "Select * from $TABLE_MACHINES  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_ID DESC"
        } else {
            query =
                "Select * from $TABLE_MACHINES  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} AND $COL_MACHINE_TYPE_ID = ${type} ORDER BY $COL_ID DESC"
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
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
                datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_TIME))
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

    fun getMachine(id: Int): Material {

        val db = this.readableDatabase
        var query = ""

        query =
            "Select * from $TABLE_MACHINES  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} AND $COL_ID = ${id} ORDER BY $COL_ID DESC"
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
        var datum = Material()

        if (result.moveToFirst()) {
            do {

                myHelper.log("Result:${result.toString()}")
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
                datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_TIME))
                datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
                datum.status = result.getInt(result.getColumnIndex(COL_STATUS))


            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return datum
    }

    fun getLocations(): ArrayList<Material> {

        var list: ArrayList<Material> = ArrayList()
        val db = this.readableDatabase

        val query =
            "Select * from $TABLE_LOCATIONS  WHERE ${COL_IS_DELETED} = 0 AND ${COL_STATUS} = 1 AND ${COL_SITE_ID} = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var datum = Material()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
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
                var datum = EWork()
                datum.ID = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

                datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))

                list.add(datum)
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
                var datum = EWork()
                datum.ID = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.eWorkId = result.getInt(result.getColumnIndex(COL_EWORK_ID))

                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(datum)
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
                var datum = EWork()
                datum.ID = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.workType = result.getInt(result.getColumnIndex(COL_EWORK_TYPE))
                datum.workActionType = result.getInt(result.getColumnIndex(COL_EWORK_ACTION_TYPE))
                datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(datum)
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
                var datum = MyData()
                datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                datum.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                datum.loadingMaterial =
                    result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                datum.loadingLocation =
                    result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                datum.unloadingWeight =
                    result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))


                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getTrip(id: Long): MyData {

        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
            "Select * from $TABLE_TRIP  WHERE $COL_ID = ${id} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        var datum = MyData()
        if (result.moveToFirst()) {

                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
                datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
                datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
                datum.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.loadedMachineNumber =
                    result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
                datum.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                datum.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))

                datum.loading_machine_id = result.getInt(result.getColumnIndex(COL_LOADING_MACHINE_ID))

                datum.loadingMaterial = result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                datum.loading_material_id = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))

                datum.loadingLocation = result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                datum.loading_location_id = result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))

                datum.unloadingWeight = result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                datum.unloadingTask = result.getString(result.getColumnIndex(COL_UNLOADING_TASK))
                datum.unloading_task_id = result.getInt(result.getColumnIndex(COL_UNLOADING_TASK_ID))

                datum.unloadingMaterial = result.getString(result.getColumnIndex(COL_UNLOADING_MATERIAL))
                datum.unloading_material_id= result.getInt(result.getColumnIndex(COL_UNLOADING_MATERIAL_ID))

                datum.unloadingLocation = result.getString(result.getColumnIndex(COL_UNLOADING_LOCATION))
                datum.unloading_location_id = result.getInt(result.getColumnIndex(COL_UNLOADING_LOCATION_ID))

                datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return datum
    }

    fun getTrips(): MutableList<MyData> {

        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
            "Select * from $TABLE_TRIP  WHERE $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)



        if (result.moveToFirst()) {
            do {
                var datum = MyData()
                datum.id = result.getInt(result.getColumnIndex(COL_ID))
                datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
                datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
                datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
                datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
                datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
                datum.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
                datum.loadedMachineNumber =
                    result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
                datum.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                datum.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                datum.loadingMaterial =
                    result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                datum.loadingLocation =
                    result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                datum.unloadingWeight =
                    result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                datum.unloadingTask = result.getString(result.getColumnIndex(COL_UNLOADING_TASK))
                datum.unloadingMaterial =
                    result.getString(result.getColumnIndex(COL_UNLOADING_MATERIAL))
                datum.unloadingLocation =
                    result.getString(result.getColumnIndex(COL_UNLOADING_LOCATION))

                datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                    )
                )
                datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                    result.getString(
                        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                    )
                )
                datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
                datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(datum)
            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getMachineStatus(id: Long = 0): MyData {

//        var list: MutableList<MyData> = ArrayList()
        val db = this.readableDatabase

        val query =
            "Select * from $TABLE_MACHINE_STATUS  WHERE ${COL_ID} = ${id} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        var datum = MyData()

        if (result.moveToFirst()) {
//            do {
            datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
            datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
            datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
            datum.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
            datum.loadedMachineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
            datum.machineStoppedReason =
                result.getString(result.getColumnIndex(COL_MACHINE_STOPPED_REASON))


            datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
            datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
            datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

            datum.time = myHelper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
            datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
            datum.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

            datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
                result.getString(
                    result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                )
            )
            datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
                result.getString(
                    result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                )
            )
            datum.operatorId = result.getInt(result.getColumnIndex(COL_USER_ID))
            datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
//                list.add(datum)
//            } while (result.moveToNext())
        } else {
            myHelper.log("else result:$result")
        }

        result.close()
        db.close()
        return datum
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

//        back unloading
        if(myData.nextAction == 3){
            cv.put(COL_UNLOADING_TASK, myData.backUnloadingTask)
            cv.put(COL_UNLOADING_TASK_ID, myData.back_unloading_task_id)

            cv.put(COL_UNLOADING_MATERIAL, myData.backUnloadingMaterial)
            cv.put(COL_UNLOADING_MATERIAL_ID, myData.back_unloading_material_id)

            cv.put(COL_UNLOADING_LOCATION, myData.backUnloadingLocation)
            cv.put(COL_UNLOADING_LOCATION_ID, myData.back_unloading_location_id)
        }else{
            cv.put(COL_UNLOADING_TASK, myData.unloadingTask)
            cv.put(COL_UNLOADING_TASK_ID, myData.unloading_task_id)

            cv.put(COL_UNLOADING_MATERIAL, myData.unloadingMaterial)
            cv.put(COL_UNLOADING_MATERIAL_ID, myData.unloading_material_id)

            cv.put(COL_UNLOADING_LOCATION, myData.unloadingLocation)
            cv.put(COL_UNLOADING_LOCATION_ID, myData.unloading_location_id)
        }



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
        cv.put(
            COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, myData.isSync)

        val updatedID = db.update(TABLE_MACHINE_STATUS, cv, "$COL_ID = ${myData.recordID}", null)

        myHelper.log("updatedID :$updatedID ")
        return updatedID

    }
}