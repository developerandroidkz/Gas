package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.setOplata
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject

class Oplata : AppCompatActivity() {
    var nal: ToggleButton? =null
    var type: String = "2"
    var sharedPreference: UserDatas?=null
    var progressDialog: ProgressDialog? = null
    var submit: Button? = null
    var value: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.oplata)
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.money)
        actionbar.setDisplayHomeAsUpEnabled(true)
        nal = findViewById(R.id.nal)
        sharedPreference=UserDatas(this.applicationContext)
        value = findViewById(R.id.val_on_litr)
        submit = findViewById(R.id.submit)
        nal!!.setOnClickListener {
            type = if(nal!!.isChecked) "2" else "3"
        }
        submit!!.setOnClickListener {
            run{
                val litr = value!!.text.toString()
                if(litr.isNotEmpty()){
                    if(verifyAvailableNetwork(this))
                        getInfo(litr)
                    else
                        Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,getString(R.string.litr), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun getInfo(litr: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.sending),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        params["type_id"] = type
        params["litr"]=litr
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,setOplata,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    var createdHistoryId = jsonObj.getString("gaz_history_id")
                    sharedPreference!!.setGazHistoryId(createdHistoryId)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}