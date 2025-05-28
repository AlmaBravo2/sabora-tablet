package com.example.saborafrontendtablet.connection

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

//escucha mensajes UDP en segundo plano
class UDPReceiver (private val port: Int){

    private var isListening = false
    private var job: Job? = null
    private var socket: DatagramSocket? = null


    fun startListening(onMessageReceived: (JSONObject) -> Unit) {
        //onMessageReceived es un callback que se ejecuta cuando se recibe un mensaje UDP válido en JSON

        // Asegúrate de que no se haya iniciado ya la escucha
        if (isListening) {
            Log.w("UDPReceiver", "Ya está escuchando en el puerto $port")
            return
        }

        isListening = true
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = DatagramSocket(port)
                val buffer = ByteArray(1024)

                Log.i("UDPReceiver", "Escuchando en puerto $port...")

                while (isListening) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    Log.i("UDPReceiver", "Esperando datos en $port...")
                    socket?.receive(packet)

                    val message = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    Log.i("UDPReceiver", "Mensaje recibido: $message")

                    try {
                        val json = JSONObject(message)
                        withContext(Dispatchers.Main) {
                            onMessageReceived(json)
                        }
                    } catch (e: Exception) {
                        Log.e("UDPReceiver", "Error procesando JSON: ${e.message}")
                    }
                    delay(500)
                }
            } catch (e: Exception) {
                Log.e("UDPReceiver", "Error en la recepción: ${e.message}")
            } finally {
                socket?.close()
                socket = null
                Log.i("UDPReceiver", "Socket cerrado")
            }
        }
    }

    fun stopListening() {
        isListening = false
        job?.cancel()
        socket?.close()
        socket = null
        Log.i("UDPReceiver", "stopListening() llamado, socket cerrado")
    }
}

