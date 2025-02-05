package com.example.saborafrontendtablet

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saborafrontendtablet.LogIn.Companion.context

class EsperandoExperiencia : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.esperando_experiencia)


        context = this
    }

    companion object{

        lateinit var context: Context


        fun getAppContext() : Context {
            return context
        }
    }
}