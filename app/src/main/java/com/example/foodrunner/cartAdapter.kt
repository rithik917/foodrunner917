package com.example.foodrunner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.model.fooditem

class cartAdapter(private val cartlist:ArrayList<fooditem>,val context: Context):RecyclerView.Adapter<cartAdapter.cartViewHolder>() {
    class cartViewHolder(view:View):RecyclerView.ViewHolder(view){
        val itemName:TextView=view.findViewById(R.id.txtCartItemName)
        val itemCost:TextView=view.findViewById(R.id.txtCartPrice)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.cart_item_custom_row,parent,false)
        return cartViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartlist.size
    }

    override fun onBindViewHolder(holder: cartViewHolder, position: Int) {
        val cartobject=cartlist[position]
        holder.itemName.text=cartobject.name.toString()
        val cost="Rs. ${cartobject.cost?.toString()}"
        holder.itemCost.text=cost

    }
}