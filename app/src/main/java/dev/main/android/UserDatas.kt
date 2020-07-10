package dev.main.android

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class UserDatas(val context: Context){
    val mSharedPref: SharedPreferences = context.getSharedPreferences("shared_android_gas_itschool", Activity.MODE_PRIVATE)
    val CHOOSED = "choosed"
    val AGZS = "agzs"
    val USER_ID = "user_id"
    val USER_NAME = "user_name"
    val USER_PHONE = "user_phone"
    val LOGGED = "logged"
    val SMENA_ID = "smena"
    val GAZ_HISTORY_ID = "gaz_history_id"

    fun isChoosed(): Boolean {
        return mSharedPref.getBoolean(CHOOSED,false)
    }

    fun setAgzs(agzsId: String) {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(AGZS, agzsId)
        editor.putBoolean(CHOOSED,true)
        editor.commit()
    }

    fun setGazHistoryId(id: String) {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(GAZ_HISTORY_ID, id)
        editor.commit()
    }

    fun getGazHistoryId():String?{
        return mSharedPref.getString(GAZ_HISTORY_ID,"0")
    }

    fun deleteGazHistoryId() {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.remove(GAZ_HISTORY_ID)
        editor.commit()
    }

    fun setSmenaId(smenaId: String) {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(SMENA_ID, smenaId)
        editor.commit()
    }

    fun getSmenaId():String?{
        return mSharedPref.getString(SMENA_ID,"0")
    }

    fun isLogged(): Boolean{
        return mSharedPref.getBoolean(LOGGED,false)
    }

    fun getChoosedAgzsId():String?{
        return mSharedPref.getString(AGZS,"0")
    }

    fun setLogin(user_id :String,user_name:String,user_phone:String){
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(USER_ID, user_id)
        editor.putString(USER_NAME, user_name)
        editor.putString(USER_PHONE, user_phone)
        editor.commit()
    }
    fun setLogged(){
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putBoolean(LOGGED,true)
        editor.commit()
    }

    fun getUserId():String?{
        return mSharedPref.getString(USER_ID,"0")
    }

    fun getUserName():String?{
        return mSharedPref.getString(USER_NAME,"0")
    }

    fun getUserPhone():String?{
        return mSharedPref.getString(USER_PHONE,"0")
    }

    fun signOut() {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.remove(USER_ID)
        editor.remove(USER_NAME)
        editor.remove(USER_PHONE)
        editor.remove(LOGGED)
        editor.commit()
    }

    fun clear() {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.clear()
        editor.commit()
    }
}