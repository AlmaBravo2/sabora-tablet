package com.example.saborafrontendtablet.network

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.saborafrontendtablet.BuildConfig
import com.example.saborafrontendtablet.InicioForm
import com.example.saborafrontendtablet.InicioForm.Companion.context
import com.example.saborafrontendtablet.LogIn
import org.json.JSONArray
import org.json.JSONObject

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
}