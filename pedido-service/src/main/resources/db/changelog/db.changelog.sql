--liquibase formatted sql

--changeset author:1
--comment: Create pedido table
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_id_cliente (id_cliente),
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
);

--changeset author:2
--comment: Create detalle_pedido table with foreign key to pedido
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    precio_total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
    INDEX idx_id_pedido (id_pedido),
    INDEX idx_id_producto (id_producto)
);

--changeset author:3
--comment: Insert sample data for pedido
INSERT INTO pedido (id_cliente, nombre_cliente, fecha, total, estado) VALUES
(1, 'Juan García', NOW(), 48000.00, 'PENDIENTE'),
(2, 'María López', NOW(), 44000.00, 'CONFIRMADO'),
(3, 'Carlos Rodríguez', NOW(), 17000.00, 'ENTREGADO'),
(4, 'Ana Martínez', NOW(), 16000.00, 'CANCELADO'),
(1, 'Juan García', NOW(), 29000.00, 'PENDIENTE'),
(2, 'María López', NOW(), 85000.00, 'CONFIRMADO'),
(3, 'Carlos Rodríguez', NOW(), 42000.00, 'EN_PREPARACION'),
(4, 'Ana Martínez', NOW(), 32000.00, 'ENTREGADO'),
(1, 'Juan García', NOW(), 7000.00, 'PENDIENTE'),
(2, 'María López', NOW(), 10000.00, 'CONFIRMADO');

--changeset author:4
--comment: Insert sample data for detalle_pedido
INSERT INTO detalle_pedido (id_pedido, id_producto, nombre_producto, cantidad, precio_unitario, precio_total) VALUES
(1, 1, 'Torta Tres Leches', 2, 15000.00, 30000.00),
(1, 2, 'Torta Chocolate', 1, 18000.00, 18000.00),
(2, 3, 'Cheesecake', 3, 12000.00, 36000.00),
(2, 4, 'Mousse de Fresa', 1, 8000.00, 8000.00),
(3, 5, 'Brownies (6 un)', 1, 7000.00, 7000.00),
(3, 6, 'Macarrones de Colores', 2, 5000.00, 10000.00),
(4, 7, 'Cupcakes (12 un)', 1, 16000.00, 16000.00),
(5, 8, 'Donas Glaseadas (6 un)', 5, 3000.00, 15000.00),
(5, 9, 'Pastel de Zanahoria', 1, 14000.00, 14000.00),
(6, 10, 'Tarta de Frutos Rojos', 2, 20000.00, 40000.00),
(6, 1, 'Torta Tres Leches', 3, 15000.00, 45000.00),
(7, 2, 'Torta Chocolate', 1, 18000.00, 18000.00),
(7, 3, 'Cheesecake', 2, 12000.00, 24000.00),
(8, 4, 'Mousse de Fresa', 4, 8000.00, 32000.00),
(9, 5, 'Brownies (6 un)', 1, 7000.00, 7000.00),
(10, 6, 'Macarrones de Colores', 2, 5000.00, 10000.00);
