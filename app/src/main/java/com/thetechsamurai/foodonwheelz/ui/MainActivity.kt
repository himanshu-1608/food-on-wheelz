package com.thetechsamurai.foodonwheelz.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.thetechsamurai.foodonwheelz.R
import com.thetechsamurai.foodonwheelz.authentication.LoginActivity
import com.thetechsamurai.foodonwheelz.favHotelDatabase.DBAsyncTaskHotel
import com.thetechsamurai.foodonwheelz.favHotelDatabase.HotelEntity
import com.thetechsamurai.foodonwheelz.navigationFragments.*
import com.thetechsamurai.foodonwheelz.orderHandling.RestaurantDetails
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.DBAsyncTaskOrder
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.GetOrderList
import com.thetechsamurai.foodonwheelz.orderHandling.orderDatabase.OrderItemEntity


class MainActivity : AppCompatActivity() {

    private lateinit var sp : SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frame: FrameLayout
    private lateinit var navigationView: NavigationView
    private var previous : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences("DataFile", Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navView)

        setUpToolbar()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        setUpFragment(HomeFragment(),"All Restaurants")

        navigationView.setCheckedItem(R.id.home)

        navigationView.setNavigationItemSelectedListener {

            if(previous!=null) {
                previous?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previous = it
            when(it.itemId){
                R.id.home -> {
                    setUpFragment(HomeFragment(),"All Restaurants")
                }
                R.id.profile -> {
                    setUpFragment(ProfileFragment(),"Profile")
                }
                R.id.favourite -> {
                    setUpFragment(FavouriteFragment(),"Favourites")
                }
                R.id.orders -> {
                    setUpFragment(OrderHistory(),"My Previous Orders")
                }
                R.id.faq -> {
                    setUpFragment(FaqFragment(),"FAQs")
                }
                R.id.logout -> {
                    sendAlert()
                }
            }
            drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
    }

    private fun sendAlert() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Confirmation")
            .setCancelable(false)
            .setMessage("Are You sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                sp.edit().clear().apply()
                val hotelEntity = HotelEntity(1,"DeleteName","DeleteRating","DeleteCost","DeleteImages")
                DBAsyncTaskHotel(applicationContext,hotelEntity,4)
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
        val alert = builder.create()
        alert.show()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val header = navigationView.getHeaderView(0)
        val txtUserName : TextView = header.findViewById(R.id.txtUserName)
        txtUserName.text = sp.getString("UserName","")
        val txtUserPhone : TextView = header.findViewById(R.id.txtUserPhone)
        txtUserPhone.text = sp.getString("UserPhone","")
    }

    fun setUpFragment(whichFragment : Fragment,whichTitle : String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                whichFragment
            )
            .commit()
        supportActionBar?.title = whichTitle
        supportActionBar?.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return
        }
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            is RestaurantDetails -> {
               if(GetOrderList(applicationContext).execute().get().size == 0) {
                   val sp2 = getSharedPreferences("OrderMetaData", Context.MODE_PRIVATE)
                   if(sp2.getString("FragType","") == "Home") {
                       setUpFragment(HomeFragment(),"All Restaurants")
                   } else {
                       setUpFragment(FavouriteFragment(),"Favourites")
                   }
               } else {
                   val builder = AlertDialog.Builder(this@MainActivity)
                   builder.setTitle("Confirmation")
                       .setCancelable(false)
                       .setMessage("All cart items will be removed.Do you want to proceed?")
                       .setPositiveButton("Yes") { _, _ ->
                           DBAsyncTaskOrder(applicationContext,OrderItemEntity(0,"Nuke","The","Table"),4).execute().get()
                           val sp2 = getSharedPreferences("OrderMetaData", Context.MODE_PRIVATE)
                           if(sp2.getString("FragType","") == "Home") {
                               setUpFragment(HomeFragment(),"All Restaurants")
                           } else {
                               setUpFragment(FavouriteFragment(),"Favourites")
                           }
                       }
                       .setNegativeButton("No", null)
                   val alert = builder.create()
                   alert.show()
               }
            }
            !is HomeFragment -> {
                setUpFragment(HomeFragment(),"All Restaurants")
                navigationView.setCheckedItem(R.id.home)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}
