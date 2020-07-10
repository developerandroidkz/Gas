package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import dev.main.android.Const.changeTaloonInfo
import dev.main.android.Const.getTaloonInfo
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject

class ChangeTaloon : AppCompatActivity() {
    var sharedPreference: UserDatas?=null
    var progressDialog: ProgressDialog? = null
    var submit: Button? = null
    var value_in_litr: EditText? = null
    var org: TextView? = null
    var name: TextView? = null
    var number: TextView? = null
    var vin: TextView? = null
    var car_max_litr: TextView? = null
    var date: TextView? = null
    var gaz_history_id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_taloon)
        submit = findViewById(R.id.submit)
        value_in_litr = findViewById(R.id.value_in_litr)
        org = findViewById(R.id.org)
        name = findViewById(R.id.name)
        number = findViewById(R.id.number)
        vin = findViewById(R.id.vin)
        date = findViewById(R.id.date)
        car_max_litr = findViewById(R.id.car_max_litr)
        sharedPreference = UserDatas(this.applicationContext)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.edit)
        actionbar.setDisplayHomeAsUpEnabled(true)
        var bundle :Bundle ?=intent.extras
        gaz_history_id = bundle!!.getString("gaz_history_id")
        if(verifyAvailableNetwork(this))
            getInfo(gaz_history_id)
        else
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        submit!!.setOnClickListener {
            run{
                if(value_in_litr!!.text.toString().isNotEmpty()){
                    if(verifyAvailableNetwork(this))
                        submitChange(value_in_litr!!.text.toString(),gaz_history_id)
                    else
                        Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,getString(R.string.litr), Toast.LENGTH_LONG).show()
                }
            }
        }
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
            Request.Method.POST,getTaloonInfo,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    var car_number = jsonObj.getString("car_number")
                    var car_name = jsonObj.getString("car_name")
                    var car_max = jsonObj.getString("car_max_litr")
                    var date_time = jsonObj.getString("date_time")
                    var zapravlen = jsonObj.getString("litr")
                    var company_name = jsonObj.getString("company_name")
                    org!!.text = company_name
                    name!!.text = car_name
                    number!!.text = car_number
                    car_max_litr!!.text = car_max
                    date!!.text = date_time
                    if(!zapravlen.equals("0")){
                        value_in_litr!!.setText(zapravlen)
                        submit!!.isEnabled = false
                        submit!!.isClickable = false

                    }else{
                        submit!!.isEnabled = true
                        submit!!.isClickable = true
                    }
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

    fun submitChange(litr: String,gas_history_id: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.sending),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        params["gas_history_id"] = gas_history_id
        params["litr"] = litr
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,changeTaloonInfo,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    sharedPreference!!.setGazHistoryId(jsonObj.getString("gaz_history_id"))
                    finish()
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