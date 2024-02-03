INSERT INTO ROLE (NAME) VALUES('ROLE_SUPERADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_ADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_USER') ON CONFLICT (NAME) DO NOTHING;
-- INSERT INTO PROPERTIES (ID, NAME, ADDRESS, SIZE, FLOORS, PICTURE,PROPERTY_TYPE) VALUES(9999, 'Yes', 'Yes', '1', '1', '1', 1) ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(1, 'Novi Sad') ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(2, 'Beograd') ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(3, 'Sombor') ON CONFLICT (ID) DO NOTHING;

INSERT INTO users (email, is_confirmed, name, surname, profile_picture, username, full_text, password)
SELECT CONCAT(substr(md5(random()::text), 1, 10), '@mail.com'),
	   TRUE,
	   'TestName',
	   'TestSurname',
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        CONCAT('username',i::text),
        CONCAT('username',i::text, ' ', 'testname', ' ', 'testsurname'),
       '$2a$10$RkfUMnbazK4E7Uuyqf2fSe.3iVOa/vQB9dAi66ORgzFba1paXp58S'
FROM generate_series(1, 1000000) i;

INSERT INTO users (email, is_confirmed, name, surname, profile_picture, username, full_text, password)
VALUES ('maya.grudge.12@gmail.com',true, 'Maja', 'Varga', 'http://127.0.0.1:9000/images/profilePictures/admin.jpg', 'maja', 'maja maja varga','$2a$10$RkfUMnbazK4E7Uuyqf2fSe.3iVOa/vQB9dAi66ORgzFba1paXp58S');

INSERT INTO users (email, is_confirmed, name, surname, profile_picture, username, full_text, password)
VALUES ('marina.grudge.12@gmail.com',true, 'Marina', 'Varga', 'http://127.0.0.1:9000/images/profilePictures/admin.jpg', 'marina', 'marina marina varga','$2a$10$RkfUMnbazK4E7Uuyqf2fSe.3iVOa/vQB9dAi66ORgzFba1paXp58S');

INSERT INTO user_role (user_id, role_id)
SELECT i,3
FROM generate_series(1, 1000002) i;


/*INSERT INTO properties (address, floors, name, picture, property_type, size, status, owner_id)
SELECT 'TestAddress',
		'1',
		CONCAT('Property ', substr(md5(random()::text), 1, 10)),
		'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
		0,
		'100',
		0,
		floor(random() * 1000 + 1)
FROM generate_series(1,1000);*/

INSERT INTO properties (address, floors, name, picture, property_type, size, status, owner_id)
VALUES ('TestAddress',
		'1',
		'1',
		'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
		0,
		'100',
		0,
		1000001);

INSERT INTO towns_properties VALUES (1,1)


/*INSERT INTO thermometers (id, energy_consumption, image, name, online, power_source, still_there, property_id,temperature_unit)
SELECT 
		i,
		5,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Thermometer ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   1,
	   'CELSIUS'
FROM generate_series(1, 2) i;


INSERT INTO washing_machines
SELECT
    i,
    30,
    'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
    CONCAT('Washing Machine ', substr(md5(random()::text), 1, 10)),
    FALSE,
    'HOUSE',
    FALSE,
    1,
    1400,
    800,
    TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 30, 90, 'CELSIUS', TRUE
FROM generate_series(3, 4) i;

INSERT INTO sprinkler_systems
SELECT 
		i,
		18,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Sprinkler System ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   floor(random() * 100000 + 1),
	   CURRENT_TIMESTAMP,
	   FALSE,
	   FALSE,
	   CURRENT_TIMESTAMP
FROM generate_series(200004, 300004) i;


INSERT INTO sprinkler_systems
VALUES ( 
		300005,
		18,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Sprinkler System ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   100001,
	   CURRENT_TIMESTAMP,
	   FALSE,
	   FALSE,
	   CURRENT_TIMESTAMP);


INSERT INTO solar_panel_systems
SELECT 
		i,
		0,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Solar panels ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   floor(random() * 100000 + 1),
	   FALSE,
	   10,
	   5.0,
	   5
FROM generate_series(300006, 400006) i;


INSERT INTO solar_panel_systems
VALUES (400007,
		0,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Solar panels ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   100001,
	   FALSE,
	   10,
	   5.0,
	   5);

INSERT INTO lamps
SELECT 
		i,
		10,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Lamp ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   floor(random() * 100000 + 1),
	   FALSE
FROM generate_series(400008, 500008) i;


INSERT INTO lamps
VALUES (500009,
		10,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Lamp ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   100001,
	   FALSE);


INSERT INTO gates
SELECT 
		i,
		20,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Gate ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   floor(random() * 100000 + 1),
	   TRUE
FROM generate_series(500010, 600010) i;


INSERT INTO gates
VALUES (600011,
		20,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Gate ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   100001,
	   TRUE);


INSERT INTO chargers
SELECT 
		i,
		200,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Charger ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   floor(random() * 100000 + 1),
	   100.0,
	   2,
	   30.0
FROM generate_series(600012, 700012) i;


INSERT INTO chargers
VALUES (700013,
		200,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Charger ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   100001,
	   100.0,
	   2,
	   30.0);


INSERT INTO batteries
SELECT 
		i,
		5,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Battery ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   floor(random() * 100000 + 1),
	   100.0,
	   0.0
FROM generate_series(700014, 800014) i;


INSERT INTO batteries
VALUES (800015,
		5,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Battery ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'AUTONOMOUS',
	   FALSE,
	   100001,
	   100.0,
	   0.0);


INSERT INTO air_conditioners
SELECT
    i,
    50,
    'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
    CONCAT('Air Conditioner ', substr(md5(random()::text), 1, 10)),
    FALSE,
    'HOUSE',
    FALSE,
    floor(random() * 100000 + 1),
    TRUE, TRUE, TRUE, 3, TRUE, TRUE, TRUE, FALSE, 40, 16, 'CELSIUS'
FROM generate_series(800016, 900016) i;


INSERT INTO air_conditioners
VALUES (900017,
		50,
	   'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
	   CONCAT('Air Conditioner ', substr(md5(random()::text), 1, 10)),
	   FALSE,
	   'HOUSE',
	   FALSE,
	   100001,
	   TRUE, TRUE, TRUE, 3, TRUE, TRUE, TRUE, FALSE, 40, 16, 'CELSIUS');


INSERT INTO device_control (device_id, owner_id) VALUES (100001, 1000002), (200003, 1000002), (900017, 1000002);
INSERT INTO device_control (device_id, owner_id)
SELECT
    floor(random() * (900016-1+1) + 1)::int,
    floor(random() * (1000002-2+1) + 2)::int
FROM generate_series(1, 100000);*/

