package dev.main.android

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.getAllAgzs
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONArray
import org.json.JSONObject


class Choose : AppCompatActivity() {
    val arrayList = ArrayList<Item>()
    private var recyclerView: RecyclerView? = null
    var adapter: ChooseAdapter? = null
    var sharedPreference: UserDatas?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose)
        sharedPreference=UserDatas(this.applicationContext)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = GridLayoutManager(this, 1)

    }

    override fun onResume() {
        super.onResume()
        if (verifyAvailableNetwork(this))
            getList()
        else
            Toast.makeText(this,getString(R.string.no_internet),Toast.LENGTH_LONG).show()
    }
    fun getList(){
        val request = JsonArrayRequest(
            Request.Method.POST,getAllAgzs,null,
            Response.Listener { response ->
                try {
                    var strResp = response.toString()
                    val jsonArray = JSONArray(strResp)
                    arrayList.clear()
                    for (i in 0 until jsonArray.length()) {
                        var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        arrayList.add(Item(jsonInner.getString("id"),
                            jsonInner.getString("name"),
                            jsonInner.getString("address")))
                    }
                    adapter = ChooseAdapter(arrayList,this)
                    recyclerView!!.adapter = adapter
                    adapter!!.onItemClick = {item: Item ->
                        sharedPreference!!.setAgzs(item.id)
                        finish()
                        startActivity(Intent(this,PinCode::class.java))
                    }
                }catch (e:Exception){
                    Log.i("Exception","$e")
                }

            }, Response.ErrorListener{
                Log.i("ErrorListener","$it")
            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0,
            1f
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}