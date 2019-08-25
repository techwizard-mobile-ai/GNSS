package com.lysaan.malik.vsptracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.others.Data
import com.lysaan.malik.vsptracker.others.EWork

const val DATABASE_NAME ="vsptracker"
const val TABLE_LOAD_HISTORY="load_history"
const val TABLE_LAST_JOURNEY="last_journey"
const val TABLE_EWORK = "ework"
const val TABLE_EWORK_ACTION_OFFLOADING = "ework_action_loading"
const val TABLE_TWAIT = "t_wait"

const val COL_TIME = "time"
const val COL_DATE = "date"
const val COL_LOADED_MACHINE = "loaded_machine"
const val COL_LOADING_MACHINE = "loading_machine"
const val COL_LOADING_MATERIAL = "loading_material"
const val COL_LOADING_LOCATION = "loading_location"
const val COL_LOADING_WEIGHT = "loading_weight"
const val COL_UNLOADING_TASK = "unloading_task"
const val COL_UNLOADING_MATERIAL = "unloading_material"
const val COL_UNLOADING_MACHINE = "unloading_machine"
const val COL_UNLOADING_LOCATION = "unloading_location"
const val COL_MACHINE_TYPE = "machine_type"
const val COL_LAT = "lat"
const val COL_LONG = "longg"
const val COL_ID = "id"

const val COL_START_TIME = "start_time"
const val COL_END_TIME = "end_time"
const val COL_TOTAL_TIME = "total_time"

const val COL_EWORK_TYPE = "ework_type"
const val COL_EWORK_ACTION_TYPE = "ework_action_type"
const val COL_EWORK_ID = "ework_id"

const val COL_WORK_MODE = "work_mode"
const val COL_MACHINE_NUMBER = "machine_number"


class DatabaseAdapter(var context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,1){

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

        val createTWaitTable = "CREATE TABLE $TABLE_TWAIT ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_MACHINE_TYPE INTEGER, " +
                "$COL_MACHINE_NUMBER TEXT, " +
                "$COL_START_TIME INTEGER, " +
                "$COL_END_TIME INTEGER, " +
                "$COL_TOTAL_TIME INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"
        
        val createEWorkActionOffloadingTable = "CREATE TABLE $TABLE_EWORK_ACTION_OFFLOADING ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_EWORK_ID INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_WORK_MODE TEXT, " +
                "FOREIGHT KEY $COL_EWORK_ID REFERENCES $TABLE_EWORK($COL_ID)" +
                ")"

        val createEWorkTable ="CREATE TABLE $TABLE_EWORK ( " +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_EWORK_TYPE INTEGER, " +
                "$COL_EWORK_ACTION_TYPE INTEGER, " +
                "$COL_START_TIME INTEGER, " +
                "$COL_END_TIME INTEGER, " +
                "$COL_TOTAL_TIME INTEGER, " +
                "$COL_DATE TEXT, " +
                "$COL_TIME INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createLoadHistoryTable = "CREATE TABLE " + TABLE_LOAD_HISTORY +" (" +
                "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_MACHINE_TYPE  INTEGER," +
                "$COL_LOADING_MACHINE  TEXT," +
                "$COL_LOADED_MACHINE  TEXT," +
                "$COL_LOADING_MATERIAL  TEXT," +
                "$COL_LOADING_LOCATION  TEXT," +
                "$COL_LOADING_WEIGHT  REAL," +
                "$COL_DATE  TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                ")"

        val createLastJourneyTable = "CREATE TABLE $TABLE_LAST_JOURNEY (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_MACHINE_TYPE  INTEGER," +
                "$COL_LOADING_MACHINE  TEXT," +
                "$COL_LOADING_MATERIAL  TEXT," +
                "$COL_LOADING_WEIGHT  REAL," +
                "$COL_LOADING_LOCATION  TEXT," +
                "$COL_UNLOADING_TASK TEXT," +
                "$COL_UNLOADING_MATERIAL TEXT," +
                "$COL_UNLOADING_MACHINE TEXT," +
                "$COL_UNLOADING_LOCATION TEXT," +
                "$COL_TIME  INTEGER," +
                "$COL_WORK_MODE TEXT" +
                " )"

