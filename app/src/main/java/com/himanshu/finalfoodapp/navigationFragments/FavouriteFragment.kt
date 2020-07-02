package com.himanshu.finalfoodapp.navigationFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.favHotelDatabase.GetFavourites
import com.himanshu.finalfoodapp.favHotelDatabase.HotelEntity
import com.himanshu.finalfoodapp.R
import com.himanshu.finalfoodapp.adapters.RecyclerAdapterHotelList

class FavouriteFragment : Fragment() {

    private lateinit var recyclerFav: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutFavManager: LinearLayoutManager
    private lateinit var recyclerFavAdapter: RecyclerAdapterHotelList
    private lateinit var dbHotelList: ArrayList<HotelEntity>
    private lateinit var noResLayout: LinearLayout
    private lateinit var txtNoRestaurants : TextView
    private lateinit var imgNoRestaurants : ImageView
    private lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        sp = (activity as MainActivity).getSharedPreferences("OrderMetaData", Context.MODE_PRIVATE)
        sp.edit().putString("FragType","Favourite").apply()
        noResLayout = view.findViewById(R.id.noResLayout)
        txtNoRestaurants = view.findViewById(R.id.txtNoRestaurants)
        imgNoRestaurants = view.findViewById(R.id.imgNoRestaurants)
        recyclerFav = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        layoutFavManager = LinearLayoutManager(context)
        if(context!=null) {
            dbHotelList = GetFavourites(context!!).execute().get()
            progressLayout.visibility = View.GONE
            recyclerFavAdapter = RecyclerAdapterHotelList(context!!, dbHotelList)
            recyclerFav.adapter = recyclerFavAdapter
            recyclerFav.layoutManager = layoutFavManager
            if(dbHotelList.isEmpty()) {
                noResLayout.visibility = View.VISIBLE
                txtNoRestaurants.visibility = View.VISIBLE
                imgNoRestaurants.visibility = View.VISIBLE
            } else {
                noResLayout.visibility = View.GONE
                txtNoRestaurants.visibility = View.GONE
                imgNoRestaurants.visibility = View.GONE
            }
        } else {
            Toast.makeText(context,"Some Error Occurred", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
