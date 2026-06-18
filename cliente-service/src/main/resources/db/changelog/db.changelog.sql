--liquibase formatted sql

--changeset author:1
--comment: Create cliente table
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_cliente VARCHAR(255),
    nombre VARCHAR(255),
    correo VARCHAR(255),
    telefono VARCHAR(255),
    direccion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset author:2
--comment: Insert sample data for cliente
INSERT INTO cliente (rut_cliente, nombre, correo, telefono, direccion) VALUES
('12345678-9', 'Juan García', 'juan@example.com', '+56912345678', 'Calle Principal 123, Santiago'),
('23456789-0', 'María López', 'maria@example.com', '+56923456789', 'Avenida Secundaria 456, Valparaíso'),
('34567890-1', 'Carlos Rodríguez', 'carlos@example.com', '+56934567890', 'Pasaje Sur 789, Concepción'),
('45678901-2', 'Ana Martínez', 'ana@example.com', '+56945678901', 'Camino Norte 321, La Serena'),
('56789012-3', 'Roberto Díaz', 'roberto@example.com', '+56956789012', 'Boulevard Este 654, Puerto Varas'),
('67890123-4', 'Francisca Torres', 'francisca@example.com', '+56967890123', 'Callejón Oeste 987, Temuco'),
('78901234-5', 'Diego Hernández', 'diego@example.com', '+56978901234', 'Ruta Interamericana 258, Punta Arenas'),
('89012345-6', 'Catalina Reyes', 'catalina@example.com', '+56989012345', 'Paseo Central 147, Iquique'),
('90123456-7', 'Andrés Flores', 'andres@example.com', '+56990123456', 'Carrera Lateral 369, Osorno'),
('01234567-8', 'Sofía Peña', 'sofia@example.com', '+56901234567', 'Avenida Principal 741, Valdivia');
