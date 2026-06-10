--liquibase formatted sql

--changeset author:1
CREATE TABLE detalle_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(500),
    contiene_lactosa BOOLEAN DEFAULT FALSE,
    contiene_huevos BOOLEAN DEFAULT FALSE,
    contiene_frutos_secos BOOLEAN DEFAULT FALSE,
    contiene_gluten BOOLEAN DEFAULT FALSE,
    alergenos VARCHAR(255),
    instrucciones_almacenamiento VARCHAR(255),
    presentacion VARCHAR(100)
);

--changeset author:2
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    precio DOUBLE,
    stock INT,
    detalle_id BIGINT UNIQUE,
    FOREIGN KEY (detalle_id) REFERENCES detalle_producto(id)
);

--changeset author:3
INSERT INTO detalle_producto (descripcion, contiene_lactosa, contiene_huevos, contiene_frutos_secos, contiene_gluten, alergenos, instrucciones_almacenamiento, presentacion) VALUES
('Torta clásica elaborada con tres tipos de leche: leche condensada, leche evaporada y crema de leche. Suave y deliciosa.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten', 'Refrigerar entre 2-8°C', 'Porciones'),
('Torta de chocolate puro con interior humedo y cobertura de ganache. Ideal para amantes del chocolate.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten, Soja', 'Refrigerar entre 2-8°C', 'Porciones'),
('Fresco y delicioso cheesecake con base de galleta y cobertura de frambuesa fresca.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten', 'Refrigerar entre 2-8°C', 'Porciones'),
('Pie tradicional de limón con merengue, acidez balanceada y dulzor perfecto.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten', 'Refrigerar entre 2-8°C', 'Porciones'),
('Kuchen alemán elaborado con nueces frescas, suave y aromático.', TRUE, TRUE, TRUE, TRUE, 'Lácteos, Huevos, Gluten, Frutos Secos (Nueces)', 'Mantener en lugar fresco y seco', 'Porciones'),
('Pequeños y esponjosos cupcakes de vainilla pura con cobertura de buttercream.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten', 'Consumir dentro de 3 días', 'Unidades'),
('Cupcakes elegantes con sabor de terciopelo rojo y cobertura de queso crema.', TRUE, TRUE, FALSE, TRUE, 'Lácteos, Huevos, Gluten', 'Consumir dentro de 3 días', 'Unidades'),
('Galletas crujientes cargadas de chips de chocolate chocolate puro de calidad.', TRUE, FALSE, FALSE, TRUE, 'Lácteos, Gluten, Soja', 'Mantener en frasco hermético', 'Bolsa'),
('Pan casero amasado a mano, esponjoso y con miga abierta. Tradicional chileno.', FALSE, FALSE, FALSE, TRUE, 'Gluten', 'Consumir el mismo día', 'Docena'),
('Empanada rellena de pino jugoso con carne molida, cebolla y especias.', FALSE, TRUE, FALSE, TRUE, 'Gluten, Soja', 'Servir caliente', 'Unidades');

--changeset author:4
INSERT INTO producto (nombre, precio, stock, detalle_id) VALUES
('Torta Tres Leches', 15000, 10, 1),
('Torta de Chocolate', 18000, 8, 2),
('Cheesecake de Frambuesa', 12000, 15, 3),
('Pie de Limón', 10000, 20, 4),
('Kuchen de Nuez', 14000, 12, 5),
('Cupcake de Vainilla', 2000, 50, 6),
('Cupcake Red Velvet', 2500, 40, 7),
('Galletas con Chips de Chocolate', 500, 100, 8),
('Pan Amasado (Docena)', 3000, 30, 9),
('Empanada de Pino', 2000, 60, 10);