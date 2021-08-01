package com.thetechsamurai.foodonwheelz.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thetechsamurai.foodonwheelz.ui.MainActivity
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.DBAsyncTaskOrder
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.GetOrderList
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.OrderItemEntity

class RecyclerAdapterMenuList(val context: Context, private val itemList: ArrayList<OrderItemEntity>, private val btnProceed: Button, private var recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerAdapterMenuList.MenusViewHolder>() {

    class MenusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCount : TextView = view.findViewById(R.id.txtCount)
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemCost : TextView = view.findViewById(R.id.txtCostforOne)
        val btnChange : Button = view.findViewById(R.id.btnChange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_single_row,parent,false)
        return MenusViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenusViewHolder, position: Int) {
        val itemObject = itemList[position]
        holder.txtCount.text = (position+1).toString()
        holder.txtItemName.text = itemObject.itemName
        holder.txtItemCost.text = context.getString(R.string.f3,itemObject.itemCost)
        val itemEntity = OrderItemEntity(
            itemObject.item_id,
            itemObject.itemName,
            itemObject.itemCost,
            itemObject.itemResID
        )
        holder.btnChange.setOnClickListener {

            if(!DBAsyncTaskOrder((context as MainActivity).applicationContext,itemEntity,1).execute().get()) {
                val added = DBAsyncTaskOrder(context.applicationContext, itemEntity, 2).execute().get()
                if(added) {
                    val list = GetOrderList(context.applicationContext).execute().get()
                    if(list.size <= 0) {
                        btnProceed.visibility = View.INVISIBLE
                    } else {
                        btnProceed.visibility = View.VISIBLE
                    }
                    holder.btnChange.setBackgroundColor(ContextCompat.getColor(context.applicationContext,R.color.colorAccent))
                    holder.btnChange.text = context.getString(R.string.f2)
                    holder.btnChange.setTextColor(ContextCompat.getColor(context.applicationContext,R.color.black))
                } else {
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }
            } else {
                val deleted = DBAsyncTaskOrder(context.applicationContext, itemEntity, 3).execute().get()
                if(deleted) {
                    val list = GetOrderList(context.applicationContext).execute().get()
                    if(list.size <= 0) {
                        btnProceed.visibility = View.INVISIBLE
                    } else {
                        btnProceed.visibility = View.VISIBLE
                    }
                    holder.btnChange.setBackgroundColor(ContextCompat.getColor(context.applicationContext,R.color.colorPrimaryDark))
                    holder.btnChange.text = context.getString(R.string.f1)
                    holder.btnChange.setTextColor(ContextCompat.getColor(context.applicationContext,R.color.white))
                } else {
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }
            }
            if(btnProceed.visibility == View.VISIBLE) {
                val param = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,0,dpToPx())
                recyclerView.layoutParams = param
            } else {
                val param = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,0, 0)
                recyclerView.layoutParams = param
            }
        }
    }
    private fun dpToPx(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, context.resources.displayMetrics).toInt()
}
