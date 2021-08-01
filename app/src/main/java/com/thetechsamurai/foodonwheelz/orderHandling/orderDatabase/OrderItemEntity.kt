package com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Orders")
data class OrderItemEntity(
    @PrimaryKey val item_id : Int,
    @ColumnInfo(name = "item_name") val itemName : String,
    @ColumnInfo(name = "item_cost") val itemCost : String,
    @ColumnInfo(name = "item_res_id") val itemResID : String
)