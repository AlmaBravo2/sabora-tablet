package com.example.saborafrontendtablet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saborafrontendtablet.network.LogInLogic

class LogIn : AppCompatActivity() {

    private lateinit var loginLogic: LogInLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        context = this

        // Inicializar lógica de login
        loginLogic = LogInLogic()

        // Referencias a los campos y botón
        val usernameField = findViewById<EditText>(R.id.editTextUser)
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonStart)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamar a la lógica de login
            loginLogic.logIn(username, password) { success, message ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                        loggedUserName = username
                        loggedUserPassword = password

                        val intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "El usuario y/o la contraseña no son correctos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val volverButton = findViewById<TextView>(R.id.buttonVolver)

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
        var loggedUserName: String? = null
        var loggedUserPassword: String? = null
        var loggedUserDni: String? = null

        fun getAppContext() : Context{
            return context
        }

    }
}