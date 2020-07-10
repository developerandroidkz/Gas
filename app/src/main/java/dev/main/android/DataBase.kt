package dev.main.android

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList

class DataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val allStudentsList: ArrayList<History>
        get() {
            val studentsArrayList = ArrayList<History>()
            var id = 0
            var type = 0
            var stationId = 0
            var staffId = 0
            var smenaHistoryId = 0
            var carId = 0
            var carNumber = ""
            var carName = ""
            var carMaxLitr = ""
            var litr = ""
            var dateTime = ""
            var zapravlen = 0
            val selectQuery = "SELECT  * FROM $TABLE_STUDENTS ORDER BY $KEY_ID DESC"
            val db = this.readableDatabase
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    id = c.getInt(c.getColumnIndex(KEY_ID))
                    type = c.getInt(c.getColumnIndex(KEY_TYPE))
                    stationId = c.getInt(c.getColumnIndex(KEY_STATION_ID))
                    staffId = c.getInt(c.getColumnIndex(KEY_STAFF_ID))
                    smenaHistoryId = c.getInt(c.getColumnIndex(KEY_SMENA_HISTORY_ID))
                    carId = c.getInt(c.getColumnIndex(KEY_CAR_ID))
                    carNumber = c.getString(c.getColumnIndex(KEY_CAR_NUMBER))
                    carName = c.getString(c.getColumnIndex(KEY_CAR_NAME))
                    carMaxLitr = c.getString(c.getColumnIndex(KEY_CAR_MAX_LITR))
                    litr = c.getString(c.getColumnIndex(KEY_LITR))
                    dateTime = c.getString(c.getColumnIndex(KEY_DATE_TIME))
                    zapravlen = c.getInt(c.getColumnIndex(KEY_ZAPRAVLEN))
                    studentsArrayList.add(History(id,type, stationId, staffId, smenaHistoryId, carId, carNumber, carName, carMaxLitr, litr, dateTime, zapravlen))
                } while (c.moveToNext())
                Log.d("array", studentsArrayList.toString())
            }
            return studentsArrayList
        }

    init {
        Log.d("table", CREATE_TABLE_STUDENTS)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_STUDENTS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS '$TABLE_STUDENTS'")
        onCreate(db)
    }

    fun add(history: History): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, history.id)
        values.put(KEY_TYPE, history.type)
        values.put(KEY_STATION_ID, history.station_id)
        values.put(KEY_STAFF_ID, history.staff_id)
        values.put(KEY_SMENA_HISTORY_ID, history.smena_history_id)
        values.put(KEY_CAR_ID, history.car_id)
        values.put(KEY_CAR_NUMBER, history.car_number)
        values.put(KEY_CAR_NAME, history.car_name)
        values.put(KEY_CAR_MAX_LITR, history.car_max_litr)
        values.put(KEY_LITR, history.litr)
        values.put(KEY_DATE_TIME, history.date_time)
        values.put(KEY_ZAPRAVLEN, history.zapravlen)

        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun delete(id: Int):Boolean{
        val db = this.writableDatabase
        return db.delete(TABLE_STUDENTS, KEY_ID+" = "+id,null)>0
    }

    fun clearTable():Boolean{
        val db = this.writableDatabase
        return db.delete(TABLE_STUDENTS, null,null)>0
    }

    companion object {

        var DATABASE_NAME = "gaz"
        private val DATABASE_VERSION = 1
        private val TABLE_STUDENTS = "gas_history"
        private val KEY_ID = "id"
        private val KEY_TYPE = "type"
        private val KEY_STATION_ID = "station_id"
        private val KEY_STAFF_ID = "staff_id"
        private val KEY_SMENA_HISTORY_ID = "smena_history_id"
        private val KEY_CAR_ID = "car_id"
        private val KEY_CAR_NUMBER = "car_number"
        private val KEY_CAR_NAME = "car_name"
        private val KEY_CAR_MAX_LITR = "car_max_litr"
        private val KEY_LITR = "litr"
        private val KEY_DATE_TIME = "date_time"
        private val KEY_ZAPRAVLEN = "zapravlen"

        private val CREATE_TABLE_STUDENTS = ("CREATE TABLE "
                + TABLE_STUDENTS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY ," + KEY_TYPE+" INTEGER,"+ KEY_STATION_ID+" INTEGER,"+ KEY_STAFF_ID+" INTEGER,"+ KEY_SMENA_HISTORY_ID+" INTEGER,"
                + KEY_CAR_ID+" INTEGER,"+ KEY_CAR_NUMBER+" TEXT,"+ KEY_CAR_NAME+" TEXT,"+ KEY_CAR_MAX_LITR+" TEXT,"+ KEY_LITR+" TEXT,"+ KEY_DATE_TIME + " TEXT,"+ KEY_ZAPRAVLEN+" INTEGER );")
    }
}