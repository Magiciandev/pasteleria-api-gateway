--liquibase formatted sql

--changeset author:1
CREATE TABLE compra (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor      BIGINT       NOT NULL,
    nombre_proveedor  VARCHAR(255) NOT NULL,
    numero_factura    VARCHAR(50)  NOT NULL,
    fecha             DATETIME     NOT NULL,
    monto_total       DOUBLE       NOT NULL,
    estado            VARCHAR(20)  NOT NULL,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME
);

--changeset author:2
CREATE TABLE detalle_compra (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra         BIGINT       NOT NULL,
    id_producto       BIGINT       NOT NULL,
    nombre_producto   VARCHAR(255) NOT NULL,
    cantidad          INT          NOT NULL,
    precio_unitario   DOUBLE       NOT NULL,
    precio_total      DOUBLE       NOT NULL,
    CONSTRAINT fk_detalle_compra_compra FOREIGN KEY (id_compra) REFERENCES compra(id)
);

--changeset author:3
INSERT INTO compra (id_proveedor, nombre_proveedor, numero_factura, fecha, monto_total, estado, created_at, updated_at) VALUES
(1, 'Distribuidora El Trigal',  'F-1001', '2024-02-01 09:00:00', 45000.0, 'RECIBIDA',  '2024-02-01 09:00:00', '2024-02-01 09:00:00'),
(2, 'Lácteos del Sur',          'F-1002', '2024-02-10 10:30:00', 30000.0, 'PENDIENTE', '2024-02-10 10:30:00', '2024-02-10 10:30:00'),
(4, 'Insumos Reposteros Ltda.', 'F-1003', '2024-02-15 11:15:00', 18000.0, 'ANULADA',   '2024-02-15 11:15:00', '2024-02-15 11:15:00');

--changeset author:4
INSERT INTO detalle_compra (id_compra, id_producto, nombre_producto, cantidad, precio_unitario, precio_total) VALUES
(1, 1, 'Torta de Chocolate', 15, 2000.0, 30000.0),
(1, 4, 'Pan de Pascua',      15, 1000.0, 15000.0),
(2, 2, 'Kuchen de Manzana',  10, 3000.0, 30000.0),
(3, 3, 'Cupcakes Vainilla (caja 6)', 6, 3000.0, 18000.0);
