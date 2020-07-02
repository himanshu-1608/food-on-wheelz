package com.himanshu.finalfoodapp.navigationFragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R
import com.himanshu.finalfoodapp.sideUtils.ConnectionManager
import com.himanshu.finalfoodapp.adapters.RecyclerAdapterHotelList
import com.himanshu.finalfoodapp.favHotelDatabase.HotelEntity
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    private lateinit var sp : SharedPreferences
    private var hotelInfoList = ArrayList<HotelEntity>()
    private var hotelInfoListCopy = ArrayList<HotelEntity>()
    private lateinit var progressBar: ProgressBar
    private lateinit var etSearch: EditText
    private lateinit var progressLayout : RelativeLayout
    private lateinit var recyclerHome : RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerHomeAdapter: RecyclerAdapterHotelList
    private lateinit var btnGo: Button


    private var costIncrease = Comparator<HotelEntity>{hotel1,hotel2 ->
        if(hotel1.hotelCost.compareTo(hotel2.hotelCost,true) == 0) {
            hotel1.hotelName.compareTo(hotel2.hotelName,true)
        } else {
            hotel1.hotelCost.compareTo(hotel2.hotelCost,true)
        }
    }

    private var costDecrease = Comparator<HotelEntity>{hotel1,hotel2 ->
        if(hotel2.hotelCost.compareTo(hotel1.hotelCost,true) == 0) {
            hotel1.hotelName.compareTo(hotel2.hotelName,true)
        } else {
            hotel2.hotelCost.compareTo(hotel1.hotelCost,true)
        }
    }

    private var ratingComparator = Comparator<HotelEntity>{hotel1,hotel2 ->
        if(hotel1.hotelRating.compareTo(hotel2.hotelRating,true) == 0) {
            hotel1.hotelName.compareTo(hotel2.hotelName,true)
        } else {
            hotel1.hotelRating.compareTo(hotel2.hotelRating,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        sp = (activity as MainActivity).getSharedPreferences("OrderMetaData",Context.MODE_PRIVATE)
        sp.edit().putString("FragType","Home").apply()
        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressBar = view.findViewById(R.id.progressBar)
        btnGo = view.findViewById(R.id.btnGo)
        etSearch = view.findViewById(R.id.etSearch)
        progressLayout = view.findViewById(R.id.progressLayout)
        layoutManager = LinearLayoutManager(activity)
        progressLayout.visibility = View.VISIBLE
        (activity as MainActivity).supportActionBar?.hide()

        btnGo.setOnClickListener {
            val etSearchText = etSearch.text.toString().trim()
            hotelInfoList = ArrayList()
            if(etSearchText.isEmpty()) {
                hotelInfoListCopy.forEach {
                    hotelInfoList.add(HotelEntity(it.hotel_id,it.hotelName,it.hotelRating,it.hotelCost,it.hotelImage))
                }
            } else {
                hotelInfoListCopy.forEach {
                    if (it.hotelName.contains(etSearchText,true)) {
                        hotelInfoList.add(HotelEntity(it.hotel_id,it.hotelName,it.hotelRating,it.hotelCost,it.hotelImage))
                    }
                }
            }
            recyclerHomeAdapter = RecyclerAdapterHotelList(activity as Context, hotelInfoList)
            recyclerHome.adapter = recyclerHomeAdapter
        }
        if(ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null, Response.Listener {

                try {
                    progressLayout.visibility = View.GONE
                    val output = it.getJSONObject("data")
                    val success = output.getBoolean("success")

                    if(success) {
                        val data = output.getJSONArray("data")

                        for(i in 0 until data.length()) {
                            val hotelJsonObject = data.getJSONObject(i)
                            val hotelObject =
                                HotelEntity(
                                    Integer.parseInt(hotelJsonObject.getString("id")),
                                    hotelJsonObject.getString("name"),
                                    hotelJsonObject.getString("rating"),
                                    hotelJsonObject.getString("cost_for_one"),
                                    hotelJsonObject.getString("image_url")
                                )
                            hotelInfoList.add(hotelObject)
                        }
                        hotelInfoListCopy = hotelInfoList
                        if(activity != null) {
                            (activity as MainActivity).supportActionBar?.show()
                            recyclerHomeAdapter = RecyclerAdapterHotelList(activity as Context, hotelInfoList)
                            recyclerHome.adapter = recyclerHomeAdapter
                            recyclerHome.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context,"Some Error has occurred", Toast.LENGTH_SHORT).show()
                    }

                } catch (e : JSONException) {
                    Toast.makeText(activity as Context,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {
                Toast.makeText(activity as Context,"A Network Error Occurred", Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sortA -> {
                Collections.sort(hotelInfoList,costIncrease)
            }
            R.id.sortB -> {
                Collections.sort(hotelInfoList,costDecrease)
            }
            R.id.sortC -> {
                Collections.sort(hotelInfoList,ratingComparator)
                hotelInfoList.reverse()
            }
        }
        recyclerHomeAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
