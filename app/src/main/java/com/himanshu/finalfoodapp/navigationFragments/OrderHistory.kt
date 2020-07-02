package com.himanshu.finalfoodapp.navigationFragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R
import com.himanshu.finalfoodapp.adapters.RecyclerAdapterHistoryList
import com.himanshu.finalfoodapp.sideUtils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class OrderHistory : Fragment() {

    private lateinit var layDefault: RelativeLayout
    private lateinit var mainDefault: RelativeLayout
    private lateinit var hotelList: ArrayList<String>
    private lateinit var dateList: ArrayList<String>
    private lateinit var itemListNames: ArrayList<ArrayList<String>>
    private lateinit var itemListCosts: ArrayList<ArrayList<String>>
    private lateinit var sp:SharedPreferences
    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var recyclerAdapterHistoryList: RecyclerAdapterHistoryList
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sp = (activity as MainActivity).getSharedPreferences("DataFile",Context.MODE_PRIVATE)
        hotelList = ArrayList()
        dateList = ArrayList()
        itemListCosts = ArrayList(ArrayList())
        itemListNames = ArrayList(ArrayList())
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layDefault = view.findViewById(R.id.layDefault)
        mainDefault = view.findViewById(R.id.mainLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        layoutManager = LinearLayoutManager(activity)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        if(ConnectionManager().checkConnectivity(activity as MainActivity)) {
            val queue = Volley.newRequestQueue(context)

            var url = "http://13.235.250.119/v2/orders/fetch_result/"
            url += sp.getString("UserID","")

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,Response.Listener {
                try {
                    progressBar.visibility = View.GONE
                    progressLayout.visibility = View.GONE

                    val output = it.getJSONObject("data")
                    val success = output.getBoolean("success")
                    if(success) {

                        val data = output.getJSONArray("data")

                        if(data.length()==0) {
                            layDefault.visibility = View.VISIBLE
                            mainDefault.visibility = View.GONE
                        } else {
                            layDefault.visibility = View.GONE
                            mainDefault.visibility = View.VISIBLE
                            for(i in 0 until data.length()) {
                                val orderItem = data.getJSONObject(i)

                                val resName = orderItem.getString("restaurant_name")
                                hotelList.add(resName)

                                val dateTime = orderItem.getString("order_placed_at")
                                //              0123456789
                                //  dateTime =  01-02-20 11:34:15
                                val dateOrder = dateTime.substring(0,2) + "/" + dateTime.substring(3,5) + "/20" + dateTime.substring(6,8)
                                dateList.add(dateOrder)

                                val itemList = orderItem.getJSONArray("food_items")
                                val shortArrayName: ArrayList<String> = ArrayList()
                                val shortArrayCost: ArrayList<String> = ArrayList()
                                for(j in 0 until itemList.length()) {
                                    val subJson = itemList.get(j) as JSONObject
                                    val foodItemName = subJson.getString("name")
                                    val foodItemCost = subJson.getString("cost")
                                    shortArrayName.add(foodItemName)
                                    shortArrayCost.add(foodItemCost)
                                }
                                itemListNames.add(shortArrayName)
                                itemListCosts.add(shortArrayCost)
                            }
                            if(activity != null) {
                                recyclerAdapterHistoryList = RecyclerAdapterHistoryList(activity as Context,hotelList,dateList,itemListNames,itemListCosts)
                                recyclerOrderHistory.adapter = recyclerAdapterHistoryList
                                recyclerOrderHistory.layoutManager = layoutManager
                            }
                        }
                    } else {
                        Toast.makeText(activity as Context,"Some Error has occurred", Toast.LENGTH_SHORT).show()
                    }
                } catch(e: JSONException) {
                    Toast.makeText(activity as Context,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                println(it)
                Toast.makeText(activity as Context,"Oops!! A Network error appeared!!", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(activity as Context)

            dialog.setTitle("Error")
                .setCancelable(false)
            dialog.setMessage("Internet connection not found!")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                (activity as MainActivity).finish()
            }
            dialog.setNeutralButton("Exit"){_,_ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.show()
        }
        return view
    }

}