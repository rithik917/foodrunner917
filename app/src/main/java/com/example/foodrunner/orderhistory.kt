package com.example.foodrunner

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.model.OrderDetails
import com.example.foodrunner.util.DrawerLocker
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [orderhistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class orderhistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var orderHistoryList = ArrayList<OrderDetails>()
    private lateinit var llHasOrders: LinearLayout
    private lateinit var rlNoOrders: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rlLoading: RelativeLayout
    private var userId = ""

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
        val view=inflater.inflate(R.layout.fragment_orderhistory, container, false)
recyclerOrderHistory=view.findViewById(R.id.recyclerOrderHistory)
        (activity as DrawerLocker).setDrawerEnabled(true)
        llHasOrders = view.findViewById(R.id.llHasOrders)
        rlNoOrders = view.findViewById(R.id.rlNoOrders)
        rlLoading = view?.findViewById(R.id.rlLoading) as RelativeLayout

        sharedPreferences =
            (activity as Context).getSharedPreferences("Food File", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null) as String
        sendServerRequest(userId)
        return view
    }
private fun sendServerRequest(userId:String) {
    val queue = Volley.newRequestQueue(activity as Context)
    val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
    val jsonObjectRequest =
        object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
            rlLoading.visibility = View.GONE
            try {
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                if (success) {
                    val orderarray = data.getJSONArray("data")
                    if (orderarray.length() == 0) {
                        llHasOrders.visibility = View.GONE
                        rlNoOrders.visibility = View.VISIBLE
                    } else {
                        for (i in 0 until orderarray.length()) {
                            val orderobject = orderarray.getJSONObject(i)
                            val fooditems = orderobject.getJSONArray("food_items")
                            val orderDetails = OrderDetails(
                                orderobject.getInt("order_id"),
                                orderobject.getString("restaurant_name"),
                                orderobject.getString("order_placed_at"),
                                fooditems
                            )
                            orderHistoryList.add(orderDetails)
                            if (orderHistoryList.isEmpty()) {
                                llHasOrders.visibility = View.GONE
                                rlNoOrders.visibility = View.VISIBLE
                            } else {
                                llHasOrders.visibility = View.VISIBLE
                                rlNoOrders.visibility = View.GONE
                                if (activity != null) {
                                    orderHistoryAdapter =
                                        OrderHistoryAdapter(activity as Context, orderHistoryList)
                                    val mLayoutManager =
                                        LinearLayoutManager(activity as Context)
                                    recyclerOrderHistory.layoutManager = mLayoutManager
                                    recyclerOrderHistory.itemAnimator = DefaultItemAnimator()
                                    recyclerOrderHistory.adapter = orderHistoryAdapter
                                } else {
                                    queue.cancelAll(this::class.java.simpleName)
                                }

                            }
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
}
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment orderhistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            orderhistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}