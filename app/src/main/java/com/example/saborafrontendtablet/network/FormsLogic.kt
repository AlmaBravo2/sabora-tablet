package com.example.saborafrontendtablet.network

import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.saborafrontendtablet.BuildConfig
import com.example.saborafrontendtablet.FormCreator
import com.example.saborafrontendtablet.InicioForm
import com.example.saborafrontendtablet.InicioForm.Companion.context
import com.example.saborafrontendtablet.LogIn
import com.example.saborafrontendtablet.forms.Pregunta
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.Normalizer.Form
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormsLogic {

    private val apiUrl = BuildConfig.API_URL

    fun getFormById(formId: String, callback: (JSONObject?) -> Unit) {
        val url = "$apiUrl/form/$formId"
        val queue = Volley.newRequestQueue(InicioForm.getAppContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                Log.i("Form Data", response.toString())
                callback(response)
            },
            { error ->
                Log.e("Error", error.toString())
                callback(null)
            }
        )
        queue.add(jsonObjectRequest)
    }


    fun sendAnswers(jsonObject: JSONObject, callback: (Boolean, String) -> Unit) {
        val url = "$apiUrl/form/${jsonObject.getInt("formId")}/answer"

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                callback(true, response.toString())
            },
            { error ->
                callback(false, error.toString())
            }
        )

        Volley.newRequestQueue(LogIn.getAppContext()).add(request)
    }

    fun getAllForms(callback: (List<JSONObject>?) -> Unit) {
        val url = "$apiUrl/form/all"
        val queue = Volley.newRequestQueue(InicioForm.getAppContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Si la respuesta es exitosa (200 OK), vamos a obtener el array de formularios
                try {
                    val formsArray = response.getJSONArray("forms") // Asumiendo que la respuesta es un array de formularios
                    val formsList = mutableListOf<JSONObject>()

                    for (i in 0 until formsArray.length()) {
                        formsList.add(formsArray.getJSONObject(i))
                    }
                    Log.i("Form Data", formsList.toString())
                    callback(formsList)  // Pasamos la lista de formularios al callback
                } catch (e: JSONException) {
                    Log.e("Error parsing JSON", e.message.toString())
                    callback(null)  // En caso de error en el parseo, devolvemos null
                }
            },
            { error ->
                // Si ocurre un error en la solicitud, mostramos el error y devolvemos null
                Log.e("Error", error.toString())
                callback(null)
            }
        )

        // Añadimos la solicitud a la cola de Volley
        queue.add(jsonObjectRequest)
    }


     fun enviarForm(formId: Int, nombreFormulario: String, foodSpecialist: String, preguntas: List<Pregunta>) {
        val url = "$apiUrl/form" // Sustituye por tu endpoint real

        val requestQueue = Volley.newRequestQueue(FormCreator.getAppContext())

         Log.d("FormsLogic", "Enviando formulario con ID: $formId, nombre: $nombreFormulario, especialista: $foodSpecialist")

        val jsonFormulario = JSONObject().apply {
            put("id", formId)
            put("name", nombreFormulario)
            put("foodSpecialist", foodSpecialist)
            put("creationDate", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))

            val jsonPreguntas = JSONArray()
            preguntas.forEach { pregunta ->
                val jsonPregunta = JSONObject().apply {
                    put("id", pregunta.id ?: JSONObject.NULL)
                    put("question", pregunta.texto)
                    put("type", pregunta.tipo)

                    if (pregunta.opciones != null) {
                        val jsonOpciones = JSONArray()
                        pregunta.opciones.forEach { opcion ->
                            jsonOpciones.put(opcion)
                        }
                        put("options", jsonOpciones)
                    }

                    if (pregunta.min != null && pregunta.max != null) {
                        put("min", pregunta.min)
                        put("max", pregunta.max)
                        put("interval", pregunta.interval ?: 1)
                    }
                }
                jsonPreguntas.put(jsonPregunta)
                Log.d("FormsLogic", "Pregunta añadida: $jsonPregunta")
            }

            put("questions", jsonPreguntas)
            Log.d("FormsLogic", "Formulario completo: $this")
        }

        /*val jsonRequest = object : JsonObjectRequest(
            Method.POST, url, jsonFormulario,
            { response ->
                Log.i("FormsLogic", "Respuesta recibida: $response")
                Toast.makeText(FormCreator.getAppContext(), "Formulario enviado con éxito", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.e("FormsLogic", "Error al enviar formulario", error)
                Toast.makeText(FormCreator.getAppContext(), "Error al enviar formulario: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                // headers["Authorization"] = "Bearer token" // si es necesario
                return headers
            }
        }*/
         val stringRequest = object : StringRequest(
             Method.POST, url,
             { response ->
                 Log.i("FormsLogic", "Respuesta recibida: $response")
                 Toast.makeText(FormCreator.getAppContext(), "Formulario enviado con éxito", Toast.LENGTH_SHORT).show()
             },
             { error ->
                 Log.e("FormsLogic", "Error al enviar formulario", error)
                 Toast.makeText(FormCreator.getAppContext(), "Error al enviar formulario: ${error.message}", Toast.LENGTH_LONG).show()
             }
         ) {
             override fun getHeaders(): MutableMap<String, String> {
                 val headers = HashMap<String, String>()
                 headers["Content-Type"] = "application/json"
                 return headers
             }

             override fun getBody(): ByteArray {
                 return jsonFormulario.toString().toByteArray(Charsets.UTF_8)
             }

             override fun getBodyContentType(): String {
                 return "application/json"
             }
         }

         requestQueue.add(stringRequest)
         Log.d("FormsLogic", "Solicitud añadida a la cola")

        requestQueue.add(stringRequest)
         Log.d("FormsLogic", "Solicitud añadida a la cola")
    }


}