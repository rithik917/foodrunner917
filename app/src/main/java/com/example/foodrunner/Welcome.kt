package com.example.foodrunner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.example.foodrunner.util.DrawerLocker
import com.example.foodrunner.util.SessionManager
import com.example.foodrunner.util.menuAdapter
import com.google.android.material.navigation.NavigationView

class Welcome : AppCompatActivity(),DrawerLocker {

    lateinit var drawerLayout: DrawerLayout;
    lateinit var toolbar: androidx.appcompat.widget.Toolbar;
    lateinit var coordinatorLayout: CoordinatorLayout;
    lateinit var frameLayout: FrameLayout;
    lateinit var navigationView: NavigationView;
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var previousitem: MenuItem? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager
    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled) {
            DrawerLayout.LOCK_MODE_UNLOCKED
        } else {
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        }
        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome);
        sessionManager = SessionManager(this)
        sharedPreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)
        drawerLayout = findViewById(R.id.DrawerLayout);
        toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.co)
        frameLayout = findViewById(R.id.frameLayout);
        sharedPreferences = getSharedPreferences("Food File", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.navigationview);
        setuptoolbar()
        openhome()
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this@Welcome,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener {
            if (previousitem != null) {
                previousitem?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousitem = it
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, home())
                        .addToBackStack("home")
                        .commit();
                    supportActionBar?.title = "Restaurants"
//                    setDrawerEnabled(true)
                    drawerLayout.closeDrawers();
                }
                R.id.myprofile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, myprofile())
                        .addToBackStack("myprofile")
                        .commit();
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()

                }
                R.id.Favourite -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, favourite())
                        .addToBackStack("favourites")
                        .commit();
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, orderhistory())
                        .addToBackStack("orderhistory")
                        .commit();
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.Faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, faq())
                        .addToBackStack("faq")
                        .commit()
                    supportActionBar?.title = "Faq"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@Welcome)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sessionManager.setLogin(false)
                            sharedPreferences.edit().clear().apply()
                            startActivity(Intent(this@Welcome, Login::class.java))
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, home())
                                .addToBackStack("home")
                                .commit();
                            supportActionBar?.title = "Restaurants"
//                    setDrawerEnabled(true)
                            drawerLayout.closeDrawers();
                        }
                        .create()
                        .show()

                }
            }


            return@setNavigationItemSelectedListener true;
        }
        val convertView = LayoutInflater.from(this@Welcome).inflate(R.layout.header, null)
        var mname: TextView = convertView.findViewById(R.id.name)
        var mmobile: TextView = convertView.findViewById(R.id.phone)
        mname.text = sharedPreferences.getString("name", null)
        mmobile.text = "+91 -${sharedPreferences.getString("mobile", null)}"
        navigationView.addHeaderView(convertView)


    }


    fun setuptoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title";
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    fun openhome() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, home())
            .addToBackStack("home")
            .commit();
        supportActionBar?.title = "Restaurants"
//        setDrawerEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        val f = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (id) {
            android.R.id.home -> {
                if (f is RestaurantMenu) {
                    onBackPressed()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)

                }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            is home -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is RestaurantMenu -> {
                if (!menuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@Welcome)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->

                            val clearCart =
                                CartActivity.ClearDBAsync(applicationContext,homeadapter.resId.toString()).execute()
                                    .get()
                            openhome()
                            menuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    openhome()
                }
            }
        }
    }
}