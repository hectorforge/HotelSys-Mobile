USE hotelsys_db;

-- Insertar usuarios con contraseñas encriptadas (BCrypt con 10 rondas de hashing)
-- Probadas en https://bcrypt-generator.com/
-- Contraseña para admin@hotel.com: "admin"
-- Contraseña para usuario@hotel.com: "usuario"
INSERT INTO usuarios (nombre_usuario, email, password, rol) VALUES 
('Admin General', 'admin@hotel.com', '$2a$10$FLMCej4jcVBeh1Uvl7R7n.Xw2Fx.qmBniSaJLbcUgzrfi6kx2QyaG', 'ADMIN'),
('Juan Recepcionista', 'usuario@hotel.com', '$2a$10$8RXO6fLzX2GTK1zebCj9Ru6urLzQE3In2YVcPZrLwXzMdlUZsbYH2', 'USER');

INSERT INTO tipos_documento (descripcion) VALUES ('DNI'), ('RUC'), ('Pasaporte');
INSERT INTO tipos_habitacion (descripcion, precio_base_noche) VALUES ('Simple', 100.00), ('Doble', 180.00), ('Matrimonial', 220.00), ('Suite', 350.00);
INSERT INTO estados_habitacion (descripcion) VALUES ('Disponible'), ('Ocupada'), ('Mantenimiento');
INSERT INTO estados_reserva (descripcion) VALUES ('Pendiente'), ('Confirmada'), ('Cancelada'), ('Finalizada');

INSERT INTO clientes (nombre_completo, numero_documento, email, telefono, activo, tipo_documento_id) VALUES
('Juan Pérez Gonzales', '71234567', 'juan.perez@email.com', '987654321', 1, 1),
('Constructora El Sol S.A.C.', '20123456789', 'contacto@elsol.com', '014567890', 1, 2),
('María López Castillo', '87654321', 'maria.lopez@email.com', '912345678', 1, 1),
('Michael Smith', 'A1B2345C', 'msmith@email.com', '555-1234', 0, 3);

INSERT INTO productos (nombre_producto, precio, stock, activo) VALUES
('Leña para fogata', 30.00, 100, 1),
('Cena Ejecutiva', 45.00, 50, 1),
('Desayuno Buffet', 30.00, 80, 1),
('Papas Lays', 2.40, 250, 1),
('Gaseosa 500 ml', 2.50, 300, 0);

INSERT INTO habitaciones (numero, tipo_habitacion_id, estado_habitacion_id, requiere_limpieza, activo) VALUES
('101', 1, 1, 0, 1), ('102', 1, 1, 0, 1),
('201', 2, 1, 0, 1), ('202', 2, 1, 1, 1),
('301', 3, 1, 0, 1),
('401', 4, 3, 1, 1),
('402', 1, 1, 0, 1),
('405', 2, 2, 0, 0);

/* NUEVOOOO */

-- Imágenes para habitación 101
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/101_1.jpg', 'Habitación 101 - vista principal', 'Habitación simple con cama individual y vista al jardín', 1, 1),
('/img/habitaciones/101_2.jpg', 'Habitación 101 - baño', 'Baño privado con ducha y agua caliente', 2, 1),
('/img/habitaciones/101_3.jpg', 'Habitación 101 - escritorio', 'Pequeño escritorio para trabajo o lectura', 3, 1);

-- Imágenes para habitación 102
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/102_1.jpg', 'Habitación 102 - vista principal', 'Habitación simple con iluminación natural', 1, 2),
('/img/habitaciones/102_2.jpg', 'Habitación 102 - baño', 'Baño privado con amenities incluidos', 2, 2),
('/img/habitaciones/102_3.jpg', 'Habitación 102 - armario', 'Armario empotrado con espacio para equipaje', 3, 2);

-- Imágenes para habitación 201
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/201_1.jpg', 'Habitación 201 - doble', 'Habitación doble con dos camas individuales', 1, 3),
('/img/habitaciones/201_2.jpg', 'Habitación 201 - escritorio', 'Espacio de trabajo con escritorio amplio', 2, 3),
('/img/habitaciones/201_3.jpg', 'Habitación 201 - baño', 'Baño con ducha tipo lluvia y secador de cabello', 3, 3);

-- Imágenes para habitación 202
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/202_1.jpg', 'Habitación 202 - doble', 'Habitación doble con decoración moderna', 1, 4),
('/img/habitaciones/202_2.jpg', 'Habitación 202 - sala de estar', 'Pequeña área de descanso junto a la ventana', 2, 4),
('/img/habitaciones/202_3.jpg', 'Habitación 202 - baño', 'Baño remodelado recientemente con ducha', 3, 4);

-- Imágenes para habitación 301
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/301_1.jpg', 'Habitación 301 - matrimonial', 'Habitación matrimonial con cama king y balcón privado', 1, 5),
('/img/habitaciones/301_2.jpg', 'Habitación 301 - baño', 'Baño con bañera y amenities premium', 2, 5),
('/img/habitaciones/301_3.jpg', 'Habitación 301 - balcón', 'Vista panorámica desde el balcón', 3, 5);

-- Imágenes para habitación 401 (Suite)
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/401_1.jpg', 'Suite 401 - sala principal', 'Suite con sala de estar y TV de 55 pulgadas', 1, 6),
('/img/habitaciones/401_2.jpg', 'Suite 401 - dormitorio', 'Dormitorio con cama king y minibar', 2, 6),
('/img/habitaciones/401_3.jpg', 'Suite 401 - jacuzzi', 'Jacuzzi privado con iluminación ambiental', 3, 6);

-- Imágenes para habitación 402
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/402_1.jpg', 'Habitación 402 - simple', 'Habitación simple con cama individual', 1, 7),
('/img/habitaciones/402_2.jpg', 'Habitación 402 - baño', 'Baño con ducha y amenities básicos', 2, 7),
('/img/habitaciones/402_3.jpg', 'Habitación 402 - decoración', 'Decoración minimalista con luz natural', 3, 7);

-- Imágenes para habitación 405
INSERT INTO imagenes_habitaciones (url, alt, descripcion, orden, habitacion_id) VALUES
('/img/habitaciones/405_1.jpg', 'Habitación 405 - doble', 'Habitación doble con camas separadas', 1, 8),
('/img/habitaciones/405_2.jpg', 'Habitación 405 - escritorio', 'Espacio de trabajo con escritorio de madera', 2, 8),
('/img/habitaciones/405_3.jpg', 'Habitación 405 - baño', 'Baño completo con ducha y toallas limpias', 3, 8);


/* NUEVOOOO */

INSERT INTO reservas (fecha_reserva, fecha_check_in, fecha_check_out, monto_total_calculado, activo, cliente_id, estado_reserva_id) VALUES
(NOW(), '2025-08-25', '2025-08-30', 985.00, 1, 3, 2);
SET @last_reserva_id = LAST_INSERT_ID();

INSERT INTO reserva_habitaciones(reserva_id, habitacion_id, precio_noche_grabado) VALUES
(@last_reserva_id, 3, 180.00);

INSERT INTO reserva_productos(reserva_id, producto_id, cantidad, precio_unitario_grabado) VALUES
(@last_reserva_id, 2, 1, 45.00),
(@last_reserva_id, 4, 4, 2.40);

UPDATE habitaciones SET estado_habitacion_id = 2 WHERE id = 3;