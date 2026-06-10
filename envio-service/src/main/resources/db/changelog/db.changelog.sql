--liquibase formatted sql

--changeset author:1
--comment: Create envio table with all required fields
CREATE TABLE IF NOT EXISTS envio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_cliente BIGINT NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    direccion_entrega VARCHAR(500) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'EN_CAMINO',
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega_estimada DATETIME,
    fecha_entrega_real DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset author:2
--comment: Insert sample shipping data for testing
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(1, 1, 'Juan García López', 'Avenida Libertador 456, Depto 5A, Santiago', 'EN_CAMINO', '2026-05-15 10:30:00', '2026-05-17 18:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:3
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(2, 2, 'María López Rodríguez', 'Pasaje Los Andes 789, Casa 12, Valparaíso', 'EN_CAMINO', '2026-05-14 14:45:00', '2026-05-16 19:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:4
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, fecha_entrega_real, created_at, updated_at) VALUES
(3, 3, 'Carlos Rodríguez Martínez', 'Camino del Mar 321, Depto 8B, Viña del Mar', 'ENTREGADO', '2026-05-13 08:15:00', '2026-05-15 20:00:00', '2026-05-15 19:45:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:5
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(4, 4, 'Ana Martínez Pérez', 'Boulevard Central 654, Casa 23, La Serena', 'EN_PUNTO_ENTREGA', '2026-05-15 09:00:00', '2026-05-18 17:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:6
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, fecha_entrega_real, created_at, updated_at) VALUES
(5, 1, 'Roberto Díaz Fernández', 'Ruta Interamericana 987, Depto 3C, Puerto Varas', 'ENTREGADO', '2026-05-12 11:20:00', '2026-05-14 21:00:00', '2026-05-14 20:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:7
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(6, 2, 'Francisca Torres Acosta', 'Paseo del Lago 147, Casa 45, Temuco', 'EN_CAMINO', '2026-05-14 15:30:00', '2026-05-16 22:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:8
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(7, 3, 'Diego Hernández Soto', 'Calle Sur 258, Depto 7D, Punta Arenas', 'EN_CAMINO', '2026-05-15 13:00:00', '2026-05-22 16:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:9
--comment: Insert more sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, fecha_entrega_real, created_at, updated_at) VALUES
(8, 4, 'Catalina Reyes Gómez', 'Avenida Este 369, Casa 67, Iquique', 'ENTREGADO', '2026-05-11 07:45:00', '2026-05-13 18:30:00', '2026-05-13 18:15:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--changeset author:10
--comment: Insert final sample shipping data
INSERT INTO envio (id_pedido, id_cliente, nombre_cliente, direccion_entrega, estado, fecha_envio, fecha_entrega_estimada, created_at, updated_at) VALUES
(9, 1, 'Andrés Flores Rojas', 'Camino Costero 741, Depto 2E, Osorno', 'EN_PUNTO_ENTREGA', '2026-05-15 12:15:00', '2026-05-17 19:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 2, 'Sofía Peña Contreras', 'Calle del Comercio 852, Casa 89, Valdivia', 'ENTREGADO', '2026-05-13 16:00:00', '2026-05-15 20:30:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

