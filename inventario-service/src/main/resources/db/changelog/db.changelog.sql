--liquibase formatted sql

--changeset author:1
CREATE TABLE movimiento_inventario (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto       BIGINT       NOT NULL,
    nombre_producto   VARCHAR(255) NOT NULL,
    tipo              VARCHAR(20)  NOT NULL,
    cantidad          INT          NOT NULL,
    stock_resultante  INT          NOT NULL,
    id_proveedor      BIGINT,
    motivo            VARCHAR(255),
    fecha             DATETIME     NOT NULL
);

--changeset author:2
INSERT INTO movimiento_inventario (id_producto, nombre_producto, tipo, cantidad, stock_resultante, id_proveedor, motivo, fecha) VALUES
(1, 'Torta de Chocolate',        'ENTRADA', 20, 20, 1,    'Reposición inicial de stock',                '2024-02-01 09:00:00'),
(1, 'Torta de Chocolate',        'SALIDA',   5, 15, NULL, 'Venta en local',                              '2024-02-05 12:30:00'),
(2, 'Kuchen de Manzana',         'ENTRADA', 15, 15, 2,    'Reposición inicial de stock',                 '2024-02-02 09:00:00'),
(2, 'Kuchen de Manzana',         'SALIDA',  10,  5, NULL, 'Venta por pedido web',                        '2024-02-10 16:45:00'),
(3, 'Cupcakes Vainilla (caja 6)','ENTRADA', 30, 30, 4,    'Reposición inicial de stock',                 '2024-02-03 09:00:00'),
(3, 'Cupcakes Vainilla (caja 6)','SALIDA',  25,  5, 4,    'Venta masiva, stock bajo umbral mínimo',      '2024-02-14 11:20:00'),
(4, 'Pan de Pascua',             'ENTRADA', 12, 12, 1,    'Reposición inicial de stock',                 '2024-02-04 09:00:00'),
(4, 'Pan de Pascua',             'AJUSTE',   2, 10, NULL, 'Ajuste por conteo físico de bodega',          '2024-02-20 10:00:00');
