-- Insertar datos en la tabla de paises
INSERT INTO paises (nombre) VALUES ('España');
INSERT INTO paises (nombre) VALUES ('Italia');
INSERT INTO paises (nombre) VALUES ('Francia');
-- Insertar datos en la tabla de localizaciones
INSERT INTO localizaciones (nombre, codigo, id_pais) VALUES ('Localizacion 1', 'LOC1', (SELECT id FROM paises WHERE nombre = 'España'));
INSERT INTO localizaciones (nombre, codigo, id_pais) VALUES ('Localizacion 2', 'LOC2', (SELECT id FROM paises WHERE nombre = 'Francia'));
INSERT INTO localizaciones (nombre, codigo, id_pais) VALUES ('Localizacion 3', 'LOC3', (SELECT id FROM paises WHERE nombre = 'Italia'));
-- Insertar datos en la tabla de hoteles
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 1', 4, 'Descripción del Hotel 1', 'contacto1@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 1'));
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 2', 5, 'Descripción del Hotel 2', 'contacto2@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 2'));
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 3', 3, 'Descripción del Hotel 3', 'contacto3@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 3'));
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 4', 1, 'Descripción del Hotel 4', 'contacto4@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 3'));
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 5', 3, 'Descripción del Hotel 5', 'contacto5@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 2'));
INSERT INTO hoteles (nombre, categoria, descripcion, contacto, id_localizacion) VALUES ('Hotel 6', 2, 'Descripción del Hotel 6', 'contacto6@example.com', (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 2'));

-- Insertar datos en la tabla de imagenes
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Hilton_Vienna_Plaza.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 1'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Hotel_Artemide_2.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 1'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Melia_Madrid_Princesa.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 2'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Nobu_Hotel.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 3'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Radisson_Blu_Hotel_Berlin.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 3'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Melia_Madrid_Princesa.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 4'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Melia_Madrid_Princesa.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 5'));
-- INSERT INTO imagenes (url, id_hotel) VALUES ('/images/Hoteles/Melia_Madrid_Princesa.jpg', (SELECT id FROM hoteles WHERE nombre = 'Hotel 6'));

-- Insertar datos en la tabla de companias
INSERT INTO companias_vuelo (nombre, contacto) VALUES ('Compania 1', 'contacto1@compania.com');
INSERT INTO companias_vuelo (nombre, contacto) VALUES ('Compania 2', 'contacto2@compania.com');
INSERT INTO companias_vuelo (nombre, contacto) VALUES ('Compania 3', 'contacto3@compania.com');
INSERT INTO companias_vuelo (nombre, contacto) VALUES ('Compania 4', 'contacto4@compania.com');

-- Insertar datos en la tabla de vuelos
INSERT INTO vuelos (nombre, descripcion, fecha_salida, fecha_llegada, id_compania, id_imagen, id_origen, id_destino) VALUES ('Vuelo 1', 'Descripción Vuelo 1','2024-07-01 08:30:00', '2024-07-01 12:00:00', (SELECT id FROM companias_vuelo WHERE nombre = 'Compania 1'), (SELECT id FROM imagenes WHERE url = '/images/Hoteles/Hilton_Vienna_Plaza.jpg'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 1'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 2'));
INSERT INTO vuelos (nombre, descripcion, fecha_salida, fecha_llegada, id_compania, id_imagen, id_origen, id_destino) VALUES ('Vuelo 2','Descripción Vuelo 2','2024-07-02 14:45:00', '2024-07-02 18:15:00', (SELECT id FROM companias_vuelo WHERE nombre = 'Compania 2'), (SELECT id FROM imagenes WHERE url = '/images/Hoteles/Hotel_Artemide_2.jpg'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 2'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 3'));
INSERT INTO vuelos (nombre, descripcion, fecha_salida, fecha_llegada, id_compania, id_imagen, id_origen, id_destino) VALUES ('Vuelo 3','Descripción Vuelo 3','2024-07-04 21:15:00', '2024-07-05 00:45:00', (SELECT id FROM companias_vuelo WHERE nombre = 'Compania 4'), (SELECT id FROM imagenes WHERE url = '/images/Hoteles/Nobu_Hotel.jpg'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 3'), (SELECT id FROM localizaciones WHERE nombre = 'Localizacion 1'));

-- Inserta los tipos de habitación en la base de datos
INSERT INTO tipo_habitacion (nombre, descripcion) VALUES ('Individual', 'Habitación con una cama individual');
INSERT INTO tipo_habitacion (nombre, descripcion) VALUES ('Doble', 'Habitación con una cama doble o dos camas individuales');
INSERT INTO tipo_habitacion (nombre, descripcion) VALUES ('Suite', 'Habitación con sala de estar y dormitorio separado');
INSERT INTO tipo_habitacion (nombre, descripcion) VALUES ('Familiar', 'Habitación con varias camas, ideal para familias');
INSERT INTO tipo_habitacion (nombre, descripcion) VALUES ('Deluxe', 'Habitación de lujo con comodidades adicionales');

-- Inserta las habitaciones en la base de datos
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (101, 2, 1, 1, 1);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (102, 4, 2, 2, 1);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (103, 2, 1, 3, 1);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (104, 3, 1, 1, 1);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (105, 4, 2, 2, 3);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (106, 2, 1, 3, 2);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (107, 3, 1, 1, 2);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (108, 4, 2, 2, 2);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (109, 2, 1, 3, 2);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (110, 3, 1, 1, 2);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (111, 2, 1, 1, 5);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (112, 4, 2, 2, 6);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (113, 2, 1, 3, 4);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (114, 3, 1, 1, 3);
INSERT INTO habitaciones (numero, capacidad, numero_camas, id_tipo, id_hotel) VALUES (115, 4, 2, 2, 3);

INSERT INTO imagenes (url, id_habitacion) VALUES ('Nobu_hotel.jpg', 6);
INSERT INTO imagenes (url, id_habitacion) VALUES ('Nobu_hotel.jpg', 7);
INSERT INTO imagenes (url, id_habitacion) VALUES ('Nobu_hotel.jpg', 8);
INSERT INTO imagenes (url, id_habitacion) VALUES ('Nobu_hotel.jpg', 9);
INSERT INTO imagenes (url, id_habitacion) VALUES ('Nobu_hotel.jpg', 10);

# Inserción de tipos de actividad
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Acuática', 'Actividades relacionadas con el agua, como natación, buceo, etc.');
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Adrenalina', 'Actividades que generan emoción y adrenalina, como paracaidismo, bungee jumping, etc.');
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Aventura de Día', 'Actividades de aventura que se realizan durante el día, como senderismo, escalada, etc.');
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Aventura de Noche', 'Actividades de aventura que se realizan durante la noche, como campamentos nocturnos, observación de estrellas, etc.');
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Gastronomía', 'Actividades relacionadas con la cocina y la comida, como clases de cocina, catas de vino, etc.');
INSERT INTO tipo_actividad (nombre, descripcion) VALUES ('Tours Históricos', 'Visitas guiadas y tours a sitios históricos y culturales.');

-- Actividades relacionadas con los tipos de actividad previamente insertados

-- Actividades de tipo "Acuática" (tipoActividad.id = 1)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Buceo en Costa Brava', 'Excursión de buceo en aguas cristalinas de la Costa Brava', '2024-07-15 09:00:00', '2024-07-15 12:00:00', 1, 1);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Natación en Marbella', 'Sesión de natación en las playas de Marbella', '2024-07-20 10:00:00', '2024-07-20 11:00:00', 1, 1);

-- Actividades de tipo "Adrenalina" (tipoActividad.id = 2)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Salto en Paracaídas', 'Experiencia emocionante de salto en paracaídas sobre los Alpes', '2024-08-01 08:00:00', '2024-08-01 11:00:00', 2, 2);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Bungee Jumping en Interlaken', 'Salto de bungee desde el puente más alto de Interlaken', '2024-08-10 14:00:00', '2024-08-10 16:00:00', 2, 2);

-- Actividades de tipo "Aventura de Día" (tipoActividad.id = 3)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Senderismo en los Pirineos', 'Ruta de senderismo por los Pirineos franceses', '2024-09-05 09:00:00', '2024-09-05 17:00:00', 3, 2);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Escalada en Montserrat', 'Día de escalada en las montañas de Montserrat cerca de Barcelona', '2024-09-10 10:00:00', '2024-09-10 15:00:00', 3, 1);

-- Actividades de tipo "Aventura de Noche" (tipoActividad.id = 4)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Campamento Nocturno en Sierra Nevada', 'Noche de campamento bajo las estrellas en Sierra Nevada', '2024-08-20 18:00:00', '2024-08-21 10:00:00', 4, 1);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Observación de Estrellas en Toscana', 'Noche de observación de estrellas en los viñedos de la Toscana', '2024-08-25 21:00:00', '2024-08-25 23:00:00', 4, 3);

