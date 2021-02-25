package com.example.foodrunner

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
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

class Registration : AppCompatActivity() {
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var btn: Button;
    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var mobile: EditText
    lateinit var address: EditText
    lateinit var password: EditText
    lateinit var confirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn = findViewById(R.id.btnRegister);
        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        mobile = findViewById(R.id.etPhoneNumber);
        password = findViewById(R.id.etPassword)
        address = findViewById(R.id.etAddress)
        confirmPassword = findViewById(R.id.etConfirmPassword)
        sessionManager = SessionManager(this@Registration)
        sharedPreferences = this@Registration.getSharedPreferences(
            sessionManager.PREF_NAME,
            sessionManager.PRIVATE_MODE
        )
        btn.setOnClickListener {
            if (Validations.validateNameLength(name.text.toString())) {
                name.error = null
                if (Validations.validateEmail(email.text.toString())) {
                    email.error = null
                    if (Validations.validateMobile(mobile.text.toString())) {
                        mobile.error = null
                        if (Validations.validatePasswordLength(password.text.toString())) {
                            password.error = null
                            if (Validations.matchPassword(
                                    password.text.toString(),
                                    confirmPassword.text.toString()
                                )
                            ) {
                                password.error = null
                                confirmPassword.error = null

                                val queue = Volley.newRequestQueue(this@Registration);
                                val url = "http://13.235.250.119/v2/register/fetch_result/"
                                val jsonParams = JSONObject();
                                jsonParams.put("name", name.text.toString())
                                jsonParams.put("email", email.text.toString())
                                jsonParams.put("mobile_number", mobile.text.toString())
                                jsonParams.put("address", address.text.toString())
                                jsonParams.put("password", password.text.toString())
                                if (ConnectionManager().isNetworkAvailable(this@Registration)) {
                                    val jsonObjectRequest = object :
                                        JsonObjectRequest(Request.Method.POST,
                                            url,
                                            jsonParams,
                                            Response.Listener {
                                                try {
                                                    val data = it.getJSONObject("data")
                                                    val success = data.getBoolean("success")
                                                    if (success) {
                                                        val obj = data.getJSONObject("data")
                                                        var user_id = obj.getString("user_id")
                                                        var name1 = obj.getString("name");
                                                        var email2 = obj.getString("email");
                                                        var mobile2 =
                                                            obj.getString("mobile_number");
                                                        var address2 = obj.getString("address");
                                                        var password = obj.getString("password");
                                                        savepreferences(
                                                            user_id,
                                                            name1,
                                                            email2,
                                                            mobile2,
                                                            address2,
                                                            password
                                                        );
                                                        val intent = Intent(
                                                            this@Registration,
                                                            Login::class.java
                                                        )
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(
                                                            this, data.getString("errorMessage"),
                                                            Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                                } catch (e: JSONException) {
                                                    Toast.makeText(
                                                        this, e.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            },
                                            Response.ErrorListener {
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
                                        val settingsIntent =
                                            Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                        startActivity(settingsIntent);

                                    }
                                    dialog.setNegativeButton("Close app") { text, listner ->
                                        ActivityCompat.finishAffinity(this);
                                    }
                                    dialog.create();
                                    dialog.show();
                                }
                            } else {

                                password.error = "Passwords don't match"
                                confirmPassword.error = "Passwords don't match"
                                Toast.makeText(
                                    this@Registration,
                                    "Passwords don't match",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {

                            password.error = "Password should be more than or equal 4 digits"
                            Toast.makeText(
                                this@Registration,
                                "Password should be more than or equal 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {

                        mobile.error = "Invalid Mobile number"
                        Toast.makeText(
                            this@Registration,
                            "Invalid Mobile number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {

                    email.error = "Invalid Email"
                    Toast.makeText(this@Registration, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            } else {

                name.error = "Invalid Name"
                Toast.makeText(this@Registration, "Invalid Name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun savepreferences(muser_id:String,mname:String,memail:String,mmobile:String,maddress:String,mpassword:String){

        sharedPreferences.edit().putString("user_id",muser_id).apply()
        sharedPreferences.edit().putString("name",mname).apply();
        sharedPreferences.edit().putString("email",memail).apply();
        sharedPreferences.edit().putString("mobile",mmobile).apply();
        sharedPreferences.edit().putString("address",maddress).apply();
        sharedPreferences.edit().putString("password",mpassword).apply();

    }
    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }
}
