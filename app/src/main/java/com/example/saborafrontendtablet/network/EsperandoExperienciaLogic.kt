package com.example.saborafrontendtablet.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Callback
import okhttp3.Response
import android.util.Log
import com.example.saborafrontendtablet.BuildConfig
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface

class EsperandoExperienciaLogic{

    private val apiUrl = BuildConfig.API_URL

    //obtiene la dirección IP local IPv4 de la tablet

    fun getLocalIp(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addresses = intf.inetAddresses
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is InetAddress) {
                    val ip = addr.hostAddress
                    if (ip.contains(".")) return ip // Solo IPv4
                }
            }
        }
        return "0.0.0.0"
    }

    fun registerTablet(name: String) {
        val localIp = getLocalIp()
        Log.i("IP", "La IP local de la tablet es: $localIp")

        val json = JSONObject().apply {
            put("name", name)
            put("localIp", localIp)
        }

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$apiUrl/tablet/connection")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TabletRegister", "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("TabletRegister", "Tablet registrada exitosamente")
                } else {
                    Log.e("TabletRegister", "Fallo: código ${response.code}")
                }
                response.body?.close()
            }
        })
    }
}