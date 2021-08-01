package com.thetechsamurai.foodonwheelz.orderHandling

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.adapters.RecyclerAdapterCartList
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.DBAsyncTaskOrder
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.GetOrderList
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.OrderItemEntity
import com.thetechsamurai.foodonwheelz.sideUtils.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var sp1: SharedPreferences
    private lateinit var sp2: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartInfoList : ArrayList<OrderItemEntity>
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var resID: String
    private lateinit var btnOrder: Button
    private lateinit var txtResName: TextView
    private lateinit var resName: String
    private var mLastClickTime: Long = 0
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerCartAdapter: RecyclerAdapterCartList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sp1 = getSharedPreferences("OrderMetaData",MODE_PRIVATE)
        sp2 = getSharedPreferences("DataFile",MODE_PRIVATE)
        resID = sp1.getString("ResID","").toString()
        resName = sp1.getString("ResName","").toString()
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerCart)
        txtResName = findViewById(R.id.txtResName)
        txtResName.text = getString(R.string.f5,resName)
        btnOrder = findViewById(R.id.btnOrder)
        layoutManager = LinearLayoutManager(this@CartActivity)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        cartInfoList = GetOrderList(applicationContext).execute().get()
        var totalCost = 0
        for(i in 0 until cartInfoList.size) {
            totalCost += Integer.parseInt(cartInfoList[i].itemCost)
        }
        btnOrder.text = getString(R.string.f6,totalCost)
        btnOrder.setOnClickListener {

            if(SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if(ConnectionManager().checkConnectivity(this@CartActivity)) {
                val queue = Volley.newRequestQueue(this@CartActivity)

                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val jsonParams = JSONObject()
                jsonParams.put("user_id",sp2.getString("UserID",""))
                jsonParams.put("restaurant_id",resID)
                jsonParams.put("total_cost",""+totalCost)
                val jsonArray = JSONArray()
                for(i in 0 until cartInfoList.size) {
                    jsonArray.put(JSONObject().put("food_item_id",""+cartInfoList[i].item_id))
                }
                jsonParams.put("food",jsonArray)

                val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {
                    DBAsyncTaskOrder(applicationContext,OrderItemEntity(0,"Nuke","The","Table"),4).execute().get()
                    try {
                        val obj = it.getJSONObject("data")
                        if(obj.getBoolean("success")){
                            val intent = Intent(this@CartActivity, OrderConfirm::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@CartActivity,obj.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@CartActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this@CartActivity,"Oops!! A Network error appeared!!", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "ec7d33dcf400a8"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)

                dialog.setTitle("Error")
                    .setCancelable(false)
                dialog.setMessage("Internet connection not found!")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNeutralButton("Exit"){_,_ ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.show()
            }
        }
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        recyclerCartAdapter = RecyclerAdapterCartList(this@CartActivity, cartInfoList)
        recyclerView.adapter = recyclerCartAdapter
        recyclerView.layoutManager = layoutManager

    }
}
