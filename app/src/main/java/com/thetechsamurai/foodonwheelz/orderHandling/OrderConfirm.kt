package com.thetechsamurai.foodonwheelz.orderHandling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.thetechsamurai.foodonwheelz.ui.MainActivity
import com.thetechsamurai.foodonwheelz.R

class OrderConfirm : AppCompatActivity() {

    private lateinit var btnMainPage: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirm)
        btnMainPage = findViewById(R.id.btnMainPage)
        btnMainPage.setOnClickListener {
            val intent = Intent(this@OrderConfirm, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    override fun onBackPressed() {}
}
