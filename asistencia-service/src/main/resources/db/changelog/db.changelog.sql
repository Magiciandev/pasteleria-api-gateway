--liquibase formatted sql

--changeset liquibase:1 author:admin
CREATE TABLE asistencia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_empleado BIGINT NOT NULL,
    nombre_empleado VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME NOT NULL,
    hora_salida TIME NOT NULL,
    horas_trabajadas DECIMAL(4,2) NOT NULL,
    horas_extra DECIMAL(4,2) NOT NULL DEFAULT 0,
    costo_horas_extra DECIMAL(10,2) NOT NULL DEFAULT 0,
    presente BOOLEAN NOT NULL DEFAULT TRUE,
    observacion VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
