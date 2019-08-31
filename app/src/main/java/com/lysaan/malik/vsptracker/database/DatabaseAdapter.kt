package com.lysaan.malik.vsptracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.classes.Data
import com.lysaan.malik.vsptracker.classes.EWork

const val DATABASE_NAME = "vsptracker"
const val TABLE_E_LOAD_HISTORY = "e_load_history"
const val TABLE_E_WORK = "ework"
const val TABLE_E_WORK_ACTION_OFFLOADING = "ework_action_loading"
const val TABLE_DELAY = "t_wait"
const val TABLE_TRIP = "ttrip_simple"
const val TABLE_MACHINE_STATUS = "machine_status"

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
const val COL_MACHINE_TYPE = "machine_type"

const val COL_LOADING_GPS_LOCATION = "loading_gps_location"
const val COL_UNLOADING_GPS_LOCATION = "unloading_gps_location"

const val COL_ID = "id"
const val COL_USER_ID = "user_id"

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

// isSync 0 = Not Uploaded to Server
// isSync 1 = Uploaded to Server
// isSync 2 = Uploaded to Server by Export
const val COL_IS_SYNC = "is_sync"


class DatabaseAdapter(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 3) {

    val TAG = "DatabaseAdapter"
    private var helper: Helper

    init {
        this.helper = Helper(TAG, context)
    }

    override fun onCreate(db: SQLiteDatabase?) {

//        val createHourMeterTable ="CREATE TABLE $TABLE_HOUR_METER ( " +
//                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "$COL_START_TIME INTEGER, " +
//                "$COL_STOP_TIME INTEGER" +
//                ")"

        val createMachineStatusTable = "CREATE TABLE  $TABLE_MACHINE_STATUS (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_MACHINE_TYPE  INTEGER," +
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
                "$COL_MACHINE_TYPE  INTEGER," +
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
                "$COL_MACHINE_TYPE INTEGER, " +
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
                "$COL_MACHINE_TYPE  INTEGER," +
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

        db?.execSQL(createMachineStatusTable)
        db?.execSQL(createTripTable)
        db?.execSQL(createTWaitTable)
        db?.execSQL(createEWorkActionOffloadingTable)
        db?.execSQL(createEWorkTable)
        db?.execSQL(createLoadHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val DROP_TABLE_TRIP = "DROP TABLE IF EXISTS " + TABLE_TRIP;
        val DROP_TABLE_WAIT = "DROP TABLE IF EXISTS " + TABLE_DELAY;
        val DROP_TABLE_E_WORK_ACTION_OFFLOADING = "DROP TABLE IF EXISTS " + TABLE_E_WORK_ACTION_OFFLOADING;
        val DROP_TABLE_E_WORK = "DROP TABLE IF EXISTS " + TABLE_E_WORK;
        val DROP_TABLE_E_LOAD_HISTORY = "DROP TABLE IF EXISTS " + TABLE_E_LOAD_HISTORY;

        db?.execSQL(DROP_TABLE_TRIP)
        db?.execSQL(DROP_TABLE_WAIT)
        db?.execSQL(DROP_TABLE_E_WORK_ACTION_OFFLOADING)
        db?.execSQL(DROP_TABLE_E_WORK)
        db?.execSQL(DROP_TABLE_E_LOAD_HISTORY)
        onCreate(db)

    }

    fun insertMachineStatus(data: Data): Long {


        val currentTime = System.currentTimeMillis()

        data.startTime = currentTime

        val time = System.currentTimeMillis()
        data.time = time.toString()
        data.date = helper.getDate(time.toString())
        data.loadedMachineType = helper.getMachineType()
        data.loadedMachineNumber = helper.getMachineNumber()

        helper.log("insertMachineStatus:$data")


        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_MACHINE_TYPE, data.loadedMachineType)
        cv.put(COL_MACHINE_NUMBER, data.loadedMachineNumber)
        cv.put(COL_MACHINE_STOPPED_REASON, data.machineStoppedReason)

        cv.put(COL_START_TIME, data.startTime)
        cv.put(COL_END_TIME, data.stopTime)
        cv.put(COL_TOTAL_TIME, data.totalTime)

        cv.put(COL_TIME, data.time.toLong())
        cv.put(COL_DATE, data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(data.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, helper.getGPSLocationToString(data.unloadingGPSLocation))
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, data.isSync)


        val insertID = db.insert(TABLE_MACHINE_STATUS, null, cv)
        helper.log("insertID:$insertID")
        return insertID
    }