        db?.execSQL(createTWaitTable)
        db?.execSQL(createEWorkActionOffloadingTable)
        db?.execSQL(createEWorkTable)
        db?.execSQL(createLoadHistoryTable)
        db?.execSQL(createLastJourneyTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion: Int,newVersion: Int) {

    }



    fun insertJourney(data: Data) :Long {

        val time  = System.currentTimeMillis()
        data.time = time.toString()
        data.date = helper.getDate(time.toString())
        data.machineType = helper.getMachineType()


        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE,data.loadingMachine)
        cv.put(COL_MACHINE_TYPE,data.machineType)
        cv.put(COL_LOADING_MATERIAL,data.loadingMaterial)
        cv.put(COL_LOADING_LOCATION,data.loadingLocation)
        cv.put(COL_LOADING_WEIGHT,data.loadedWeight)
        cv.put(COL_UNLOADING_TASK,data.unloadingTask)
        cv.put(COL_UNLOADING_MATERIAL,data.unloadingMaterial)
        cv.put(COL_UNLOADING_MACHINE,data.unloadingMachine)
        cv.put(COL_UNLOADING_LOCATION,data.unloadingLocation)
        cv.put(COL_TIME,data.time.toLong())

        var insertID  : Long
        insertID = db.insert(TABLE_LAST_JOURNEY,null,cv)

