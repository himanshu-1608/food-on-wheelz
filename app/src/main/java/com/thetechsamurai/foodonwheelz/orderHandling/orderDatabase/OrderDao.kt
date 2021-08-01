package com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertItem(orderItemEntity: OrderItemEntity)

    @Delete
    fun removeItem(orderItemEntity: OrderItemEntity)

    @Query("SELECT * FROM Orders")
    fun getAllItems():List<OrderItemEntity>

    @Query("SELECT * FROM Orders WHERE item_id = :itemId")
    fun getItemById(itemId: String): OrderItemEntity

    @Query("DELETE FROM Orders")
    fun nukeOrderList()

}