from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
import random, datetime

# InfluxDB connection settings
url = 'http://localhost:8086'
token = 'U559sfYWoV3E1dYKKfajmDEEy55c4R1Hk_bJ3tLIN_Kg1BY9zUYC0gxsKr58IgVgam1KnYVwH0LifIaL_v-WRw=='
org = 'Tiba'
bucket = 'measurements2'
measurements = ['humidity', 'temperature']

# Create InfluxDB client
client = InfluxDBClient(url=url, token=token, org=org)
write_api = client.write_api(write_options=SYNCHRONOUS)

# Function to generate sample data
def generate_data():
    now = datetime.datetime.utcnow()
    timestamp = now - datetime.timedelta(days=90)
    limit = now - datetime.timedelta(days=30)
    while timestamp <= limit:
        device_id = random.randint(3, 10000)
        for measurement in measurements:
            unit = 'C'
            value = 0
            if measurement == "humidity":
                unit = "%"
                value = random.uniform(30, 60)
            else:
                value = random.uniform(-5, 5)
            point = Point(measurement).field("value", value).tag("deviceId", str(1)).tag("unit", unit).time(timestamp, WritePrecision.NS)
            write_to_influxdb(point)
        timestamp += datetime.timedelta(seconds=5)


def generate_states_data():
    now = datetime.datetime.utcnow()
    timestamp = now - datetime.timedelta(days=62)
    limit = now - datetime.timedelta(days=30)
    while timestamp <= limit:
        ac_point = generate_ac_data(timestamp)
        write_to_influxdb(ac_point)
        wm_point = generate_wm_data(timestamp)
        write_to_influxdb(wm_point)
        timestamp += datetime.timedelta(seconds=5)



def generate_data_2():
    now = datetime.datetime.utcnow()
    timestamp = now - datetime.timedelta(days=91)
    limit = now
    count = 0
    while timestamp <= limit:
        battery_point_1, battery_point_2 = generate_battery_data_3m(timestamp) # every 30 secs
        write_to_influxdb(battery_point_1)
        write_to_influxdb(battery_point_2)

        if count % 720 == 0: # every 6 hrs
            sps_point = generate_sps_data_3m(timestamp)
            write_to_influxdb(sps_point)

        if count % 360 == 0: # every 3 hrs
            charger_point = generate_charger_data_3m(timestamp)
            write_to_influxdb(charger_point)

        if count % 2 == 0: # every min
            consumption, production, balance = generate_power_data(timestamp)
            write_to_influxdb(consumption)
            write_to_influxdb(production)
            write_to_influxdb(balance)

        count += 1
        timestamp += datetime.timedelta(seconds=30)
        


def generate_power_data(curr_timestamp):
    id = 1
    consumption = random.random() * 2
    production = random.random() * 2
    balance = production - consumption

    consumption_point = Point("totalConsumption").field("value", consumption).time(curr_timestamp, WritePrecision.NS)
    production_point = Point("totalProduction").field("value", production).time(curr_timestamp, WritePrecision.NS)
    balance_point = Point("totalBalance").field("value", balance).time(curr_timestamp, WritePrecision.NS)

    tags = {
        "deviceId": str(id),
        "unit": "kWh"
    }

    for key,value in tags.items():
        consumption_point.tag(key, str(value))
        production_point.tag(key, str(value))
        balance_point.tag(key, str(value))

    return consumption_point, production_point, balance_point


def generate_battery_data_3m(curr_timestamp):
    id_1 = 5
    id_2 = 6
    user_id = 2
    battery_kwh_1 = random.randint(0, 100)
    battery_kwh_2 = random.randint(0, 100)

    tags_1 = {
        "userId" : str(user_id),
        "deviceId": str(id_1)
    }
    tags_2 = {
        "userId" : str(user_id),
        "deviceId": str(id_2)
    }

    point_1 = Point("battery").field("value", float(battery_kwh_1)).time(curr_timestamp, WritePrecision.NS)
    point_2 = Point("battery").field("value", float(battery_kwh_2)).time(curr_timestamp, WritePrecision.NS)

    for key,value in tags_1.items():
        point_1.tag(key,str(value))

    for key,value in tags_2.items():
        point_2.tag(key,str(value))

    return point_1, point_2


def generate_charger_data_3m(curr_timestamp):
    id = random.randint(3, 4)
    user_id = 2
    state = "START_CHARGE"
    portNum = random.randint(1, 2)

    tags = {
        "userId" : str(user_id),
        "deviceId": str(id),
        "portNum": str(portNum)
    }

    if random.randint(0, 1) == 0:
        state = "END_CHARGE"
        carCharge = random.randint(20, 150)
        spentEnergy = random.randint(1, 130)
        tags['carCharge'] = str(carCharge)
        tags['spentEnergy'] = str(spentEnergy)
    else:
        carCapacity = random.randint(20, 150)
        carCharge = random.randint(0, carCapacity - 5)
        tags['carCapacity'] = carCapacity
        tags['carCharge'] = carCharge

    point = Point("states").field("value", state).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_sps_data_3m(curr_timestamp):
    id = random.randint(1, 2)
    user_id = 2
    val = random.randint(0, 1)

    if val == 0:
        val = "ON"
    else:
        val = "OFF"

    tags = {
        "userId" : str(user_id),
        "deviceId": str(id)
    }

    point = Point("states").field("value", val).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))

    return point


