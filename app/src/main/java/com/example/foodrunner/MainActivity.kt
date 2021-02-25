package com.example.foodrunner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.example.foodrunner.util.SessionManager

class MainActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            Handler().postDelayed({
                openNewActivity()
            }, 2000)


        } else {
            Handler().postDelayed({
                openNewActivity()
            }, 2000)


        }
    }

    fun openNewActivity() {
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@MainActivity)
        super.onBackPressed()
    }
}




