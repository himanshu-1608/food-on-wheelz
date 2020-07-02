package com.himanshu.finalfoodapp.authentication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R
import com.himanshu.finalfoodapp.sideUtils.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var btnlogin: Button
    private lateinit var txtforgot: TextView
    private lateinit var txtsignup: TextView
    private lateinit var imgeye: ImageView
    private lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sp = getSharedPreferences("DataFile", Context.MODE_PRIVATE)
        phone = findViewById(R.id.etphone)
        password = findViewById(R.id.etpassword)
        btnlogin = findViewById(R.id.btnlogin)
        txtforgot = findViewById(R.id.txtforgot)
        txtsignup = findViewById(R.id.txtsignup)
        imgeye = findViewById(R.id.imgeye)
        var checker = false

        imgeye.setOnClickListener {
            if(!checker) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgeye.setBackgroundResource(R.drawable.ic_invisible)
                checker = true
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                imgeye.setBackgroundResource(R.drawable.ic_visible)
                checker = false
            }
        }

        btnlogin.setOnClickListener {
            val phonetext = phone.text.toString().trim()
            val passwordtext = password.text.toString().trim()
            if(validate(phonetext,passwordtext)) {
                if(ConnectionManager().checkConnectivity(this)) {
                    checkLogin(phonetext,passwordtext)
                } else {
                    val dialog = AlertDialog.Builder(this)

                    dialog.setTitle("Error")
                    dialog.setMessage("Internet connection not found!")
                    dialog.setPositiveButton("Open Settings"){ _, _ ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()
                    }
                    dialog.setNeutralButton("Exit"){_,_ ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.show()
                }
            }
        }

        txtforgot.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetActivity::class.java))
        }

        txtsignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }

    }

    private fun checkLogin(phonetext: String, passwordtext: String) {
        val queue = Volley.newRequestQueue(this@LoginActivity)

        val url = "http://13.235.250.119/v2/login/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number",phonetext)
        jsonParams.put("password",passwordtext)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

            try {
                val obj = it.getJSONObject("data")
                val success = obj.getBoolean("success")
                if(success) {
                    val data = obj.getJSONObject("data")
                    sp.edit().putBoolean("isLogged",true).apply()
                    sp.edit().putString("UserID",data.getString("user_id")).apply()
                    sp.edit().putString("UserName",data.getString("name")).apply()
                    sp.edit().putString("UserEmail",data.getString("email")).apply()
                    sp.edit().putString("UserPhone",data.getString("mobile_number")).apply()
                    sp.edit().putString("UserAddress",data.getString("address")).apply()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity,obj.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                }
            } catch (e : Exception) {
                Toast.makeText(this@LoginActivity,"OOPS!! Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        },Response.ErrorListener {
            Toast.makeText(this@LoginActivity, "OOPS!! Some Network Error Occurred", Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "ec7d33dcf400a8"
                return headers
            }
        }

        queue.add(jsonObjectRequest)

    }

    private fun validate(phonetext: String, passwordtext: String): Boolean {
        when {
            phonetext.isEmpty() -> {
                phone.error = "Required!"
                phone.requestFocus()
                return false
            }
            phonetext.length != 10 -> {
                phone.error = "Invalid Phone Number!"
                phone.requestFocus()
                return false
            }
            passwordtext.isEmpty() -> {
                password.error = "Required!"
                password.requestFocus()
                return false
            }
            passwordtext.length <= 4 -> {
                password.error = "Invalid Password!"
                password.requestFocus()
                return false
            }
            else -> {
                return true
            }
        }
    }
}
