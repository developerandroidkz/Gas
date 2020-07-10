package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.getAgzsInfo
import dev.main.android.Const.loginUrl
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject


class PinCode : AppCompatActivity() {
    var sharedPreference: UserDatas?=null
    var mPinLockView: PinLockView?=null
    var name: TextView? = null
    var address: TextView? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_view)
        sharedPreference=UserDatas(this.applicationContext)
        name = findViewById(R.id.name)
        address = findViewById(R.id.address)
        mPinLockView  = findViewById(R.id.pin_lock_view)
        mPinLockView!!.setPinLockListener(mPinLockListener)
        val mIndicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots)
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)

    }

    override fun onResume() {
        super.onResume()
        if(verifyAvailableNetwork(this))
            getInfo()
        else
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
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

    private fun login( pincode: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.pin_code_check),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["pin_code"] = pincode
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,loginUrl,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    sharedPreference!!.setLogin(jsonObj.getString("id"),jsonObj.getString("name"),jsonObj.getString("phone"))
                    finish()
                    startActivity(Intent(this,ChooseSmena::class.java))
                }catch (e:Exception){
                    mPinLockView!!.resetPinLockView()
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

    private val mPinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            if(verifyAvailableNetwork(this@PinCode)){
                login(pin)
            }else{
                Toast.makeText(this@PinCode,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
            }
        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this,Choose::class.java))
    }
}
