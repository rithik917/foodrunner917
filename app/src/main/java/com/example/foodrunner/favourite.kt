package com.example.foodrunner

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.Database.RestaurantEntity
import com.example.foodrunner.model.restaurants
import com.example.foodrunner.util.DrawerLocker

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [favourite.newInstance] factory method to
 * create an instance of this fragment.
 */
class favourite : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var rlfavourites:RelativeLayout
    lateinit var recyclerres:RecyclerView
    lateinit var homeadapter: homeadapter
    var restaurantList= ArrayList<restaurants>()
    lateinit var rlNofavourites:RelativeLayout
    lateinit var rlLoading:RelativeLayout

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
        val view=inflater.inflate(R.layout.fragment_favourite, container, false)
        (activity as DrawerLocker).setDrawerEnabled(true)
        rlfavourites=view.findViewById(R.id.rlFavorites)
        recyclerres=view.findViewById(R.id.recyclerRestaurants)
        rlNofavourites=view.findViewById(R.id.rlNoFavorites)
        rlLoading=view.findViewById(R.id.rlLoading)
        rlLoading.visibility=View.VISIBLE
val backgroundList=GetAllRestaurants(activity as Context).execute().get()
        if(backgroundList.isEmpty()){
rlLoading.visibility=View.GONE
            rlfavourites.visibility=View.GONE
rlNofavourites.visibility=View.VISIBLE
        }
        else{
            rlfavourites.visibility=View.VISIBLE
            rlLoading.visibility=View.GONE
            rlNofavourites.visibility=View.GONE
            for(i in backgroundList){
                restaurantList.add(restaurants(i.id.toString(),i.name,i.rating,i.costForTwo,i.imageUrl))
            }
            homeadapter= homeadapter(restaurantList,activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerres.layoutManager=mLayoutManager
            recyclerres.adapter=homeadapter

        }




        return view
    }
    class GetAllRestaurants(context:Context):AsyncTask<Void,Void,List<RestaurantEntity>>(){
        val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            return db.restaurantDao().getAllRestaurants()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment favourite.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            favourite().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}