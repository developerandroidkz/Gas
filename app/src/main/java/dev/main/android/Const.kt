package dev.main.android

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

object Const {
    val BASE_URL = "https://example.site/mobile/"
    val smenaInfoUrl = BASE_URL+"aboutSmena"
    val getOplataInfo = BASE_URL+"getOplataInfo"
    val getTaloonInfo = BASE_URL+"getTaloonInfo"
    val changeTaloonInfo = BASE_URL+"changeTaloonInfo"
    val getAllAgzs = BASE_URL+"getAllAgzs"
    val getSmena = BASE_URL+"getSmena"
    val getAgzsInfo = BASE_URL+"getAgzsInfo"
    val startSmena = BASE_URL+"startSmena"
    val qrScan = BASE_URL+"qrScan"
    val getGazHistory = BASE_URL+"getGazHistory"
    val getInfoUrl = BASE_URL+"getInfo"
    val setOplata = BASE_URL+"setOplata"
    val loginUrl = BASE_URL+"login"
    val submitSmena = BASE_URL+"submitSmena"
    val checkPinCode = BASE_URL+"checkPinCode"

    fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }
}