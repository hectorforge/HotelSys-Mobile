DROP DATABASE IF EXISTS hotelsys_db;
CREATE DATABASE hotelsys_db;

USE hotelsys_db;

CREATE TABLE IF NOT EXISTS tipos_documento (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS tipos_habitacion (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(100) NOT NULL,
  `precio_base_noche` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS estados_habitacion (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS estados_reserva (
  `id` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS clientes (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre_completo` VARCHAR(200) NOT NULL,
  `numero_documento` VARCHAR(20) NOT NULL,
  `email` VARCHAR(100) NULL,
  `telefono` VARCHAR(20) NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `tipo_documento_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `numero_documento_UNIQUE` (`numero_documento` ASC),
  CONSTRAINT `fk_clientes_tipos_documento`
    FOREIGN KEY (`tipo_documento_id`)
    REFERENCES `tipos_documento` (`id`));

CREATE TABLE IF NOT EXISTS habitaciones (
  `id` INT NOT NULL AUTO_INCREMENT,
  `numero` VARCHAR(10) NOT NULL,
  `requiere_limpieza` TINYINT(1) NOT NULL DEFAULT 0,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `tipo_habitacion_id` INT NOT NULL,
  `estado_habitacion_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `numero_UNIQUE` (`numero` ASC),
  CONSTRAINT `fk_habitaciones_tipos_habitacion`
    FOREIGN KEY (`tipo_habitacion_id`)
    REFERENCES `tipos_habitacion` (`id`),
  CONSTRAINT `fk_habitaciones_estados_habitacion`
    FOREIGN KEY (`estado_habitacion_id`)
    REFERENCES `estados_habitacion` (`id`));

CREATE TABLE IF NOT EXISTS productos (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre_producto` VARCHAR(100) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS usuarios (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre_usuario` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `rol` VARCHAR(20) NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));

CREATE TABLE IF NOT EXISTS reservas (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fecha_reserva` DATETIME NOT NULL,
  `fecha_check_in` DATE NOT NULL,
  `fecha_check_out` DATE NOT NULL,
  `descuento` DECIMAL(10,2) NULL DEFAULT 0.00,
  `monto_total_calculado` DECIMAL(10,2) NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `cliente_id` INT NOT NULL,
  `estado_reserva_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_reservas_clientes`
    FOREIGN KEY (`cliente_id`)
    REFERENCES `clientes` (`id`),
  CONSTRAINT `fk_reservas_estados_reserva`
    FOREIGN KEY (`estado_reserva_id`)
    REFERENCES `estados_reserva` (`id`));

CREATE TABLE IF NOT EXISTS reserva_habitaciones (
  `id` INT NOT NULL AUTO_INCREMENT,
  `precio_noche_grabado` DECIMAL(10,2) NOT NULL,
  `reserva_id` INT NOT NULL,
  `habitacion_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_reserva_habitaciones_reservas`
    FOREIGN KEY (`reserva_id`)
    REFERENCES `reservas` (`id`),
  CONSTRAINT `fk_reserva_habitaciones_habitaciones`
    FOREIGN KEY (`habitacion_id`)
    REFERENCES `habitaciones` (`id`));

CREATE TABLE IF NOT EXISTS reserva_productos (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cantidad` INT NOT NULL,
  `precio_unitario_grabado` DECIMAL(10,2) NOT NULL,
  `reserva_id` INT NOT NULL,
  `producto_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_reserva_productos_reservas`
    FOREIGN KEY (`reserva_id`)
    REFERENCES `reservas` (`id`),
  CONSTRAINT `fk_reserva_productos_productos`
    FOREIGN KEY (`producto_id`)
    REFERENCES `productos` (`id`));
    
CREATE TABLE IF NOT EXISTS imagenes_habitaciones (
  `id` INT NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(255) NOT NULL,
  `alt` VARCHAR(255) NOT NULL,
  `descripcion` VARCHAR(255) NULL,
  `orden` INT NULL,
  `habitacion_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_imagenes_habitaciones_habitaciones`
    FOREIGN KEY (`habitacion_id`)
    REFERENCES `habitaciones` (`id`)
    ON DELETE CASCADE
);
