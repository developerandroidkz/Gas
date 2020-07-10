package dev.main.android

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dev.main.android.Const.getGazHistory
import dev.main.android.Const.getInfoUrl
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    var sharedPreference:UserDatas? = null
    var scannedResult: String = ""
    var talon: Button? = null
    var oplata: Button? = null
    private var databaseHelper: DataBase? = null
    private var arrayList: ArrayList<History>? = null
    private var recyclerView: RecyclerView? = null
    var start: TextView? = null
    var end: TextView? = null
    var litr: TextView? = null
    var progressDialog: ProgressDialog? = null
    var adapter: MainAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreference =UserDatas(this.applicationContext)
        talon = findViewById(R.id.talon)
        oplata = findViewById(R.id.oplata)
        talon!!.setOnClickListener{
          run{
              IntentIntegrator(this@MainActivity).initiateScan()
          }
        }
        oplata!!.setOnClickListener {
            run{
                startActivity(Intent(this,Oplata::class.java))
            }
        }
        databaseHelper = DataBase(this)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = GridLayoutManager(this, 1)

        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        litr = findViewById(R.id.litr)
    }

    override fun onResume() {
        super.onResume()
        if(sharedPreference!!.isChoosed()){
            if(sharedPreference!!.isLogged()){
                this.supportActionBar!!.title = sharedPreference!!.getUserName()
                reloadList()
                var historyId = sharedPreference!!.getGazHistoryId()
                if(!historyId.equals("0")){
                    getHistory(historyId!!)
                }
            }else{
                startActivity(Intent(this,PinCode::class.java))
            }
        }else{
            startActivity(Intent(this,Choose::class.java))
        }
    }

    private fun reloadList(){
        arrayList = databaseHelper!!.allStudentsList
        adapter = MainAdapter(arrayList!!,this)
        recyclerView!!.adapter = adapter
        adapter!!.onItemClick = {item: History ->
            val value: String = item.id.toString()
            if(item.type==1){
                val intent = Intent(this, ChangeTaloon::class.java)
                intent.putExtra("gaz_history_id", value)
                startActivity(intent)
            }else{
                val intent = Intent(this, ChangeOplata::class.java)
                intent.putExtra("gaz_history_id", value)
                startActivity(intent)
            }
        }
        getInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            if(result.contents != null){
                scannedResult = result.contents
               scanned(scannedResult)
            } else {
                Toast.makeText(this,getString(R.string.scan_error), Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            scannedResult = it.getString("scannedResult")
            scanned(scannedResult)
        }
    }

    fun scanned(result: String){
        val intent = Intent(this, Info::class.java)
        intent.putExtra("qr", result)
        startActivity(intent)
    }

    private fun getHistory(id: String){
        progressDialog = ProgressDialog.show(this,"",getString(R.string.loading),true)
        val params = HashMap<String,String>()
        params["agzs_id"] = sharedPreference!!.getChoosedAgzsId() as String
        params["staff_id"] = sharedPreference!!.getUserId() as String
        params["smena_history_id"] = sharedPreference!!.getSmenaId() as String
        params["gas_history_id"] = id
        val jsonObject = JSONObject(params)
        val request = JsonObjectRequest(
            Request.Method.POST,getGazHistory,jsonObject,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    var strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    var id = jsonObj.getInt("id")
                    var type = jsonObj.getInt("type")
                    var stationId = jsonObj.getInt("station_id")
                    var staffId = jsonObj.getInt("staff_id")
                    var smenaHistoryId = jsonObj.getInt("smena_history_id")
                    var carId = jsonObj.getInt("car_id")
                    var carNumber = jsonObj.getString("car_number")
                    var carName = jsonObj.getString("name")
                    var carMaxLitr = jsonObj.getString("car_max_litr")
                    var litr = jsonObj.getString("litr")
                    var dateTime = jsonObj.getString("date_time")
                    var zapravlen = jsonObj.getInt("zapravlen")
                    databaseHelper!!.delete(id)
                    databaseHelper!!.add(History(id,type,stationId,staffId,smenaHistoryId,carId,carNumber,carName,carMaxLitr,litr,dateTime,zapravlen))
                    sharedPreference!!.deleteGazHistoryId()
                    reloadList()
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
                    start!!.text = jsonObj.getString("start")
                    end!!.text = jsonObj.getString("end")
                    litr!!.text = jsonObj.getString("released")
                }catch (e:Exception){
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_one) {
            val intent = Intent(this, SubmitSmena::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
