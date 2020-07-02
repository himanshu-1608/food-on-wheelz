package com.himanshu.finalfoodapp.favHotelDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Hotels")
data class HotelEntity(
    @PrimaryKey val hotel_id: Int,
    @ColumnInfo(name = "hotel_name") val hotelName: String,
    @ColumnInfo(name = "hotel_rating") val hotelRating: String,
    @ColumnInfo(name = "hotel_cost_for_one") val hotelCost: String,
    @ColumnInfo(name = "hotel_image") val hotelImage: String
)