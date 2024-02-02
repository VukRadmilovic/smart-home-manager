from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
import random, datetime

# InfluxDB connection settings
url = 'http://localhost:8086'
token = 'cq6nclb3oF18TopO-KJ20Iq1U1b2tPhnl-KCaPiDXcs9RuidUxrkRNrGXpwkrmibyT4nkFmHTcPqEyGV8pJbfw=='
org = 'Tiba'
bucket = 'measurements'
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
    generate_states_data()
    print('Done')