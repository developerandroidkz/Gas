package dev.main.android

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.qrScan
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject

class Info : AppCompatActivity() {
    var sharedPreference: UserDatas?=null
    var org: TextView? = null
    var name: TextView? = null
    var number: TextView? = null
    var vin: TextView? = null
    private var carMaxLitr: TextView? = null
    private var createdHistoryId: String = ""
    var progressDialog: ProgressDialog? = null
    var close: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)
        sharedPreference=UserDatas(this.applicationContext)
        org = findViewById(R.id.org)
        name= findViewById(R.id.name)
        number = findViewById(R.id.number)
        vin = findViewById(R.id.vin)
        carMaxLitr = findViewById(R.id.car_max_litr)
        close = findViewById(R.id.close)
        var bundle :Bundle ?=intent.extras
        var qr = bundle!!.getString("qr") // 1
        if(verifyAvailableNetwork(this))
            getInfo(qr)
        else
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        close!!.setOnClickListener {
            run{
                sharedPreference!!.setGazHistoryId(createdHistoryId)
                finish()

            }
        }
    }
    fun getInfo(qr: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.check_qr),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        params["qr"] = qr
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,qrScan,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    org!!.text = jsonObj.getString("company_type_name")+" "+jsonObj.getString("name")
                    name!!.text = jsonObj.getString("car_name")
                    number!!.text = jsonObj.getString("car_number")
                    vin!!.text = jsonObj.getString("car_vin_code")
                    carMaxLitr!!.text = jsonObj.getString("car_max_litr")
                    createdHistoryId = jsonObj.getString("gaz_history_id")
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