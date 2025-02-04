package com.example.saborafrontendtablet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ComienzoPreguntas:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.cuestiones_producto_inicio)

        val button = findViewById<Button>(R.id.buttonNext)

        // Aquí puedes añadirle funcionalidad al botón si es necesario
        button.setOnClickListener {
            val intent = Intent(this, Pregunta1::class.java)

            startActivity(intent)
        }
    }
}