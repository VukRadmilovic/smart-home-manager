from locust import HttpUser, task, between, tag
import random, string, time, json


class Test(HttpUser):
    wait_time = between(1, 5)
    host = "http://192.168.0.111:8080"
    token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzbWFydC1ob21lIiwic3ViIjoibWFqYSIsImF1ZCI6IndlYiIsImlhdCI6MTcwNjcyNDEzMiwiZXhwIjoxNzA4ODcxNjE1LCJyb2xlIjoiUk9MRV9VU0VSIn0.W4HEqUEVNeeTnLQbVcm5Saw70wKk2ADprBgH4kk5KY9y3pGLO3ZJNWq6VEOKjG_s_B7qvsFWrYhcCDDKpVZaLA"
    @tag("scene1")
    @task
    def register_activate_login(self):
        picture = open('castrovalva_escher.jpg', 'rb')
        files = {'profilePicture': ('castrovalva_escher.jpg', picture, 'image/jpeg')}
        new_user = {
            'username': ''.join(random.choices(string.ascii_letters, k=7)),
            'name': 'TestName',
            'surname': 'TestSurname',
            'email': ''.join(random.choices(string.ascii_letters, k=5)) + '@mail.com',
            'password': 'password123',
            'role': 'ROLE_USER'
        }
        login = {
            "username": new_user['username'],
            "password" : "password123"
        }
        headers = {'Content-Type': 'application/json', 'Accept': 'application/json'}
        with self.client.post("/api/user/register", data=new_user, files=files, catch_response=True) as register_response:
            if register_response.status_code == 200:
                with self.client.get("/api/user/activate/" + str(register_response.text), catch_response=True, name="Account Activation") as activation_response:
                    if activation_response.status_code in [200,201,305]:
                        with self.client.post('/api/user/login', json=login, headers=headers, catch_response=True) as login_response:
                            if login_response.status_code == 200:
                                pass
                            else:
                                print("LOGIN_ERR:" + login_response.text)
                    else:
                        print("ACTIVATION_ERR:" + activation_response.text)
            else:
                print(register_response.text)

    @tag("scene2")
    @task
    def device_retrieval_thermo_charts_month(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        to_timestamp = round(time.time())
        options = [60 * 60, 60 * 60 * 3, 60 * 60 * 6, 60 * 60 * 12, 60 * 60 * 24, 60 * 60 * 24 * 7]
        coin_flip = random.random()
        if coin_flip < 0.5:
            from_timestamp = to_timestamp - 60 * 60 * 24 * 30
        else:
            from_timestamp = to_timestamp - options[random.randint(0,5)]
        measurements_url = "/api/devices/measurements?from=" + str(from_timestamp) + "&to=" + str(to_timestamp) + "&deviceId=1&measurement="
        with self.client.get(measurements_url + "humidity", catch_response=True, headers=headers, name="Humidity") as hum_response:
            pass
        with self.client.get(measurements_url + "temperature", catch_response=True, headers=headers, name="Temperature") as tem_response:
            pass

    
    @tag("scene3")
    @task
    def device_permissions_retrieval(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        device_id = random.randint(1,900017)
        with self.client.get("/api/devices/shareControl/get/" + str(device_id), catch_response=True, headers=headers, name='Shared Permissions For Device') as fetch_response:
            print(fetch_response.text)

    @tag("scene3")
    @task
    def property_permissions_retrieval(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        property_id = random.randint(1,100001)
        with self.client.get("/api/devices/shareControl/get/property/" + str(property_id), catch_response=True, headers=headers, name='Shared Permissions For Property') as fetch_response:
            print(fetch_response.text)

    
    @tag("scene4")
    @task
    def login_user_info_retrieval(self):
        login = {
            "username": "username" + str(random.randint(1,1000002)),
            "password" : "password123"
        }
        with self.client.post('/api/user/login', json=login, catch_response=True) as login_response:
                            if login_response.status_code == 200:
                                token = json.loads(login_response.text)['token']
                                headers = {
                                     "Authorization" : "Bearer " + token
                                }
                                with self.client.get('/api/user/info', headers=headers, catch_response=True) as info_response:
                                     pass
                            else:
                                print(login_response.text)
    

    @tag("scene5")
    @task
    def password_reset_login(self):
        user = random.randint(1, 100002)
        new_pass = ''.join(random.choices(string.ascii_letters, k=10))
        new_password = {
             "userId": str(user),
             "password": new_pass
        }
        with self.client.post("/api/user/passwordReset", json=new_password, catch_response=True) as reset_response:
            login = {
                "username": "username" + str(user),
                "password" : new_pass
            }
            if reset_response.status_code == 200:
                with self.client.post('/api/user/login', json=login, catch_response=True) as login_response:
                                    if login_response.status_code == 200:
                                        print(login_response.text)
    
    
    @tag("scene6")
    @task
    def login_device_and_shared_retrieval(self):
        user = random.randint(1, 1000002)
        login = {
                "username": "username" + str(user),
                "password" : "password123"
            }
        with self.client.post('/api/user/login', json=login, catch_response=True) as login_response:
            token = json.loads(login_response.text)['token']
            headers = {"Authorization" : "Bearer " + token}
            with self.client.get("/api/devices/ownerAll", headers=headers, catch_response=True) as devices_response:
                if devices_response.status_code == 200:
                    print(devices_response.text)
                    with self.client.get("/api/devices/shared", headers=headers, catch_response=True) as shared_response:
                        if shared_response.status_code == 200:
                            print(shared_response.text)
    
    @tag("scene7")
    @task
    def device_permission_with_search_edit(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        device_id = random.randint(1,900017)
        with self.client.get("/api/devices/shareControl/get/" + str(device_id), catch_response=True, headers=headers, name='Shared Permissions For Device') as fetch_response:
            if(fetch_response.status_code == 200):
                fetch_response.success()
            time.sleep(5,10)
            perms = json.loads(fetch_response.text)
            edit = []
            if len(perms) != 0:
                for perm in perms:
                    delete = {
                        "userId" : perm['id'],
                        "action" : "d"
                    }
                    edit.append(delete)
            add_count = random.randint(1, 5)
            for i in range(add_count):
                add = {
                    "userId" : str(random.randint(1, 1000002)),
                    "action" : "a"  
                }
                edit.append(add)
            final = {
                 "details": edit
            }
            with self.client.put("/api/devices/shareControl/" + str(device_id), json=final, catch_response=True, headers=headers, name = "Permission Editing") as edit_response:
                pass

    @tag("scene7", "scene8")
    @task
    def user_search(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        user_search_times = random.randint(1,5)
        for i in range(user_search_times):
            key = "username" + str(random.randint(1, 1000002))
            with self.client.get("/api/user/info/" + key, catch_response=True, headers=headers, name='User Search') as users_response:
                print(users_response.text)
            time.sleep(3,6)

    @tag("scene8")
    @task
    def property_permission_with_search_edit(self):
        headers = {'Authorization': 'Bearer ' + self.token}
        property_id = random.randint(1,10001)
        with self.client.get("/api/devices/shareControl/get/property/" + str(property_id), catch_response=True, headers=headers, name='Shared Permissions For Property') as fetch_response:
            perms = json.loads(fetch_response.text)
            edit = []
            if len(perms) != 0:
                for perm in perms:
                    delete = {
                        "userId" : perm['id'],
                        "action" : "d"
                    }
                    edit.append(delete)
            add_count = random.randint(1, 5)
            for i in range(add_count):
                add = {
                    "userId" : str(random.randint(1, 100002)),
                    "action" : "a"  
                }
                edit.append(add)
            final = {
                 "details": edit
            }
            with self.client.put("/api/devices/shareControl/property/" + str(property_id), json=final, catch_response=True, headers=headers, name = "Permission Editing") as edit_response:
                print(edit_response.text)

    @tag("scene9")
    @task
    def device_and_commands_history_retrieval(self):
        user = random.randint(1, 100002)
        login = {
                "username": "username" + str(user),
                "password" : "password123"
            }
        with self.client.post('/api/user/login', json=login, catch_response=True) as login_response:
            token = json.loads(login_response.text)['token']
            headers = {"Authorization" : "Bearer " + token}
            with self.client.get("/api/devices/ownerAll", headers=headers, catch_response=True) as devices_response:
                if devices_response.status_code == 200:
                    devices = json.loads(devices_response.text)
                    if len(devices) != 0:
                        print(devices)
                        for device in devices:
                            if device['type'] == "WM" or device['type'] == "AC":
                                print("yes")
                                commands_url = "/api/devices/commands?from=0&to=2147483646&page=0&size=100&firstFetch=true&userId=-1&deviceId=" + str(device['id']) 
                                with self.client.get(commands_url, headers=headers, catch_response=True, name='Commands') as commands_response:
                                    print(commands_response.text)

    @tag("scene10")
    @task
    def command_history_retrieval(self):
        headers = {"Authorization" : "Bearer " + self.token}
        first_fetch = True
        times = random.randint(1, 10)
        users = []
        for i in range(times):
            if random.randint(0,1) == 0:
                deviceId = random.randint(10002, 20003)
            else:
                deviceId = random.randint(80016, 90017)
            if first_fetch:
                commands_url = "/api/devices/commands?from=0&to=2147483646&page=0&size=100&firstFetch=true&userId=-1&deviceId=" + str(deviceId)
            else:
                page = 0 if random.randint(0,1) == 0 else random.randint(2,5)
                userId = -1 if random.randint(0,1) == 0 else users[random.randint(0,len(users) - 1)]
                commands_url = "/api/devices/commands?from=0&to=2147483646&page=" + str(page) + "&size=100&firstFetch=false&userId=" + str(userId) + "&deviceId=" + str(deviceId)
           
            if first_fetch:
                with self.client.get(commands_url, headers=headers, catch_response=True, name='First Fetch') as commands_response:
                    comms = json.loads(commands_response.text)
                    for user in comms['allUsers']:
                        users.append(user['id'])
                    first_fetch = False
                    #print(commands_response.text)
            else:
                with self.client.get(commands_url, headers=headers, catch_response=True, name='Filtered') as commands_response:
                    print(commands_response.text)
       
        
     
       
                
            

