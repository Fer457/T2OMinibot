package com.intec.t2o.chatGPT

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class ChatGPTManager(private val apiKey: String) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun fetchGPT3ChatResponse(prompt: String, onResponse: (String) -> Unit) {
        coroutineScope.launch {
            val url = URL("https://api.openai.com/v1/chat/completions")
            (url.openConnection() as? HttpURLConnection)?.run {
                try {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer $apiKey")
                    doOutput = true

                    val body = """
                        {
                            "model": "gpt-4",
                            "messages": [
                                {   "role": "system",
                                    "content": "Eres un robot de intecRobots ubicado en alicante y tu nombre es Peter"
                                },
                                {
                                    "role": "user",
                                    "content": "$prompt respondeme en una sola frase"
                                }
                            ]
                        }
                    """.trimIndent()

                    outputStream.use { os ->
                        os.write(body.toByteArray())
                    }

                    val response = inputStream.bufferedReader().use { it.readText() }

                    // Parse the JSON response to get only the ChatGPT's response text
                    val jsonResponse = JSONObject(response)
                    val choices = jsonResponse.getJSONArray("choices")
                    var chatGPTResponse = ""

                    if (choices.length() > 0) {
                        val firstChoice = choices.getJSONObject(0)
                        val message = firstChoice.getJSONObject("message")
                        chatGPTResponse = message.getString("content")
                    }

                    withContext(Dispatchers.Main) {
                        onResponse(chatGPTResponse)
                        Log.d("respuesta", chatGPTResponse)
                    }
                } catch (e: FileNotFoundException) {
                    val errorStream = errorStream.bufferedReader().use { it.readText() }
                    withContext(Dispatchers.Main) {
                        Log.d("Error al obtener respuesta:", errorStream)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Error al obtener respuesta:", e.toString())
                    }
                }
            }
        }
    }
    fun fetchGPT3ChatSinonimo(prompt: String, onResponse: (String) -> Unit) {
        coroutineScope.launch {
            val url = URL("https://api.openai.com/v1/chat/completions")
            (url.openConnection() as? HttpURLConnection)?.run {
                try {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer $apiKey")
                    doOutput = true

                    val body = """
                        {
                        "model": "gpt-4",
                        "messages": [
                            {   
                                "role": "system",
                                "content": "te voy a pasar una lista de comandos, cuando recibas el prompt del usuario buscaras si el contexto o alguna palabra es sinonimo del comando que hay en la lista y devuelveme el comando, si no lo encuentras devuelve no encontrado"
                            },
                            {
                                "role": "assistant",
                                "content": "lista de comandos: reunion, mensajeria, baÃ±o "
                            },
                            {   
                                "role": "user",
                                "content": "$prompt devuelveme solo el comando de la lista"
                            }
                        ]
                    }
                    """.trimIndent()

                    outputStream.use { os ->
                        os.write(body.toByteArray())
                    }

                    val response = inputStream.bufferedReader().use { it.readText() }

                    // Parse the JSON response to get only the ChatGPT's response text
                    val jsonResponse = JSONObject(response)
                    val choices = jsonResponse.getJSONArray("choices")
                    var chatGPTResponse = ""

                    if (choices.length() > 0) {
                        val firstChoice = choices.getJSONObject(0)
                        val message = firstChoice.getJSONObject("message")
                        chatGPTResponse = message.getString("content")
                    }

                    withContext(Dispatchers.Main) {
                        onResponse(chatGPTResponse)
                        Log.d("respuesta", chatGPTResponse)
                    }
                } catch (e: FileNotFoundException) {
                    val errorStream = errorStream.bufferedReader().use { it.readText() }
                    withContext(Dispatchers.Main) {
                        Log.d("Error al obtener respuesta:", errorStream)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Error al obtener respuesta:", e.toString())
                    }
                }
            }
        }
    }


}