-- Script para inicializar las bases de datos
-- Ejecutar este script en MySQL antes de iniciar los microservicios

-- Eliminar bases de datos si existen
DROP DATABASE IF EXISTS bd_envio;
DROP DATABASE IF EXISTS bd_pedidos;

-- Crear bases de datos
CREATE DATABASE bd_envio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE bd_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Verificar que se crearon correctamente
SHOW DATABASES LIKE 'bd_%';
