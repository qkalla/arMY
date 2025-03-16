package com.example.army

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.rendering.ModelRenderable

class ARActivity : ComponentActivity() {
    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArScreen()
        }

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        loadARObjects()
    }

    private fun loadARObjects() {
        ModelRenderable.builder()
            .setSource(this, R.raw.ar_object) // Add 3D Model in res/raw folder
            .build()
            .thenAccept { modelRenderable ->
                // Attach the model to AR
            }
            .exceptionally {
                Toast.makeText(this, "Failed to load AR object", Toast.LENGTH_SHORT).show()
                null
            }
    }
}

@Composable
fun ArScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Yandex Taxi, Food, Bus Detection...", style = MaterialTheme.typography.headlineMedium)
    }
}
