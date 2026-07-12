# 🎂 Pastelería API — Arquitectura de Microservicios

Proyecto semestral de **Desarrollo FullStack 1 (DSY1103)** — Evaluación Final Transversal.
Sistema de gestión integral para una pastelería, construido como un ecosistema de microservicios independientes con Spring Boot, comunicación REST síncrona, persistencia real con JPA/Liquibase y un API Gateway como punto de entrada único.

**Equipo:** Victor Navarrete · Max Herrera ([Magiciandev](https://github.com/Magiciandev))
**Repositorio:** [pasteleria-api-gateway — rama EXAMEN](https://github.com/Magiciandev/pasteleria-api-gateway/tree/EXAMEN)

---

## 📦 Arquitectura general

```
                         ┌─────────────────────┐
                         │   api-gateway :9090   │   ← único punto de entrada
                         └──────────┬───────────┘
        ┌──────────┬──────────┬────┼────┬──────────┬──────────┬──────────┐
        ▼          ▼          ▼    ▼    ▼          ▼          ▼          ▼
    auth:9097  cliente:9091 empleado producto pedido:9094 asistencia envio:9096 proveedor:9098
                                :9092   :9093                  :9095
                                                                          │
                                                          ┌───────────────┴───────────────┐
                                                          ▼                                ▼
                                                  inventario-service:9099       compra-service:9100
                                                          ▲                                │
                                                          └────────────────────────────────┘
                                                     (compra-service carga stock en inventario
                                                      tras validar el proveedor)
```

Todas las peticiones externas pasan por el **API Gateway (puerto 9090)**. Los puertos internos de cada microservicio no deben consumirse directamente salvo para depuración local.

---

## 🧩 Catálogo de microservicios

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `api-gateway` | **9090** | — | Enrutamiento único, seguridad JWT centralizada (Spring Cloud Gateway). |
| `auth-service` | **9097** | `bd_users` | Registro, login y emisión de tokens JWT. |
| `cliente-service` | **9091** | `bd_cliente` | Maestro de clientes de la pastelería. |
| `empleado-service` | **9092** | `bd_empleado` | Maestro del personal interno, cargos y contratos. |
| `producto-service` | **9093** | `bd_producto` | Catálogo de productos (tortas, kuchenes, pan de pascua, etc.), precios y stock base. |
| `pedido-service` | **9094** | `bd_pedidos` | Orquesta pedidos: valida cliente vía `cliente-service`, calcula totales desde `DetallePedido`. |
| `asistencia-service` | **9095** | `bd_asistencia` | Registro de asistencia del personal, ligado a `empleado-service`. |
| `envio-service` | **9096** | `bd_envio` | Despachos, ligado a `pedido-service` y `cliente-service`. |
| `proveedor-service` | **9098** | `bd_proveedor` | Directorio maestro de proveedores. |
| `inventario-service` | **9099** | `bd_inventario` | Movimientos de stock (ENTRADA/SALIDA/RESERVA/AJUSTE); valida reposición contra `proveedor-service`. |
| `compra-service` | **9100** | `bd_compra` | Órdenes de compra a proveedores; al recibirse, carga stock automáticamente en `inventario-service`. |

**Cadena de comunicación entre microservicios (relevante para la defensa):**

```
pedido-service ──▶ cliente-service        (valida cliente al crear pedido)
envio-service ──▶ pedido-service, cliente-service   (arma datos del despacho)
inventario-service ──▶ proveedor-service  (valida proveedor activo al reponer stock bajo)
compra-service ──▶ proveedor-service      (valida proveedor activo al crear la orden de compra)
compra-service ──▶ inventario-service     (carga stock ENTRADA al recibir la compra)
```

Todas usan `WebClient` síncrono (patrón `.retrieve().bodyToMono(...).block()`), con timeout de 5 segundos configurado y manejo de errores remotos vía `ExternalServiceException` / `ResourceNotFoundException`.

---

## 🔌 Endpoints por microservicio

> Todas las rutas se acceden a través del Gateway: `http://localhost:9090<ruta>`

### `auth-service` — `/auth`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/register` | Registra un nuevo usuario. |
| POST | `/auth/login` | Autentica y devuelve el token JWT. |
| GET | `/auth/user/{email}/exists` | Verifica si un correo ya está registrado. |

### `cliente-service` — `/clientes`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/clientes` | Crea un cliente. |
| GET | `/clientes` | Lista todos los clientes. |
| GET | `/clientes/{id}` | Obtiene un cliente por id. |
| PUT | `/clientes/{id}` | Actualiza un cliente. |
| DELETE | `/clientes/{id}` | Elimina un cliente. |
| GET | `/clientes/total` | Total de clientes registrados. |
| GET | `/clientes/correo/{correo}` | Busca por correo. |
| GET | `/clientes/nombre/{nombre}` | Busca por nombre. |
| GET | `/clientes/{id}/exists` | Verifica existencia (usado por `pedido-service`). |

### `empleado-service` — `/empleados`
CRUD estándar: `POST /empleados`, `GET /empleados`, `GET /empleados/{id}`, `PUT /empleados/{id}`, `DELETE /empleados/{id}`.

### `producto-service` — `/productos`
CRUD estándar: `POST /productos`, `GET /productos`, `GET /productos/{id}`, `PUT /productos/{id}`, `DELETE /productos/{id}`.

### `pedido-service` — `/pedidos`
CRUD estándar sobre `Pedido` (con `DetallePedido` anidado): `POST /pedidos`, `GET /pedidos`, `GET /pedidos/{id}`, `PUT /pedidos/{id}`, `DELETE /pedidos/{id}`.

### `asistencia-service` — `/asistencias`
CRUD estándar: `POST /asistencias`, `GET /asistencias`, `GET /asistencias/{id}`, `PUT /asistencias/{id}`, `DELETE /asistencias/{id}`.

### `envio-service` — `/envios`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/envios` | Crea un despacho ligado a un pedido. |
| GET | `/envios` | Lista todos los envíos. |
| GET | `/envios/{id}` | Obtiene un envío por id. |
| GET | `/envios/cliente/{idCliente}` | Envíos de un cliente. |
| GET | `/envios/pedido/{idPedido}` | Envío asociado a un pedido. |
| PUT | `/envios/{id}/estado` | Cambia el estado logístico (PENDIENTE / EN_TRANSITO / ENTREGADO). |
| DELETE | `/envios/{id}` | Elimina un envío. |

### `proveedor-service` — `/proveedores`
CRUD estándar: `POST /proveedores`, `GET /proveedores`, `GET /proveedores/{id}`, `PUT /proveedores/{id}`, `DELETE /proveedores/{id}`.

### `inventario-service` — `/inventario`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/inventario` | Registra un movimiento (ENTRADA/SALIDA/RESERVA/AJUSTE). Si es SALIDA y deja el stock bajo 10 unidades, valida contra `proveedor-service` que el proveedor esté activo. |
| GET | `/inventario` | Lista todos los movimientos. |
| GET | `/inventario/{id}` | Obtiene un movimiento por id. |
| GET | `/inventario/producto/{idProducto}` | Historial de un producto, ordenado por fecha descendente. |
| GET | `/inventario/tipo/{tipo}` | Filtra por tipo de movimiento. |
| PUT | `/inventario/{id}` | Actualiza un movimiento. |
| DELETE | `/inventario/{id}` | Elimina un movimiento. |

### `compra-service` — `/compras`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/compras` | Crea una orden de compra con sus detalles; valida proveedor activo y calcula `montoTotal`. |
| GET | `/compras` | Lista todas las compras. |
| GET | `/compras/{id}` | Obtiene una compra por id. |
| GET | `/compras/proveedor/{idProveedor}` | Compras de un proveedor específico. |
| GET | `/compras/estado/{estado}` | Filtra por estado (PENDIENTE / RECIBIDA / ANULADA). |
| PATCH | `/compras/{id}/recibir` | Marca la compra como RECIBIDA y carga stock en `inventario-service` por cada línea. |
| PATCH | `/compras/{id}/anular` | Anula la compra (bloqueado si ya fue RECIBIDA). |
| DELETE | `/compras/{id}` | Elimina una compra. |

> Cada microservicio también expone una **API v2 con HATEOAS** en `/<recurso>/v2` (ej. `/compras/v2`, `/inventario/v2`).

---

## 🔐 Autenticación en Swagger — sin extensiones de navegador

Cada microservicio tiene su documentación en:
```
http://localhost:9090/<servicio>/doc/swagger-ui/index.html
```
Ejemplo: `http://localhost:9090/cliente-service/doc/swagger-ui/index.html` (cambiar el prefijo por `pedido-service`, `inventario-service`, `compra-service`, etc.)

---

## 🚀 Cómo levantar el proyecto

### Opción A — Docker (recomendado)
```bash
docker-compose up --build
```
Levanta los 10 microservicios + el gateway. Requiere MySQL corriendo en el host (XAMPP u otro) accesible vía `host.docker.internal:3306`.

### Opción B — Manual / scripts locales
```bash
# Windows
.\build-all.bat      # compila todos los servicios (mvnw clean package -DskipTests)
.\clean-all.bat       # limpia los target/ de todos los servicios

# Linux / Mac
./build-all.sh
./clean-all.sh
```
Luego cada servicio se levanta individualmente con `./mvnw spring-boot:run` desde su carpeta, o vía Docker.

### Reinicio de base de datos (si Liquibase queda en un estado inconsistente)
Ejecutar en MySQL/phpMyAdmin y volver a levantar el proyecto:
```sql
DROP DATABASE bd_cliente;
DROP DATABASE bd_envio;
DROP DATABASE bd_pedidos;
DROP DATABASE bd_producto;
DROP DATABASE bd_users;
DROP DATABASE bd_empleado;
DROP DATABASE bd_asistencia;
DROP DATABASE bd_proveedor;
DROP DATABASE bd_inventario;
DROP DATABASE bd_compra;
```

### Pruebas unitarias
```bash
./mvnw test
```
Ejecutar dentro de cada carpeta de microservicio. Cobertura verificada con JaCoCo (`target/site/jacoco/index.html` tras `./mvnw test`).

---

## 🛡️ Guía rápida para la defensa técnica

**Si piden cambiar una ruta de un endpoint:**
1. Ir al `Controller` correspondiente (ej. `ClienteController.java`)
2. Modificar la anotación del método (ej. `@GetMapping("/{id}")` → `@GetMapping("/buscar/{id}")`)
3. Guardar, recompilar (`./mvnw spring-boot:run` o reiniciar el contenedor) — el Gateway enruta por prefijo de servicio, así que el cambio de sub-ruta no requiere tocar `api-gateway`
4. Si se cambia la ruta **base** del microservicio completo, ahí sí hay que actualizar el `@RequestMapping` de la clase **y** el `predicates: Path=...` correspondiente en `api-gateway/src/main/resources/application.yml`

**Si piden agregar un microservicio nuevo:**
1. Crear el proyecto Spring Boot con el mismo esqueleto CSR (Controller–Service–Repository/Model) que el resto
2. Agregar la ruta en `api-gateway/src/main/resources/application.yml` (bloque `routes`)
3. Copiar `SwaggerConfig.java` desde otro servicio y ajustar el `package` y el `title`
4. Agregarlo a `docker-compose.yml` y a `build-all.bat`/`clean-all.bat`

**Manejo de errores (respuesta típica ante "¿qué pasa si busco un id que no existe?"):**
> El `Service` valida la existencia del recurso y, si no lo encuentra, lanza una `ResourceNotFoundException` propia. Un `GlobalExceptionHandler` centralizado (`@RestControllerAdvice`) la captura y responde con `HttpStatus.NOT_FOUND` (404) y un cuerpo `ApiErrorResponse` consistente, en vez de dejar que la aplicación se caiga con un 500.

---

## 📁 Estructura de cada microservicio (patrón CSR)

```
<servicio>/
├── src/main/java/com/example/<servicio>/
│   ├── controller/       # Controller (CSR) + ControllerV2 (HATEOAS)
│   ├── service/          # Lógica de negocio y reglas de validación
│   ├── repository/       # JpaRepository
│   ├── model/             # Entidades JPA (@Entity)
│   ├── dto/               # DTOs de entrada/salida y de comunicación entre servicios
│   ├── assemblers/         # RepresentationModelAssembler (HATEOAS)
│   ├── config/             # SecurityConfig, SwaggerConfig, WebClientConfig, LiquibaseConfig
│   └── exception/           # GlobalExceptionHandler, ResourceNotFoundException, BadRequestException, ExternalServiceException
├── src/main/resources/
│   ├── application.yml (perfiles dev/docker)
│   └── db/changelog-master.xml + db/changelog/db.changelog.sql (Liquibase)
└── src/test/java/...        # Pruebas unitarias JUnit5 + Mockito
```

---

