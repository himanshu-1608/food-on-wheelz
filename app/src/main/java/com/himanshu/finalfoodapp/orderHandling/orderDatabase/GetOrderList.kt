package com.himanshu.finalfoodapp.orderHandling.orderDatabase

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class GetOrderList(val context: Context): AsyncTask<Void, Void, ArrayList<OrderItemEntity>>() {
    override fun doInBackground(vararg params: Void?): ArrayList<OrderItemEntity> {
        val send: ArrayList<OrderItemEntity>
        val db = Room.databaseBuilder(context.applicationContext, OrderDatabase::class.java,"Orders").build()
        send = db.orderDao().getAllItems() as ArrayList<OrderItemEntity>
        db.close()
        return send
    }
}