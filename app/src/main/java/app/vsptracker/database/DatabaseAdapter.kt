@file:Suppress("UNUSED_VARIABLE")

package app.vsptracker.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor.FIELD_TYPE_STRING
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import app.vsptracker.R
import app.vsptracker.apis.delay.EWork
import app.vsptracker.apis.operators.OperatorAPI
import app.vsptracker.apis.trip.MyData
import app.vsptracker.classes.CheckFormData
import app.vsptracker.classes.Material
import app.vsptracker.others.MyHelper

//const val DATABASE_NAME = "vsptracker"
const val TABLE_E_LOAD_HISTORY = "e_load_history"
const val TABLE_E_WORK = "ework"
const val TABLE_E_WORK_ACTION_OFFLOADING = "ework_action_loading"
const val TABLE_WAITS = "t_wait"
const val TABLE_TRIP = "ttrip_simple"
const val TABLE_LOCATIONS = "locations"
const val TABLE_MACHINES = "machines"
const val TABLE_MATERIALS = "materials"
const val TABLE_MACHINES_STOPS = "machine_status"
const val TABLE_STOP_REASONS = "stop_reasons"
const val TABLE_OPERATORS = "operators"
const val TABLE_SITES = "sites"
const val TABLE_MACHINES_TYPES = "machines_types"
const val TABLE_MACHINES_BRANDS = "machines_brands"
const val TABLE_MACHINES_PLANTS = "machines_plants"
const val TABLE_MACHINES_TASKS = "machines_tasks"
const val TABLE_MACHINES_HOURS = "machines_hours"
const val TABLE_MACHINES_AUTO_LOGOUTS = "machines_auto_logouts"
const val TABLE_OPERATORS_HOURS = "operators_hours"
const val TABLE_QUESTIONS_TYPES = "questions_types"
const val TABLE_QUESTIONS = "questions"
const val TABLE_ADMIN_CHECKFORMS_SCHEDULES = "admin_checkforms_schedules"
const val TABLE_ADMIN_CHECKFORMS = "admin_checkforms"
const val TABLE_ADMIN_CHECKFORMS_COMPLETED = "admin_checkforms_completed"
const val TABLE_ADMIN_CHECKFORMS_DATA = "admin_checkforms_data"
const val TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER = "admin_checkforms_completed_server"
const val TABLE_ORGS_MAPS = "orgs_maps"
const val TABLE_MVP_ORGS_PROJECTS = "mvp_orgs_projects"

const val COL_TIME = "time"
const val COL_DATE = "date"
const val COL_LOADING_MACHINE_ID = "loading_machine_id"
const val COL_LOADING_MATERIAL_ID = "loading_material_id"
const val COL_LOADING_LOCATION_ID = "loading_location_id"
const val COL_UNLOADING_WEIGHT = "loading_weight"
const val COL_UNLOADING_TASK_ID = "unloading_task_id"

const val COL_UNLOADING_MATERIAL_ID = "unloading_material_id"
const val COL_UNLOADING_LOCATION_ID = "unloading_location_id"

const val COL_MACHINE_TYPE_ID = "machine_type_id"
const val COL_MACHINE_BRAND_ID = "machine_brand_id"
const val COL_MACHINE_TASK_ID = "machine_task_id"

const val COL_LOADING_GPS_LOCATION = "loading_gps_location"
const val COL_UNLOADING_GPS_LOCATION = "unloading_gps_location"

const val COL_ID = "id"
const val COL_OPERATOR_ID = "operator_id"
const val COL_ORG_ID = "org_id"

const val COL_START_TIME = "start_time"
const val COL_END_TIME = "end_time"
const val COL_TOTAL_TIME = "total_time"

const val COL_START_HOURS = "start_hours"
const val COL_TOTAL_HOURS = "total_hours"

const val COL_EWORK_TYPE = "ework_type"
const val COL_EWORK_ACTION_TYPE = "ework_action_type"
const val COL_EWORK_ID = "ework_id"
const val COL_TOTAL_LOADS = "total_loads"

// tripType 0 = Simple Trip
// tripType 1 = Trip For Back Load
const val COL_TRIP_TYPE = "trip_type"
const val COL_TRIP0_ID = "trip0_id"
const val COL_MACHINE_ID = "machine_id"
const val COL_LOAD_TYPE_ID = "load_type_id"

//const val COL_MACHINE_STOPPED_REASON = "machine_stop_reason"
const val COL_MACHINE_STOP_REASON_ID = "machine_stop_reason_id"

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
const val COL_IS_DAY_WORKS = "is_day_works"
const val COL_PIN = "pin"
const val COL_SITE_ID = "site_id"
const val COL_MACHINE_PLANT_ID = "machine_plant_id"

// Auto Logout Time is in Minutes and col Type is Text
const val COL_AUTO_LOGOUT_TIME = "auto_logout_time"
const val COL_ANSWERS_OPTIONS = "answers_options"
const val COL_TOTAL_CORRECT_ANSWERS = "total_correct_answers"
const val COL_ADMIN_QUESTIONS_TYPES_ID = "admin_questions_types_id"
const val COL_ADMIN_QUESTIONS_ID = "admin_questions_id"
const val COL_ANSWER_DATA = "answer_data"
const val COL_ANSWER = "answer"
const val COL_ADMIN_CHECKFORMS_SCHEDULES_ID = "admin_checkforms_schedules_id"
const val COL_QUESTIONS_DATA = "questions_data"
const val COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE = "admin_checkforms_schedules_value"
const val COL_ADMIN_CHECKFORMS_ID = "admin_checkforms_id"
const val COL_ADMIN_CHECKFORMS_COMPLETED_ID = "admin_checkforms_completed_id"
const val COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID = "admin_checkforms_completed_local_id"
const val COL_IMAGES_LIMIT = "images_limit"
const val COL_IMAGES_QUALITY = "images_quality"
const val COL_ATTEMPTED_QUESTIONS = "attempted_questions"
const val COL_AWS_PATH = "aws_path"
const val COL_UPDATED_AT = "updated_at"
const val COL_IS_DOWNLOADED = "is_downloaded"
const val COL_CREATED_AT = "created_at"
const val COL_DETAILS = "details"

// Completed CheckForm Entry Type
// 0 : automatically completed when due
// 1 : manually completed by operator before due time
const val COL_ENTRY_TYPE = "entry_type"
const val COL_PRIORITY = "priority"

const val createMachinesHoursTable = "CREATE TABLE IF NOT EXISTS  $TABLE_MACHINES_HOURS (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID  INTEGER," +
        "$COL_MACHINE_STOP_REASON_ID  INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_START_HOURS TEXT," +
        "$COL_IS_START_HOURS_CUSTOM INTEGER," +
        "$COL_TOTAL_HOURS TEXT," +
        "$COL_IS_TOTAL_HOURS_CUSTOM INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"


const val createMachinesTasksTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES_TASKS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_TASK_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_PRIORITY INTEGER, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createMachinesPlantsTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES_PLANTS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_BRAND_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createMachinesBrandsTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES_BRANDS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createMachinesTypesTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES_TYPES ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_NAME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createSitesTable = "CREATE TABLE IF NOT EXISTS $TABLE_SITES ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_NAME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createOperatorsTable = "CREATE TABLE IF NOT EXISTS $TABLE_OPERATORS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_PIN TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createStopReasonsTable = "CREATE TABLE IF NOT EXISTS $TABLE_STOP_REASONS (" +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createMaterialsTable = "CREATE TABLE IF NOT EXISTS $TABLE_MATERIALS (" +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_PRIORITY INTEGER, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"


const val createLocationsTable = "CREATE TABLE IF NOT EXISTS $TABLE_LOCATIONS (" +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_PRIORITY INTEGER, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"


const val createMachinesStopsTable = "CREATE TABLE IF NOT EXISTS  $TABLE_MACHINES_STOPS (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID  INTEGER," +
        "$COL_MACHINE_STOP_REASON_ID INTEGER, " +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"


