package com.example.saviri

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

abstract class BaseActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val inactivityTimeout = 1000 // 5 minutes (in milliseconds)
    private var inactivityHandler: Handler? = null
    private val inactivityRunnable = Runnable {
        // Perform logout or any other action on user inactivity
        signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inactivityHandler = Handler()
    }

    override fun onResume() {
        super.onResume()
        startInactivityTimer()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        restartInactivityTimer()
    }

    private fun startInactivityTimer() {
        inactivityHandler?.postDelayed(inactivityRunnable, inactivityTimeout.toLong())
    }

    private fun restartInactivityTimer() {
        inactivityHandler?.removeCallbacks(inactivityRunnable)
        startInactivityTimer()
    }

    private fun signOut() {
        auth.signOut()
        // Redirect to the login screen or perform any other necessary actions
    }
}
