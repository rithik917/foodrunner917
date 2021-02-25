package com.example.foodrunner

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.model.restaurants
import com.example.foodrunner.util.ConnectionManager
import com.example.foodrunner.util.DrawerLocker
import com.example.foodrunner.util.Sorter
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var recyclerView: RecyclerView
    lateinit var adapterHome: homeadapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val restList = arrayListOf<restaurants>()
    private var param1: String? = null
    private var param2: String? = null
    private var checkedItem: Int = -1


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
        // Inflate the layout for this frag
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recycleview)
        progresslayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progresslayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity);
        val requestQueue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        (activity as DrawerLocker).setDrawerEnabled(true)
        setHasOptionsMenu(true)
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    progresslayout.visibility = View.GONE
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success");
                        if (success) {
                            val dataarray = data.getJSONArray("data");
                            for (i in 0 until dataarray.length()) {
                                var jsonObject = dataarray.getJSONObject(i);
                                var rest = restaurants(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("rating"),
                                    jsonObject.getString("cost_for_one"),
                                    jsonObject.getString("image_url")
                                )

                                restList.add(rest)
                                if (activity != null) {
                                    adapterHome = homeadapter(restList, activity as Context);
                                    recyclerView.adapter = adapterHome
                                    recyclerView.layoutManager = layoutManager;


                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context, e.message,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
                        "Volley Exception",
                        Toast.LENGTH_SHORT
                    ).show();
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json";
                        headers["token"] = "45a6a7d55891fc"
                        return headers;

                    }

                }
            requestQueue.add(jsonObjectRequest);
        } else {
            val dialog = AlertDialog.Builder(activity as Context);
            dialog.setTitle("Failure");
            dialog.setMessage("Not Connected to internet");
            dialog.setPositiveButton("Open settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(settingsIntent);
                activity?.finish();
            }
            dialog.setNegativeButton("Close app") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity);
            }
            dialog.create();
            dialog.show();
        }
        return view;

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_sort -> showDialog(activity as Context)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(context: Context) {
        val builder: AlertDialog.Builder? = AlertDialog.Builder(context)
        builder?.setTitle("Sort by?")
        builder?.setSingleChoiceItems(R.array.filters, checkedItem) { _, isChecked ->
            checkedItem = isChecked
        }
        builder?.setPositiveButton("Ok") { _, _ ->

            when (checkedItem) {
                0 -> {
                    Collections.sort(restList, Sorter.costComparator)
                }
                1 -> {
                    Collections.sort(restList, Sorter.costComparator)
                    restList.reverse()
                }
                2 -> {
                    Collections.sort(restList, Sorter.ratingComparator)
                    restList.reverse()
                }
            }
            adapterHome.notifyDataSetChanged()
        }
        builder?.setNegativeButton("Cancel") { _, _ ->

        }
        builder?.create()
        builder?.show()

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}