        helper.log("insertJourney:$insertID")
        return  insertID
    }

    fun insertTWait(eWork: EWork): Long {

        val time  = System.currentTimeMillis()

        eWork.stopTime = time
        eWork.totalTime = eWork.stopTime - eWork.startTime
        eWork.time = time.toString()
        eWork.date = helper.getDate(time)

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

        val insertID = db.insert(TABLE_TWAIT,null,cv)
        helper.log("insertID:$insertID"   )
        return  insertID
    }

    fun insertEWorkOffLoad(eWork : EWork): Long {
        val time  = System.currentTimeMillis()
        eWork.time = time.toString()
        eWork.date = helper.getDate(time)

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_EWORK_ID, eWork.eWorkID)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())

        val insertID = db.insert(TABLE_EWORK_ACTION_OFFLOADING,null,cv)
        helper.log("insertID:$insertID"   )
        return  insertID
    }
    
    // eWorkType 1 = General Digging
    // eWorkType 2 = Trenching
    // eWorkActionType 1 = Side Casting
    // eWorkActionType 2 = Off Loading
    fun insertEWork(eWork : EWork): Long {


        val currentTime  = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime   - eWork.startTime

        eWork.time = currentTime  .toString()
        eWork.date = helper.getDate(currentTime  )

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

        val insertID = db.insert(TABLE_EWORK,null,cv)
        helper.log("insertID:$insertID")
        return  insertID

    }
    fun insertLoad(data: Data): Long {

        val time  = System.currentTimeMillis()
        data.time = time.toString()
        data.date = helper.getDate(time.toString())
        data.machineType = helper.getMachineType()

        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_LOADING_MACHINE,data.loadingMachine)
        cv.put(COL_MACHINE_TYPE,data.machineType)
        cv.put(COL_LOADED_MACHINE,data.loadedMachine)
        cv.put(COL_LOADING_MATERIAL,data.loadingMaterial)
        cv.put(COL_LOADING_LOCATION,data.loadingLocation)
        cv.put(COL_LOADING_WEIGHT,data.loadedWeight)
        cv.put(COL_TIME,data.time.toLong())
        cv.put(COL_DATE,data.date)
        cv.put(COL_WORK_MODE, helper.getWorkMode())

        val insertID = db.insert(TABLE_LOAD_HISTORY,null,cv)
        helper.log("insertID:$insertID")
        return  insertID
    }

    fun getLastJourney() : MutableList<Data>{
        var list : MutableList<Data> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_LAST_JOURNEY WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID ASC"
        val result = db.rawQuery(query,null)

        if(result.moveToLast()){
            do {
                var myData = Data()
                myData.recordID = result.getString(result.getColumnIndex(COL_ID))
                myData.machineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                myData.loadingMachine= result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                myData.loadingMaterial= result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                myData.loadingLocation = result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                myData.loadedWeight = result.getDouble(result.getColumnIndex(COL_LOADING_WEIGHT))
                myData.unloadingTask = result.getString(result.getColumnIndex(COL_UNLOADING_TASK))
                myData.unloadingMaterial = result.getString(result.getColumnIndex(COL_UNLOADING_MATERIAL))
                myData.unloadingMachine = result.getString(result.getColumnIndex(COL_UNLOADING_MACHINE))
                myData.unloadingLocation = result.getString(result.getColumnIndex(COL_UNLOADING_LOCATION))

                myData.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))

                list.add(myData)
            }while (result.moveToNext())
        }else{
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }


    fun getTWaits() : MutableList<EWork>{

        var list : MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_TWAIT  WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query,null)


        if(result.moveToFirst()){
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.machineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                eWork.machineNumber = result.getString(result.getColumnIndex(COL_MACHINE_NUMBER))

                eWork.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                eWork.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                eWork.totalTime= result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
                list.add(eWork)
            }while (result.moveToNext())
        }else{
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getEWorksOffLoads(eWorkID: Int) : MutableList<EWork>{

        var list : MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_EWORK_ACTION_OFFLOADING WHERE ${COL_EWORK_ID} = ${eWorkID} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query,null)


        if(result.moveToFirst()){
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.eWorkID = result.getInt(result.getColumnIndex(COL_EWORK_ID))

                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
                list.add(eWork)
            }while (result.moveToNext())
        }else{
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
    fun getEWorks(workType : Int) : MutableList<EWork>{

        var list : MutableList<EWork> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_EWORK WHERE ${COL_EWORK_TYPE} = ${workType} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query,null)


        if(result.moveToFirst()){
            do {
                var eWork = EWork()
                eWork.ID = result.getInt(result.getColumnIndex(COL_ID))
                eWork.workType = result.getInt(result.getColumnIndex(COL_EWORK_TYPE))
                eWork.workActionType = result.getInt(result.getColumnIndex(COL_EWORK_ACTION_TYPE))

                eWork.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
                eWork.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
                eWork.totalTime= result.getLong(result.getColumnIndex(COL_TOTAL_TIME))

                eWork.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                eWork.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
                list.add(eWork)
            }while (result.moveToNext())
        }else{
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

    fun getLoadHistroy(): MutableList<Data>{

        var list : MutableList<Data> = ArrayList()
        val db = this.readableDatabase

        val query = "Select * from $TABLE_LOAD_HISTORY WHERE ${COL_MACHINE_TYPE} = ${helper.getMachineType()} ORDER BY $COL_ID DESC"
        val result = db.rawQuery(query,null)

        if(result.moveToFirst()){
            do {
                var myData = Data()
                myData.recordID = result.getString(result.getColumnIndex(COL_ID))
                myData.machineType = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE))
                myData.loadedMachine = result.getString(result.getColumnIndex(COL_LOADED_MACHINE))
                myData.loadingMachine= result.getString(result.getColumnIndex(COL_LOADING_MACHINE))
                myData.loadingMaterial= result.getString(result.getColumnIndex(COL_LOADING_MATERIAL))
                myData.loadingLocation = result.getString(result.getColumnIndex(COL_LOADING_LOCATION))
                myData.loadedWeight = result.getDouble(result.getColumnIndex(COL_LOADING_WEIGHT))
                myData.time = helper.getTime(result.getLong(result.getColumnIndex(COL_TIME)))
                myData.date = helper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
                myData.workMode = result.getString(result.getColumnIndex(COL_WORK_MODE))
                list.add(myData)
            }while (result.moveToNext())
        }else{
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }



    fun updateEWork(eWork : EWork): Int {


        val currentTime  = System.currentTimeMillis()

        eWork.stopTime = currentTime
        eWork.totalTime = currentTime - eWork.startTime

        eWork.time = currentTime.toString()
        eWork.date = helper.getDate(currentTime)


        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_END_TIME, eWork.stopTime)
        cv.put(COL_TOTAL_TIME, eWork.totalTime)
        cv.put(COL_TIME, eWork.time.toLong())
        cv.put(COL_DATE, eWork.date)

        val updatedID = db.update(TABLE_EWORK, cv, "$COL_ID = ${eWork.ID}" ,null)

        helper.log("updatedID :$updatedID ")
        return  updatedID

    }
}