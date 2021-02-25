package com.example.foodrunner

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.Database.RestaurantEntity
import com.example.foodrunner.model.restaurants
import com.squareup.picasso.Picasso

class homeadapter(private var restaurants: ArrayList<restaurants>,val context:Context):RecyclerView.Adapter<homeadapter.homeViewHolder>() {
    class homeViewHolder(view:View):RecyclerView.ViewHolder(view){
        val resThumbnail = view.findViewById(R.id.imgRestaurantThumbnail) as ImageView
        val restaurantName = view.findViewById(R.id.txtRestaurantName) as TextView
        val rating = view.findViewById(R.id.txtRestaurantRating) as TextView
        val cost = view.findViewById(R.id.txtCostForTwo) as TextView
        val cardRestaurant = view.findViewById(R.id.cardRestaurant) as CardView
        val favImage = view.findViewById(R.id.imgIsFav) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): homeViewHolder {
        val itemview=LayoutInflater.from(parent.context).inflate(R.layout.restaurant_single_row,parent,false)
        return homeViewHolder(itemview)
    }
companion object{
    var resId:Int=0
}
    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: homeViewHolder, position: Int) {
val restaurant=restaurants[position];
        holder.restaurantName.text=restaurant.name;
        holder.rating.text=restaurant.rating;
        holder.cost.text= "${restaurant.cost_for_one.toString()}/person";
        Picasso.get().load(restaurant.image_url).into(holder.resThumbnail);
val listOfFavourites=getAllOrders(context).execute().get()
        if(listOfFavourites.isNotEmpty()&&listOfFavourites.contains(restaurant.id.toString())){
            holder.favImage.setImageResource(R.drawable.ic_action_fav_checked)
        }
        else{
            holder.favImage.setImageResource(R.drawable.ic_action_fav)
        }

        holder.favImage.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.id.toInt(),
                restaurant.name,
                restaurant.rating,
                restaurant.cost_for_one.toString(),
                restaurant.image_url
            )

            if (!Dbasync(context, restaurantEntity, 1).execute().get()) {
//                agar restaurant pehle se nhi hai tab usko add karo
                val async =
                    Dbasync(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_action_fav_checked)
                }
            } else {
//                agar pehle se hai to usko delete karo
                val async = Dbasync(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_action_fav)
                }
            }
        }
        holder.cardRestaurant.setOnClickListener{
            resId=restaurant.id.toInt()
val fragment=RestaurantMenu()
             val args=Bundle()
            args.putInt("id",restaurant.id.toInt())
            args.putString("name",restaurant.name)
            fragment.arguments=args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title = holder.restaurantName.text.toString()
        }
    }
    class Dbasync(context:Context,val restaurantEntity: RestaurantEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
                    val resEntity:RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return resEntity!=null
                }
                2->{
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3->{
db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

            }
            return false

        }

    }
    class getAllOrders(context: Context):AsyncTask<Void,Void,List<String>>(){
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg p0: Void?): List<String> {
            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds

        }

    }
}