--liquibase formatted sql

--changeset liquibase:1 author:admin
CREATE TABLE empleado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_empleado VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    correo VARCHAR(255),
    telefono VARCHAR(255),
    direccion VARCHAR(255),
    cargo VARCHAR(255) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    sueldo_base DECIMAL(10,2) NOT NULL,
    valor_hora_extra DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset liquibase:2 author:admin
INSERT INTO empleado (rut_empleado, nombre, correo, telefono, direccion, cargo, fecha_ingreso, sueldo_base, valor_hora_extra, activo) VALUES
('12345678-1', 'Juan Pérez González', 'juan.perez@pasteleria.cl', '+56912345678', 'Av. Principal 123', 'Pastelero', '2022-03-15', 1500000.00, 15000.00, TRUE),
('23456789-2', 'María García López', 'maria.garcia@pasteleria.cl', '+56923456789', 'Calle Central 456', 'Pastelero', '2021-06-20', 1550000.00, 15500.00, TRUE),
('34567890-3', 'Carlos Rodríguez Martín', 'carlos.rodriguez@pasteleria.cl', '+56934567890', 'Pasaje Lateral 789', 'Repartidor', '2023-01-10', 1200000.00, 12000.00, TRUE),
('45678901-4', 'Rosa Martínez Flores', 'rosa.martinez@pasteleria.cl', '+56945678901', 'Boulevard Norte 234', 'Cajero', '2022-09-05', 1100000.00, 11000.00, TRUE),
('56789012-5', 'Roberto López Silva', 'roberto.lopez@pasteleria.cl', '+56956789012', 'Calle Sur 567', 'Pastelero', '2020-11-12', 1700000.00, 17000.00, TRUE),
('67890123-6', 'Ana Sánchez Ramírez', 'ana.sanchez@pasteleria.cl', '+56967890123', 'Av. Oriente 890', 'Repartidor', '2023-02-28', 1150000.00, 11500.00, TRUE),
('78901234-7', 'Diego Torres Jiménez', 'diego.torres@pasteleria.cl', '+56978901234', 'Camino Este 345', 'Cajero', '2022-05-18', 1080000.00, 10800.00, TRUE),
('89012345-8', 'Patricia Fernández Ruiz', 'patricia.fernandez@pasteleria.cl', '+56989012345', 'Carrera Oeste 123', 'Pastelero Senior', '2019-08-22', 1900000.00, 19000.00, TRUE),
('90123456-9', 'Fernando Ruiz Vargas', 'fernando.ruiz@pasteleria.cl', '+56990123456', 'Avenida Sur 678', 'Repartidor', '2023-04-03', 1250000.00, 12500.00, TRUE),
('01234567-K', 'Francisca Díaz Morales', 'francisca.diaz@pasteleria.cl', '+56901234567', 'Plaza Mayor 789', 'Cajero', '2022-07-11', 1120000.00, 11200.00, TRUE);
