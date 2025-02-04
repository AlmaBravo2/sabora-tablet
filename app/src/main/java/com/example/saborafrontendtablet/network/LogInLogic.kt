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

    fun logIn(username : String, password : String){


        val url = "$apiUrl/user?username=$username&password=$password"

        val queue = Volley.newRequestQueue(LogIn.getAppContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val user = JSONObject(response.toString())

                Log.i("User is ", user.toString())
            },
            {
                Log.i("Error", it.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
}