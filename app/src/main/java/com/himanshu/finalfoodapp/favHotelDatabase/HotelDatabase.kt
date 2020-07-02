package com.himanshu.finalfoodapp.favHotelDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HotelEntity::class],version = 1,exportSchema = false)
abstract class HotelDatabase : RoomDatabase() {
    abstract fun hotelDao() : HotelDao
}