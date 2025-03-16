package com.example.army.utils

import android.graphics.Bitmap
import android.util.Log
import com.yandex.vision.Vision
import com.yandex.vision.detection.Detection
import com.yandex.vision.detection.DetectionModel

class ObjectDetectionHelper {

    private lateinit var detectionModel: DetectionModel

    fun initialize() {
        detectionModel = Vision.createDetectionModel()
    }

    fun detectObjects(bitmap: Bitmap): List<Detection> {
        val results = detectionModel.detect(bitmap)
        Log.d("AI Detection", "Objects Detected: ${results.size}")
        return results
    }
}
