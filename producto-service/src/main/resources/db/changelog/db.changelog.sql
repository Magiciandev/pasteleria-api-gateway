--liquibase formatted sql

--changeset author:1
CREATE TABLE detalle_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(500),
    contiene_lactosa BOOLEAN DEFAULT FALSE,
    contiene_huevos BOOLEAN DEFAULT FALSE,
    contiene_frutos_secos BOOLEAN DEFAULT FALSE,
    contiene_gluten BOOLEAN DEFAULT FALSE,
    alergenos VARCHAR(255),
    instrucciones_almacenamiento VARCHAR(255),
    presentacion VARCHAR(100)
);

--changeset author:2
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    precio DOUBLE,
    stock INT,
    detalle_id BIGINT UNIQUE,
    FOREIGN KEY (detalle_id) REFERENCES detalle_producto(id)
);
