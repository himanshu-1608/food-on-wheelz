package com.thetechsamurai.foodonwheelz.orderHandling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.thetechsamurai.foodonwheelz.ui.MainActivity
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.adapters.RecyclerAdapterMenuList
import com.thetechsamurai.foodonwheelz.favHotelDatabase.DBAsyncTaskHotel
import com.thetechsamurai.foodonwheelz.favHotelDatabase.HotelEntity
import com.thetechsamurai.foodonwheelz.navigationFragments.HomeFragment
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.*
import com.thetechsamurai.foodonwheelz.sideUtils.ConnectionManager
import com.thetechsamurai.foodonwheelz.navigationFragments.FavouriteFragment
import org.json.JSONException


class RestaurantDetails : Fragment() {

    private lateinit var menuInfoList : ArrayList<OrderItemEntity>
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var iconFav: ImageView
    private lateinit var recyclerViewOrder: RecyclerView
    private lateinit var btnProceed: Button
    private lateinit var toolbar: Toolbar
    private lateinit var resID: String
    private lateinit var resName: String
    private lateinit var resRating: String
    private lateinit var resCost: String
    private lateinit var resImage: String
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerItemAdapter: RecyclerAdapterMenuList
    private lateinit var sp : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_details, container, false)

        sp = (activity as MainActivity).getSharedPreferences("OrderMetaData",Context.MODE_PRIVATE)
        toolbar = view.findViewById(R.id.secondaryToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            if(GetOrderList((activity as MainActivity).applicationContext).execute().get().size == 0) {
                if(sp.getString("FragType","") == "Home") {
                    (activity as MainActivity).setUpFragment(HomeFragment(),"All Restaurants")
                } else {
                    (activity as MainActivity).setUpFragment(FavouriteFragment(),"Favourites")
                }
            } else {
                val builder = AlertDialog.Builder(activity as Context)
                builder.setTitle("Confirmation")
                    .setCancelable(false)
                    .setMessage("All cart items will be removed.Do you want to proceed?")
                    .setPositiveButton("Yes") { _, _ ->
                        if(sp.getString("FragType","") == "Home") {
                            (activity as MainActivity).setUpFragment(HomeFragment(),"All Restaurants")
                        } else {
                            (activity as MainActivity).setUpFragment(FavouriteFragment(),"Favourites")
                        }
                    }
                    .setNegativeButton("No", null)
                val alert = builder.create()
                alert.show()
            }
        }
        (activity as MainActivity).supportActionBar?.hide()

        resID = sp.getString("ResID","").toString()
        resName = sp.getString("ResName","").toString()
        resRating = sp.getString("ResRating","").toString()
        resCost = sp.getString("ResCost","").toString()
        resImage = sp.getString("ResImage","").toString()

        toolbar.title = resName
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        iconFav = view.findViewById(R.id.iconFav)
        recyclerViewOrder = view.findViewById(R.id.recyclerViewOrder)
        btnProceed = view.findViewById(R.id.btnProceed)
        layoutManager = LinearLayoutManager(activity)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        val hotelEntity = HotelEntity(
            Integer.parseInt(resID),
            resName,
            resRating,
            resCost,
            resImage
        )
        val checkFav = DBAsyncTaskHotel((activity as MainActivity).applicationContext, hotelEntity, 1).execute()
        val isFav = checkFav.get()
        if(isFav) {
            iconFav.setImageResource(R.drawable.ic_favourite)
        } else {
            iconFav.setImageResource(R.drawable.ic_nonfavourite)
        }
        iconFav.setOnClickListener{
            if(!DBAsyncTaskHotel((activity as MainActivity).applicationContext, hotelEntity, 1).execute().get()) {
                val added = DBAsyncTaskHotel((activity as MainActivity).applicationContext, hotelEntity, 2).execute().get()
                if(added) {
                    iconFav.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context,"Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val deleted = DBAsyncTaskHotel((activity as MainActivity).applicationContext, hotelEntity, 3).execute().get()
                if(deleted) {
                    iconFav.setImageResource(R.drawable.ic_nonfavourite)
                } else {
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnProceed.setOnClickListener {
            startActivity(Intent((activity as MainActivity), CartActivity::class.java))
        }

        if(ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(context)

            var url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            url += resID

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    val output = it.getJSONObject("data")
                    val success = output.getBoolean("success")
                    if(success) {
                        menuInfoList = ArrayList()
                        val data = output.getJSONArray("data")

                        for(i in 0 until data.length()) {
                            val menuJsonObject = data.getJSONObject(i)
                            val menuObject =
                                OrderItemEntity(
                                    Integer.parseInt(menuJsonObject.getString("id")),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id")
                                )
                            menuInfoList.add(menuObject)
                        }
                        if(activity != null) {
                            recyclerItemAdapter = RecyclerAdapterMenuList(activity as Context, menuInfoList,btnProceed,recyclerViewOrder)
                            recyclerViewOrder.adapter = recyclerItemAdapter
                            recyclerViewOrder.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context,"Some Error has occurred", Toast.LENGTH_SHORT).show()
                    }
                } catch (e : JSONException) {
                    Toast.makeText(activity as Context,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
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