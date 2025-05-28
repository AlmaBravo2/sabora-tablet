package com.example.saborafrontendtablet

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saborafrontendtablet.LogIn.Companion.context
import com.example.saborafrontendtablet.connection.UDPReceiver
import com.example.saborafrontendtablet.network.EsperandoExperienciaLogic

class EsperandoExperiencia : AppCompatActivity() {

    private val udpReceiver = UDPReceiver(3940)
    private val logic = EsperandoExperienciaLogic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.esperando_experiencia)

        context = this

        logic.registerTablet("NuevaTablet")
        udpReceiver.stopListening()
        udpReceiver.startListening { json ->
            Log.i("UDP", "Mensaje recibido : $json")
            /*recibiré algo así??
            *{
              "id_usuario": "5678",
              "id_experiencia": "1234",
              "formularios": [
                {
                  "form": "formulario1",

                },
                {
                  "form": "formulario2",
                },
                {
                  "form": "formulario3",
                  "campo1": "valor5",
                  "campo2": "valor6"
                }
              ]
            }
            */

            //aquí dentro cogemos lo que nos llegue del json y actuamos con esa info
            try{
                val idExperiencia = json.optString("id_experience", "0")
                val idUsuario = json.optString("id_user", "0")
                //val idFormulario = json.optString("id_form", "")
                val formularios = json.optJSONArray("id_form")

                Log.i("UDP", "Extraídos -> id_user: $idUsuario, id_experience: $idExperiencia, id_form: $formularios")

                // Lista para almacenar los ID de los formularios
                val formIds = mutableListOf<String>()

                if (formularios != null) {
                    // Iterar sobre el array de formularios y agregar los IDs a la lista
                    for (i in 0 until formularios.length()) {
                        val idFormulario = formularios.optInt(i, -1)  // Obtener el ID de cada formulario
                        if (idFormulario != -1) {
                            formIds.add(idFormulario.toString())  // Convertirlo a String y agregarlo
                        }
                    }
                } else{

                    Log.i("UDP", "El campo 'id_form' no está presente o es null")
                }

                Log.i("UDPReceiver", "id_experience: $idExperiencia, id_user: $idUsuario, formularios: $formIds")

                if (formIds.isNotEmpty()) {
                    Log.d("UDPReceiver", "Lanzando PreguntaActivity con los datos recibidos")
                    val intent = Intent(this, PreguntaActivity::class.java).apply {
                        putStringArrayListExtra("formIds", ArrayList(formIds))
                        putExtra("idUsuario", idUsuario)
                        putExtra("idExperiencia", idExperiencia)
                    }
                    startActivity(intent)
                } else{
                    Log.w("UDPReceiver", "No se recibieron IDs de formulario válidos")
                }
            } catch (e: Exception) {
                Log.e("UDPReceiver", "Error procesando los datos: ${e.message}")
            }


        }

        Log.d("EsperandoExperiencia", "Escuchando mensajes UDP...")

            val imageView = findViewById<ImageView>(R.id.logoSabora)

            val fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f)
            val fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)

            fadeOut.duration = 1000 // duración en milisegundos
            fadeIn.duration = 1000

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(fadeOut, fadeIn)
            animatorSet.startDelay = 500
            animatorSet.start()

            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animatorSet.start() // vuelve a empezar
                }
            })
            animatorSet.start()

            val volverButton = findViewById<TextView>(R.id.buttonVolver)

            val content = SpannableString(volverButton.text)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            volverButton.text = content

            // Listener de click para cambiar de pantalla
            volverButton.setOnClickListener {
                val intent = Intent(this, Menu::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }


        }

    override fun onDestroy() {
        super.onDestroy()
        udpReceiver.stopListening()
        Log.i("EsperandoExperiencia", "UDP Receiver detenido en onDestroy()")
    }

    companion object{

        lateinit var context: Context


        fun getAppContext() : Context {
            return context
        }
    }
}