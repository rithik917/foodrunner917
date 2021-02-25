package com.example.foodrunner.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.model.fooditem

class menuAdapter(val context: Context
                  ,private val menuList:ArrayList<fooditem>,
                  private val listener:OnitemclickListener
):RecyclerView.Adapter<menuAdapter.MenuViewHolder>() {
    interface OnitemclickListener{
        fun onItemAddListener(fooditem: fooditem)
        fun onItemRemoveListener(fooditem: fooditem)
    }

    class MenuViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val foodItemName: TextView = view.findViewById(R.id.txtItemName)
        val foodItemCost: TextView = view.findViewById(R.id.txtItemCost)
        val sno: TextView = view.findViewById(R.id.txtSNo)
        val addToCart: Button = view.findViewById(R.id.btnAddToCart)
        val removeFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }
    companion object {
        var isCartEmpty = true

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
val itemView=LayoutInflater.from(parent.context).inflate(R.layout.menu_custom_row,parent,false)
        return MenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
val menuObject=menuList[position]
        holder.foodItemName.text=menuObject.name

        val cost = "Rs. ${menuObject.cost?.toString()}"
        holder.foodItemCost.text = cost
        holder.sno.text = (position + 1).toString()
        holder.addToCart.setOnClickListener {
            holder.addToCart.visibility=View.GONE
            holder.removeFromCart.visibility=View.VISIBLE
            listener.onItemAddListener(menuObject)
        }
        holder.removeFromCart.setOnClickListener {
            holder.removeFromCart.visibility=View.GONE
            holder.addToCart.visibility=View.VISIBLE
            listener.onItemRemoveListener(menuObject)
        }
    }

}