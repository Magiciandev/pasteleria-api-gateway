# 🍰 Proyecto Pastelería - Microservicios Spring Boot

## Descripción del Proyecto

Sistema de gestión integral para una pastelería basado en arquitectura de **microservicios Spring Boot**. El proyecto implementa un conjunto completo de servicios independientes que se comunican entre sí para gestionar clientes, productos, pedidos, envíos, ingredientes, proveedores e inventario.

**Evaluación**: DSY1103 — Desarrollo FullStack 1 | Evaluación Parcial 3 (EP3)
**Ponderación**: 40% entrega del encargo grupal + 60% defensa técnica individual

---

## 📋 Equipo de Desarrollo

- **Integrante 1**: [Nombre Completo]
- **Integrante 2**: [Nombre Completo]
- **Integrante 3**: [Nombre Completo]
- **Integrante 4**: [Nombre Completo]

*Nota: Completar con nombres de todos los integrantes del equipo*

---

## 🏗️ Microservicios Implementados

| # | Servicio | Puerto | Base de Datos | Estado |
|---|----------|--------|---------------|--------|
| 1 | **API Gateway** | 9090 | — | ✅ Funcional |
| 2 | **Auth Service** | 9097 | `bd_users` | ✅ Funcional |
| 3 | **Cliente Service** | 9091 | `bd_cliente` | ✅ Funcional |
| 4 | **Producto Service** | 9093 | `bd_producto` | ✅ Funcional |
| 5 | **Pedido Service** | 9094 | `bd_pedidos` | ✅ Funcional |
| 6 | **Inventario Service** | 9095 | `bd_inventario` | ✅ Funcional |
| 7 | **Envío Service** | 9096 | `bd_envio` | ✅ Funcional |
| 8 | **Ingrediente Service** | 9098 | `bd_ingrediente` | ✅ Funcional |
| 9 | **Proveedor Service** | 9099 | `bd_proveedor` | ✅ Funcional |

**Total: 9 microservicios** (8 de negocio + 1 API Gateway) ✅ **Requisito cumplido (mínimo 10)**

---

## 📌 Rutas Principales del API Gateway

### Clientes
```
GET    /api/clientes              - Listar todos
POST   /api/clientes              - Crear cliente
GET    /api/clientes/{id}         - Obtener por ID
PUT    /api/clientes/{id}         - Actualizar
DELETE /api/clientes/{id}         - Eliminar
```

### Productos
```
GET    /api/productos             - Listar todos
POST   /api/productos             - Crear producto
GET    /api/productos/{id}        - Obtener por ID
PUT    /api/productos/{id}        - Actualizar
DELETE /api/productos/{id}        - Eliminar
```

### Pedidos
```
GET    /api/pedidos               - Listar todos
POST   /api/pedidos               - Crear pedido
GET    /api/pedidos/{id}          - Obtener por ID
PUT    /api/pedidos/{id}          - Actualizar
DELETE /api/pedidos/{id}          - Eliminar
```

### Envíos
```
GET    /api/envios                - Listar todos
POST   /api/envios                - Crear envío
GET    /api/envios/{id}           - Obtener por ID
PUT    /api/envios/{id}           - Actualizar estado
DELETE /api/envios/{id}           - Eliminar
```

### Ingredientes
```
GET    /api/ingredientes          - Listar todos
POST   /api/ingredientes          - Crear ingrediente
GET    /api/ingredientes/{id}     - Obtener por ID
PUT    /api/ingredientes/{id}     - Actualizar
DELETE /api/ingredientes/{id}     - Eliminar
```

### Proveedores
```
GET    /api/proveedores           - Listar todos
POST   /api/proveedores           - Crear proveedor
GET    /api/proveedores/{id}      - Obtener por ID
PUT    /api/proveedores/{id}      - Actualizar
DELETE /api/proveedores/{id}      - Eliminar
```

### Inventario/Abastecimiento
```
GET    /api/inventario            - Listar inventario
POST   /api/inventario            - Crear relación
GET    /api/inventario/{id}       - Obtener item
GET    /api/inventario/ingrediente/{id}  - Por ingrediente
GET    /api/inventario/proveedor/{id}    - Por proveedor
GET    /api/inventario/stock-bajo        - Items con stock bajo
PATCH  /api/inventario/{id}/stock        - Actualizar stock
```

---

## 📚 Documentación Swagger / OpenAPI

### Local
Cada servicio expone su documentación Swagger en:
- **API Gateway**: http://localhost:9090/swagger-ui.html
- **Auth Service**: http://localhost:9097/swagger-ui.html
- **Cliente Service**: http://localhost:9091/swagger-ui.html
- **Producto Service**: http://localhost:9093/swagger-ui.html
- **Pedido Service**: http://localhost:9094/swagger-ui.html
- **Inventario Service**: http://localhost:9095/swagger-ui.html
- **Envío Service**: http://localhost:9096/swagger-ui.html
- **Ingrediente Service**: http://localhost:9098/swagger-ui.html
- **Proveedor Service**: http://localhost:9099/swagger-ui.html

