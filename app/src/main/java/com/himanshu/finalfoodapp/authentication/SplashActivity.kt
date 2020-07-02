package com.himanshu.finalfoodapp.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R

class SplashActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sp = getSharedPreferences("DataFile", Context.MODE_PRIVATE)
        Handler().postDelayed({
            val isLogged = sp.getBoolean("isLogged", false)
            if (isLogged) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1000)
    }
}
