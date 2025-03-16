package com.example.army

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class FoodService {
    private val client = OkHttpClient()

    fun getFoodMenu(callback: (String) -> Unit) {
        val url = "https://yandex.eat.com/api/menu"

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
                val foodName = json.getJSONArray("menu").getJSONObject(0).getString("name")
                callback("Food preview: $foodName")
            }
        })
    }
}