const val createTripTable = "CREATE TABLE IF NOT EXISTS  $TABLE_TRIP (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_TRIP_TYPE  INTEGER," +
        "$COL_TRIP0_ID  TEXT," +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID INTEGER," +
        "$COL_LOADING_MACHINE_ID  INTEGER," +
        "$COL_LOADING_MATERIAL_ID  INTEGER," +
        "$COL_LOADING_LOCATION_ID  INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_UNLOADING_TASK_ID INTEGER," +
        "$COL_UNLOADING_MATERIAL_ID INTEGER," +
        "$COL_UNLOADING_LOCATION_ID INTEGER," +
        "$COL_UNLOADING_WEIGHT  REAL," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createWaitsTable = "CREATE TABLE IF NOT EXISTS $TABLE_WAITS ( " +
        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_ID INTEGER," +
        "$COL_START_TIME INTEGER, " +
        "$COL_END_TIME INTEGER, " +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_TOTAL_TIME INTEGER, " +
        "$COL_DATE TEXT, " +
        "$COL_TIME INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createEWorkActionOffloadingTable = "CREATE TABLE IF NOT EXISTS $TABLE_E_WORK_ACTION_OFFLOADING ( " +
        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_ID INTEGER," +
        "$COL_EWORK_ID INTEGER, " +
        "$COL_DATE TEXT, " +
        "$COL_TIME INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createEWorkTable = "CREATE TABLE IF NOT EXISTS $TABLE_E_WORK ( " +
        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID  INTEGER," +
        "$COL_EWORK_TYPE INTEGER, " +
        "$COL_EWORK_ACTION_TYPE INTEGER, " +
        "$COL_TOTAL_LOADS INTEGER, " +
        "$COL_START_TIME INTEGER, " +
        "$COL_END_TIME INTEGER, " +
        "$COL_LOADING_MATERIAL_ID INTEGER, " +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_TOTAL_TIME INTEGER, " +
        "$COL_DATE TEXT, " +
        "$COL_TIME INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createLoadHistoryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_E_LOAD_HISTORY + " (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID  INTEGER," +
        "$COL_LOAD_TYPE_ID  INTEGER," +
        "$COL_LOADING_MATERIAL_ID  INTEGER," +
        "$COL_LOADING_LOCATION_ID  INTEGER," +
        "$COL_UNLOADING_WEIGHT  REAL," +
        "$COL_START_TIME INTEGER, " +
        "$COL_END_TIME INTEGER, " +
        "$COL_TOTAL_TIME INTEGER, " +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createMachinesTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_BRAND_ID INTEGER, " +
        "$COL_MACHINE_PLANT_ID INTEGER, " +
        "$COL_NUMBER TEXT, " +
        "$COL_PRIORITY INTEGER, " +
        "$COL_TOTAL_TIME TEXT," +
        "$COL_STATUS INTEGER," +
        "$COL_IS_DELETED INTEGER" +
        " )"


const val createMachinesAutoLogoutsTable = "CREATE TABLE IF NOT EXISTS $TABLE_MACHINES_AUTO_LOGOUTS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_AUTO_LOGOUT_TIME TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        " )"

const val createOperatorsHoursTable = "CREATE TABLE IF NOT EXISTS  $TABLE_OPERATORS_HOURS (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createQuestionsTypesTable = "CREATE TABLE IF NOT EXISTS  $TABLE_QUESTIONS_TYPES (" +
        "$COL_ID  INTEGER PRIMARY KEY ," +
        "$COL_NAME  TEXT," +
        "$COL_ANSWERS_OPTIONS INTEGER," +
        "$COL_TOTAL_CORRECT_ANSWERS INTEGER," +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        ")"

const val createQuestionsTable = "CREATE TABLE IF NOT EXISTS  $TABLE_QUESTIONS (" +
        "$COL_ID  INTEGER PRIMARY KEY ," +
        "$COL_ORG_ID INTEGER," +
        "$COL_NAME  TEXT," +
        "$COL_ADMIN_QUESTIONS_TYPES_ID INTEGER," +
        "$COL_IMAGES_LIMIT INTEGER," +
        "$COL_IMAGES_QUALITY INTEGER," +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        ")"

const val createAdminCheckFormsSchedulesTable = "CREATE TABLE IF NOT EXISTS  $TABLE_ADMIN_CHECKFORMS_SCHEDULES (" +
        "$COL_ID  INTEGER PRIMARY KEY ," +
        "$COL_NAME  TEXT," +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        ")"

const val createAdminCheckFormsTable = "CREATE TABLE IF NOT EXISTS  $TABLE_ADMIN_CHECKFORMS (" +
        "$COL_ID  INTEGER PRIMARY KEY ," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_MACHINE_TYPE_ID INTEGER, " +
        "$COL_MACHINE_ID INTEGER, " +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_ID INTEGER, " +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE  TEXT," +
        "$COL_NAME  TEXT," +
        "$COL_QUESTIONS_DATA TEXT," +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER" +
        ")"

const val createAdminCheckFormsCompletedTable = "CREATE TABLE IF NOT EXISTS  $TABLE_ADMIN_CHECKFORMS_COMPLETED (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE TEXT," +
        "$COL_ENTRY_TYPE INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createAdminCheckFormsDataTable = "CREATE TABLE IF NOT EXISTS  $TABLE_ADMIN_CHECKFORMS_DATA (" +
        "$COL_ID  INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COL_ADMIN_CHECKFORMS_COMPLETED_ID INTEGER, " +
        "$COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID INTEGER, " +
        "$COL_ADMIN_QUESTIONS_ID INTEGER, " +
        "$COL_ANSWER  TEXT," +
        "$COL_ANSWER_DATA  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_IS_SYNC  INTEGER " +
        ")"

const val createAdminCheckFormsCompletedServerTable = "CREATE TABLE IF NOT EXISTS  $TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER (" +
        "$COL_ID  INTEGER PRIMARY KEY ," +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_OPERATOR_ID  TEXT," +
        "$COL_MACHINE_TYPE_ID  INTEGER," +
        "$COL_MACHINE_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_ID INTEGER," +
        "$COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE TEXT," +
        "$COL_ENTRY_TYPE INTEGER," +
        "$COL_LOADING_GPS_LOCATION  TEXT," +
        "$COL_UNLOADING_GPS_LOCATION  TEXT," +
        "$COL_START_TIME  INTEGER," +
        "$COL_END_TIME INTEGER," +
        "$COL_TOTAL_TIME INTEGER," +
        "$COL_DATE  TEXT," +
        "$COL_TIME  INTEGER," +
        "$COL_ATTEMPTED_QUESTIONS  INTEGER," +
        "$COL_IS_DAY_WORKS  INTEGER" +
        ")"

const val createOrgsMapsTable = "CREATE TABLE IF NOT EXISTS $TABLE_ORGS_MAPS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_SITE_ID INTEGER, " +
        "$COL_AWS_PATH TEXT, " +
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER, " +
        "$COL_TIME  INTEGER," +
        "$COL_IS_DOWNLOADED  INTEGER DEFAULT 0, " +
        "$COL_UPDATED_AT TEXT " +
        " )"

const val createMvpOrgsProjectsTable = "CREATE TABLE IF NOT EXISTS $TABLE_MVP_ORGS_PROJECTS ( " +
        "$COL_ID INTEGER PRIMARY KEY, " +
        "$COL_ORG_ID INTEGER, " +
        "$COL_NAME TEXT, " +
        "$COL_DETAILS TEXT, " + // this is details column
        "$COL_STATUS INTEGER, " +
        "$COL_IS_DELETED INTEGER, " +
        "$COL_CREATED_AT TEXT, " +
        "$COL_UPDATED_AT TEXT " +
        " )"

const val DROP_TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER = "DROP TABLE IF EXISTS $TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER"
const val DROP_TABLE_ADMIN_CHECKFORMS_DATA = "DROP TABLE IF EXISTS $TABLE_ADMIN_CHECKFORMS_DATA"
const val DROP_TABLE_ADMIN_CHECKFORMS_COMPLETED = "DROP TABLE IF EXISTS $TABLE_ADMIN_CHECKFORMS_COMPLETED"
const val DROP_TABLE_ADMIN_CHECKFORMS = "DROP TABLE IF EXISTS $TABLE_ADMIN_CHECKFORMS"
const val DROP_TABLE_ADMIN_CHECKFORMS_SCHEDULES = "DROP TABLE IF EXISTS $TABLE_ADMIN_CHECKFORMS_SCHEDULES"
const val DROP_TABLE_QUESTIONS = "DROP TABLE IF EXISTS $TABLE_QUESTIONS"
const val DROP_TABLE_MACHINES_HOURS = "DROP TABLE IF EXISTS $TABLE_MACHINES_HOURS"
const val DROP_TABLE_MACHINES_TASKS = "DROP TABLE IF EXISTS $TABLE_MACHINES_TASKS"
const val DROP_TABLE_MACHINES_PLANTS = "DROP TABLE IF EXISTS $TABLE_MACHINES_PLANTS"
const val DROP_TABLE_MACHINES_BRANDS = "DROP TABLE IF EXISTS $TABLE_MACHINES_BRANDS"
const val DROP_TABLE_MACHINES_TYPES = "DROP TABLE IF EXISTS $TABLE_MACHINES_TYPES"
const val DROP_TABLE_SITES = "DROP TABLE IF EXISTS $TABLE_SITES"
const val DROP_TABLE_OPERATORS = "DROP TABLE IF EXISTS $TABLE_OPERATORS"
const val DROP_TABLE_STOP_REASONS = "DROP TABLE IF EXISTS $TABLE_STOP_REASONS"
const val DROP_TABLE_MATERIALS = "DROP TABLE IF EXISTS $TABLE_MATERIALS"
const val DROP_TABLE_MACHINES = "DROP TABLE IF EXISTS $TABLE_MACHINES"
const val DROP_TABLE_LOCATIONS = "DROP TABLE IF EXISTS $TABLE_LOCATIONS"
const val DROP_TABLE_MACHINES_STOPS = "DROP TABLE IF EXISTS $TABLE_MACHINES_STOPS"
const val DROP_TABLE_TRIP = "DROP TABLE IF EXISTS $TABLE_TRIP"
const val DROP_TABLE_WAIT = "DROP TABLE IF EXISTS $TABLE_WAITS"
const val DROP_TABLE_E_WORK_ACTION_OFFLOADING = "DROP TABLE IF EXISTS $TABLE_E_WORK_ACTION_OFFLOADING"
const val DROP_TABLE_E_WORK = "DROP TABLE IF EXISTS $TABLE_E_WORK"
const val DROP_TABLE_E_LOAD_HISTORY = "DROP TABLE IF EXISTS $TABLE_E_LOAD_HISTORY"
const val DROP_TABLE_MACHINES_AUTO_LOGOUTS = "DROP TABLE IF EXISTS $TABLE_MACHINES_AUTO_LOGOUTS"
const val DROP_TABLE_OPERATORS_HOURS = "DROP TABLE IF EXISTS $TABLE_OPERATORS_HOURS"
const val DROP_TABLE_QUESTIONS_TYPES = "DROP TABLE IF EXISTS $TABLE_QUESTIONS_TYPES"
const val DROP_TABLE_ORGS_MAPS = "DROP TABLE IF EXISTS $TABLE_ORGS_MAPS"
const val DROP_TABLE_MVP_ORGS_PROJECTS = "DROP TABLE IF EXISTS $TABLE_MVP_ORGS_PROJECTS"

@SuppressLint("Range")
class DatabaseAdapter(var context: Context) : SQLiteOpenHelper(context, context.getString(R.string.db_name), null, 18) {
  
  val tag = "DatabaseAdapter"
  private var myHelper: MyHelper
  
  init {
    this.myHelper = MyHelper(tag, context)
  }
  
  override fun onCreate(db: SQLiteDatabase?) {
    myHelper.log("DatabaseAdapter-onCreate")
    db?.execSQL(createOrgsMapsTable)
    db?.execSQL(createAdminCheckFormsCompletedServerTable)
    db?.execSQL(createAdminCheckFormsDataTable)
    db?.execSQL(createAdminCheckFormsCompletedTable)
    db?.execSQL(createAdminCheckFormsTable)
    db?.execSQL(createAdminCheckFormsSchedulesTable)
    db?.execSQL(createQuestionsTable)
    db?.execSQL(createQuestionsTypesTable)
    db?.execSQL(createOperatorsHoursTable)
    db?.execSQL(createMachinesAutoLogoutsTable)
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
    db?.execSQL(createMachinesStopsTable)
    db?.execSQL(createTripTable)
    db?.execSQL(createWaitsTable)
    db?.execSQL(createEWorkActionOffloadingTable)
    db?.execSQL(createEWorkTable)
    db?.execSQL(createLoadHistoryTable)
    db?.execSQL(createMvpOrgsProjectsTable)
  }
  
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    myHelper.log("onUpgrade")
    db?.execSQL(DROP_TABLE_MVP_ORGS_PROJECTS)
    db?.execSQL(DROP_TABLE_ORGS_MAPS)
    db?.execSQL(DROP_TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER)
    db?.execSQL(DROP_TABLE_ADMIN_CHECKFORMS_DATA)
    db?.execSQL(DROP_TABLE_ADMIN_CHECKFORMS_COMPLETED)
    db?.execSQL(DROP_TABLE_ADMIN_CHECKFORMS)
    db?.execSQL(DROP_TABLE_ADMIN_CHECKFORMS_SCHEDULES)
    db?.execSQL(DROP_TABLE_QUESTIONS)
    db?.execSQL(DROP_TABLE_QUESTIONS_TYPES)
    db?.execSQL(DROP_TABLE_OPERATORS_HOURS)
    db?.execSQL(DROP_TABLE_MACHINES_AUTO_LOGOUTS)
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
    db?.execSQL(DROP_TABLE_MACHINES_STOPS)
    db?.execSQL(DROP_TABLE_TRIP)
    db?.execSQL(DROP_TABLE_WAIT)
    db?.execSQL(DROP_TABLE_E_WORK_ACTION_OFFLOADING)
    db?.execSQL(DROP_TABLE_E_WORK)
    db?.execSQL(DROP_TABLE_E_LOAD_HISTORY)
    onCreate(db)
  }
  
  fun updateMap(datum: MyData): Boolean {
    var updateMap = true
    
    val existedOrgsMap = getOrgsMapByID(datum.id)
    if (existedOrgsMap !== null) {
      if (existedOrgsMap.updated_at == datum.updated_at)
        updateMap = false
    }
    return updateMap
  }
  
  fun insertMvpOrgsProjects(data: ArrayList<MyData>) {
    myHelper.log("insertAdminCheckFormsCompletedServer:${data.size}")
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MVP_ORGS_PROJECTS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_DETAILS, datum.details)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      cv.put(COL_CREATED_AT, datum.created_at)
      cv.put(COL_UPDATED_AT, datum.updated_at)
      
      val insertedID = db.replace(tableName, null, cv)
      myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  /**
   * Here we are using number instead of name parameter as Adapter [CustomGridLMachine] is used and that is showing number instead of name.
   */
  fun getMvpOrgsProjects(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    val query: String =
      "Select * from $TABLE_MVP_ORGS_PROJECTS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_ORG_ID = ${myHelper.getLoginAPI().org_id}   ORDER BY $COL_NAME ASC"
    
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.number = result.getString(result.getColumnIndex(COL_NAME))
        list.add(datum)
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun insertOrgsMaps(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ORGS_MAPS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_AWS_PATH, datum.aws_path)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      cv.put(COL_TIME, myHelper.getTimestampFromDate(datum.updated_at))
      cv.put(COL_IS_DOWNLOADED, datum.isDownloaded)
      cv.put(COL_UPDATED_AT, datum.updated_at)
      
      if (updateMap(datum)) {
        val insertedID = db.replace(tableName, null, cv)
//                myHelper.printInsertion(tableName, insertedID, datum)
      }
      
    }
  }
  
  fun insertAdminCheckFormsCompletedServer(data: ArrayList<MyData>) {
    myHelper.log("insertAdminCheckFormsCompletedServer:${data.size}")
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_OPERATOR_ID, datum.operatorId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_ID, datum.machineId)
      cv.put(COL_ADMIN_CHECKFORMS_ID, datum.admin_checkforms_id)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_ID, datum.admin_checkforms_schedules_id)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE, datum.admin_checkforms_schedules_value)
      cv.put(COL_ENTRY_TYPE, datum.entry_type)
      cv.put(COL_LOADING_GPS_LOCATION, datum.loadingGPSLocationString)
      cv.put(COL_UNLOADING_GPS_LOCATION, datum.unloadingGPSLocationString)
      cv.put(COL_START_TIME, datum.startTime)
      cv.put(COL_END_TIME, datum.stopTime)
      cv.put(COL_TOTAL_TIME, datum.totalTime)
      cv.put(COL_TIME, datum.stopTime)
      cv.put(COL_DATE, datum.date)
      cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)
      cv.put(COL_ATTEMPTED_QUESTIONS, datum.attempted_questions)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
    
  }
  
  fun insertAdminCheckFormsData(data: ArrayList<CheckFormData>, checkFormCompletedLocalID: Long) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS_DATA
    
    for (datum in data) {
      cv.put(COL_ADMIN_CHECKFORMS_COMPLETED_ID, datum.admin_checkforms_completed_id)
      cv.put(COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID, checkFormCompletedLocalID)
      cv.put(COL_ADMIN_QUESTIONS_ID, datum.admin_questions_id)
      cv.put(COL_ANSWER, datum.answer)
      cv.put(COL_ANSWER_DATA, myHelper.getAnswerDataToString(datum.answerDataObj))
      cv.put(COL_TIME, datum.time)
      cv.put(COL_IS_SYNC, datum.isSync)
      
      
      val insertedID = db.insert(tableName, null, cv)
      myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertAdminCheckFormsCompleted(datum: MyData): Long {
    myHelper.log("insertAdminCheckFormsCompleted:$datum")
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS_COMPLETED
    
    cv.put(COL_ORG_ID, datum.orgId)
    cv.put(COL_SITE_ID, datum.siteId)
    cv.put(COL_OPERATOR_ID, datum.operatorId)
    cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
    cv.put(COL_MACHINE_ID, datum.machineId)
    cv.put(COL_ADMIN_CHECKFORMS_ID, datum.admin_checkforms_id)
    cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_ID, datum.admin_checkforms_schedules_id)
    cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE, datum.admin_checkforms_schedules_value)
    cv.put(COL_ENTRY_TYPE, datum.entry_type)
    cv.put(COL_LOADING_GPS_LOCATION, datum.loadingGPSLocationString)
    cv.put(COL_UNLOADING_GPS_LOCATION, datum.unloadingGPSLocationString)
    cv.put(COL_START_TIME, datum.startTime)
    cv.put(COL_END_TIME, datum.stopTime)
    cv.put(COL_TOTAL_TIME, datum.totalTime)
    cv.put(COL_TIME, datum.stopTime)
    cv.put(COL_DATE, datum.date)
    cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)
    cv.put(COL_IS_SYNC, datum.isSync)
    
    val insertedID = db.insert(tableName, null, cv)
    myHelper.printInsertion(tableName, insertedID, datum)
    return insertedID
    
  }
  
  fun insertAdminCheckFormsCompleted(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS_COMPLETED
    
    for (datum in data) {
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_OPERATOR_ID, datum.operatorId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_ID, datum.machineId)
      cv.put(COL_ADMIN_CHECKFORMS_ID, datum.admin_checkforms_id)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_ID, datum.admin_checkforms_schedules_id)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE, datum.admin_checkforms_schedules_value)
      cv.put(COL_ENTRY_TYPE, datum.entry_type)
      cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.loadingGPSLocation))
      cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.unloadingGPSLocation))
      cv.put(COL_START_TIME, datum.startTime)
      cv.put(COL_END_TIME, datum.stopTime)
      cv.put(COL_TOTAL_TIME, datum.totalTime)
      cv.put(COL_TIME, datum.time.toLong())
      cv.put(COL_DATE, datum.date)
      cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)
      cv.put(COL_IS_SYNC, datum.isSync)
      
      val insertedID = db.insert(tableName, null, cv)
      myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertAdminCheckForms(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_ID, datum.machineId)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_ID, datum.admin_checkforms_schedules_id)
      cv.put(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE, datum.admin_checkforms_schedules_value)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_QUESTIONS_DATA, datum.questions_data)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertAdminCheckFormsSchedules(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_ADMIN_CHECKFORMS_SCHEDULES
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertQuestions(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_QUESTIONS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_ADMIN_QUESTIONS_TYPES_ID, datum.admin_questions_types_id)
      cv.put(COL_IMAGES_LIMIT, datum.images_limit)
      cv.put(COL_IMAGES_QUALITY, datum.images_quality)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertQuestionsTypes(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_QUESTIONS_TYPES
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_ANSWERS_OPTIONS, datum.answers_options)
      cv.put(COL_TOTAL_CORRECT_ANSWERS, datum.total_correct_answers)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertOperatorHour(myData: MyData): Long {
    val currentTime = System.currentTimeMillis()
    myData.time = currentTime.toString()
    myData.date = myHelper.getDate(currentTime.toString())
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, myData.orgId)
    cv.put(COL_SITE_ID, myData.siteId)
    cv.put(COL_OPERATOR_ID, myData.operatorId)
    cv.put(COL_START_TIME, myData.startTime)
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
    cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation))
    cv.put(COL_IS_SYNC, myData.isSync)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    return db.insert(TABLE_OPERATORS_HOURS, null, cv)
  }
  
  /**
   * Save Machines Logout Times in Database.
   * This Logout Time is different for each Site and each Machine Type.
   * After inactivity of Operator, Logout function will be called and required actions will be taken.
   */
  fun insertMachinesAutoLogouts(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES_AUTO_LOGOUTS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_AUTO_LOGOUT_TIME, datum.autoLogoutTime)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      @Suppress("UNUSED_VARIABLE") val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachineHours(datum: MyData): Long {
    val db = this.writableDatabase
    val cv = ContentValues()
//        val currentTime = System.currentTimeMillis()
//        datum.stopTime = currentTime
//
//        val time = System.currentTimeMillis()
//        datum.time = time.toString()
//        datum.date = myHelper.getDate(time.toString())
    
    cv.put(COL_ORG_ID, datum.orgId)
    cv.put(COL_SITE_ID, datum.siteId)
    cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
    cv.put(COL_MACHINE_ID, datum.machineId)
    cv.put(COL_MACHINE_STOP_REASON_ID, datum.machine_stop_reason_id)
    cv.put(COL_START_TIME, datum.startTime)
    cv.put(COL_END_TIME, datum.stopTime)
    cv.put(COL_TOTAL_TIME, datum.totalTime)
    cv.put(COL_START_HOURS, datum.startHours)
    cv.put(COL_IS_START_HOURS_CUSTOM, datum.isStartHoursCustom)
    cv.put(COL_TOTAL_HOURS, datum.totalHours)
    cv.put(COL_IS_TOTAL_HOURS_CUSTOM, datum.isTotalHoursCustom)
    
    if (!datum.time.isNullOrEmpty())
      cv.put(COL_TIME, datum.time.toLong())
    cv.put(COL_DATE, datum.date)
    cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.loadingGPSLocation))
    cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.unloadingGPSLocation))
    cv.put(COL_OPERATOR_ID, datum.operatorId)
    cv.put(COL_IS_SYNC, datum.isSync)
    return db.insert(TABLE_MACHINES_HOURS, null, cv)
  }

