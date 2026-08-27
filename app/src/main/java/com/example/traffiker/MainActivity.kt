package com.example.traffiker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // This instantly moves the user to our Smart Dashboard
        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Closes MainActivity so user can't go back to it
    }
}