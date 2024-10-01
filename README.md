# Smart Home Manager Project

## Tech Stack
- Spring Boot
- React
- Nginx
- MinIO
- PostgreSQL
- InfluxDB
- Mosquitto

## Description
This project is a smart home manager web application developed by a team of university students as part of their engineering software course. The project is a software solution for monitoring smart homes within smart cities. Each smart home contains various devices that measure values such as temperature, humidity, and energy consumption. Some devices have manual commands (e.g., turning on air conditioning) or automatic functions (e.g., lowering blinds when the sunlight is intense). The system provides a platform where homeowners can log in and monitor their home in real-time. Multiple users can track their properties, while administrators oversee energy consumption and production across neighborhoods or entire cities.

## Functionalities per role

### Unregistered user
- Register an account
- Login onto the system

### Registered user
- Register smart homes and devices within them
- Monitor smart device status
- Control smart devices (lights, thermostats, etc.)
- Share access to devices
- Set up automation rules for devices
- Access historical data and analytics from InfluxDB
- Receive notifications for device alerts

### Administrator
- Review home registration requests
- View energy production and expenditure data

## How to run
1. Install all required technologies.
2. To run the REST server, navigate to the `smart-home-back` folder and execute the command `mvn spring-boot:run` in the terminal.
3. To run the socket server, navigate to the `smart-home-sockets` folder and execute the command `mvn spring-boot:run` in the terminal.
4. To run the simulation server, navigate to the `smart-home-simulator` folder and execute the command `mvn spring-boot:run` in the terminal.
5. To run Nginx, navigate to the `nginx` folder and execute `./nginx` or `nginx` in the terminal, or double-click the corresponding file.
6. To run InfluxDB, navigate to the installation folder (default: `C:\Program Files\InfluxData\influxdb`) and execute `./influxd` or `influxd` in the terminal.
7. To run the Mosquitto MQTT broker, navigate to the installation folder (default: `C:\Program Files\mosquitto`) and execute `./mosquitto` or `mosquitto` in the terminal.
8. To run the MinIO file server, navigate to the installation folder (default: `C:\Program Files\minio`) and execute `. \minio.exe server C:\minio --console-address :9090`.
9. To access the user application, open a browser and go to `http://localhost:80`.

## Notes
- The application assumes that there is an organization named "ftn" and a bucket named "measurements" in InfluxDB.
- The API token located in the `application.properties` file in the `smart-home-back` folder will not be valid and needs to be regenerated.
- The MQTT username and password specified in the `application.properties` files for each Spring Boot application (smart-home-back, smart-home-sockets, and smart-home-simulator) need to be set according to your installation.
- The default folder for temporarily storing images during compression is `C:/temp`. This path can be changed as per your requirements.
- The application requires an empty PostgreSQL database named "smart-home" to be created beforehand. The schema and data will be automatically generated on the first run.