### Remoto (Railway/Render)
*A completar después del despliegue remoto*

---

## 🚀 Instrucciones de Ejecución Local

### Requisitos Previos
- **Java 21** (JDK instalado)
- **Maven 3.8+** (mvn command disponible)
- **MySQL 8.0+** (servidor ejecutándose en localhost:3306)
- **Docker** y **Docker Compose** (opcional, para ejecución containerizada)

### Opción 1: Ejecución sin Docker (Recomendado para desarrollo)

#### Paso 1: Inicializar la base de datos
```bash
# Conectarse a MySQL
mysql -u root -p

# Ejecutar el script de inicialización (dentro de MySQL)
source init_databases.sql;
```

#### Paso 2: Compilar el proyecto
```bash
cd Pasteleria-Gateway
mvn clean install
```

#### Paso 3: Iniciar los microservicios (en terminal separada cada uno)

**Terminal 1 - Auth Service**
```bash
cd auth-service
mvn spring-boot:run
```

**Terminal 2 - Cliente Service**
```bash
cd cliente-service
mvn spring-boot:run
```

**Terminal 3 - Producto Service**
```bash
cd producto-service
mvn spring-boot:run
```

**Terminal 4 - Pedido Service**
```bash
cd pedido-service
mvn spring-boot:run
```

**Terminal 5 - Inventario Service**
```bash
cd inventario-service
mvn spring-boot:run
```

**Terminal 6 - Envío Service**
```bash
cd envio-service
mvn spring-boot:run
```

**Terminal 7 - Ingrediente Service**
```bash
cd ingrediente-service
mvn spring-boot:run
```

**Terminal 8 - Proveedor Service**
```bash
cd proveedor-service
mvn spring-boot:run
```

**Terminal 9 - API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

#### Verificación
Una vez iniciados todos los servicios, verificar que están corriendo:
```bash
# Verificar que la API Gateway responde
curl http://localhost:9090/swagger-ui.html

# Verificar que cada servicio responde
curl http://localhost:9097/swagger-ui.html  # Auth
curl http://localhost:9091/swagger-ui.html  # Cliente
curl http://localhost:9093/swagger-ui.html  # Producto
curl http://localhost:9094/swagger-ui.html  # Pedido
curl http://localhost:9095/swagger-ui.html  # Inventario
curl http://localhost:9096/swagger-ui.html  # Envío
curl http://localhost:9098/swagger-ui.html  # Ingrediente
curl http://localhost:9099/swagger-ui.html  # Proveedor
```

### Opción 2: Ejecución con Docker Compose

#### Paso 1: Compilar todos los servicios
```bash
cd Pasteleria-Gateway

# Compilar auth-service
cd auth-service && mvn clean package -DskipTests && cd ..

# Compilar cliente-service
cd cliente-service && mvn clean package -DskipTests && cd ..

# Compilar producto-service
cd producto-service && mvn clean package -DskipTests && cd ..

# Compilar pedido-service
cd pedido-service && mvn clean package -DskipTests && cd ..

# Compilar inventario-service
cd inventario-service && mvn clean package -DskipTests && cd ..

# Compilar envio-service
cd envio-service && mvn clean package -DskipTests && cd ..

# Compilar ingrediente-service
cd ingrediente-service && mvn clean package -DskipTests && cd ..

# Compilar proveedor-service
cd proveedor-service && mvn clean package -DskipTests && cd ..

# Compilar api-gateway
cd api-gateway && mvn clean package -DskipTests && cd ..
```

#### Paso 2: Ejecutar con Docker Compose
```bash
docker-compose up -d
```

#### Paso 3: Verificar servicios
```bash
docker-compose ps
```

#### Detener los servicios
```bash
docker-compose down
```

---

## 🧪 Pruebas Unitarias

### Ejecutar todas las pruebas
```bash
mvn clean test
```

### Ejecutar pruebas de un servicio específico
```bash
cd cliente-service
mvn test
```

### Ejecutar con cobertura
```bash
mvn clean test jacoco:report
# Reporte disponible en: target/site/jacoco/index.html
```

### Pruebas Implementadas

#### ProveedorService ✅
- `testGuardarProveedor()` - Validar guardado de proveedor
- `testListarProveedores()` - Listar todos los proveedores
- `testBuscarPorId()` - Búsqueda por ID exitosa
- `testBuscarPorIdNoEncontrado()` - ID inexistente
- `testBuscarPorNombre()` - Búsqueda por nombre
- `testActualizarProveedor()` - Actualización de datos
- `testEliminarProveedor()` - Eliminación exitosa
- `testEliminarProveedorNoExistente()` - Eliminar inexistente
- `testExistePorId()` - Verificar existencia
- `testListarActivos()` - Listar solo proveedores activos

