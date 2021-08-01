package com.thetechsamurai.foodonwheelz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.OrderItemEntity

class RecyclerAdapterCartList(val context: Context, private val itemList: ArrayList<OrderItemEntity>) : RecyclerView.Adapter<RecyclerAdapterCartList.CartsViewHolder>() {

    class CartsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCount : TextView = view.findViewById(R.id.txtCounter)
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemCost : TextView = view.findViewById(R.id.txtItemCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_list_single_row,parent,false)
        return CartsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartsViewHolder, position: Int) {
        val itemObject = itemList[position]
        holder.txtCount.text = (position+1).toString()
        holder.txtItemName.text = itemObject.itemName
        holder.txtItemCost.text = context.getString(R.string.f3,itemObject.itemCost)
    }

}