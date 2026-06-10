--liquibase formatted sql

--changeset author:1
--comment: Create pedido table with detalle_pedido as JSON
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detalle_pedido TEXT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_id_cliente (id_cliente),
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
);

--changeset author:2
--comment: Insert sample data for pedido
INSERT INTO pedido (id_cliente, nombre_cliente, fecha, detalle_pedido, total, estado) VALUES
(1, 'Juan García', NOW(), '[{\"idProducto\":1,\"nombreProducto\":\"Torta Tres Leches\",\"cantidad\":2,\"precioUnitario\":15000.00,\"precioTotal\":30000.00},{\"idProducto\":2,\"nombreProducto\":\"Torta Chocolate\",\"cantidad\":1,\"precioUnitario\":18000.00,\"precioTotal\":18000.00}]', 48000.00, 'PENDIENTE'),
(2, 'María López', NOW(), '[{\"idProducto\":3,\"nombreProducto\":\"Cheesecake\",\"cantidad\":3,\"precioUnitario\":12000.00,\"precioTotal\":36000.00},{\"idProducto\":4,\"nombreProducto\":\"Mousse de Fresa\",\"cantidad\":1,\"precioUnitario\":8000.00,\"precioTotal\":8000.00}]', 44000.00, 'CONFIRMADO'),
(3, 'Carlos Rodríguez', NOW(), '[{\"idProducto\":5,\"nombreProducto\":\"Brownies (6 un)\",\"cantidad\":1,\"precioUnitario\":7000.00,\"precioTotal\":7000.00},{\"idProducto\":6,\"nombreProducto\":\"Macarrones de Colores\",\"cantidad\":2,\"precioUnitario\":5000.00,\"precioTotal\":10000.00}]', 17000.00, 'ENTREGADO'),
(4, 'Ana Martínez', NOW(), '[{\"idProducto\":7,\"nombreProducto\":\"Cupcakes (12 un)\",\"cantidad\":1,\"precioUnitario\":16000.00,\"precioTotal\":16000.00}]', 16000.00, 'CANCELADO'),
(1, 'Juan García', NOW(), '[{\"idProducto\":8,\"nombreProducto\":\"Donas Glaseadas (6 un)\",\"cantidad\":5,\"precioUnitario\":3000.00,\"precioTotal\":15000.00},{\"idProducto\":9,\"nombreProducto\":\"Pastel de Zanahoria\",\"cantidad\":1,\"precioUnitario\":14000.00,\"precioTotal\":14000.00}]', 29000.00, 'PENDIENTE'),
(2, 'María López', NOW(), '[{\"idProducto\":10,\"nombreProducto\":\"Tarta de Frutos Rojos\",\"cantidad\":2,\"precioUnitario\":20000.00,\"precioTotal\":40000.00},{\"idProducto\":1,\"nombreProducto\":\"Torta Tres Leches\",\"cantidad\":3,\"precioUnitario\":15000.00,\"precioTotal\":45000.00}]', 85000.00, 'CONFIRMADO'),
(3, 'Carlos Rodríguez', NOW(), '[{\"idProducto\":2,\"nombreProducto\":\"Torta Chocolate\",\"cantidad\":1,\"precioUnitario\":18000.00,\"precioTotal\":18000.00},{\"idProducto\":3,\"nombreProducto\":\"Cheesecake\",\"cantidad\":2,\"precioUnitario\":12000.00,\"precioTotal\":24000.00}]', 42000.00, 'EN_PREPARACION'),
(4, 'Ana Martínez', NOW(), '[{\"idProducto\":4,\"nombreProducto\":\"Mousse de Fresa\",\"cantidad\":4,\"precioUnitario\":8000.00,\"precioTotal\":32000.00}]', 32000.00, 'ENTREGADO'),
(1, 'Juan García', NOW(), '[{\"idProducto\":5,\"nombreProducto\":\"Brownies (6 un)\",\"cantidad\":1,\"precioUnitario\":7000.00,\"precioTotal\":7000.00}]', 7000.00, 'PENDIENTE'),
(2, 'María López', NOW(), '[{\"idProducto\":6,\"nombreProducto\":\"Macarrones de Colores\",\"cantidad\":2,\"precioUnitario\":5000.00,\"precioTotal\":10000.00}]', 10000.00, 'CONFIRMADO');
