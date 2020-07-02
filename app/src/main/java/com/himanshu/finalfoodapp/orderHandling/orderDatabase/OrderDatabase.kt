package com.himanshu.finalfoodapp.orderHandling.orderDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderItemEntity::class],version = 1,exportSchema = false)
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao() : OrderDao
}