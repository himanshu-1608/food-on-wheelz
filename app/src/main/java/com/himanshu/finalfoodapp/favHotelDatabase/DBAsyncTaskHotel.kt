package com.himanshu.finalfoodapp.favHotelDatabase

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBAsyncTaskHotel(val context: Context, private val HotelEntity: HotelEntity, private val mode: Int) : AsyncTask<Void, Void, Boolean>() {

    // Mode 1 -> Checker
    // Mode 2 -> Add
    // Mode 3 -> Delete

    private val db = Room.databaseBuilder(context, HotelDatabase::class.java,"Hotels").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when(mode) {
            1 -> {
                //Checker
                val hotel : HotelEntity? = db.hotelDao().getHotelById(HotelEntity.hotel_id.toString())
                db.close()
                return hotel != null
            }
            2 -> {
                //Add
                db.hotelDao().insertHotel(HotelEntity)
                db.close()
                return true
            }
            3 -> {
                //Delete
                db.hotelDao().deleteHotel(HotelEntity)
                db.close()
                return true
            }
        }
        return false
    }

}