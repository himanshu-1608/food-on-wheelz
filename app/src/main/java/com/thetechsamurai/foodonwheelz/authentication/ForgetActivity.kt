package com.thetechsamurai.foodonwheelz.authentication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.thetechsamurai.foodonwheelz.sideUtils.ConnectionManager
import com.thetechsamurai.foodonwheelz.R
import org.json.JSONObject
import java.lang.Exception

class ForgetActivity : AppCompatActivity() {

    private lateinit var etphone : EditText
    private lateinit var etemail : EditText
    private lateinit var btnnext : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        etphone = findViewById(R.id.etphone)
        etemail = findViewById(R.id.etemail)
        btnnext = findViewById(R.id.btnnext)

        btnnext.setOnClickListener {
            val phoneText = etphone.text.toString().trim()
            val emailText = etemail.text.toString().trim()
            if(validate(phoneText,emailText)) {
                if(ConnectionManager().checkConnectivity(this@ForgetActivity)) {
                    sendOTP(phoneText,emailText)
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

    private fun sendOTP(phoneText: String, emailText: String) {

        val queue = Volley.newRequestQueue(this@ForgetActivity)

        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonParams =  JSONObject()

        jsonParams.put("mobile_number",phoneText)
        jsonParams.put("email",emailText)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {
            try{
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                if(success) {
                    val check = data.getBoolean("first_try")
                    if(check) {
                        Toast.makeText(this@ForgetActivity, "OTP Sent to $emailText",Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ForgetActivity,"Reuse OTP sent to $emailText",Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this@ForgetActivity,ResetPassword::class.java)
                    intent.putExtra("UserPhone",phoneText)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ForgetActivity,data.getString("errorMessage"),Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ForgetActivity,"OOPS! Some Unexpected Error Occurred!",Toast.LENGTH_SHORT).show()
            }
        },Response.ErrorListener {
            Toast.makeText(this@ForgetActivity,"OOPS! Some Network Error Occurred!",Toast.LENGTH_SHORT).show()
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

    private fun validate(phoneText: String, emailText: String): Boolean {
        if(phoneText.isEmpty()) {
            etphone.error = "Required"
            etphone.requestFocus()
            return false
        } else if(phoneText.length != 10) {
            etphone.error = "Enter a valid Mobile Number"
            etphone.requestFocus()
            return false
        } else if(emailText.isEmpty()) {
            etemail.error = "Required"
            etemail.requestFocus()
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            etemail.error = "Enter a Valid Email ID!"
            etemail.requestFocus()
            return false
        } else {
            return true
        }
    }
}