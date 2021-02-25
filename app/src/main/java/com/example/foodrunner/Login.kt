package com.example.foodrunner

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.util.ConnectionManager
import com.example.foodrunner.util.SessionManager
import com.example.foodrunner.util.Validations
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    lateinit var mobile:EditText;
    lateinit var pass:EditText;
    lateinit var login:Button;
    lateinit var forgotPassword:TextView
    lateinit var signup:TextView
    lateinit var sharedPreferences: SharedPreferences;
    lateinit var sessionManager:SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login);
        title = "LOGIN"
        sessionManager = SessionManager(this)
        sharedPreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE);

//        if(isLoggedIn){
//            val intent = Intent(this@Login, Welcome::class.java)
//            startActivity(intent);
//            finish();
////            isko finish kar diya kyonki jab agli screen pe aagye hai to hme wapas nhi jaana hai login par aur agr jyenge bhi to tb bhi islogged in true hoga jisse wo waps list pe chala jyega
//        }
        mobile = findViewById(R.id.editTextPhone);
        pass = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.btn);
        forgotPassword = findViewById(R.id.forgotpassword);
        signup = findViewById(R.id.signupnow);

        login.setOnClickListener {
            var mphone: String = mobile.text.toString();
            var mpass: String = pass.text.toString()
            val queue = Volley.newRequestQueue(this@Login)
            val url = "http://13.235.250.119/v2/login/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mphone)
            jsonParams.put("password", mpass)
            if (Validations.validateMobile(mobile.text.toString()) && Validations.validatePasswordLength(
                    pass.text.toString()
                )
            ) {
                if (ConnectionManager().isNetworkAvailable(this@Login)) {
                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "mobile",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("email", response.getString("email")).apply()
                                    sessionManager.setLogin(true)
                                    val intent = Intent(this@Login, Welcome::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@Login,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this, e.message,
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                            , Response.ErrorListener {

                                Toast.makeText(
                                    this,
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
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this);
                    dialog.setTitle("Failure");
                    dialog.setMessage("Not Connected to internet");
                    dialog.setPositiveButton("Open settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(settingsIntent);

                    }
                    dialog.setNegativeButton("Close app") { text, listner ->
                        ActivityCompat.finishAffinity(this);
                    }
                    dialog.create();
                    dialog.show();
                }
            } else {
                Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
            forgotPassword.setOnClickListener {
                var intent = Intent(this@Login, ForgotPassword::class.java);
                startActivity(intent);
            }
            signup.setOnClickListener {
                var intent = Intent(this@Login, Registration::class.java);
                startActivity(intent);
            }
        }

    override fun onPause() {
        super.onPause()
        finish();
    }
    fun savepreferences(){
//        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();

    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@Login)
        finish()
        super.onBackPressed()
    }
}