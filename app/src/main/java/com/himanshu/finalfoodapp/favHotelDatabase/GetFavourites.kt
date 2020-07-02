package com.himanshu.finalfoodapp.favHotelDatabase

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class GetFavourites(val context: Context): AsyncTask<Void, Void, ArrayList<HotelEntity>>() {
    override fun doInBackground(vararg params: Void?): ArrayList<HotelEntity> {
        val send: ArrayList<HotelEntity>
        val db = Room.databaseBuilder(context, HotelDatabase::class.java,"Hotels").build()
        send = db.hotelDao().getAllHotels() as ArrayList<HotelEntity>
        db.close()
        return send
    }
}