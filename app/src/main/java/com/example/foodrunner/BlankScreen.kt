package com.example.foodrunner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BlankScreen : AppCompatActivity() {
    lateinit var okBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_placed_dialog)
        okBtn=findViewById(R.id.btnOk)
        okBtn.setOnClickListener {
            val intent=Intent(this@BlankScreen,Welcome::class.java)
            startActivity(intent)
        }
    }
}