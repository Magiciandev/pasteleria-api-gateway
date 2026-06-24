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
