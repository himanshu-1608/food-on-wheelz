package com.himanshu.finalfoodapp.authentication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.finalfoodapp.sideUtils.ConnectionManager
import com.himanshu.finalfoodapp.ui.MainActivity
import com.himanshu.finalfoodapp.R
import org.json.JSONObject
import java.lang.Exception

class SignupActivity : AppCompatActivity() {

    private lateinit var sp : SharedPreferences
    private lateinit var btnregister : Button
    private lateinit var etname : EditText
    private lateinit var etemail : EditText
    private lateinit var etphone : EditText
    private lateinit var etlocation : EditText
    private lateinit var etpassword : EditText
    private lateinit var etconfirmpass : EditText
    private lateinit var imgeye1: ImageView
    private lateinit var imgeye2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sp = getSharedPreferences("DataFile", Context.MODE_PRIVATE)
        supportActionBar?.title = "Register Yourself"
        etname = findViewById(R.id.etname)
        etemail = findViewById(R.id.etemail)
        etphone = findViewById(R.id.etphone)
        etlocation = findViewById(R.id.etlocation)
        etpassword = findViewById(R.id.etpassword)
        etconfirmpass = findViewById(R.id.etconfirmpass)
        btnregister = findViewById(R.id.btnregister)
        imgeye1 = findViewById(R.id.imgeye1)
        imgeye2 = findViewById(R.id.imgeye2)
        var checker1 = false
        var checker2 = false

        imgeye1.setOnClickListener {
            if(!checker1) {
                etpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgeye1.setBackgroundResource(R.drawable.ic_invisible)
                checker1 = true
            } else {
                etpassword.transformationMethod = PasswordTransformationMethod.getInstance()
                imgeye1.setBackgroundResource(R.drawable.ic_visible)
                checker1 = false
            }
        }

        imgeye2.setOnClickListener {
            if(!checker2) {
                etconfirmpass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgeye2.setBackgroundResource(R.drawable.ic_invisible)
                checker2 = true
            } else {
                etconfirmpass.transformationMethod = PasswordTransformationMethod.getInstance()
                imgeye2.setBackgroundResource(R.drawable.ic_visible)
                checker2 = false
            }
        }

        btnregister.setOnClickListener {
            val userName = etname.text.toString().trim()
            val userEmail = etemail.text.toString().trim()
            val userPhone = etphone.text.toString().trim()
            val userLocation = etlocation.text.toString().trim()
            val userPass = etpassword.text.toString().trim()
            val userConfirm = etconfirmpass.text.toString().trim()
            if(validate(userName,userEmail,userPhone,userLocation,userPass,userConfirm)) {
                if(ConnectionManager().checkConnectivity(this@SignupActivity)) {
                    sendToRegister(userName,userEmail,userPhone,userLocation,userPass)
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
    }

    private fun sendToRegister(userName: String, userEmail: String, userPhone: String, userLocation: String, userPass: String) {
        val query = Volley.newRequestQueue(this@SignupActivity)

        val url = "http://13.235.250.119/v2/register/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("name",userName)
        jsonParams.put("mobile_number",userPhone)
        jsonParams.put("password",userPass)
        jsonParams.put("address",userLocation)
        jsonParams.put("email",userEmail)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {
            try {
                val obj = it.getJSONObject("data")
                val success = obj.getBoolean("success")
                if (success) {
                    val data = obj.getJSONObject("data")
                    sp.edit().putBoolean("isLogged", true).apply()
                    sp.edit().putString("UserID", data.getString("user_id")).apply()
                    sp.edit().putString("UserName", data.getString("name")).apply()
                    sp.edit().putString("UserEmail", data.getString("email")).apply()
                    sp.edit().putString("UserPhone", data.getString("mobile_number")).apply()
                    sp.edit().putString("UserAddress", data.getString("address")).apply()
                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity,obj.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                }
            } catch (e : Exception) {
                Toast.makeText(this@SignupActivity, "Some Unexpected Error Occurred",Toast.LENGTH_LONG).show()
            }
        },Response.ErrorListener {
            Toast.makeText(this@SignupActivity,"Some Network Error Occurred",Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "ec7d33dcf400a8"
                return headers
            }
        }

        query.add(jsonObjectRequest)

    }

    private fun validate(userName: String, userEmail: String, userPhone: String, userLocation: String, userPass: String, userConfirm: String): Boolean {
       if(userName.isEmpty()) {
            etname.error = "Required"
            etname.requestFocus()
            return false
       } else if(userEmail.isEmpty()) {
           etemail.error = "Required"
           etemail.requestFocus()
           return false
       } else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
           etemail.error = "Enter a Valid Email ID!"
           etemail.requestFocus()
           return false
       } else if(userPhone.isEmpty()) {
           etphone.error = "Required"
           etphone.requestFocus()
           return false
       } else if(userPhone.length != 10) {
           etphone.error = "Enter a Valid Mobile Number!"
           etphone.requestFocus()
           return false
       } else if(userLocation.isEmpty()) {
           etlocation.error = "Required"
           etlocation.requestFocus()
           return false
       } else if(userPass.isEmpty()) {
           etpassword.error = "Required"
           etpassword.requestFocus()
           return false
       } else if(userPass.length <= 4) {
           etpassword.error = "Minimum Length 5 Required!"
           etpassword.requestFocus()
           return false
       } else if(userConfirm != userPass) {
           etconfirmpass.error = "Confirmed Password is wrong!"
           etconfirmpass.requestFocus()
           return false
       } else {
           return true
       }
    }

}
