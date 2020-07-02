package com.himanshu.finalfoodapp.navigationFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.himanshu.finalfoodapp.R

class ProfileFragment : Fragment() {

    private var sp: SharedPreferences? = null
    private lateinit var txtName : TextView
    private lateinit var txtPhone : TextView
    private lateinit var txtEmail : TextView
    private lateinit var txtDelivery : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sp = this.activity?.getSharedPreferences("DataFile",0)
        txtName = view.findViewById(R.id.txtName)
        txtPhone = view.findViewById(R.id.txtPhone)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtDelivery = view.findViewById(R.id.txtDelivery)

        if(sp != null) {
            txtName.text = sp?.getString("UserName","--")
            txtPhone.text = sp?.getString("UserPhone","--")
            txtEmail.text = sp?.getString("UserEmail","--")
            txtDelivery.text = sp?.getString("UserAddress","--")
        } else {
            Toast.makeText(activity as Context,"Error fetching User Details", Toast.LENGTH_SHORT).show()
        }
        return view
    }

}
