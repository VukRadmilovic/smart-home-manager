# Smart Home

## Tim 11 - članovi
- Maja Varga SV54/2020 - **Student 1**
- Vuk Radmilović SV73/2020 - **Student 3**
- Marko Milijanović SV56/2020 - **Student 2**

## Korišćene tehnologije
- Server - Spring Boot sa Maven-om
- Korisnička aplikacija - React
- Reverse Proxy/Server statičkog sadržaja - nginx
- Fajl server za slike - MinIO
- Relaciona baza podataka - PostgreSQL
- Time-series baza podataka - InfluxDB
- MQTT broker - Mosquitto


## Pokretanje
1. Potrebno je da instalirate sve navedene tehnologije
2. Za pokretanje REST servera, pozicionirajte se u folder *smart-home-back* i izvršite komandu **mvn spring-boot:run** terminalu
3. Za pokretanje Socket servera, pozicionirajte se u folder *smart-home-sockets* i izvršite komandu **mvn spring-boot:run** u terminalu
4. Za pokretanje simulacionog servera, pozicionirajte se u folder *smart-home-simulator* i izvršite komandu **mvn spring-boot:run** u terminalu
5. Za pokretanje nginx-a, pozicionirajte se u folder *nginx* i izvršite komandu **./nginx** ili **nginx** u terminalu, ili dvokliknite na istoimenu datoteku
6. Za pokretanje InfluxDB-a, pozicionirajte se u folder u kome se nalazi vaša instalacija (ukoliko niste menjali podrazumevanu putanju prilikom instalacije *C:\Program Files\InfluxData\influxdb*) i izvršite komandu **./influxd**
ili **influxd** u terminalu
7. Za pokretanje Mosquitto MQTT brokera, pozicionirajte se u folder u kome se nalazi vaša instalacija (ukoliko niste menjali podrazumevanu putanju prilikom instalacije *C:\Program Files\mosquitto*) i izvršite komandu **./mosquitto** ili **mosquitto** u terminalu
8. Za pokretanje MinIO fajl servera, pozicionirajte se u folder u kome se nalazi vaša instalacija (ukoliko niste menjali podrazumevanu putanju prilikom instalacije *C:\Program Files\minio*) i izvršite komandu **.\minio.exe server C:\minio --console-address :9090**
9. Da pristupite korisničkoj aplikaciji, u pretraživaču ukucajte **http://localhost:80**

## Napomene
1. Aplikacija podrazumeva da u okviru InfluxDB-a postoji organizacija pod imenom **Tiba** i *bucket* pod imenom *measurements*. Takođe, API token koji se nalazi u fajlu *application.properties* u *smart-home-back* folderu neće biti validan, pa je potrebno izgenerisati novi i njega kopirati umesto postojećeg. Sve ove akcije možete izvršiti na putanji **http://localhost:8086**
2. MQTT username i password koji su navedeni u *application.properties* fajlu **svake Spring Boot aplikacije (smart-home-back, smart-home-sockets i smart-home-simulator)** neće biti validni. Unesite username i password koji ste podesili prilikom instalacije.
3. Podrazumevani folder u kome se privremeno smeštaju slike prilikom kompresije je **C:/temp**. Ovu putanju možete menjati u skladu sa vašim potrebama.
4. Aplikacija podrazumeva da u PostgreSQL serveru imate kreiranu praznu bazu podataka pod imenom **smart-home**. Šemu baze i podatke će aplikacija automatski kreirati prilikom prvog pokretanja.
