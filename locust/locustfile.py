from locust import HttpUser, task, between, tag
import random, datetime, string


class Test(HttpUser):
    wait_time = between(1, 1)
    host = "http://localhost:8080"
    user_token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzbWFydC1ob21lIiwic3ViIjoidnVrIiwiYXVkIjoid2ViIiwiaWF0IjoxNzA3MDEyOTg1LCJleHAiOjE3MDkxNjA0NjksInJvbGUiOiJST0xFX1VTRVIifQ.ZQcW5XHcAw-Hd5W6a8_lu7nNIXjUuNjYhWg3xH1E0almlWFQbbYwFwBI6mSU3wZTKFFzzKj_dwc65NC5V-7fIA"
    admin_token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzbWFydC1ob21lIiwic3ViIjoiYWRtaW4iLCJhdWQiOiJ3ZWIiLCJpYXQiOjE3MDcwMjE1MTgsImV4cCI6MTcwOTE2OTAwMiwicm9sZSI6IlJPTEVfU1VQRVJBRE1JTiJ9.x5OT_59DFTOBJ7OkVUly2rc8zqgpygxPnHrViXuYp35UuwnwoOv9r1pXU_i-hksb_lNnaJ5fVQjq79oFllq1dg"

    @tag('scene1')
    @task
    def register_thermometer(self):
        picture = open('castrovalva_escher.jpg', 'rb')
        files = {'image': ('castrovalva_escher.jpg', picture, 'image/jpeg')}
        thermo = {
            'name': ''.join(random.choices(string.ascii_letters, k=7)),
            'propertyId': 1,
            'powerSource': 'AUTONOMOUS',
            'energyConsumption': 2,
            'temperatureUnit': 'CELSIUS'
        }
        headers = {"Authorization": f"Bearer {self.user_token}"}
        with self.client.post("/api/devices/registerThermometer", headers=headers, data=thermo, files=files, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                print(response.text)
                response.failure("Failed to register thermometer because of status code: " + str(response.status_code))

    @tag("scene2")
    @task
    def view_properties_admin(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        with self.client.get("/api/property/approvedProperties", headers=headers, catch_response=True, name="View properties") as response:
            if response.status_code != 200:
                response.failure("Failed to view properties")
            else:
                response.success()

    @tag("scene3")
    @task
    def view_property_power_admin(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        now = datetime.datetime.utcnow()
        start = now - datetime.timedelta(days=30)
        with self.client.get(f"http://localhost/api/devices/measurements?from={int(start.timestamp())}&to={int(now.timestamp())}&deviceId=1"
                             f"&measurement=totalProduction", headers=headers, catch_response=True,
                             name="View property power production") as response:
            if response.status_code != 200:
                response.failure("Failed to view property power production")
            else:
                with self.client.get(
                        f"http://localhost/api/devices/measurements?from={int(start.timestamp())}&to={int(now.timestamp())}&deviceId=1"
                        f"&measurement=totalConsumption", headers=headers, catch_response=True,
                        name="View property power consumption") as response:
                    if response.status_code != 200:
                        response.failure("Failed to view property power consumption")
                    else:
                        response.success()

    @tag("scene4")
    @task
    def view_city_power_admin(self):
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        now = datetime.datetime.utcnow()
        start = now - datetime.timedelta(days=30)
        with self.client.get(f"http://localhost/api/devices/powerMeasurements?from={int(start.timestamp())}&to={int(now.timestamp())}&cityId=1"
                             f"&measurement=totalProduction", headers=headers, catch_response=True,
                             name="View city power production") as response:
            if response.status_code != 200:
                response.failure("Failed to view city power production")
            else:
                with self.client.get(
                        f"http://localhost/api/devices/powerMeasurements?from={int(start.timestamp())}&to={int(now.timestamp())}&cityId=1"
                        f"&measurement=totalConsumption", headers=headers, catch_response=True,
                        name="View city power consumption") as response:
                    if response.status_code != 200:
                        response.failure("Failed to view city power consumption")
                    else:
                        response.success()

    @tag("scene5")
    @task
    def solar_panel_turn_on_off(self):
        headers = {"Authorization": f"Bearer {self.user_token}"}
        sps_id = random.randint(1, 2)
        with self.client.put(f"/api/devices/sps/{sps_id}/off", headers=headers, catch_response=True, name="Turn off solar panel") as response:
            if response.status_code != 200:
                response.failure("Failed to turn off solar panel")
            else:
                with self.client.put(f"/api/devices/sps/{sps_id}/on", headers=headers, catch_response=True,
                                     name="Turn on solar panel") as response:
                    if response.status_code != 200:
                        response.failure("Failed to turn on solar panel")
                    else:
                        response.success()

    @tag("scene6")
    @task
    def view_solar_panel_commands(self):
        headers = {"Authorization": f"Bearer {self.user_token}"}
        sps_id = random.randint(1, 2)
        with self.client.get(f"/api/devices/commands?from=0&to=9007199254740991&deviceId={sps_id}&page=0&size=100&firstFetch=true&userId=-1", headers=headers, catch_response=True, name="View solar panel commands") as response:
            if response.status_code != 200:
                response.failure("Failed to view solar panel commands")
            else:
                response.success()

    @tag("scene7")
    @task
    def view_property_power_consumption(self):
        headers = {"Authorization": f"Bearer {self.user_token}"}
        now = datetime.datetime.utcnow()
        start = now - datetime.timedelta(days=30)
        with self.client.get(f"/api/devices/measurements?from={int(start.timestamp())}&to={int(now.timestamp())}"
                             f"&deviceId=1&measurement=totalConsumption", headers=headers, catch_response=True,
                             name="View property power consumption") as response:
            if response.status_code != 200:
                response.failure("Failed to view property power consumption")
            else:
                response.success()

    @tag("scene8")
    @task
    def view_charger_commands(self):
        headers = {"Authorization": f"Bearer {self.user_token}"}
        charger_id = random.randint(3, 4)
        with self.client.get(f"/api/devices/commands?from=0&to=9007199254740991&deviceId={charger_id}&page=0&size=100&firstFetch=true&userId=-1", headers=headers, catch_response=True, name="View charger commands") as response:
            if response.status_code != 200:
                response.failure("Failed to view charger commands")
            else:
                response.success()

    @tag("scene9")
    @task
    def register_solar_panel(self):
        picture = open('castrovalva_escher.jpg', 'rb')
        files = {'image': ('castrovalva_escher.jpg', picture, 'image/jpeg')}
        solar = {
            'name': ''.join(random.choices(string.ascii_letters, k=7)),
            'propertyId': 1,
            'powerSource': 'AUTONOMOUS',
            'energyConsumption': 2,
            'numberOfPanels': random.randint(1, 10),
            'panelSize': random.randint(1, 10),
            'panelEfficiency': random.random()
        }
        headers = {"Authorization": f"Bearer {self.user_token}"}
        with self.client.post("/api/devices/registerSolarPanelSystem", headers=headers, data=solar, files=files, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                print(response.text)
                response.failure("Failed to register solar panel system because of status code: " + str(response.status_code))

    @tag("scene10")
    @task
    def view_devices(self):
        headers = {"Authorization": f"Bearer {self.user_token}"}
        with self.client.get("/api/devices/ownerAll", headers=headers, catch_response=True, name="View devices") as response:
            if response.status_code != 200:
                response.failure("Failed to view devices")
            else:
                response.success()