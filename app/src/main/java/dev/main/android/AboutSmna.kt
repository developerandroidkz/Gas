package dev.main.android

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.smenaInfoUrl
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONArray
import org.json.JSONObject

class AboutSmna : AppCompatActivity() {
    var start: TextView? = null
    var end: TextView? = null
    var released: TextView? = null
    var smena_id: String = ""
    var adapter: CompanyAdapter? = null
    val arrayList = ArrayList<Company>()
    var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_smena)
        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        released = findViewById(R.id.released)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.about_smena)
        actionbar.setDisplayHomeAsUpEnabled(true)
        var bundle : Bundle?=intent.extras
        smena_id = bundle!!.getString("smena_id")
        recyclerView = findViewById(R.id.recycleView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = GridLayoutManager(this, 1)
        if(verifyAvailableNetwork(this))
            getInfo(smena_id)
        else
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getInfo(smena_id: String){
        val params = HashMap<String,String>()
        params["smena_id"] = smena_id
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,smenaInfoUrl,jsonObject,
            Response.Listener { response ->
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    var released_s = jsonObj.getString("released")
                    var start_s = jsonObj.getString("start")
                    var end_s = jsonObj.getString("end")
                    released!!.text = released_s
                    start!!.text = start_s
                    end!!.text = end_s
                    val jsonArray = JSONArray(jsonObj.getString("companies"))
                    arrayList.clear()
                    for (i in 0 until jsonArray.length()) {
                        var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        arrayList.add(Company(jsonInner.getString("name"),
                            jsonInner.getString("litr"),jsonInner.getString("bin")))
                    }
                    adapter = CompanyAdapter(arrayList,this)
                    recyclerView!!.adapter = adapter
                    adapter!!.onItemClick = { _: Company ->
                        //var bin = item.bin
                    }

                }catch (e:Exception){
                    var strResp = response.toString()
                    val jsonObj: JSONObject = JSONObject(strResp)
                    Toast.makeText(this,jsonObj.getString("error"), Toast.LENGTH_LONG).show()
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


}