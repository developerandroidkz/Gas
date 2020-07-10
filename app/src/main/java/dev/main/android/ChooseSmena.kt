package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.android.volley.toolbox.StringRequest
import dev.main.android.Const.getAgzsInfo
import dev.main.android.Const.getSmena
import dev.main.android.Const.startSmena
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONArray
import org.json.JSONObject

class ChooseSmena :AppCompatActivity(){
    var sharedPreference: UserDatas?=null
    var progressDialog: ProgressDialog? = null
    var name: TextView? = null
    var userName: TextView? = null
    var address: TextView? = null
    var userPhone: TextView? = null
    var adapter: ChooseSmenaAdapter? = null
    val arrayList = ArrayList<Smena>()
    var recyclerView: RecyclerView? = null
    var selectedSmena: Smena?=null
    var next: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_smena)
        sharedPreference=UserDatas(this.applicationContext)
        name = findViewById(R.id.name)
        userName = findViewById(R.id.userName)
        address = findViewById(R.id.address)
        userPhone = findViewById(R.id.userPhone)
        next = findViewById(R.id.next)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = GridLayoutManager(this, 1)
        next!!.setOnClickListener {
            if(selectedSmena!=null){
                startWork(selectedSmena!!.start_date,selectedSmena!!.start,selectedSmena!!.end_date,selectedSmena!!.end)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userName!!.text = sharedPreference!!.getUserName()
        userPhone!!.text = sharedPreference!!.getUserPhone()
        if(verifyAvailableNetwork(this)){
            getInfo()
            getList()
        }
        else
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
    }
    private fun getList(){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading_smena_list),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["user_id"] = sharedPreference!!.getUserId() as String
        val request = object : StringRequest(Method.POST,getSmena,Response.Listener<String> { s->
            progressDialog!!.dismiss()
            try {
                var strResp = s.toString()
                val jsonArray = JSONArray(strResp)
                arrayList.clear()
                for (i in 0 until jsonArray.length()) {
                    var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                    arrayList.add(Smena(jsonInner.getString("start"),jsonInner.getString("end"),jsonInner.getString("start_date")
                        ,jsonInner.getString("end_date")
                        ,jsonInner.getString("start_date_time")
                        ,jsonInner.getString("end_date_time")
                        ,jsonInner.getString("start_date_string")
                        ,jsonInner.getString("end_date_string")
                        ,jsonInner.getBoolean("now"),false))
                }
                adapter = ChooseSmenaAdapter(arrayList,this)
                recyclerView!!.adapter = adapter
                adapter!!.onItemClick = {item: Smena ->
                    selectedSmena = item
                    for (i in 0 until arrayList.size){
                        var sme: Smena = arrayList.get(i)
                        if (sme==item){
                            arrayList.removeAt(i)
                            arrayList.add(i, Smena(sme.start,sme.end,sme.start_date,sme.end_date,sme.start_date_time,
                                sme.end_date_time,sme.start_date_string,sme.end_date_string,sme.now,true))
                        }else{
                            arrayList.removeAt(i)
                            arrayList.add(i, Smena(sme.start,sme.end,sme.start_date,sme.end_date,sme.start_date_time,
                                sme.end_date_time,sme.start_date_string,sme.end_date_string,sme.now,false))
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                    next!!.isEnabled=true
                    next!!.visibility= View.VISIBLE
                }
            }catch (e:Exception){
                var strResp = s.toString()
                val jsonObj = JSONObject(strResp)
                Toast.makeText(this,jsonObj.getString("error"), Toast.LENGTH_LONG).show()
            }
        }, Response.ErrorListener { e ->
            // TODO: error response
        }){
            override fun getParams(): Map<String, String> = params
        }

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0,
            1f
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun getInfo(){
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,getAgzsInfo,jsonObject,
            Response.Listener { response ->
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    name!!.text = jsonObj.getString("name")
                    address!!.text = jsonObj.getString("address")
                }catch (e:Exception){
                    onBackPressed()
                }

            }, Response.ErrorListener{

            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0,
            1f
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun startWork(start_date: String, start_time: String, end_date: String, end_time: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.check_data),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["start_date"] = start_date
        params["start_time"] = start_time
        params["end_date"] = end_date
        params["end_time"] = end_time
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,startSmena,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    sharedPreference!!.setLogged()
                    sharedPreference!!.setSmenaId(jsonObj.getString("id"))
                    finish()
                }catch (e:Exception){
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    Toast.makeText(this,jsonObj.getString("error"), Toast.LENGTH_LONG).show()
                    openAbout(jsonObj.getString("submitted_id"));
                }

            }, Response.ErrorListener{

            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0,
            1f
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun openAbout(id: String){
        val intent = Intent(this, AboutSmna::class.java)
        intent.putExtra("smena_id", id)
        startActivity(intent)
    }

}