import React, { useState, useEffect } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MapContainer, TileLayer, Marker } from "react-leaflet";
import "leaflet/dist/leaflet.css";

const TaxiBookingAR = () => {
  const [destination, setDestination] = useState("");
  const [estimatedTime, setEstimatedTime] = useState(null);
  const [estimatedCost, setEstimatedCost] = useState(null);
  const [taxis, setTaxis] = useState([]);

  useEffect(() => {
    // Simulate fetching nearby taxis from an API
    const fetchTaxis = async () => {
      const response = await fetch("https://api.example.com/nearest-taxis");
      const data = await response.json();
      setTaxis(data.taxis);
    };

    fetchTaxis();
    const interval = setInterval(fetchTaxis, 5000); // Refresh location every 5 seconds
    return () => clearInterval(interval);
  }, []);

  const handleBooking = async () => {
    if (!destination) return;
    const response = await fetch("https://api.example.com/book-taxi", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ destination }),
    });
    const data = await response.json();
    setEstimatedTime(data.estimated_time);
    setEstimatedCost(data.estimated_cost);
  };

  return (
    <div className="flex flex-col items-center p-4 bg-black text-white min-h-screen">
      <h1 className="text-3xl font-bold mb-4">🚖 AR Taxi Booking</h1>
      <input
        type="text"
        placeholder="Enter destination..."
        className="p-2 text-black w-full mb-4 rounded"
        value={destination}
        onChange={(e) => setDestination(e.target.value)}
      />
      <Button onClick={handleBooking} className="mb-4 bg-yellow-500 text-black">
        🚕 Book Taxi
      </Button>

      {estimatedTime && estimatedCost && (
        <Card className="w-full p-4 bg-gray-800 text-white">
          <CardContent>
            ⏳ Estimated Time: {estimatedTime} minutes
            <br />💰 Cost: {estimatedCost} $
          </CardContent>
        </Card>
      )}

      <h2 className="text-xl font-semibold mt-6">🚖 Nearby Taxis</h2>
      <MapContainer center={[40.108479, 44.471657]} zoom={15} className="w-full h-64 mt-4 rounded-lg">
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
        {taxis.map((taxi, index) => (
          <Marker key={index} position={[taxi.lat, taxi.lon]} />
        ))}
      </MapContainer>
    </div>
  );
};

export default TaxiBookingAR;
