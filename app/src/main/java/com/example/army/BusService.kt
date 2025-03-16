package com.example.army

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class BusService {
    private val client = OkHttpClient()

    fun getBusRoute(origin: String, destination: String, callback: (String) -> Unit) {
        val url = "https://yandex.transport.com/api/bus?origin=$origin&destination=$destination"

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer YOUR_YANDEX_API_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val busNumber = json.getJSONArray("buses").getJSONObject(0).getString("number")
                val eta = json.getJSONArray("buses").getJSONObject(0).getInt("eta")
                callback("Take Bus $busNumber - Arrives in $eta minutes")
            }
        })
    }
}
