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

    fun isChoosed(): Boolean {
        return mSharedPref.getBoolean(CHOOSED,false)
    }

    fun setAgzs(agzsId: String) {
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(AGZS, agzsId)
        editor.putBoolean(CHOOSED,true)
        editor.commit()
    }

    fun isLogged(): Boolean{
        return mSharedPref.getBoolean(LOGGED,false)
    }

    fun setLogin(user_id :String,user_name:String,user_phone:String){
        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(USER_ID, user_id)
        editor.putString(USER_NAME, user_name)
        editor.putString(USER_PHONE, user_phone)
        editor.putBoolean(LOGGED,true)
        editor.commit()
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