from seleniumwire import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import json
import gzip
import brotli
import time
from geopy.distance import geodesic  # To calculate distance

# Set up WebDriver
options = webdriver.ChromeOptions()
options.add_argument("--start-maximized")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

# Open Yandex Go
driver.get("https://taxi.yandex.com/")
time.sleep(10)  # Allow time for the page to load

print("✅ Yandex Go is open!")

# User's pickup location (example: Yerevan city center)
pickup_location = (40.177200, 44.503490)  # Update this with actual pickup coordinates

# Dictionary to store real-time taxi locations
live_taxi_locations = {}

# Function to process taxi data
def process_taxi_data(data):
    global live_taxi_locations

    try:
        json_data = json.loads(data)

        if "drivers" in json_data:
            print("\n🚖 Live Taxi Locations:")
            nearest_taxi = None
            min_distance = float("inf")

            for driver_info in json_data["drivers"]:
                taxi_id = driver_info["id"]
                if driver_info["positions"]:
                    latest_position = driver_info["positions"][0]
                    lat, lon = latest_position["lat"], latest_position["lon"]

                    # Calculate distance from pickup location
                    taxi_distance = geodesic(pickup_location, (lat, lon)).km
                    live_taxi_locations[taxi_id] = (lat, lon, taxi_distance)

                    print(f"📍 Taxi ID {taxi_id} at: {lat}, {lon} - Distance: {taxi_distance:.2f} km")

                    # Check for the nearest taxi
                    if taxi_distance < min_distance:
                        min_distance = taxi_distance
                        nearest_taxi = (taxi_id, lat, lon, taxi_distance)

            # Show nearest taxi
            if nearest_taxi:
                print(f"\n🚖 Nearest Taxi: {nearest_taxi[0]} - {nearest_taxi[3]:.2f} km away")

    except json.JSONDecodeError:
        print("📄 Response is not JSON")

# Function to detect accepted orders
def check_for_accepted_order():
    print("\n🔍 Checking for accepted orders...")

    for request in driver.requests:
        if "nearestdrivers" in request.url:  # Ignore irrelevant requests
            continue

        if "order" in request.url and request.response:
            try:
                content_encoding = request.response.headers.get("Content-Encoding", "")

                # Decode response
                if "gzip" in content_encoding:
                    data = gzip.decompress(request.response.body).decode("utf-8", errors="ignore")
                elif "br" in content_encoding:
                    data = brotli.decompress(request.response.body).decode("utf-8", errors="ignore")
                else:
                    data = request.response.body.decode("utf-8", errors="ignore")

                # ✅ Ensure it's JSON before processing
                if data.startswith("{") and "driver_id" in data:
                    json_data = json.loads(data)
                    assigned_taxi_id = json_data.get("order", {}).get("driver_id")
                    if assigned_taxi_id:
                        print(f"\n🚖 Order accepted by Taxi ID: {assigned_taxi_id}")

                        # Check distance & estimated time
                        if assigned_taxi_id in live_taxi_locations:
                            taxi_lat, taxi_lon, taxi_distance = live_taxi_locations[assigned_taxi_id]
                            estimated_time = (taxi_distance / 40) * 60  # Assuming average speed = 40 km/h
                            print(f"🛣️ Distance to pickup: {taxi_distance:.2f} km")
                            print(f"⏳ Estimated time: {estimated_time:.1f} min")

                        return assigned_taxi_id
                else:
                    print("🚨 Ignored non-JSON or irrelevant response.")

            except json.JSONDecodeError:
                print("🚨 JSON Decode Error: Response is not valid JSON.")
            except Exception as e:
                print(f"🚨 Error processing order data: {e}")

    return None

# Live tracking loop
assigned_taxi_id = None
while True:
    time.sleep(5)

    if assigned_taxi_id is None:
        for request in driver.requests:
            if "nearestdrivers" in request.url and request.response:
                try:
                    content_encoding = request.response.headers.get("Content-Encoding", "")

                    # Decode response
                    if "gzip" in content_encoding:
                        data = gzip.decompress(request.response.body).decode("utf-8", errors="ignore")
                    elif "br" in content_encoding:
                        data = brotli.decompress(request.response.body).decode("utf-8", errors="ignore")
                    else:
                        data = request.response.body.decode("utf-8", errors="ignore")

                    process_taxi_data(data)

                except Exception as e:
                    print(f"🚨 Error: {e}")

        # Check for an accepted order
        assigned_taxi_id = check_for_accepted_order()

    # Track only the assigned taxi
    if assigned_taxi_id and assigned_taxi_id in live_taxi_locations:
        print(f"\n🎯 Tracking assigned taxi: {assigned_taxi_id} - Location: {live_taxi_locations[assigned_taxi_id]}")
