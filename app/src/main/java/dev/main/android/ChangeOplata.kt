package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.getOplataInfo
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject


class ChangeOplata : AppCompatActivity() {
    var sharedPreference: UserDatas?=null
    var progressDialog: ProgressDialog? = null
    var value_in_litr: TextView? = null
    var org: TextView? = null
    var name: TextView? = null
    var date: TextView? = null
    var gaz_history_id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_oplata)
        value_in_litr = findViewById(R.id.value_in_litr)
        org = findViewById(R.id.org)
        name = findViewById(R.id.name)
        date = findViewById(R.id.date)
        sharedPreference = UserDatas(this.applicationContext)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.edit)
        actionbar.setDisplayHomeAsUpEnabled(true)
        var bundle : Bundle?=intent.extras
        gaz_history_id = bundle!!.getString("gaz_history_id")
        if(verifyAvailableNetwork(this))
            getInfo(gaz_history_id)
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

    fun getInfo(gas_history_id: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.check_data),true)
        val params = HashMap<String,String>()
        params["gas_history_id"] = gas_history_id
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,getOplataInfo,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    var car_name = jsonObj.getString("car_name")
                    var date_time = jsonObj.getString("date_time")
                    var zapravlen = jsonObj.getString("litr")
                    var company_name = jsonObj.getString("car_number")
                    org!!.text = company_name
                    name!!.text = car_name
                    date!!.text = date_time
                    value_in_litr!!.text = zapravlen
                }catch (e:Exception){
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
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