/*    fun insertMachinesHours(data: ArrayList<MyData>) {
        
        
        val db = this.writableDatabase
        val cv = ContentValues()
        
        for (datum in data) {
//            myHelper.log("MachinesHours:${datum.loadingGPSLocationString}-- ${datum.unloadingGPSLocationString}")
            cv.put(COL_ORG_ID, datum.orgId)
            cv.put(COL_SITE_ID, datum.siteId)
            cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
            cv.put(COL_MACHINE_ID, datum.machineId)
            cv.put(COL_START_TIME, datum.startTime)
            cv.put(COL_END_TIME, datum.stopTime)
            
            cv.put(COL_START_HOURS, datum.startHours)
            cv.put(COL_TOTAL_HOURS, datum.totalHours)
            
            if (!datum.time.isNullOrEmpty())
                cv.put(COL_TIME, datum.time.toLong())
            cv.put(COL_DATE, datum.date)
            cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)


//            cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.loadingGPSLocation))
            cv.put(COL_LOADING_GPS_LOCATION, datum.loadingGPSLocationString)
//            cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.unloadingGPSLocation))
            cv.put(COL_UNLOADING_GPS_LOCATION, datum.unloadingGPSLocationString)
            
            cv.put(COL_USER_ID, datum.operatorId)
            cv.put(COL_IS_SYNC, datum.isSync)


//            db.insert(TABLE_MACHINES_HOURS, null, cv)
            db.replace(TABLE_MACHINES_HOURS, null, cv)
            // myHelper.log("MachinesHour:$datum")
            // myHelper.log("MachinesHour:insertID:$insertID")
        }
    }*/
  
  fun insertMachinesTasks(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES_TASKS
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_TASK_ID, datum.machineTaskId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_PRIORITY, datum.priority)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachinesPlants(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES_PLANTS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_BRAND_ID, datum.machineBrandId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachinesBrands(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES_BRANDS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachinesTypes(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES_TYPES
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertSites(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_SITES
    
    for (datum in data) {
      cv.put(COL_ID, datum.siteId)
      cv.put(COL_NAME, datum.siteName)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertOperators(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_OPERATORS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_PIN, datum.pin)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertStopReasons(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_STOP_REASONS
    
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(TABLE_STOP_REASONS, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMaterials(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MATERIALS
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_PRIORITY, datum.priority)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachines(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_MACHINES
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
      cv.put(COL_MACHINE_BRAND_ID, datum.machineBrandId)
      cv.put(COL_MACHINE_PLANT_ID, datum.machinePlantId)
      cv.put(COL_NUMBER, datum.number)
      cv.put(COL_TOTAL_TIME, datum.totalHours)
      cv.put(COL_PRIORITY, datum.priority)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertLocations(data: ArrayList<OperatorAPI>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    val tableName = TABLE_LOCATIONS
    for (datum in data) {
      cv.put(COL_ID, datum.id)
      cv.put(COL_ORG_ID, datum.orgId)
      cv.put(COL_SITE_ID, datum.siteId)
      cv.put(COL_NAME, datum.name)
      cv.put(COL_PRIORITY, datum.priority)
      cv.put(COL_STATUS, datum.status)
      cv.put(COL_IS_DELETED, datum.isDeleted)
      
      val insertedID = db.replace(tableName, null, cv)
//            myHelper.printInsertion(tableName, insertedID, datum)
    }
  }
  
  fun insertMachineStop(myData: MyData): Long {
    
    myHelper.log("insertMachineStop:$myData")
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, myData.orgId)
    cv.put(COL_SITE_ID, myData.siteId)
    cv.put(COL_MACHINE_TYPE_ID, myData.machineTypeId)
    cv.put(COL_MACHINE_ID, myData.machineId)
    cv.put(COL_MACHINE_STOP_REASON_ID, myData.machine_stop_reason_id)
    
    cv.put(COL_START_TIME, myData.startTime)
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    
    
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
    )
    cv.put(COL_OPERATOR_ID, myData.operatorId)
    cv.put(COL_IS_SYNC, myData.isSync)
    
    
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_MACHINES_STOPS, null, cv)
  }
  
  fun insertTrip(myData: MyData): Long {
    
    myHelper.log("insertTrip:$myData")
    val currentTime = System.currentTimeMillis()
    myData.startTime = currentTime
    
    val time = System.currentTimeMillis()
    myData.time = time.toString()
    myData.date = myHelper.getDate(time.toString())
    myData.machineTypeId = myHelper.getMachineTypeID()
    myData.machineId = myHelper.getMachineID()
    
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, myData.orgId)
    cv.put(COL_SITE_ID, myData.siteId)
    cv.put(COL_TRIP_TYPE, myData.tripType)
    cv.put(COL_TRIP0_ID, myData.trip0ID)
    cv.put(COL_MACHINE_TYPE_ID, myData.machineTypeId)
    cv.put(COL_MACHINE_ID, myData.machineId)

//        Back Load saving as Loading item with Trip 0 ID
    if (myData.nextAction == 2) {
      cv.put(COL_LOADING_MACHINE_ID, myData.back_loading_machine_id)
      
      cv.put(COL_LOADING_MATERIAL_ID, myData.back_loading_material_id)
      
      cv.put(COL_LOADING_LOCATION_ID, myData.back_loading_location_id)
      
    } else {
      cv.put(COL_LOADING_MACHINE_ID, myData.loading_machine_id)
      
      cv.put(COL_LOADING_MATERIAL_ID, myData.loading_material_id)
      
      cv.put(COL_LOADING_LOCATION_ID, myData.loading_location_id)
    }
    
    cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)
    
    cv.put(COL_START_TIME, myData.startTime)
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    
    
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
    )
    cv.put(COL_OPERATOR_ID, myData.operatorId)
    cv.put(COL_IS_SYNC, myData.isSync)
    
    // myHelper.log("BeforeInsertTrip--:$myData")
    
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_TRIP, null, cv)
  }
  
  fun insertDelay(eWork: EWork): Long {

//        val time = System.currentTimeMillis()
////        eWork.time = time.toString()
//
//        eWork.stopTime = time
//        eWork.totalTime = eWork.stopTime - eWork.startTime
//        eWork.date = myHelper.getDate(time)
    
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, eWork.orgId)
    cv.put(COL_SITE_ID, eWork.siteId)
    cv.put(COL_MACHINE_TYPE_ID, eWork.machineTypeId)
    cv.put(COL_MACHINE_ID, eWork.machineId)
    cv.put(COL_START_TIME, eWork.startTime)
    cv.put(COL_END_TIME, eWork.stopTime)
    cv.put(COL_TOTAL_TIME, eWork.totalTime)
    cv.put(COL_TIME, eWork.time.toLong())
    cv.put(COL_DATE, eWork.date)
    cv.put(COL_IS_DAY_WORKS, eWork.isDayWorks)
    
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
    )
    cv.put(COL_OPERATOR_ID, eWork.operatorId)
    cv.put(COL_IS_SYNC, eWork.isSync)
    
    myHelper.log("delayInsert:$eWork")
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_WAITS, null, cv)
  }
  
  fun insertEWorkOffLoad(eWork: EWork): Long {
    val time = System.currentTimeMillis()
    eWork.time = time.toString()
    eWork.date = myHelper.getDate(time)
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, eWork.orgId)
    cv.put(COL_SITE_ID, eWork.siteId)
    cv.put(COL_MACHINE_TYPE_ID, eWork.machineTypeId)
    cv.put(COL_MACHINE_ID, eWork.machineId)
    cv.put(COL_EWORK_ID, eWork.eWorkId)
    cv.put(COL_TIME, eWork.time.toLong())
    cv.put(COL_DATE, eWork.date)
    cv.put(COL_IS_DAY_WORKS, eWork.isDayWorks)
    
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
    )
    cv.put(COL_OPERATOR_ID, eWork.operatorId)
    cv.put(COL_IS_SYNC, eWork.isSync)
    
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_E_WORK_ACTION_OFFLOADING, null, cv)
  }
  
  /**
   * eWorkType 1 = General Digging
   * eWorkType 2 = Trenching
   * eWorkType 3 = Scraper Trimming
   * eWorkActionType 1 = Side Casting
   * eWorkActionType 2 = Off Loading
   */
  fun insertEWork(eWork: EWork): Long {
    
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, eWork.orgId)
    cv.put(COL_SITE_ID, eWork.siteId)
    cv.put(COL_MACHINE_TYPE_ID, eWork.machineTypeId)
    cv.put(COL_MACHINE_ID, eWork.machineId)
    cv.put(COL_EWORK_TYPE, eWork.workType)
    cv.put(COL_EWORK_ACTION_TYPE, eWork.workActionType)
    cv.put(COL_START_TIME, eWork.startTime)
    
    cv.put(COL_END_TIME, eWork.stopTime)
    
    cv.put(COL_TOTAL_TIME, eWork.totalTime)
    cv.put(COL_TIME, eWork.time.toLong())
    cv.put(COL_DATE, eWork.date)
    cv.put(COL_IS_DAY_WORKS, eWork.isDayWorks)