def generate_battery_data(curr_timestamp):
    id = random.randint(900018, 1000017)
    user_id = random.randint(1,1000002)
    battery_kwh = random.randint(0, 100)
    tags = {
        "userId" : str(user_id),
        "deviceId": str(id)
    }
    point = Point("battery").field("value", battery_kwh).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_charger_data(curr_timestamp):
    id = random.randint(1000017, 1100017)
    user_id = random.randint(1,1000002)
    state = "START_CHARGE"
    portNum = random.randint(1, 4)

    tags = {
        "userId" : str(user_id),
        "deviceId": str(id),
        "portNum": str(portNum)
    }

    if random.randint(0, 1) == 0:
        state = "END_CHARGE"
        carCharge = random.randint(20, 150)
        spentEnergy = random.randint(1, 130)
        tags['carCharge'] = str(carCharge)
        tags['spentEnergy'] = str(spentEnergy)
    else:
        carCapacity = random.randint(20, 150)
        carCharge = random.randint(0, carCapacity - 5)
        tags['carCapacity'] = carCapacity
        tags['carCharge'] = carCharge
    

    point = Point("states").field("value", state).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_sps_data(curr_timestamp):
    id = random.randint(1100017, 1200017)
    user_id = random.randint(1,1000002)
    val = random.randint(0, 1)
    if val == 0:
        val = "ON"
    else:
        val = "OFF"
    tags = {
        "userId" : str(user_id),
        "deviceId": str(id)
    }
    point = Point("states").field("value", val).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_consumption_data(curr_timestamp):
    id = random.randint(1100017, 1200017)
    val = random.randint(0, 10)
    
    tags = {
        "deviceId": str(id),
        "unit": "kWh"
    }

    point = Point("totalConsumption").field("value", val).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_production_data(curr_timestamp):
    id = random.randint(1100017, 1200017)
    val = random.randint(0, 10)
    
    tags = {
        "deviceId": str(id),
        "unit": "kWh"
    }

    point = Point("totalProduction").field("value", val).time(curr_timestamp, WritePrecision.NS)
    for key,value in tags.items():
        point.tag(key,str(value))
    return point


def generate_ac_data(curr_timestamp):
    id = random.randint(800016, 900017)
    user_id = random.randint(1,1000002)
    modes = ['HEAT_MODE', 'COOL_MODE', 'AUTO_MODE', 'DRY_MODE', 'FUNGUS_CHANGE', 'HEALTH_CHANGE', 'FAN_SPEED_CHANGE', 'TEMP_CHANGE',
             'CANCEL_SCHEDULED', 'SCHEDULE', 'SCHEDULE_OFF', 'ON', 'OFF', 'SCHEDULE_ON', 'CHANGE']
    state_idx = random.randint(8,14)
    tags = {
            "userId" : str(user_id),
            "deviceId": str(id)
        }
    val = modes[state_idx]
    if val == 'CHANGE':
        mode = modes[random.randint(0,7)]
        if mode == "FUNGUS_CHANGE":
            tags['isFungus'] = False if random.randint(0,1) == 0 else True
        if mode == "HEALTH_CHANGE":
            tags['isHealth'] = False if random.randint(0,1) == 0 else True
        if mode == 'FAN_SPEED_CHANGE':
            tags['fanSpeed'] = random.randint(1,3)
        if mode == "TEMP_CHANGE":
            tags['target'] = random.randint(16,40)
        val = mode

    if val == "SCHEDULE" or val == "CANCEL_SCHEDULED":
        tags['from'] = round((curr_timestamp - datetime.timedelta(hours=1)).timestamp()) * 1000
        tags['to'] = round((curr_timestamp - datetime.timedelta(minutes=15)).timestamp()) * 1000
        tags['everyDay'] = False if random.randint(0,1) == 0 else True

    point = Point("states").field("value", val).time(curr_timestamp, WritePrecision.NS)
    for key,valu in tags.items():
        point.tag(key,str(valu))
    return point


def generate_wm_data(curr_timestamp):
    id = random.randint(100002, 200003)
    user_id = random.randint(1,1000002)
    states = ['ON', 'OFF', 'SCHEDULE_ON', 'SCHEDULE_OFF', 'SCHEDULE', 'CANCEL_SCHEDULED']
    modes = ['COTTONS_MODE', 'SYNTHETICS_MODE', 'DAILY_EXPRESS_MODE', 'WOOL_MODE', 'DARK_WASH_MODE',
    'OUTDOOR_MODE', 'SHIRTS_MODE', 'DUVET_MODE', 'MIXED_MODE', 'STEAM_MODE', 'RINSE_SPIN_MODE', 'SPIN_ONLY_MODE', 'HYGIENE_MODE']
    centrifuge_speeds = [800, 900, 1000, 1100, 1200, 1300, 1400]
    temps = [30, 40, 50, 60, 70, 80, 90]
    state_idx = random.randint(0,5)
    tags = {
            "userId" : str(user_id),
            "deviceId": str(id)
        }
    if states[state_idx] == 'ON':
        tags['mode'] = modes[random.randint(0,12)]
        tags['temp'] = temps[random.randint(0,6)]
        tags['centrifuge'] = centrifuge_speeds[random.randint(0,6)]
    if states[state_idx] == "SCHEDULE" or states[state_idx] == "CANCEL_SCHEDULED":
        tags['from'] = round((curr_timestamp - datetime.timedelta(hours=1)).timestamp()) * 1000
        tags['to'] = round((curr_timestamp - datetime.timedelta(minutes=15)).timestamp()) * 1000
    point = Point("states").field("value", states[state_idx]).time(curr_timestamp, WritePrecision.NS)
    for key,val in tags.items():
        point.tag(key,str(val))
    return point

# Function to write data to InfluxDB
def write_to_influxdb(point):
    try:
        write_api.write(bucket=bucket, record=point)
    except Exception as e:
        print(f"Error writing to InfluxDB: {e}")

# Main execution
if __name__ == "__main__":
    generate_data_2()
    print('Done')