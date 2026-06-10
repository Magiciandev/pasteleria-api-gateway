-- Script para verificar las tablas creadas por Liquibase

-- Verificar base de datos bd_envio
USE bd_envio;
SHOW TABLES;
SELECT COUNT(*) as total_envios FROM envio;
SELECT * FROM envio LIMIT 1;

-- Verificar base de datos bd_pedidos
USE bd_pedidos;
SHOW TABLES;
SELECT COUNT(*) as total_pedidos FROM pedido;
SELECT COUNT(*) as total_detalles FROM detalle_pedido;
SELECT * FROM pedido LIMIT 1;
SELECT * FROM detalle_pedido LIMIT 1;

-- Información de Liquibase
SELECT * FROM databasechangelog;
SELECT * FROM databasechangeloglock;
