# Smart Home



## Pokretanje
Potrebno je da instalirate Docker desktop, PostgreSQL bazu (poželjno sa PgAdmin-om), Mosquitto MQTT broker (https://mosquitto.org/download/), InfluxDB  i Javu 17. Podesite da vam je ta verzija Jave postavljena u JAVA_HOME environment varijablu (https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html). Isto tako, podesite da vam se projekat u IntelliJ-u pokreće sa tom verzijom (https://www.baeldung.com/intellij-change-java-version).

Prvo uđite na back, i izvršite sledeću komandu u terminalu: ./mvnw install
Ovo će vam generisati target folder, koji će biti bitan za pravljenje docker image-a back-a.

Posle toga isto u terminalu izvršite komandu: docker build -t ftn/smart-home .
Ako imate Docker Desktop, u Images će se pojaviti image sa tim imenom.

Da napravite instancu back-a, u terminalu pokrenite komandu: docker run -p 1111:8080 ftn/smart-home.
Za drugu instancu samo promenite 1111 na 2222, a za treću 3333. Za svaku sledeću instancu otvorite novi terminal, jer će prethodni biti blokiran.
Ako ti portovi nisu dostupni, napišite neki drugi, ali vodite računa da u nginx.conf fajlu stoje ti portovi kod upstream backend dela (linije 21-25).
Kada budete zatvarali aplikaciju, kroz Docker Desktop stopirajte kontejnere koji run-uju ftn/smart-home image. Možete ih i obrisati, svakako će sledeći put da napravi neki drugi container (posle ću rešavati docker_compose). 

Da pokrenete nginx kod sebe, samo izvršite nginx.exe fajl unutar nginx-1.25.3 folderu. Biće dostupno na localhost:80 lokaciji. Ako to ne odgovara, u nginx.conf fajlu na 42. liniji promenite port.
nginx.conf fajl se nalazi u folderu nginx-1.25.3/conf folderu.
Da se BILO KAKVA izmena oslika na nginx-u, u terminalu U FOLDERU gde se nalazi nginx treba izvršiti: .\nginx -s reload.
U browser-u, da se oslika ta izmena treba reload-ovati prozor, verovatno će morati biti hard reload (Ctrl + Shift + R).

Da se nginx ugasi, izvršiti komandu .\nginx -s quit.

Da pokrenete mosquitto broker, pozicionirajte se u folder gde ste skinuli mosquitto (vrv C:/Program Files/mosquitto) i izvršite sledeću komandu: mosquitto_passwd -c pwfile.txt client
Posle toga, lozinka treba da bude 'komarac' (bez navodnika).
Proverite da li je fajl kreiran i da li ima client:blablablabla u sebi (hash lozinke).
Da pokrenete brokera, u istom terminalu pokrenite: mosquitto -v. Radi na portu 1883, ali ako ne odgovara, u mosquitto.conf fajlu možete napisati 'listener   broj_porta' (bez navodnika), i tamo će onda raditi.

Redosled kojim treba komponente startovati je:
1. PostgreSQL
2. InfluxDB
3. Mosquitto Broker
4. Simulator Server (Spring boot aplikacija na portu 8081)
5. Docker-izovane verzije glavnog backend-a
6. nginx

