package com.example.army

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.fragment.app.FragmentActivity
import com.google.ar.sceneform.ux.ArFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TransportRouter
import com.yandex.mapkit.geometry.Point

class MainActivity : FragmentActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var taxiButton: Button
    private lateinit var foodButton: Button
    private lateinit var busButton: Button
    private lateinit var transportRouter: TransportRouter

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure you use this layout

        // Initialize AR Fragment
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        // Initialize Buttons
        taxiButton = findViewById(R.id.taxi_button)
        foodButton = findViewById(R.id.food_button)
        busButton = findViewById(R.id.bus_button)

        // Set Button Click Listeners
        taxiButton.setOnClickListener {
            // Show Taxi AR content
            showTaxiAR()
        }

        foodButton.setOnClickListener {
            // Show Food AR content
            showFoodAR()
        }

        busButton.setOnClickListener {
            // Show Bus AR content
            showBusAR()
        }

        // Initially show Taxi AR content
        showTaxiAR()
    }

    private fun showTaxiAR() {
        // Hide AR content for other sections and show Taxi AR
        // Implement logic to display Taxi AR (i.e., taxi tracking)
        fetchNearestTaxis()
    }

    private fun showFoodAR() {
        // Hide AR content for other sections and show Food AR
        // Implement logic to display Food AR (i.e., food menu overlay)
    }

    private fun showBusAR() {
        // Hide AR content for other sections and show Bus AR
        // Implement logic to display Bus AR (i.e., bus station overlay)
    }

    // Fetch nearest taxis from Yandex API
    private fun fetchNearestTaxis() {
        val userLocation = Point(40.1776, 44.5126) // Example: Yerevan coordinates
        transportRouter = TransportFactory.getInstance().createTransportRouter()

        val session = transportRouter.requestRoutes(
            listOf(userLocation),
            object : Session.RouteListener {
                override fun onMasstransitRoutes(routes: MutableList<Route>) {
                    if (routes.isNotEmpty()) {
                        val nearestTaxi = routes[0]
                        showTaxiOnMap(nearestTaxi)
                    }
                }

                override fun onMasstransitRoutesError(error: com.yandex.runtime.Error) {
                    // Handle error
                }
            })
    }

    // Display the taxi location on the map
    private fun showTaxiOnMap(route: Route) {
        val taxiPoint = route.geometry.points.last()
        // Use ARFragment to display the taxi point on the AR map
        // This will depend on how you're displaying AR content for the taxi section
    }

    private fun fetchBusRoutes() {
        // Implement fetching and displaying bus routes in AR (future functionality)
     }
}
