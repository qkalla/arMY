from seleniumwire import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import json
import gzip
import brotli
import time

# Set up WebDriver
options = webdriver.ChromeOptions()
options.add_argument("--headless")  # Run browser in the background
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

# Spoof browser fingerprint
options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

# Open Yandex Taxi website
driver.get("https://taxi.yandex.com/")

# Wait for network requests to load
time.sleep(5)  # Adjust if necessary

# Open a file to store results
with open("yandex_data_log.txt", "w", encoding="utf-8") as log_file:
    for request in driver.requests:
        if "yandex.com" in request.url and request.response:
            log_file.write(f"\n🔍 API URL: {request.url}\n")

            try:
                # Get response headers
                content_encoding = request.response.headers.get("Content-Encoding", "")

                # Decode GZIP compressed data
                if "gzip" in content_encoding:
                    data = gzip.decompress(request.response.body).decode("utf-8", errors="ignore")

                # Decode Brotli compressed data
                elif "br" in content_encoding:
                    data = brotli.decompress(request.response.body).decode("utf-8", errors="ignore")

                # Handle plain text responses
                else:
                    data = request.response.body.decode("utf-8", errors="ignore")

                # Try to parse JSON data
                try:
                    json_data = json.loads(data)
                    log_file.write("📦 JSON Data:\n")
                    log_file.write(json.dumps(json_data, indent=4) + "\n")

                    # Extract GPS coordinates if available
                    if "latitude" in data or "longitude" in data:
                        log_file.write("🚖 Found Taxi Locations!\n")

                except json.JSONDecodeError:
                    log_file.write("📄 Response is not JSON, displaying raw text:\n")
                    log_file.write(data[:500] + "\n")  # Save first 500 characters

            except Exception as e:
                log_file.write(f"🚨 Error decoding response: {e}\n")

driver.quit()
