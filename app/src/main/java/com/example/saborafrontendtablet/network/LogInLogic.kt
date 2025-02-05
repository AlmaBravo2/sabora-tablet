package com.example.saborafrontendtablet.network

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.saborafrontendtablet.BuildConfig
import com.example.saborafrontendtablet.LogIn
import org.json.JSONObject

class LogInLogic {

    private val apiUrl = BuildConfig.API_URL

    private var isLogInQueue = false
    lateinit var userVolleyQueue : RequestQueue

    fun logIn(username : String, password : String, callback: (Boolean, String?) -> Unit){


        val url = "$apiUrl/user?username=$username&password=$password"

        val queue = Volley.newRequestQueue(LogIn.getAppContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                val user = JSONObject(response.toString())

                callback(true, "Login exitoso: ${user.toString()}")

                } catch (e: Exception) {
                    callback(false, "Error al procesar la respuesta")
                }
            },
            { error ->
                callback(false, "Error en la solicitud: ${error.message}")

            }
        )
        queue.add(jsonObjectRequest)
    }
}