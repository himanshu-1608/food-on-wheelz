package com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBAsyncTaskOrder(val context: Context, private val orderItemEntity: OrderItemEntity, private val mode: Int) : AsyncTask<Void, Void, Boolean>() {

    // Mode 1 -> Checker
    // Mode 2 -> Add
    // Mode 3 -> Delete
    // Mode 4 -> Delete All

    private val db = Room.databaseBuilder(context, OrderDatabase::class.java,"Orders").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when(mode) {
            1 -> {
                //Checker
                val check : OrderItemEntity? = db.orderDao().getItemById(orderItemEntity.item_id.toString())
                db.close()
                return check != null
            }
            2 -> {
                //Add
                db.orderDao().insertItem(orderItemEntity)
                db.close()
                return true
            }
            3 -> {
                //Delete
                db.orderDao().removeItem(orderItemEntity)
                db.close()
                return true
            }
            4 -> {
                // Nuke the Table
                db.orderDao().nukeOrderList()
                db.close()
                return true
            }
        }
        return false
    }

}