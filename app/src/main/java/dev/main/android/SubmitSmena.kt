package dev.main.android

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.main.android.Const.checkPinCode
import dev.main.android.Const.getInfoUrl
import dev.main.android.Const.submitSmena
import dev.main.android.Const.verifyAvailableNetwork
import org.json.JSONObject


class SubmitSmena : AppCompatActivity() {
    var sharedPreference: UserDatas?=null
    var mPinLockView: PinLockView?=null
    var start: TextView? = null
    var end: TextView? = null
    var released: TextView? = null
    var progressDialog: ProgressDialog? = null
    var submit: Button? = null
    var about: Button? = null

    private var databaseHelper: DataBase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_1)
        databaseHelper = DataBase(this)
        sharedPreference=UserDatas(this.applicationContext)
        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        released = findViewById(R.id.released)
        mPinLockView  = findViewById(R.id.pin_lock_view)
        about = findViewById(R.id.about)
        mPinLockView!!.setPinLockListener(mPinLockListener)
        val mIndicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots)
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)
        submit = findViewById(R.id.submit)
        submit!!.setOnClickListener {
            run{
                    if(verifyAvailableNetwork(this)){
                        submit()
                    }else{
                        Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
                    }

            }
        }
        about!!.setOnClickListener { run{
            openAbout(sharedPreference!!.getSmenaId() as String)
        } }
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.submit_smena)
        actionbar.setDisplayHomeAsUpEnabled(true)

    }
    private fun openAbout(id: String){
        val intent = Intent(this, AboutSmna::class.java)
        intent.putExtra("smena_id", id)
        startActivity(intent)
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
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,getInfoUrl,jsonObject,
            Response.Listener { response ->
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    start!!.text = "Начало смены: " +jsonObj.getString("start")
                    end!!.text = "Окончание смены: " +jsonObj.getString("end")
                    released!!.text = "Реализовано: " +jsonObj.getString("released")
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
    private fun submit(){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.pin_code_check),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,submitSmena,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    if(jsonObj.getBoolean("result")){
                        databaseHelper!!.clearTable()
                        sharedPreference!!.signOut()
                        finish()
                    }else{
                        Toast.makeText(this,getString(R.string.submit_smena_error), Toast.LENGTH_LONG).show()
                    }
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
    fun login( pincode: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.pin_code_check),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["pin_code"] = pincode
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,checkPinCode,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    if(jsonObj.getBoolean("result")){
                        submit!!.isEnabled = true
                        submit!!.isClickable =true
                        submit!!.visibility = View.VISIBLE
                        mPinLockView!!.visibility = View.GONE
                    }else{

                        mPinLockView!!.resetPinLockView()
                        Toast.makeText(this,getString(R.string.pin_code_is_not_valid), Toast.LENGTH_LONG).show()

                    }
                }catch (e:Exception){
                    mPinLockView!!.resetPinLockView()
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

    private val mPinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            if(verifyAvailableNetwork(this@SubmitSmena)){
                login(pin)
            }else{
                Toast.makeText(this@SubmitSmena,getString(R.string.no_internet), Toast.LENGTH_LONG).show()
            }
        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

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
}