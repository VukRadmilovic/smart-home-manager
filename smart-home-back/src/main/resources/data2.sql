INSERT INTO ROLE (NAME) VALUES('ROLE_SUPERADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_ADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_USER') ON CONFLICT (NAME) DO NOTHING;
-- INSERT INTO PROPERTIES (ID, NAME, ADDRESS, SIZE, FLOORS, PICTURE,PROPERTY_TYPE) VALUES(9999, 'Yes', 'Yes', '1', '1', '1', 1) ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(1, 'Novi Sad') ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(2, 'Beograd') ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(3, 'Sombor') ON CONFLICT (ID) DO NOTHING;

INSERT INTO users (email, is_confirmed, name, surname, profile_picture, username, password, full_text)
VALUES ('2001vuk2@gmail.com', true, 'Vuk', 'Radmilovic', 'http://127.0.0.1:9000/images/profilePictures/admin.jpg', 'vuk2', '$2a$10$nExLq9vNH8vOk0Aq6H4PW.0RcdVoK95/twx0bUD.sFp96gTABg4da', 'vuk2 vuk radmilovic');

INSERT INTO users (email, is_confirmed, name, surname, profile_picture, username, password, full_text)
VALUES ('2001vuk@gmail.com', true, 'Vuk', 'Radmilovic', 'http://127.0.0.1:9000/images/profilePictures/admin.jpg', 'vuk', '$2a$10$nExLq9vNH8vOk0Aq6H4PW.0RcdVoK95/twx0bUD.sFp96gTABg4da', 'vuk vuk radmilovic');

INSERT INTO user_role (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'vuk'), (SELECT id FROM role WHERE name = 'ROLE_USER'));

INSERT INTO properties (address, floors, name, picture, property_type, size, status, owner_id)
VALUES ('TestAddress',
        '1',
        '1',
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        0,
        '100',
        0,
        (SELECT id FROM users WHERE username = 'vuk'));

INSERT INTO towns_properties (town_id, properties_id)
VALUES (1, 1);

INSERT INTO solar_panel_systems
VALUES (1,
        1,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Solar panel system 1',
        FALSE,
        'AUTONOMOUS',
        FALSE,
        1,
        TRUE,
        10,
        25.0,
        15),
       (2,
        1,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Solar panel system 2',
        FALSE,
        'AUTONOMOUS',
        FALSE,
        1,
        TRUE,
        8,
        22.0,
        10);


INSERT INTO chargers
VALUES (3,
        100,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Charger 1',
        FALSE,
        'HOUSE',
        FALSE,
        1,
        100.0,
        2,
        90.0),
       (4,
        80,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Charger 2',
        FALSE,
        'HOUSE',
        FALSE,
        1,
        100.0,
        2,
        70.0);


INSERT INTO batteries
VALUES (5,
        5,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Battery 1',
        FALSE,
        'AUTONOMOUS',
        FALSE,
        1,
        100.0,
        0.0),
       (6,
        5,
        'http://127.0.0.1:9000/images/profilePictures/admin.jpg',
        'Battery 2',
        FALSE,
        'AUTONOMOUS',
        FALSE,
        1,
        125.0,
        0.0);

UPDATE device_generator SET next_val = 7;