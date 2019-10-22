package dev.main.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreference:UserDatas=UserDatas(this.applicationContext)
        if(sharedPreference.isChoosed()){

        }else{

        }
    }

}
