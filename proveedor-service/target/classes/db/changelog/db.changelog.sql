--liquibase formatted sql

--changeset author:1
CREATE TABLE proveedor (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    rut            VARCHAR(12)  NOT NULL UNIQUE,
    correo         VARCHAR(255) NOT NULL,
    telefono       VARCHAR(20),
    direccion      VARCHAR(500),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_registro DATE         NOT NULL
);

--changeset author:2
INSERT INTO proveedor (nombre, rut, correo, telefono, direccion, activo, fecha_registro) VALUES
('Distribuidora Harinera El Molino Ltda.',  '76123456-7', 'ventas@elmolino.cl',       '+56912345001', 'Av. Panamericana 1200, Santiago',          TRUE,  '2022-03-10'),
('Lácteos del Sur S.A.',                    '76234567-8', 'contacto@lacteosdelsur.cl', '+56912345002', 'Ruta 5 Sur Km 45, San Bernardo',           TRUE,  '2022-05-22'),
('Huevos Frescos Arauco SpA',               '76345678-9', 'pedidos@huevosarauco.cl',   '+56912345003', 'Camino El Guindo 890, Arauco',             TRUE,  '2022-07-15'),
('Chocolates Finos Belgique Ltda.',         '76456789-0', 'info@belgique.cl',          '+56912345004', 'Av. Vitacura 5432, Las Condes',            TRUE,  '2022-08-30'),
('Azucarera Río Cautín S.A.',               '76567890-1', 'ventas@azucarerarcautin.cl','+56912345005', 'Calle Comercio 340, Temuco',               TRUE,  '2022-10-05'),
('Frutales del Maipo SpA',                  '76678901-2', 'despachos@frutalesmaipo.cl','+56912345006', 'Parcela 12, Isla de Maipo',                FALSE, '2023-01-18'),
('Esencias y Colorantes Gourmet Ltda.',     '76789012-3', 'soporte@ecgourmet.cl',      '+56912345007', 'Av. Independencia 678, Santiago',          TRUE,  '2023-02-27'),
('Empaques y Packaging Confitería S.A.',    '76890123-4', 'ventas@empaquesconf.cl',    '+56912345008', 'Parque Industrial Quilicura, Nave 7',      TRUE,  '2023-04-11'),
('Levaduras y Leudantes Del Valle SpA',     '76901234-5', 'pedidos@delvalle.cl',       '+56912345009', 'Calle Principal 220, Curicó',              TRUE,  '2023-06-03'),
('Frutos Secos Premium Ltda.',              '77012345-6', 'info@frutossecos.cl',       '+56912345010', 'Los Conquistadores 1818, Providencia',     TRUE,  '2023-07-19'),
('Cremas y Mantequillas Naturales SpA',     '77123456-7', 'despachos@cremasymanteq.cl','+56912345011', 'Av. Lo Espejo 456, Pudahuel',              TRUE,  '2023-09-08'),
('Utensilios Pastelería Profesional Ltda.', '77234567-8', 'ventas@utensiliospro.cl',   '+56912345012', 'Merced 128 Local 4, Santiago Centro',      FALSE, '2024-01-14');