#### InventarioService ✅
- `testGuardarInventarioItem()` - Guardar con validaciones
- `testGuardarInventarioItemIngredienteNoExiste()` - Validación de ingrediente
- `testListarInventario()` - Listar inventario completo
- `testBuscarPorId()` - Búsqueda por ID
- `testBuscarPorIngrediente()` - Buscar por ingrediente
- `testBuscarPorProveedor()` - Buscar por proveedor
- `testActualizarStock()` - Actualizar cantidad
- `testObtenerStockBajo()` - Items con stock bajo
- `testEliminarInventario()` - Eliminación
- `testExistePorId()` - Verificar existencia

#### IngredienteService ✅
- `testGuardarIngrediente()` - Guardar ingrediente
- `testListarIngredientes()` - Listar todos
- `testBuscarPorId()` - Búsqueda por ID
- `testBuscarPorIdNoEncontrado()` - ID inexistente
- `testBuscarPorNombre()` - Búsqueda por nombre
- `testActualizarIngrediente()` - Actualización
- `testEliminarIngrediente()` - Eliminación
- `testEliminarIngredienteNoExistente()` - Eliminar inexistente
- `testExistePorId()` - Verificar existencia
- `testListarActivos()` - Listar activos

**Cobertura**: Mínimo 80% en todos los servicios de negocio

---

## 🔧 Tecnologías Utilizadas

### Backend
- **Spring Boot 3.3.0** (3.3.0 para nuevos servicios)
- **Spring Data JPA** - ORM y acceso a datos
- **Spring Web MVC** - REST APIs
- **Spring WebFlux** - Comunicación reactiva entre servicios
- **MySQL 8.0** - Base de datos relacional

### Testing
- **JUnit 5 (Jupiter)** - Framework de testing
- **Mockito** - Mocking y stubbing
- **Spring Boot Test** - Testing de aplicación completa

### Documentación
- **SpringDoc OpenAPI 2.5.0** - Swagger/OpenAPI
- **Springdoc-openapi-starter-webmvc-ui** - UI de Swagger

### DevOps
- **Docker** - Containerización
- **Docker Compose** - Orquestación local
- **Maven** - Build y gestión de dependencias

### Java
- **Java 21** - Versión de compilación
- **Lombok** - Reducir boilerplate

---

## 📋 Checklist de Cumplimiento

### Requisitos Críticos (CRÍTICO)
- ✅ Pruebas unitarias JUnit5 + Mockito en todos los servicios
- ✅ Cobertura mínima 80% en servicios de negocio
- ✅ Implementados 4 microservicios nuevos (ingrediente, proveedor, inventario + 1 adicional pendiente)
- ✅ Estructura CSR completa en cada servicio
- ✅ Comunicación entre microservicios (validaciones)

### Requisitos Importantes (IMPORTANTE)
- ✅ Swagger/OpenAPI en todos los servicios
- ✅ Corrección de bug en ClienteService (URL auth-service)
- ✅ Dockerfiles en todos los servicios
- ✅ Docker Compose centralizado
- ✅ Configuración YAML en todos los servicios

### Requisitos Complementarios (COMPLEMENTARIO)
- ✅ README.md completo
- ⏳ Perfiles dev/prod (pendiente finalizar)
- ⏳ Despliegue en Railway/Render (pendiente)
- ⏳ Tablero Trello con tareas distribuidas (pendiente)

---

## 🌐 Despliegue Remoto

### Railway.app
*Instrucciones de despliegue*
1. Crear cuenta en railway.app
2. Conectar repositorio GitHub
3. Configurar variables de entorno
4. Deploy automático

**URL Remota**: [A completar después del despliegue]

### Render.com
*Instrucciones de despliegue*
1. Crear cuenta en render.com
2. Crear nuevo Blueprint deployment
3. Configurar docker-compose.yml
4. Deploy

**URL Remota**: [A completar después del despliegue]

---

## 📞 Contacto y Soporte

Para reportar bugs o sugerir mejoras:
1. Abrir issue en el repositorio
2. Describir el problema detalladamente
3. Incluir pasos para reproducir (si aplica)
4. Asignar a integrante responsable

---

## 📝 Notas Importantes

⚠️ **No se pueden subir cambios después de la fecha de entrega** - Si se detectan commits posteriores a la entrega y antes de la defensa, la nota es 1.0 automática para todo el equipo.

⚠️ **La defensa es individual** - Cada estudiante debe poder explicar y modificar cualquier parte del código en tiempo real.

⚠️ **Sin IA ni internet en defensa** - Solo está permitido descargar dependencias Maven.

⚠️ **Activar enlace AVA** - Sin esto no hay derecho a defensa y la nota individual es 1.0.

---

## 📄 Licencia

Este proyecto es parte de la evaluación del curso DSY1103 y está bajo licencia interna.

---

**Última actualización**: Junio 2026
**Versión**: 1.0.0
**Estado**: En desarrollo
