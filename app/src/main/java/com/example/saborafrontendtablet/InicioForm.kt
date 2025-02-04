package com.example.saborafrontendtablet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.saborafrontendtablet.network.FormsLogic

class InicioForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        enableEdgeToEdge()
        setContentView(R.layout.comenzar_cuestionario)


        val button = findViewById<Button>(R.id.buttonStart)

        button.setOnClickListener {
            val intent = Intent(this,PreguntaActivity::class.java)

            startActivity(intent)
        }



            }

    companion object{

        lateinit var context: Context


        fun getAppContext() : Context {
            return context
        }
    }
}