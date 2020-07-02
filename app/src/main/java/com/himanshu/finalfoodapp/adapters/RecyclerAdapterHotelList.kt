package com.himanshu.finalfoodapp.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R
import com.himanshu.finalfoodapp.orderHandling.RestaurantDetails
import com.himanshu.finalfoodapp.favHotelDatabase.DBAsyncTaskHotel
import com.himanshu.finalfoodapp.favHotelDatabase.HotelEntity
import com.squareup.picasso.Picasso

class RecyclerAdapterHotelList(val context: Context, private val itemList: ArrayList<HotelEntity>) : RecyclerView.Adapter<RecyclerAdapterHotelList.HotelsViewHolder>() {

    private lateinit var sp : SharedPreferences
    class HotelsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hotelname : TextView = view.findViewById(R.id.hotelnameitem)
        val hotelimage : ImageView = view.findViewById(R.id.hotelimageitem)
        val hotelprice : TextView = view.findViewById(R.id.hotelpriceitem)
        val hotelrating : TextView = view.findViewById(R.id.hotelratingitem)
        val hotelfav : ImageView = view.findViewById(R.id.hotelfavitem)
        val homellsingle : LinearLayout = view.findViewById(R.id.homellsingle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelsViewHolder {
        sp = context.getSharedPreferences("OrderMetaData",Context.MODE_PRIVATE)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_hotel_single_row,parent,false)
        return HotelsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HotelsViewHolder, position: Int) {
        val hotel = itemList[position]
        holder.hotelname.text = hotel.hotelName
        holder.hotelprice.text = context.getString(R.string.f4,hotel.hotelCost)
        holder.hotelrating.text = hotel.hotelRating
        Picasso.get().load(hotel.hotelImage).error(R.drawable.app_image).into(holder.hotelimage)
        holder.homellsingle.setOnClickListener {
            val newFrag = RestaurantDetails()
            sp.edit().putString("ResID",""+hotel.hotel_id).apply()
            sp.edit().putString("ResImage",hotel.hotelImage).apply()
            sp.edit().putString("ResName",hotel.hotelName).apply()
            sp.edit().putString("ResCost",hotel.hotelCost).apply()
            sp.edit().putString("ResRating",hotel.hotelRating).apply()
            (context as MainActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame,newFrag)
                .commit()
        }
        val hotelEntity = HotelEntity(
            hotel.hotel_id,
            hotel.hotelName,
            hotel.hotelRating,
            hotel.hotelCost,
            hotel.hotelImage
        )
        val checkFav = DBAsyncTaskHotel(context.applicationContext, hotelEntity, 1).execute()
        val isFav = checkFav.get()
        if(isFav) {
            holder.hotelfav.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.hotelfav.setImageResource(R.drawable.ic_nonfavourite)
        }
        holder.hotelfav.setOnClickListener {
            if(!DBAsyncTaskHotel(context.applicationContext, hotelEntity, 1).execute().get()) {
                val added = DBAsyncTaskHotel(context.applicationContext, hotelEntity, 2).execute().get()
                if(added) {
                    holder.hotelfav.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }
            } else {
                val deleted = DBAsyncTaskHotel(context.applicationContext, hotelEntity, 3).execute().get()
                if(deleted) {
                    holder.hotelfav.setImageResource(R.drawable.ic_nonfavourite)
                } else {
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}