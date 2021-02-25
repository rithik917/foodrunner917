package com.example.foodrunner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.Database.OrderEntity
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.model.fooditem
import com.example.foodrunner.util.ConnectionManager
import com.example.foodrunner.util.DrawerLocker
import com.example.foodrunner.util.menuAdapter
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantMenu : Fragment() {
    // TODO: Rename and change types of parameters
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RestaurantMenu.
         */
        // TODO: Rename and change types and number of parameters
        @SuppressLint("StaticFieldLeak")
        lateinit var gotocart: Button
        var resId: Int? = 0
        var resName: String? = ""
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var restaurantMenuAdapter: menuAdapter
    private var menuList = arrayListOf<fooditem>()

    private lateinit var rlLoading: RelativeLayout
    private var orderList = arrayListOf<fooditem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false)
        recyclerMenu = view.findViewById(R.id.recyclerMenuItems)
        rlLoading = view.findViewById(R.id.rlLoading)
        rlLoading.visibility = View.VISIBLE
        gotocart=view.findViewById(R.id.btnGoToCart)
        gotocart.visibility=View.GONE
        gotocart.setOnClickListener {
            proceedTocart()
        }
        resId = arguments?.getInt("id")
        resName = arguments?.getString("name")
        (activity as DrawerLocker).setDrawerEnabled(false)
        setHasOptionsMenu(true)
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/" + resId
            Log.d("url",url)
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    rlLoading.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = fooditem(
                                    menuObject.getString("id"),
                                    menuObject.getString("name"),
                                    menuObject.getString("cost_for_one").toInt()
                                )
                                menuList.add(foodItem)
                                val mLayoutManager = LinearLayoutManager(activity)
                                if (activity != null) {
                                    restaurantMenuAdapter=menuAdapter(activity as Context,menuList,object:menuAdapter.OnitemclickListener{
                                        override fun onItemAddListener(fooditem: fooditem) {
                                            orderList.add(fooditem)
                                            if(orderList.size>0){
                                                gotocart.visibility=View.VISIBLE
                                                menuAdapter.isCartEmpty=false
                                            }
                                        }

                                        override fun onItemRemoveListener(fooditem: fooditem) {
                                            orderList.remove(fooditem)
                                            if(orderList.isEmpty()){
                                                gotocart.visibility=View.GONE
                                                menuAdapter.isCartEmpty=true
                                            }
                                        }
                                    })
                                    recyclerMenu.layoutManager = mLayoutManager

                                    recyclerMenu.adapter = restaurantMenuAdapter
                                }
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"

                        /*The below used token will not work, kindly use the token provided to you in the training*/
                        headers["token"] = "45a6a7d55891fc"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }





        return view
    }
    fun proceedTocart() {
        val gson = Gson()
        val fooditems = gson.toJson(orderList)
        val async = ItemsOfCart(activity as Context, resId.toString(), fooditems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data",data)
            startActivity(intent)
        } else {
            Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                .show()

        }
    }
    class ItemsOfCart(context: Context,
    val restaurantId:String,
    val foodItems:String,
    val mode:Int):AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
                    db.orderDao().insertOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
                2->{
                    db.orderDao().deleteOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }

    }




}