//        cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.loadingGPSLocation))
    cv.put(COL_LOADING_GPS_LOCATION, eWork.loadingGPSLocationString)
    
    cv.put(COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(eWork.unloadingGPSLocation))
    
    cv.put(COL_OPERATOR_ID, eWork.operatorId)
    cv.put(COL_IS_SYNC, eWork.isSync)
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_E_WORK, null, cv)
    
  }
  
  fun insertELoad(myData: MyData): Long {

//        val time = System.currentTimeMillis()
//        myData.time = time.toString()
//        myData.date = myHelper.getDate(time.toString())
//        myData.loadedMachineType = myHelper.getMachineTypeID()
    
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_ORG_ID, myData.orgId)
    cv.put(COL_SITE_ID, myData.siteId)
    cv.put(COL_MACHINE_ID, myData.machineId)
    cv.put(COL_LOAD_TYPE_ID, myData.loadTypeId)
    cv.put(COL_MACHINE_TYPE_ID, myData.machineTypeId)
    cv.put(COL_LOADING_MATERIAL_ID, myData.loading_material_id)
    cv.put(COL_LOADING_LOCATION_ID, myData.loading_location_id)
    cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)
    
    
    cv.put(COL_START_TIME, myData.startTime)
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    
    
    cv.put(COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.loadingGPSLocation))
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
    )
    cv.put(COL_OPERATOR_ID, myData.operatorId)
    cv.put(COL_IS_SYNC, myData.isSync)
    // myHelper.log("insertID:$insertID")
    return db.insert(TABLE_E_LOAD_HISTORY, null, cv)
  }
  
  /**
   * This method will return Orgs Map details for selected site
   */
  fun getCurrentOrgsMap(): MyData? {
    
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ORGS_MAPS  WHERE $COL_ORG_ID = ${myHelper.getLoginAPI().org_id} AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId} AND  $COL_IS_DELETED = 0 AND $COL_STATUS = 1  ORDER BY $COL_ID DESC "
    val result = db.rawQuery(query, null)
    var datum: MyData? = null
    if (result.moveToFirst()) {
      
      datum = MyData()
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.aws_path = result.getString(result.getColumnIndex(COL_AWS_PATH))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
      datum.isDownloaded = result.getInt(result.getColumnIndex(COL_IS_DOWNLOADED))
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.updated_at = result.getString(result.getColumnIndex(COL_UPDATED_AT))
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getOrgsMaps(): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase

//        val query = "Select * FROM $TABLE_ORGS_MAPS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1  ORDER BY $COL_ID DESC "
    val query = "Select * FROM $TABLE_ORGS_MAPS ORDER BY $COL_ID DESC "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.aws_path = result.getString(result.getColumnIndex(COL_AWS_PATH))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.isDownloaded = result.getInt(result.getColumnIndex(COL_IS_DOWNLOADED))
        datum.updated_at = result.getString(result.getColumnIndex(COL_UPDATED_AT))
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getOrgsMapByID(id: Int): MyData? {
    
    val db = this.readableDatabase
    
    val query = "Select * FROM $TABLE_ORGS_MAPS  WHERE $COL_ID = $id  ORDER BY $COL_ID DESC LIMIT 1"
    val result = db.rawQuery(query, null)
    
    var datum: MyData? = null
    
    if (result.moveToFirst()) {
      datum = MyData()
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.aws_path = result.getString(result.getColumnIndex(COL_AWS_PATH))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.isDownloaded = result.getInt(result.getColumnIndex(COL_IS_DOWNLOADED))
      datum.updated_at = result.getString(result.getColumnIndex(COL_UPDATED_AT))
    }
    result.close()
//        db.close()
    return datum
  }
  
  fun getAdminCheckFormsCompletedServer(orderBy: String = "DESC"): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER WHERE $COL_MACHINE_ID = ${myHelper.getMachineID()} ORDER BY $COL_ID $orderBy "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.admin_checkforms_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_ID))
        datum.admin_checkforms_schedules_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_ID))
        datum.admin_checkforms_schedules_value = result.getString(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE))
        datum.entry_type = result.getInt(result.getColumnIndex(COL_ENTRY_TYPE))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        datum.attempted_questions = result.getInt(result.getColumnIndex(COL_ATTEMPTED_QUESTIONS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getAdminCheckFormsDataByLocalID(completedCheckFormLocalID: Int): ArrayList<CheckFormData> {
    val list: ArrayList<CheckFormData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS_DATA  WHERE $COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID = $completedCheckFormLocalID   "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = CheckFormData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.admin_checkforms_completed_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_COMPLETED_ID))
        datum.admin_checkform_completed_local_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID))
        datum.admin_questions_id = result.getInt(result.getColumnIndex(COL_ADMIN_QUESTIONS_ID))
        datum.answer = result.getString(result.getColumnIndex(COL_ANSWER))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        
        datum.answerDataObj = myHelper.getStringToAnswerData(
          result.getString(
            result.getColumnIndex(COL_ANSWER_DATA)
          )
        )
        datum.answerDataString = result.getString(
          result.getColumnIndex(COL_ANSWER_DATA)
        )
        
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getAdminCheckFormsData(): ArrayList<CheckFormData> {
    val list: ArrayList<CheckFormData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS_DATA ORDER BY $COL_ID DESC "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = CheckFormData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.admin_checkforms_completed_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_COMPLETED_ID))
        datum.admin_checkform_completed_local_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_COMPLETED_LOCAL_ID))
        datum.admin_questions_id = result.getInt(result.getColumnIndex(COL_ADMIN_QUESTIONS_ID))
        datum.answer = result.getString(result.getColumnIndex(COL_ANSWER))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        
        datum.answerDataObj = myHelper.getStringToAnswerData(
          result.getString(
            result.getColumnIndex(COL_ANSWER_DATA)
          )
        )
        datum.answerDataString = result.getString(
          result.getColumnIndex(COL_ANSWER_DATA)
        )
        
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getAdminCheckFormsDue(): ArrayList<MyData> {
    val dueCheckForms = ArrayList<MyData>()
    val adminCheckForms = getAdminCheckForms()
//        myHelper.log("adminCheckForms:$adminCheckForms")
    
    adminCheckForms.forEach { adminCheckForm ->
      myHelper.log("getMeterTimeForFinish1:${myHelper.getMeterTimeForFinish()}")
//            myHelper.log("adminCheckForm:$adminCheckForm")
      // Check If CheckForm is valid for Current, Site, Machine Type and Machine
      if (myHelper.isValidCheckForm(adminCheckForm)) {
        val adminCheckFormsCompleted = getAdminCheckFormsCompletedServer(adminCheckForm.id)
        
        myHelper.log("Case:${adminCheckForm.admin_checkforms_schedules_id}")
        when (adminCheckForm.admin_checkforms_schedules_id) {
          
          1 -> {
            // Due after every Days passed
            if (myHelper.isDueCheckFormAfterDaysPassed(adminCheckForm, adminCheckFormsCompleted, getMachineHours("ASC"))) {
              dueCheckForms.add(adminCheckForm)
            }
          }
          2 -> {
            // Due after every Machine Hours duration
            if (myHelper.isDueCheckFormAfterMachineHoursCompleted(adminCheckForm, adminCheckFormsCompleted)) {
              dueCheckForms.add(adminCheckForm)
            }
          }
          3 -> {
            // Due at each machine start
            dueCheckForms.add(adminCheckForm)
          }
          4 -> {
            // Due at machine start - one time
            if (adminCheckFormsCompleted == null) {
              myHelper.log("already not completed")
              dueCheckForms.add(adminCheckForm)
            } else {
              // As it is one time so no need to show in due CheckForms
              myHelper.log("already completed")
            }
          }
          5 -> {
            // Due after Machine Hours - one time
            if (adminCheckFormsCompleted == null) {
              myHelper.log("already not completed")
              if (myHelper.isDueCheckFormAfterMachineHoursCompleted(adminCheckForm, adminCheckFormsCompleted)) {
                dueCheckForms.add(adminCheckForm)
              }
            } else {
              // As it is one time so no need to show in due CheckForms
              myHelper.log("already completed")
            }
          }
          6 -> {
            // Due after Days - one time
            if (adminCheckFormsCompleted == null) {
              myHelper.log("already not completed")
              if (myHelper.isDueCheckFormAfterDaysPassed(adminCheckForm, adminCheckFormsCompleted, getMachineHours("ASC"))) {
                dueCheckForms.add(adminCheckForm)
              }
            } else {
              // As it is one time so no need to show in due CheckForms
              myHelper.log("already completed")
            }
          }
        }
      }
      
    }
    return dueCheckForms
  }
  
  /**
   * Get Latest Completed CheckForm for selected Machine from AdminCheckForms.
   */
  fun getAdminCheckFormsCompletedServer(checkForm_id: Int): MyData? {
    val db = this.readableDatabase
    
    val query = "SELECT * FROM $TABLE_ADMIN_CHECKFORMS_COMPLETED_SERVER " +
            "WHERE $COL_ADMIN_CHECKFORMS_ID = $checkForm_id AND $COL_MACHINE_ID = ${myHelper.getMachineID()} ORDER BY $COL_ID DESC LIMIT 1"
    val result = db.rawQuery(query, null)
    
    var datum: MyData? = null
    if (result.moveToFirst()) {
      datum = MyData()
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
      datum.admin_checkforms_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_ID))
      datum.admin_checkforms_schedules_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_ID))
      datum.admin_checkforms_schedules_value = result.getString(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE))
      datum.entry_type = result.getInt(result.getColumnIndex(COL_ENTRY_TYPE))
      
      datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
      datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
      datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
      
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
      datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
      
      datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
        result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
      )
      datum.loadingGPSLocationString = result.getString(
        result.getColumnIndex(COL_LOADING_GPS_LOCATION)
      )
      
      datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
        result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
      )
      datum.unloadingGPSLocationString = result.getString(
        result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
      )
      datum.attempted_questions = result.getInt(result.getColumnIndex(COL_ATTEMPTED_QUESTIONS))
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getAdminCheckFormsCompleted(orderBy: String = "DESC"): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS_COMPLETED ORDER BY $COL_ID $orderBy "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.admin_checkforms_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_ID))
        datum.admin_checkforms_schedules_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_ID))
        datum.admin_checkforms_schedules_value = result.getString(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE))
        datum.entry_type = result.getInt(result.getColumnIndex(COL_ENTRY_TYPE))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        datum.checkFormData = getAdminCheckFormsDataByLocalID(datum.id)
        
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getAdminCheckFormByID(id: Int): MyData {
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS WHERE $COL_ID = $id  "
    val result = db.rawQuery(query, null)
    val datum = MyData()
    if (result.moveToFirst()) {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
      datum.admin_checkforms_schedules_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_ID))
      datum.admin_checkforms_schedules_value = result.getString(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.questions_data = result.getString(result.getColumnIndex(COL_QUESTIONS_DATA))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
      
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getAdminCheckForms(): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * FROM $TABLE_ADMIN_CHECKFORMS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1  ORDER BY $COL_ID DESC "
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.admin_checkforms_schedules_id = result.getInt(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_ID))
        datum.admin_checkforms_schedules_value = result.getString(result.getColumnIndex(COL_ADMIN_CHECKFORMS_SCHEDULES_VALUE))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        
        // If not Questions are added to CheckForm then populate questions_data with empty String
        if (result.getType(result.getColumnIndex(COL_QUESTIONS_DATA)) == FIELD_TYPE_STRING) {
          datum.questions_data = result.getString(result.getColumnIndex(COL_QUESTIONS_DATA))
        } else {
          datum.questions_data = ""
        }
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        // Check if CheckForm is valid for this Site, Machine Type and Machine
        if (myHelper.isValidCheckForm(datum))
          list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getAdminCheckFormScheduleByID(id: Int): MyData {
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_ADMIN_CHECKFORMS_SCHEDULES WHERE $COL_ID = $id "
    val result = db.rawQuery(query, null)
    val datum = MyData()
    if (result.moveToFirst()) {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getQuestionByID(id: Int): MyData {
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_QUESTIONS WHERE $COL_ID = $id "
    val result = db.rawQuery(query, null)
    val datum = MyData()
    if (result.moveToFirst()) {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.admin_questions_types_id = result.getInt(result.getColumnIndex(COL_ADMIN_QUESTIONS_TYPES_ID))
      datum.images_limit = result.getInt(result.getColumnIndex(COL_IMAGES_LIMIT))
      datum.images_quality = result.getInt(result.getColumnIndex(COL_IMAGES_QUALITY))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getQuestionsByIDs(ids: String, questionsIDsList: List<Int>): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    // ordering the questions as per admin selected order
    var orderBy = "CASE $TABLE_QUESTIONS.id "
    var i = 1
    for (id in questionsIDsList) {
      orderBy = "$orderBy WHEN $id THEN $i"
      i++
    }
    orderBy = "$orderBy END"
    
    
    val query =
      "Select * from $TABLE_QUESTIONS WHERE $COL_ID  IN ( $ids ) ORDER BY $orderBy"

//        myHelper.log("query:$query")
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.admin_questions_types_id = result.getInt(result.getColumnIndex(COL_ADMIN_QUESTIONS_TYPES_ID))
        datum.images_limit = result.getInt(result.getColumnIndex(COL_IMAGES_LIMIT))
        datum.images_quality = result.getInt(result.getColumnIndex(COL_IMAGES_QUALITY))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getQuestionsTypesByID(id: Int): MyData {
    
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_QUESTIONS_TYPES WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    val datum = MyData()
    if (result.moveToFirst()) {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.answers_options = result.getInt(result.getColumnIndex(COL_ANSWERS_OPTIONS))
      datum.total_correct_answers = result.getInt(result.getColumnIndex(COL_TOTAL_CORRECT_ANSWERS))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
      
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getOperatorsHours(orderBy: String = "DESC"): ArrayList<MyData> {
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    val query = "Select * from $TABLE_OPERATORS_HOURS  ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = result.getString(result.getColumnIndex(COL_TIME))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(result.getColumnIndex(COL_LOADING_GPS_LOCATION))
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(result.getColumnIndex(COL_UNLOADING_GPS_LOCATION))
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachinesAutoLogout(): ArrayList<OperatorAPI> {
    
    val list: ArrayList<OperatorAPI> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_AUTO_LOGOUTS WHERE " +
              "$COL_IS_DELETED = 0 AND $COL_STATUS = 1  AND $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} AND " +
              "$COL_SITE_ID = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = OperatorAPI()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.autoLogoutTime = result.getString(result.getColumnIndex(COL_AUTO_LOGOUT_TIME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachinesAutoLogouts(): ArrayList<OperatorAPI> {
    
    val list: ArrayList<OperatorAPI> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_AUTO_LOGOUTS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1  ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = OperatorAPI()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.autoLogoutTime = result.getString(result.getColumnIndex(COL_AUTO_LOGOUT_TIME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachineHours(orderBy: String = "DESC"): MyData {
    
    
    val db = this.readableDatabase
    val query = "Select * from $TABLE_MACHINES_HOURS WHERE $COL_MACHINE_ID = ${myHelper.getMachineID()} ORDER BY $COL_ID $orderBy LIMIT 1"
    val result = db.rawQuery(query, null)
    val datum = MyData()
    if (result.moveToFirst()) {
      
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
      datum.machine_stop_reason_id = result.getInt(result.getColumnIndex(COL_MACHINE_STOP_REASON_ID))
      
      datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
      datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
      datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
//                // myHelper.log("DB_startTime:${result.getLong(result.getColumnIndex(COL_START_TIME))}")
//                // myHelper.log("DB_StartHour:${result.getString(result.getColumnIndex(COL_START_HOURS))}")
      if (result.getString(result.getColumnIndex(COL_START_HOURS)) != null)
        datum.startHours = result.getString(result.getColumnIndex(COL_START_HOURS))
      
      if (result.getString(result.getColumnIndex(COL_TOTAL_HOURS)) != null)
        datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_HOURS))
      
      datum.isStartHoursCustom = result.getInt(result.getColumnIndex(COL_IS_START_HOURS_CUSTOM))
      datum.isTotalHoursCustom = result.getInt(result.getColumnIndex(COL_IS_TOTAL_HOURS_CUSTOM))
      
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
      datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
      
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
      
      datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
      datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
      datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getMachinesHours(orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_HOURS  ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.machine_stop_reason_id = result.getInt(result.getColumnIndex(COL_MACHINE_STOP_REASON_ID))
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        if (result.getString(result.getColumnIndex(COL_START_HOURS)) != null)
          datum.startHours = result.getString(result.getColumnIndex(COL_START_HOURS))
        
        if (result.getString(result.getColumnIndex(COL_TOTAL_HOURS)) != null)
          datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_HOURS))
        
        datum.isStartHoursCustom = result.getInt(result.getColumnIndex(COL_IS_START_HOURS_CUSTOM))
        datum.isTotalHoursCustom = result.getInt(result.getColumnIndex(COL_IS_TOTAL_HOURS_CUSTOM))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
//                myHelper.log("loadingGPS:$loadingGPS")
//                if(loadingGPS == null)
//                    loadingGPS = GPSLocation()
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(datum.loadingGPSLocationString)
        
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(datum.unloadingGPSLocationString)
        
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getTasks(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_TASKS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId}  ORDER BY $COL_PRIORITY < 0, $COL_PRIORITY ASC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineTaskId = result.getInt(result.getColumnIndex(COL_MACHINE_TASK_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getTaskByID(id: Int): OperatorAPI {
    
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_TASKS WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    val datum = OperatorAPI()
    if (result.moveToFirst()) {
//            do {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineTaskId = result.getInt(result.getColumnIndex(COL_MACHINE_TASK_ID))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
      datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))

//            } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  @SuppressWarnings("unused")
  fun getMachinesPlants(): ArrayList<OperatorAPI> {
    
    val list: ArrayList<OperatorAPI> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_PLANTS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = OperatorAPI()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineBrandId = result.getInt(result.getColumnIndex(COL_MACHINE_BRAND_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachinesBrands(): ArrayList<OperatorAPI> {
    
    val list: ArrayList<OperatorAPI> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_BRANDS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = OperatorAPI()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachinesTypes(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_TYPES WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachineTypeByID(id: Int): Material {

//        val list: ArrayList<Material> = ArrayList()
    
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MACHINES_TYPES WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    val datum = Material()
    
    if (result.moveToFirst()) {
      do {
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
//                list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getSites(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query = "Select * from $TABLE_SITES WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getSiteByID(id: Int): Material {
    
    val db = this.readableDatabase
    
    val query = "Select * from $TABLE_SITES WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    val datum = Material()
    if (result.moveToFirst()) {
      do {
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getOperators(): ArrayList<OperatorAPI> {
    
    val list: ArrayList<OperatorAPI> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_OPERATORS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = OperatorAPI()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.pin = result.getString(result.getColumnIndex(COL_PIN))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getOperatorByID(id: Int): OperatorAPI {
    
    val datum = OperatorAPI()
    
    val db = this.readableDatabase

//        val query = "Select * from $TABLE_OPERATORS WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_ID =? ORDER BY $COL_ID DESC"
    val query = "SELECT * FROM $TABLE_OPERATORS WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.pin = result.getString(result.getColumnIndex(COL_PIN))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getOperator(pin: String): OperatorAPI {
    
    val datum = OperatorAPI()
    
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_OPERATORS WHERE $COL_PIN =? AND $COL_ORG_ID = ${myHelper.getLoginAPI().org_id} AND $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID DESC"
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
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getStopReasons(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_STOP_REASONS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 ORDER BY $COL_ID"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getStopReasonByID(id: Int): Material {
    
    val db = this.readableDatabase
    val query =
      "Select * from $TABLE_STOP_REASONS  WHERE $COL_ID = $id  ORDER BY $COL_ID"
    val result = db.rawQuery(query, null)
    
    
    val datum = Material()
    if (result.moveToFirst()) {
//            do {
      datum.id = result.getInt(result.getColumnIndex(COL_ID))
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.name = result.getString(result.getColumnIndex(COL_NAME))
      datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))

//            } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getMaterialByID(id: Int): Material {

//        val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MATERIALS  WHERE $COL_ID = $id ORDER BY $COL_ID DESC LIMIT 1"
    val result = db.rawQuery(query, null)
    val datum = Material()
    if (result.moveToFirst()) {
      do {
        
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
//                list.add(datum)
      
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return datum
  }
  
  fun getMaterials(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_MATERIALS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1  AND $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_PRIORITY < 0, $COL_PRIORITY ASC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
        
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getMachines(type: Int = 0): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    val query: String = if (type < 1) {
      "Select * from $TABLE_MACHINES  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId}   ORDER BY $COL_PRIORITY < 0, $COL_PRIORITY ASC"
    } else {
      "Select * from $TABLE_MACHINES  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId} AND $COL_MACHINE_TYPE_ID = $type   ORDER BY $COL_PRIORITY < 0, $COL_PRIORITY ASC"
    }
    // myHelper.log("rawQuery:$query")
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
        datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_TIME))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachineByID(id: Int): Material {
    
    val db = this.readableDatabase
    val query =
      "Select * from $TABLE_MACHINES  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId} AND $COL_ID = $id ORDER BY $COL_ID DESC"
    
    // myHelper.log("rawQuery:$query")
    val result = db.rawQuery(query, null)
    
    val datum = Material()
    
    if (result.moveToFirst()) {
      do {
        
        // myHelper.log("Result:${result.toString()}")
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.number = result.getString(result.getColumnIndex(COL_NUMBER))
        datum.totalHours = result.getString(result.getColumnIndex(COL_TOTAL_TIME))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        datum.status = result.getInt(result.getColumnIndex(COL_STATUS))
        
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getLocationByID(id: Int): Material {

//        val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_LOCATIONS  WHERE $COL_ID = $id ORDER BY $COL_ID DESC LIMIT 1"
    val result = db.rawQuery(query, null)
    
    val datum = Material()
    if (result.moveToFirst()) {
      do {
        
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
//                list.add(datum)
      
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getLocations(): ArrayList<Material> {
    
    val list: ArrayList<Material> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_LOCATIONS  WHERE $COL_IS_DELETED = 0 AND $COL_STATUS = 1 AND $COL_SITE_ID = ${myHelper.getMachineSettings().siteId} ORDER BY $COL_PRIORITY <0, $COL_PRIORITY ASC"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = Material()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.name = result.getString(result.getColumnIndex(COL_NAME))
        datum.priority = result.getInt(result.getColumnIndex(COL_PRIORITY))
        datum.isDeleted = result.getInt(result.getColumnIndex(COL_IS_DELETED))
        list.add(datum)
        
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getWaits(orderBy: String = "DESC"): ArrayList<EWork> {
    
    val list: ArrayList<EWork> = ArrayList()
    val db = this.readableDatabase

//        val query = "Select * from $TABLE_DELAY  WHERE $COL_MACHINE_TYPE_ID = ${myHelper.getMachineTypeID()} ORDER BY $COL_ID DESC"
    val query = "Select * from $TABLE_WAITS  ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = EWork()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getEWorksOffLoads(eWorkID: Int): ArrayList<EWork> {
    
    val list: ArrayList<EWork> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_E_WORK_ACTION_OFFLOADING WHERE $COL_EWORK_ID = $eWorkID ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = EWork()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.eWorkId = result.getInt(result.getColumnIndex(COL_EWORK_ID))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
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
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
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
  fun getEWorks(workType: Int, orderBy: String = "DESC"): ArrayList<EWork> {
    
    val list: ArrayList<EWork> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_E_WORK WHERE $COL_EWORK_TYPE = $workType ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = EWork()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.workType = result.getInt(result.getColumnIndex(COL_EWORK_TYPE))
        datum.workActionType = result.getInt(result.getColumnIndex(COL_EWORK_ACTION_TYPE))
        datum.totalLoads = result.getInt(result.getColumnIndex(COL_TOTAL_LOADS))
        datum.materialId = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
//                myHelper.log("getEWorks:$datum")
        list.add(datum)
      } while (result.moveToNext())
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
  fun getCurrentLoginEWorks(workType: Int, orderBy: String = "DESC"): ArrayList<EWork> {
    
    val list: ArrayList<EWork> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_E_WORK WHERE $COL_EWORK_TYPE = $workType AND  $COL_START_TIME > ${myHelper.getMeter().hourStartTime} AND $COL_OPERATOR_ID  = ${myHelper.getOperatorAPI().id} ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    
    if (result.moveToFirst()) {
      do {
        val datum = EWork()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.workType = result.getInt(result.getColumnIndex(COL_EWORK_TYPE))
        datum.workActionType = result.getInt(result.getColumnIndex(COL_EWORK_ACTION_TYPE))
        datum.totalLoads = result.getInt(result.getColumnIndex(COL_TOTAL_LOADS))
        datum.materialId = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
//                myHelper.log("getEWorks:$datum")
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getELoadHistory(orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_E_LOAD_HISTORY ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.loadTypeId = result.getInt(result.getColumnIndex(COL_LOAD_TYPE_ID))
        
        datum.loading_material_id =
          result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        datum.loading_location_id =
          result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
        datum.unloadingWeight =
          result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        datum.time = result.getString(result.getColumnIndex(COL_TIME))
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getCurrentLoginELoadHistory(orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query =
      "Select * from $TABLE_E_LOAD_HISTORY WHERE $COL_START_TIME > ${myHelper.getMeter().hourStartTime} AND $COL_OPERATOR_ID  = ${myHelper.getOperatorAPI().id} ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.loadTypeId = result.getInt(result.getColumnIndex(COL_LOAD_TYPE_ID))
        
        datum.loading_material_id =
          result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        datum.loading_location_id =
          result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
        datum.unloadingWeight =
          result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        datum.time = result.getString(result.getColumnIndex(COL_TIME))
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
    }
    result.close()
    db.close()
    return list
  }
  
  fun getTrip(id: Long): MyData {
    
    val db = this.readableDatabase
    val query = "Select * from $TABLE_TRIP  WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    val datum = MyData()
    if (result.moveToFirst()) {
      
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
      datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
      datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineId =
        result.getInt(result.getColumnIndex(COL_MACHINE_ID))
      
      datum.loading_machine_id = result.getInt(result.getColumnIndex(COL_LOADING_MACHINE_ID))
      datum.loading_material_id = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
      
      datum.loading_location_id = result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
      
      datum.unloadingWeight = result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
      datum.unloading_task_id = result.getInt(result.getColumnIndex(COL_UNLOADING_TASK_ID))
      
      datum.unloading_material_id = result.getInt(result.getColumnIndex(COL_UNLOADING_MATERIAL_ID))
      
      datum.unloading_location_id = result.getInt(result.getColumnIndex(COL_UNLOADING_LOCATION_ID))
      
      datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
      datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
      datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
      
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
      datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
      
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
      datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
      datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getTrips(): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    val query = "Select * from $TABLE_TRIP  ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
        datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
        datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
        datum.loading_machine_id = result.getInt(result.getColumnIndex(COL_LOADING_MACHINE_ID))
        datum.loading_material_id = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        datum.loading_location_id = result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
        datum.unloadingWeight =
          result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
        datum.unloading_task_id = result.getInt(result.getColumnIndex(COL_UNLOADING_TASK_ID))
        datum.unloading_material_id = result.getInt(result.getColumnIndex(COL_UNLOADING_MATERIAL_ID))
        datum.unloading_location_id = result.getInt(result.getColumnIndex(COL_UNLOADING_LOCATION_ID))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
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
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getTripsByTypes(type: Int, orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    val query = "Select * from $TABLE_TRIP   WHERE $COL_MACHINE_TYPE_ID = $type ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
        datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
        datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
        
        datum.loading_machine_id = result.getInt(result.getColumnIndex(COL_LOADING_MACHINE_ID))
        
        datum.loading_material_id = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        
        datum.loading_location_id = result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
        
        datum.unloadingWeight =
          result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
        datum.unloading_task_id = result.getInt(result.getColumnIndex(COL_UNLOADING_TASK_ID))
        datum.unloading_material_id = result.getInt(result.getColumnIndex(COL_UNLOADING_MATERIAL_ID))
        datum.unloading_location_id = result.getInt(result.getColumnIndex(COL_UNLOADING_LOCATION_ID))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        myHelper.log("orderBy:$orderBy  tripData:$datum")
        // For Server Sync; send data which has stop time greater than 0. As if stop_time=0 then
        // Trip Unload data is not saved/updated yet. This could be the case when Load is done and machine is
        // stopped. And when machine is started again data is synced with server but Unload is not done yet.
        if (orderBy == "ASC") {
          if (datum.stopTime > 0)
            list.add(datum)
        } else {
          list.add(datum)
        }
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getCurrentLoginTrips(orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    val query =
      "Select * from $TABLE_TRIP   WHERE $COL_START_TIME > ${myHelper.getMeter().hourStartTime} AND $COL_OPERATOR_ID  = ${myHelper.getOperatorAPI().id} ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
        datum.tripType = result.getInt(result.getColumnIndex(COL_TRIP_TYPE))
        datum.trip0ID = result.getString(result.getColumnIndex(COL_TRIP0_ID))
        
        datum.loading_machine_id = result.getInt(result.getColumnIndex(COL_LOADING_MACHINE_ID))
        
        datum.loading_material_id = result.getInt(result.getColumnIndex(COL_LOADING_MATERIAL_ID))
        
        datum.loading_location_id = result.getInt(result.getColumnIndex(COL_LOADING_LOCATION_ID))
        
        datum.unloadingWeight =
          result.getDouble(result.getColumnIndex(COL_UNLOADING_WEIGHT))
        datum.unloading_task_id = result.getInt(result.getColumnIndex(COL_UNLOADING_TASK_ID))
        datum.unloading_material_id = result.getInt(result.getColumnIndex(COL_UNLOADING_MATERIAL_ID))
        datum.unloading_location_id = result.getInt(result.getColumnIndex(COL_UNLOADING_LOCATION_ID))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        list.add(datum)
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun getMachineStopByID(id: Long = 0): MyData {

//        var list: ArrayList<MyData> = ArrayList()
    val db = this.readableDatabase
    
    val query = "Select * from $TABLE_MACHINES_STOPS  WHERE $COL_ID = $id ORDER BY $COL_ID DESC"
    val result = db.rawQuery(query, null)
    
    val datum = MyData()
    
    if (result.moveToFirst()) {
//            do {
      datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
      datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
      datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
      datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
      datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
      datum.machine_stop_reason_id =
        result.getInt(result.getColumnIndex(COL_MACHINE_STOP_REASON_ID))
      
      
      datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
      datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
      datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
      
      datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
      datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
      datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
      
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
      datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
      datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
//                list.add(datum)
//            } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return datum
  }
  
  fun getMachinesStops(orderBy: String = "DESC"): ArrayList<MyData> {
    
    val list = ArrayList<MyData>()
    val db = this.readableDatabase
    
    val query = "Select * from $TABLE_MACHINES_STOPS  ORDER BY $COL_ID $orderBy"
    val result = db.rawQuery(query, null)
    
    if (result.moveToFirst()) {
      do {
        val datum = MyData()
        datum.id = result.getInt(result.getColumnIndex(COL_ID))
        datum.orgId = result.getInt(result.getColumnIndex(COL_ORG_ID))
        datum.siteId = result.getInt(result.getColumnIndex(COL_SITE_ID))
        datum.recordID = result.getLong(result.getColumnIndex(COL_ID))
        datum.machineTypeId = result.getInt(result.getColumnIndex(COL_MACHINE_TYPE_ID))
        datum.machineId = result.getInt(result.getColumnIndex(COL_MACHINE_ID))
        datum.machine_stop_reason_id =
          result.getInt(result.getColumnIndex(COL_MACHINE_STOP_REASON_ID))
        
        datum.startTime = result.getLong(result.getColumnIndex(COL_START_TIME))
        datum.stopTime = result.getLong(result.getColumnIndex(COL_END_TIME))
        datum.totalTime = result.getLong(result.getColumnIndex(COL_TOTAL_TIME))
        
        datum.time = result.getLong(result.getColumnIndex(COL_TIME)).toString()
        datum.date = myHelper.getDateTime(result.getLong(result.getColumnIndex(COL_TIME)))
        datum.isDayWorks = result.getInt(result.getColumnIndex(COL_IS_DAY_WORKS))
        
        datum.loadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_LOADING_GPS_LOCATION)
          )
        )
        datum.loadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_LOADING_GPS_LOCATION)
        )
        datum.unloadingGPSLocation = myHelper.getStringToGPSLocation(
          result.getString(
            result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
          )
        )
        datum.unloadingGPSLocationString = result.getString(
          result.getColumnIndex(COL_UNLOADING_GPS_LOCATION)
        )
        datum.operatorId = result.getInt(result.getColumnIndex(COL_OPERATOR_ID))
        datum.isSync = result.getInt(result.getColumnIndex(COL_IS_SYNC))
        myHelper.log("getMachinesStops:$datum")
        // When Server sync data is required then send only data which has stop_time greater than 0
        // Machine stop entry is recorded in database when a machine is stopped and updated when a machine
        // is started again. So send data to server when we have complete data e.g start_time and stop_time
        if (orderBy == "ASC") {
          if (datum.stopTime > 0)
            list.add(datum)
        } else {
          list.add(datum)
        }
      } while (result.moveToNext())
    }
    
    result.close()
    db.close()
    return list
  }
  
  fun updateDelay(eWork: EWork): Int {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_IS_SYNC, eWork.isSync)
    // myHelper.log("updatedID :$updatedID ")
    return db.update(TABLE_WAITS, cv, "$COL_ID = ${eWork.id}", null)
  }
  
  fun updateEWork(eWork: EWork): Int {

//        val currentTime = System.currentTimeMillis()
//
//        eWork.stopTime = currentTime
//        eWork.totalTime = currentTime - eWork.startTime
//
//        eWork.time = currentTime.toString()
//        eWork.date = myHelper.getDate(currentTime)
    
    // myHelper.log("updateEWork:$eWork")
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_TOTAL_LOADS, eWork.totalLoads)
    cv.put(COL_END_TIME, eWork.stopTime)
    cv.put(COL_TOTAL_TIME, eWork.totalTime)
    cv.put(COL_TIME, eWork.time.toLong())
    cv.put(COL_DATE, eWork.date)
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(eWork.unloadingGPSLocation)
    )
    cv.put(COL_IS_SYNC, eWork.isSync)
    cv.put(COL_LOADING_MATERIAL_ID, eWork.materialId)
    cv.put(COL_IS_DAY_WORKS, eWork.isDayWorks)
    cv.put(COL_IS_SYNC, eWork.isSync)
    
    // myHelper.log("updatedID :$updatedID ")
    return db.update(TABLE_E_WORK, cv, "$COL_ID = ${eWork.id}", null)
    
  }
  
  fun updateTrip(myData: MyData): Int {
    myHelper.log("updateTrip:$myData")
    val currentTime = System.currentTimeMillis()
    
    myData.stopTime = currentTime
    myData.totalTime = currentTime - myData.startTime
    
    myData.time = currentTime.toString()
    myData.date = myHelper.getDate(currentTime)
    
    // myHelper.log("updateTrip:$myData")
    
    val db = this.writableDatabase
    val cv = ContentValues()
    cv.put(COL_UNLOADING_WEIGHT, myData.unloadingWeight)

//        back unloading
    if (myData.nextAction == 3) {
      cv.put(COL_UNLOADING_TASK_ID, myData.back_unloading_task_id)
      cv.put(COL_UNLOADING_MATERIAL_ID, myData.back_unloading_material_id)
      cv.put(COL_UNLOADING_LOCATION_ID, myData.back_unloading_location_id)
    } else {
      cv.put(COL_UNLOADING_TASK_ID, myData.unloading_task_id)
      cv.put(COL_UNLOADING_MATERIAL_ID, myData.unloading_material_id)
      cv.put(COL_UNLOADING_LOCATION_ID, myData.unloading_location_id)
    }
    
    
    
    cv.put(COL_MACHINE_TYPE_ID, myData.machineTypeId)
    cv.put(COL_MACHINE_ID, myData.machineId)
    
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    cv.put(
      COL_UNLOADING_GPS_LOCATION,
      myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
    )
    cv.put(COL_IS_SYNC, myData.isSync)
    return db.update(TABLE_TRIP, cv, "$COL_ID = ${myData.recordID}", null)
  }
  
  fun updateMachineStop(myData: MyData): Int {
    
    val currentTime = System.currentTimeMillis()
    
    myData.stopTime = currentTime
    myData.totalTime = currentTime - myData.startTime
    
    myData.time = currentTime.toString()
    myData.date = myHelper.getDate(currentTime)
    
    // myHelper.log("updateTrip:$myData")
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    cv.put(COL_END_TIME, myData.stopTime)
    cv.put(COL_TOTAL_TIME, myData.totalTime)
    cv.put(COL_TIME, myData.time.toLong())
    cv.put(COL_DATE, myData.date)
    cv.put(COL_IS_DAY_WORKS, myData.isDayWorks)
    cv.put(
      COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(myData.unloadingGPSLocation)
    )
    cv.put(COL_IS_SYNC, myData.isSync)
    
    // myHelper.log("updatedID :$updatedID ")
    return db.update(TABLE_MACHINES_STOPS, cv, "$COL_ID = ${myData.recordID}", null)
    
  }

/*    fun updateMachineHours(datum: MyData): Int {
        
        val db = this.writableDatabase
        val cv = ContentValues()
        
        val currentTime = System.currentTimeMillis()
        datum.stopTime = currentTime
        
        val time = System.currentTimeMillis()
        datum.time = time.toString()
        datum.date = myHelper.getDate(time.toString())
        
        cv.put(COL_ORG_ID, datum.orgId)
        cv.put(COL_SITE_ID, datum.siteId)
        cv.put(COL_MACHINE_TYPE_ID, datum.machineTypeId)
        cv.put(COL_MACHINE_ID, datum.machineId)
        
        cv.put(COL_START_TIME, datum.startTime)
        cv.put(COL_END_TIME, datum.stopTime)
        
        cv.put(COL_START_HOURS, datum.startHours)
        cv.put(COL_IS_START_HOURS_CUSTOM, datum.isStartHoursCustom)
        cv.put(COL_TOTAL_HOURS, datum.totalHours)
        cv.put(COL_IS_TOTAL_HOURS_CUSTOM, datum.isTotalHoursCustom)
        
        cv.put(COL_TIME, datum.time.toLong())
        cv.put(COL_DATE, datum.date)
        cv.put(COL_IS_DAY_WORKS, datum.isDayWorks)
        
        
        cv.put(
            COL_LOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.loadingGPSLocation)
        )
        cv.put(
            COL_UNLOADING_GPS_LOCATION, myHelper.getGPSLocationToString(datum.unloadingGPSLocation)
        )
        cv.put(COL_USER_ID, datum.operatorId)
        cv.put(COL_IS_SYNC, datum.isSync)
        
        // myHelper.log("MachinesHour:$datum")
        // myHelper.log("MachinesHour:insertID:$insertID")
        return db.update(TABLE_MACHINES_HOURS, cv, "$COL_ID = ${datum.recordID}", null)
    }*/
  
  fun updateOperatorsHours(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_OPERATORS_HOURS, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateOperatorsHours:updateID:$updatedID")
    }
  }
  
  fun updateTrips(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_TRIP, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateTrips:updateID:$updatedID")
    }
  }
  
  fun updateWaits(data: ArrayList<EWork>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_WAITS, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateWaits:updateID:$updatedID")
    }
  }
  
  fun updateMachinesHours(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_MACHINES_HOURS, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateMachinesHours:updateID:$updatedID")
    }
  }
  
  fun updateMachinesStops(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_MACHINES_STOPS, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateMachinesStops:updateID:$updatedID")
    }
  }
  
  fun updateEWorks(data: ArrayList<EWork>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_E_WORK, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateEWorks:updateID:$updatedID")
    }
  }
  
  fun updateELoads(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_E_LOAD_HISTORY, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateELoads:updateID:$updatedID")
    }
  }
  
  fun updateAdminCheckFormsData(data: ArrayList<CheckFormData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_ADMIN_CHECKFORMS_COMPLETED_ID, datum.admin_checkforms_completed_id)
      cv.put(COL_ANSWER, datum.answer)
      cv.put(COL_ANSWER_DATA, myHelper.getAnswerDataToString(datum.answerDataObj))
      cv.put(COL_IS_SYNC, datum.isSync)
      
      val updatedID = db.update(TABLE_ADMIN_CHECKFORMS_DATA, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateAdminCheckFormsData:updateID:$updatedID")
      myHelper.log("updateAdminCheckFormsData:$data")
    }
  }
  
  fun updateAdminCheckFormsCompleted(data: ArrayList<MyData>) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    for (datum in data) {
      cv.put(COL_IS_SYNC, datum.isSync)
      val updatedID = db.update(TABLE_ADMIN_CHECKFORMS_COMPLETED, cv, "$COL_ID = ${datum.id}", null)
      myHelper.log("updateAdminCheckFormsCompleted:updateID:$updatedID")
    }
  }
  
  
  fun updateOrgsMap(datum: MyData) {
    
    val db = this.writableDatabase
    val cv = ContentValues()
    
    cv.put(COL_IS_DOWNLOADED, datum.isDownloaded)
    val updatedID = db.update(TABLE_ORGS_MAPS, cv, "$COL_ID = ${datum.id}", null)
    myHelper.log("updateOrgsMap:updateID:$updatedID")
  }
}