-- Actividades de tipo "Gastronomía" (tipoActividad.id = 5)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Clase de Cocina en Barcelona', 'Clase práctica de cocina catalana en el corazón de Barcelona', '2024-07-25 11:00:00', '2024-07-25 14:00:00', 5, 1);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Cata de Vinos en Burdeos', 'Visita a bodegas y cata de vinos en la región de Burdeos', '2024-08-05 16:00:00', '2024-08-05 18:00:00', 5, 2);

-- Actividades de tipo "Tours Históricos" (tipoActividad.id = 6)
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Visita Guiada por el Coliseo Romano', 'Recorrido histórico por el Coliseo Romano y sus alrededores', '2024-07-25 11:00:00', '2024-07-25 14:00:00', 6, 3);
INSERT INTO actividades (nombre, descripcion, fecha_inicio, fecha_fin, id_tipo, id_localizacion) VALUES ('Tour por los Museos de París', 'Visita guiada por los museos más importantes de París', '2024-09-20 10:00:00', '2024-11-30 14:00:00', 6, 2);


--  Precios Habitaciones Hotel 1
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (100.00, '2024-01-01T00:00:00', '2024-03-31T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (120.00, '2024-04-01T00:00:00', '2024-06-30T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (110.00, '2024-07-01T00:00:00', '2024-09-30T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (130.00, '2024-10-01T00:00:00', '2024-12-31T23:59:59', 1);

INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (100.00, '2024-01-01T00:00:00', '2024-03-31T23:59:59', 2);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (120.00, '2024-04-01T00:00:00', '2024-06-30T23:59:59', 2);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (110.00, '2024-07-01T00:00:00', '2024-09-30T23:59:59', 2);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (130.00, '2024-10-01T00:00:00', '2024-12-31T23:59:59', 2);

INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (100.00, '2024-01-01T00:00:00', '2024-03-31T23:59:59', 3);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (120.00, '2024-04-01T00:00:00', '2024-06-30T23:59:59', 3);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (110.00, '2024-07-01T00:00:00', '2024-09-30T23:59:59', 3);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (130.00, '2024-10-01T00:00:00', '2024-12-31T23:59:59', 3);

INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (100.00, '2024-01-01T00:00:00', '2024-03-31T23:59:59', 4);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (120.00, '2024-04-01T00:00:00', '2024-06-30T23:59:59', 4);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (110.00, '2024-07-01T00:00:00', '2024-09-30T23:59:59', 4);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_habitacion) VALUES (130.00, '2024-10-01T00:00:00', '2024-12-31T23:59:59', 4);

-- Precios Actividades Acuáticas
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_actividad) VALUES (100.00, '2024-01-01T00:00:00', '2024-03-31T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_actividad) VALUES (120.00, '2024-04-01T00:00:00', '2024-06-30T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_actividad) VALUES (110.00, '2024-07-01T00:00:00', '2024-09-30T23:59:59', 1);
INSERT INTO precio (valor, fecha_inicio, fecha_fin, id_actividad) VALUES (130.00, '2024-10-01T00:00:00', '2024-12-31T23:59:59', 1);