    fun insertTrip(data: Data): Long {


        val currentTime = System.currentTimeMillis()

        data.startTime = currentTime
//        data.stopTime = currentTime
//        data.totalTime = currentTime - data.startTime

        val time = System.currentTimeMillis()
        data.time = time.toString()
        data.date = helper.getDate(time.toString())
        data.loadedMachineType = helper.getMachineType()
        data.loadedMachineNumber = helper.getMachineNumber()

        helper.log("insertTrip:$data")

        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE, data.loadingMachine)
        cv.put(COL_TRIP_TYPE, data.tripType)
        cv.put(COL_TRIP0_ID, data.trip0ID)
        cv.put(COL_MACHINE_TYPE, data.loadedMachineType)
        cv.put(COL_MACHINE_NUMBER, data.loadedMachineNumber)
        cv.put(COL_LOADED_MACHINE, data.loadedMachine)
        cv.put(COL_LOADING_MATERIAL, data.loadingMaterial)
        cv.put(COL_LOADING_LOCATION, data.loadingLocation)

        cv.put(COL_UNLOADING_TASK, data.unloadingTask)
        cv.put(COL_UNLOADING_MATERIAL, data.unloadingMaterial)
        cv.put(COL_UNLOADING_LOCATION, data.unloadingLocation)
        cv.put(COL_UNLOADING_WEIGHT, data.unloadingWeight)

        cv.put(COL_START_TIME, data.startTime)
        cv.put(COL_END_TIME, data.stopTime)
        cv.put(COL_TOTAL_TIME, data.totalTime)

        cv.put(COL_TIME, data.time.toLong())
        cv.put(COL_DATE, data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(data.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, helper.getGPSLocationToString(data.unloadingGPSLocation))
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, data.isSync)


