package com.lysaan.malik.vsptracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lysaan.malik.vsptracker.Helper
import com.lysaan.malik.vsptracker.others.Data

const val DATABASE_NAME ="vsptracker"
const val TABLE_LOAD_HISTORY="load_history"
const val TABLE_LAST_JOURNEY="last_journey"
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


//const val TABLE_HOUR_METER="hour_meter"
//const val COL_START_TIME = "time"
//const val COL_STOP_TIME = "time"

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

        val createLoadHistoryTable = "CREATE TABLE " + TABLE_LOAD_HISTORY +" (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_MACHINE_TYPE + " INTEGER," +
                COL_LOADING_MACHINE + " TEXT," +
                COL_LOADED_MACHINE + " TEXT," +
                COL_LOADING_MATERIAL + " TEXT," +
                COL_LOADING_LOCATION + " TEXT," +
                COL_LOADING_WEIGHT + " REAL," +
                COL_DATE + " TEXT," +
                COL_TIME + " INTEGER" +
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
                "$COL_TIME  INTEGER" +
                " )"

//        db?.execSQL(createHourMeterTable)
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

        val insertID = db.insert(TABLE_LOAD_HISTORY,null,cv)
        helper.log("insertLoad:$insertID")
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

                list.add(myData)
            }while (result.moveToNext())
        }else{
            helper.log("else result:$result")
        }

        result.close()
        db.close()
        return list
    }

}