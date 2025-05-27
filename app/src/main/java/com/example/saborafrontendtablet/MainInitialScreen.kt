package com.example.saborafrontendtablet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class MainInitialScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_initial_screen)

        val button = findViewById<Button>(R.id.buttonIniciarSesion)

        button.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}