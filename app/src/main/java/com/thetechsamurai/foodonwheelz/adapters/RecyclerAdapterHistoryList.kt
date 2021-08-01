package com.thetechsamurai.foodonwheelz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.OrderItemEntity

class RecyclerAdapterHistoryList(val context: Context, private val hotelList: ArrayList<String>, private val dateList: ArrayList<String>, private val itemListName: ArrayList<ArrayList<String>>, private val itemListCost: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<RecyclerAdapterHistoryList.ItemsViewHolder>() {

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderHotel : TextView = view.findViewById(R.id.txtOrderHotel)
        val txtOrderName : TextView = view.findViewById(R.id.txtOrderDate)
        val subList : RecyclerView = view.findViewById(R.id.subList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_single_row,parent,false)
        return ItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hotelList.size
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.txtOrderHotel.text = hotelList[position]
        holder.txtOrderName.text = dateList[position]
        val itemList = ArrayList<OrderItemEntity>()
        for(i in 0 until itemListName[position].size) {
            itemList.add(OrderItemEntity(0,itemListName[position][i],itemListCost[position][i],"1"))
        }
        val listAdapter = RecyclerAdapterCartList(context,itemList)
        holder.subList.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        holder.subList.layoutManager = linearLayoutManager
    }
}