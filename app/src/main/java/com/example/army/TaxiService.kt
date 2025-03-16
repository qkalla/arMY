package com.example.army

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class TaxiService {
    private val client = OkHttpClient()

    fun getNearbyTaxis(lat: Double, lon: Double, callback: (String) -> Unit) {
        val url = "https://taxi.yandex.com/api/taxi?lat=$lat&lon=$lon"

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
                val eta = json.getJSONArray("taxis").getJSONObject(0).getInt("eta")
                callback("Taxi arriving in $eta minutes")
            }
        })
    }
}
