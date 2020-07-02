package com.himanshu.finalfoodapp.authentication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.himanshu.finalfoodapp.sideUtils.ConnectionManager
import com.himanshu.finalfoodapp.R
import org.json.JSONObject
import java.lang.Exception

class ResetPassword : AppCompatActivity() {

    private lateinit var etotp : EditText
    private lateinit var etPassword : EditText
    private lateinit var etConfirm : EditText
    private lateinit var btnSubmit : Button
    private var phoneText : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        if(intent != null) {
            phoneText = intent.getStringExtra("UserPhone")
        }
        etotp = findViewById(R.id.etotp)
        etPassword = findViewById(R.id.etpassword)
        etConfirm = findViewById(R.id.etconfirmpass)
        btnSubmit = findViewById(R.id.btnsubmit)

        btnSubmit.setOnClickListener {
            val otpText = etotp.text.toString().trim()
            val passwordText = etPassword.text.toString().trim()
            val confirmText = etConfirm.text.toString().trim()
            if(validate(otpText,passwordText,confirmText)) {
                if(ConnectionManager().checkConnectivity(this@ResetPassword)) {
                    resetPassword(otpText,passwordText,phoneText)
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

    private fun resetPassword(otpText: String, passwordText: String, phoneText: String?) {
        val queue = Volley.newRequestQueue(this@ResetPassword)

        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val jsonParams =  JSONObject()

        jsonParams.put("mobile_number",phoneText)
        jsonParams.put("password",passwordText)
        jsonParams.put("otp",otpText)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {
            try{
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                if(success) {
                    Toast.makeText(this@ResetPassword,data.getString("successMessage"),Toast.LENGTH_LONG).show()
                    getSharedPreferences("DataFile", Context.MODE_PRIVATE).edit().clear().apply()
                    val intent = Intent(this@ResetPassword,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ResetPassword,data.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResetPassword,"OOPS! Some Unexpected Error Occurred!", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            Toast.makeText(this@ResetPassword,"OOPS! Some Network Error Occurred!", Toast.LENGTH_SHORT).show()
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

    private fun validate(otpText: String, passwordText: String, confirmText: String): Boolean {
        when {
            otpText.isEmpty() -> {
                etotp.error = "Required"
                etotp.requestFocus()
                return false
            }
            otpText.length != 4 -> {
                etotp.error = "Incorrect OTP"
                etotp.requestFocus()
                return false
            }
            passwordText.isEmpty() -> {
                etPassword.error = "Required"
                etPassword.requestFocus()
                return false
            }
            passwordText.length <= 4 -> {
                etPassword.error = "Use a strong password(Min. Length 5)"
                etPassword.requestFocus()
                return false
            }
            confirmText.isEmpty() -> {
                etConfirm.error = "Required"
                etConfirm.requestFocus()
                return false
            }
            confirmText != passwordText -> {
                etConfirm.error = "Confirmed Password is wrong"
                etConfirm.requestFocus()
                return false
            }
            else -> {
                return true
            }
        }
    }
}
