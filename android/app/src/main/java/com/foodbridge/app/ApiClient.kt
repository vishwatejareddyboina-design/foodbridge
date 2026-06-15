package com.foodbridge.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    const val BASE_URL = "http://127.0.0.1:8080/backend/api/"

    suspend fun post(endpoint: String, jsonInput: JSONObject): JSONObject {
        return withContext(Dispatchers.IO) {
            val result = StringBuilder()
            try {
                val url = URL(BASE_URL + endpoint)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.doOutput = true

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonInput.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line?.trim())
                    }
                    reader.close()
                    JSONObject(result.toString())
                } else {
                    JSONObject().apply {
                        put("status", "error")
                        put("message", "HTTP Error: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                JSONObject().apply {
                    put("status", "error")
                    put("message", e.message ?: "Network error or timeout occurred")
                }
            }
        }
    }
}
