package com.thetechsamurai.foodonwheelz.favHotelDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HotelDao {

    @Insert
    fun insertHotel(hotelEntity: HotelEntity)

    @Delete
    fun deleteHotel(hotelEntity: HotelEntity)

    @Query("SELECT * FROM Hotels")
    fun getAllHotels(): List<HotelEntity>

    @Query("SELECT * FROM Hotels WHERE hotel_id = :hotelId")
    fun getHotelById(hotelId: String): HotelEntity

    @Query("DELETE FROM Hotels")
    fun deleteAll()
}