        val insertID = db.insert(TABLE_TRIP, null, cv)
        helper.log("insertID:$insertID")
        return insertID
    }

    fun insertDelay(eWork: EWork): Long {

        val time = System.currentTimeMillis()

        eWork.stopTime = time
        eWork.totalTime = eWork.stopTime - eWork.startTime
        eWork.time = time.toString()
        eWork.date = helper.getDate(time)

        helper.log("insertDelay:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_MACHINE_TYPE, helper.getMachineType())
        cv.put(COL_MACHINE_NUMBER, helper.getMachineNumber())
        cv.put(COL_START_TIME, eWork.startTime)
        cv.put(COL_END_TIME, eWork.stopTime)
        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())

        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION,helper.getGPSLocationToString(eWork.unloadingGPSLocation))
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)

        val insertID = db.insert(TABLE_DELAY, null, cv)
        helper.log("insertID:$insertID")
        return insertID
    }

    fun insertEWorkOffLoad(eWork: EWork): Long {
        val time = System.currentTimeMillis()
        eWork.time = time.toString()
        eWork.date = helper.getDate(time)

        helper.log("insertEWorkOffLoad:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_EWORK_ID, eWork.eWorkID)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())

        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                helper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)

        val insertID = db.insert(TABLE_E_WORK_ACTION_OFFLOADING, null, cv)
        helper.log("insertID:$insertID")
        return insertID
    }

    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    fun insertEWork(eWork: EWork): Long {


        val currentTime = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime - eWork.startTime

        eWork.time = currentTime.toString()
        eWork.date = helper.getDate(currentTime)

        helper.log("insertEWork:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_EWORK_TYPE, eWork.workType)
        cv.put(COL_EWORK_ACTION_TYPE, eWork.workActionType)
        cv.put(COL_START_TIME, eWork.startTime)

        cv.put(COL_END_TIME, eWork.stopTime)

        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(eWork.loadingGPSLocation))
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                helper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, eWork.isSync)
        val insertID = db.insert(TABLE_E_WORK, null, cv)
        helper.log("insertID:$insertID")
        return insertID

    }

    fun insertELoad(data: Data): Long {

        val time = System.currentTimeMillis()
        data.time = time.toString()
        data.date = helper.getDate(time.toString())
        data.loadedMachineType = helper.getMachineType()

        helper.log("insertELoad:$data")

        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE, data.loadingMachine)
        cv.put(COL_MACHINE_TYPE, data.loadedMachineType)
        cv.put(COL_LOADED_MACHINE, data.loadedMachine)
        cv.put(COL_LOADING_MATERIAL, data.loadingMaterial)
        cv.put(COL_LOADING_LOCATION, data.loadingLocation)
        cv.put(COL_UNLOADING_WEIGHT, data.unloadingWeight)

        cv.put(COL_TIME, data.time.toLong())
        cv.put(COL_DATE, data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())


        cv.put(COL_LOADING_GPS_LOCATION, helper.getGPSLocationToString(data.loadingGPSLocation))
        cv.put(COL_UNLOADING_GPS_LOCATION, helper.getGPSLocationToString(data.unloadingGPSLocation))
        cv.put(COL_USER_ID, helper.getUserID())
        cv.put(COL_IS_SYNC, data.isSync)
        val insertID = db.insert(TABLE_E_LOAD_HISTORY, null, cv)
        helper.log("insertID:$insertID")
        return insertID
    }

    fun getWaits(): MutableList<EWork> {

        var list: MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_DELAY  WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.machineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                eWork.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

                eWork.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                eWork.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                eWork.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))

                list.add(eWork)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
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

                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(eWork)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
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
                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                eWork.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                eWork.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                eWork.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                eWork.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(eWork)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getELoadHistroy(): MutableList<Data> {

        var list: MutableList<Data> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_E_LOAD_HISTORY WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var data = Data()
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                data.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                data.loadingMachine = result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                data.loadingMaterial =
                        result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                data.loadingLocation =
                        result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                data.unloadingWeight =
                        result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
                data.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))


                data.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(data)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getTrips(): MutableList<Data> {

        var list: MutableList<Data> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_TRIP  WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)



        if (result.moveToFirst()) {
            do {
                var data = Data()
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
                data.trip0ID = result.getInt(result.getColumnIndex(COL_TRIP0_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
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

                data.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                data.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(data)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getMachineStatus(): MutableList<Data> {

        var list: MutableList<Data> = ArrayList()
        val db = this.readableDatabase

        val query =
                "Select * from $TABLE_MACHINE_STATUS  WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query, null)


        if (result.moveToFirst()) {
            do {
                var data = Data()
                data.recordID = result.getLong(result.getColumnIndex(COL_ID))
                data.loadedMachineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                data.loadedMachineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))
                data.machineStoppedReason = result.getString(result.getColumnIndex(COL_MACHINE_STOPPED_REASON))


                data.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                data.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                data.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                data.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                data.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))

                data.loadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_LOADING_GPS_LOCATION)
                        )
                )
                data.unloadingGPSLocation = helper.getStringToGPSLocation(
                        result.getString(
                                result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
                        )
                )
                data.userID = result.getString(result.getColumnIndex(COL_USER_ID))
                data.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
                list.add(data)
            } while (result.moveToNext())
        } else {
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }


    fun updateEWork(eWork: EWork): Int {


        val currentTime = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime - eWork.startTime

        eWork.time = currentTime.toString()
        eWork.date = helper.getDate(currentTime)

        helper.log("updateEWork:$eWork")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_END_TIME, eWork.stopTime)
        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                helper.getGPSLocationToString(eWork.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, eWork.isSync)
        val updatedID = db.update(TABLE_E_WORK, cv, "$COL_ID = ${eWork.ID}", null)

        helper.log("updatedID :$updatedID ")
        return updatedID

    }

    fun updateTrip(data: Data): Int {


        val currentTime = System.currentTimeMillis()

        data.stopTime = currentTime
        data.totalTime = currentTime - data.startTime

        data.time = currentTime.toString()
        data.date = helper.getDate(currentTime)

        helper.log("updateTrip:$data")

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_UNLOADING_WEIGHT, data.unloadingWeight)
        cv.put(COL_UNLOADING_TASK, data.unloadingTask)
        cv.put(COL_UNLOADING_MATERIAL, data.unloadingMaterial)
        cv.put(COL_UNLOADING_LOCATION, data.unloadingLocation)
        cv.put(COL_END_TIME, data.stopTime)
        cv.put(COL_TOTAL_TIME, data.totalTime)
        cv.put(COL_TIME, data.time.toLong())
        cv.put(COL_DATE, data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())
        cv.put(
                COL_UNLOADING_GPS_LOCATION,
                helper.getGPSLocationToString(data.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, data.isSync)

        val updatedID = db.update(TABLE_TRIP, cv, "$COL_ID = ${data.recordID}", null)

        helper.log("updatedID :$updatedID ")
        return updatedID

    }

    fun updateMachineStatus(data: Data): Int {

        val currentTime = System.currentTimeMillis()

        data.stopTime = currentTime
        data.totalTime = currentTime - data.startTime

        data.time = currentTime.toString()
        data.date = helper.getDate(currentTime)

        helper.log("updateTrip:$data")

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_END_TIME, data.stopTime)
        cv.put(COL_TOTAL_TIME, data.totalTime)
        cv.put(COL_TIME, data.time.toLong())
        cv.put(COL_DATE, data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())
        cv.put(COL_UNLOADING_GPS_LOCATION, helper.getGPSLocationToString(data.unloadingGPSLocation)
        )
        cv.put(COL_IS_SYNC, data.isSync)

        val updatedID = db.update(TABLE_MACHINE_STATUS, cv, "$COL_ID = ${data.recordID}", null)

        helper.log("updatedID :$updatedID ")
        return updatedID

    }
}