package com.example.saborafrontendtablet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.saborafrontendtablet.network.LogInLogic

class Menu: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_layout)
        context = this

        val comenzarExperienciaButton = findViewById<Button>(R.id.buttonExperiencia)

        comenzarExperienciaButton.setOnClickListener {
            val intent = Intent(this,EsperandoExperiencia::class.java)
            startActivity(intent)
        }

        val crearFormularioButton = findViewById<Button>(R.id.buttonNuevoForm)

        crearFormularioButton.setOnClickListener {
            val intent = Intent(this,FormCreator::class.java)
            startActivity(intent)
        }

        val volverButton = findViewById<TextView>(R.id.buttonSalir)

        val content = SpannableString(volverButton.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        volverButton.text = content

        // Listener de click para cambiar de pantalla
        volverButton.setOnClickListener {
            val intent = Intent(this, MainInitialScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }


    companion object{

        lateinit var context: Context


        fun getAppContext() : Context {
            return context
        }
    }
}

