INSERT INTO ROLE (NAME) VALUES('ROLE_SUPERADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_ADMIN') ON CONFLICT (NAME) DO NOTHING;
INSERT INTO ROLE (NAME) VALUES('ROLE_USER') ON CONFLICT (NAME) DO NOTHING;
-- INSERT INTO PROPERTIES (ID, NAME, ADDRESS, SIZE, FLOORS, PICTURE,PROPERTY_TYPE) VALUES(9999, 'Yes', 'Yes', '1', '1', '1', 1) ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(1, 'Novi Sad') ON CONFLICT (ID) DO NOTHING;
INSERT INTO TOWNS (ID, NAME) VALUES(2, 'Beograd') ON CONFLICT (ID) DO NOTHING;