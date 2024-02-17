package com.example.saviri

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.saviri.databinding.ActivityMainBinding
import com.example.saviri.ui.login.LoginFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var  isAfterIdle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navhost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.loginFragment,
            R.id.homeFragment2,
            R.id.allShopping,
            R.id.add_Item_cart,
            R.id.fragment_countryList,
        ).build()

        val navcontroller = navhost.navController

        setupActionBarWithNavController(navcontroller,appBarConfiguration)


    }

    override fun onSupportNavigateUp(): Boolean {

        val navController  = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()

    }

    override fun onPause() {
        super.onPause()

        var timer = Timer()
        Log.i("TAG", "onPause:--- Invoking Logout")
        val logout = LogOutTimerTask()
        timer.schedule(logout,300000)
    }

    override fun onResume() {
        super.onResume()
        var timer = Timer()
        Log.i("TAG", "onResume:--- Invoking Logout")
        if (isAfterIdle) {
            Log.d("TAG", "run: ------------------asdas")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_host_fragment, true)
                .build()
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
            Log.d("---TAG---", "run: asdadweafsdc")
        }
        timer.cancel()
        isAfterIdle = false
    }

    private inner class LogOutTimerTask : TimerTask() {

        override fun run() {
             isAfterIdle=true

        }
    }
}