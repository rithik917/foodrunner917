package com.example.foodrunner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.FtsOptions
import com.example.foodrunner.model.OrderDetails
import com.example.foodrunner.model.fooditem
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context:Context,private val orderhistoryList:ArrayList<OrderDetails>):RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryHolder>() {

    class OrderHistoryHolder(view:View):RecyclerView.ViewHolder(view){
val txtResName:TextView=view.findViewById(R.id.resname)
        val txtDate:TextView=view.findViewById(R.id.date)
        val recyclerreshistory:RecyclerView=view.findViewById(R.id.recyclehistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.order_history_custom_row,parent,false)
        return OrderHistoryHolder(view)
    }

    override fun getItemCount(): Int {
        return orderhistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryHolder, position: Int) {
        val historyobject=orderhistoryList[position]
        holder.txtResName.text=historyobject.resName
        holder.txtDate.text=formatDate(historyobject.orderDate)
            setuprecycler(holder.recyclerreshistory,historyobject)

    }
    private fun setuprecycler(recyclerreshistory:RecyclerView,orderhistoryList: OrderDetails){
val fooditemList=ArrayList<fooditem>()
        for (i in 0 until orderhistoryList.foodItem.length()) {
            val historyJson = orderhistoryList.foodItem.getJSONObject(i)
            fooditemList.add(
                fooditem(
                    historyJson.getString("food_item_id"),
                    historyJson.getString("name"),
                    historyJson.getString("cost").toInt()
                )
            )

        }
        val cartItemAdapter=cartAdapter(fooditemList,context)
        val mlayoutManager=LinearLayoutManager(context)
        recyclerreshistory.layoutManager=mlayoutManager
        recyclerreshistory.adapter=cartItemAdapter

        }
    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
    }
