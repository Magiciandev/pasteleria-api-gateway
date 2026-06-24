# Envío-Service Reconstruido - Documentación Técnica

## 📋 Resumen de Cambios

El microservicio `envio-service` ha sido reconstruido para corregir los problemas de inserción de datos y mejorar su integración con `cliente-service`.

## 🏗️ Arquitectura Base (Mantenida)

```
envio-service/
├── src/main/java/com/example/envio_service/
│   ├── controller/
│   │   └── EnvioController.java          # Endpoints REST
│   ├── service/
│   │   └── EnvioService.java             # Lógica de negocio + WebClient
│   ├── repository/
│   │   └── EnvioRepository.java          # Acceso a Envio
│   ├── model/
│   │   └── Envio.java                    # Entidad Envio (ACTUALIZADA)
│   ├── dto/
│   │   ├── EnvioDTO.java                 # DTO Envio (ACTUALIZADO)
│   │   └── ClienteDTO.java               # DTO Cliente para integración
│   ├── config/
│   │   └── WebClientConfig.java          # Config para comunicación inter-servicios
│   └── EnvioServiceApplication.java      # Clase principal
├── src/main/resources/
│   ├── application.properties
│   └── db/
│       ├── changelog-master.xml
│       └── changelog/
│           └── db.changelog.sql          # Scripts de BD (ACTUALIZADO)
└── pom.xml
```

## 📊 Modelo de Datos

### Tabla: `envio` (ACTUALIZADA)
```sql
CREATE TABLE envio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    direccion_entrega VARCHAR(500) NOT NULL,
    fecha_envio DATE NOT NULL,
    fecha_entrega DATE,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    costo DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 🔗 Campos de la Entidad Envio

| Campo | Tipo | Descripción | Origen |
|-------|------|-------------|--------|
| `id` | BIGINT | ID único del envío | Base de datos |
| `clienteId` | BIGINT | Referencia a cliente | cliente-service |
| `nombre` | VARCHAR | Nombre del cliente | Sincronizado desde cliente-service |
| `direccion` | VARCHAR | Dirección del cliente | Sincronizado desde cliente-service |
| `telefono` | VARCHAR | Teléfono del cliente | Sincronizado desde cliente-service |
| `direccionEntrega` | VARCHAR | Dirección de entrega específica | Formulario |
| `fechaEnvio` | DATE | Fecha del envío | Formulario |
| `fechaEntrega` | DATE | Fecha de entrega (puede ser NULL) | Sistema |
| `estado` | VARCHAR | PENDIENTE, EN_TRANSITO, ENTREGADO | Sistema |
| `costo` | DECIMAL | Costo del envío | Formulario |
| `createdAt` | TIMESTAMP | Fecha de creación automática | Sistema (@PrePersist) |
| `updatedAt` | TIMESTAMP | Fecha de actualización automática | Sistema (@PreUpdate) |

## 🔌 Endpoints REST

### Envíos Base
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/envios` | Crear nuevo envío |
| `GET` | `/envios` | Listar todos los envíos |
| `GET` | `/envios/{id}` | Obtener envío por ID |
| `PUT` | `/envios/{id}` | Actualizar envío |
| `DELETE` | `/envios/{id}` | Eliminar envío |

### Filtros y Búsquedas (NUEVOS)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/envios/estado/{estado}` | Obtener envíos por estado |
| `GET` | `/envios/cliente/{clienteId}` | Obtener envíos de un cliente |
| `GET` | `/envios/{id}/existe` | Validar existencia |

## 🔄 Integración con Cliente-Service

### Flujo de Sincronización

1. **Crear/Actualizar Envío**: Se proporciona `clienteId`
2. **EnvioService**: Contacta a cliente-service (`http://localhost:9091/clientes/{id}`)
3. **Obtener Datos**: Nombre, dirección y teléfono del cliente
4. **Almacenar Copia**: Se guardan en tabla envio para no depender de cliente-service en queries
5. **Fallback**: Si cliente-service no está disponible, usa datos proporcionados en el envío

### Código de Integración

```java
ClienteDTO cliente = webClientBuilder.build()
    .get()
    .uri("http://localhost:9091/clientes/" + envio.getClienteId())
    .retrieve()
    .bodyToMono(ClienteDTO.class)
    .block();

if (cliente != null) {
    envio.setNombre(cliente.getNombre());
    envio.setDireccion(cliente.getDireccion());
    envio.setTelefono(cliente.getTelefono());
}
```

## 📤 Ejemplo de Payload JSON

### POST /envios (Crear)
```json
{
  "clienteId": 1,
  "direccionEntrega": "Calle Principal 123, Depto 5, Santiago",
  "fechaEnvio": "2026-05-25",
  "estado": "PENDIENTE",
  "costo": 15000.00
}
```

**Nota:** Nombre, dirección y teléfono se sincronizan automáticamente desde cliente-service

### GET /envios/{id} (Respuesta)
```json
{
  "id": 1,
  "clienteId": 1,
  "nombre": "Juan García López",
  "direccion": "Calle Principal 123, Santiago",
  "telefono": "+56912345678",
  "direccionEntrega": "Avenida Libertador 456, Depto 5A, Santiago",
  "fechaEnvio": "2026-05-15",
  "fechaEntrega": "2026-05-17",
  "estado": "PENDIENTE",
  "costo": 15000.00,
  "createdAt": "2026-05-23T10:30:00",
  "updatedAt": "2026-05-23T10:30:00"
}
```

## 🗄️ Datos de Prueba

El archivo `db.changelog.sql` incluye 10 envíos con información completa:

| ID | Cliente | Nombre | Estado | Costo |
|----|---------|--------|--------|-------|
| 1 | 1 | Juan García López | PENDIENTE | $15,000 |
| 2 | 2 | María López Rodríguez | EN_TRANSITO | $12,500 |
| 3 | 3 | Carlos Rodríguez Martínez | ENTREGADO | $18,000 |
| 4 | 4 | Ana Martínez Pérez | PENDIENTE | $14,000 |
| 5 | 1 | Roberto Díaz Fernández | ENTREGADO | $20,000 |
| 6 | 2 | Francisca Torres Acosta | EN_TRANSITO | $11,500 |
| 7 | 3 | Diego Hernández Soto | PENDIENTE | $25,000 |
| 8 | 4 | Catalina Reyes Gómez | ENTREGADO | $16,500 |
| 9 | 1 | Andrés Flores Rojas | PENDIENTE | $13,000 |
| 10 | 2 | Sofía Peña Contreras | ENTREGADO | $19,000 |

## 🔧 Configuración (application.properties)

```properties
spring.application.name=envio-service
server.port=9096

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/bd_envio?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none

# Liquibase
spring.liquibase.change-log=classpath:db/changelog-master.xml

# Logging
logging.level.envio_service=DEBUG
```

## ⚙️ Dependencias Clave

- **Spring Boot 4.0.5**
- **Spring Data JPA** - Acceso a datos
- **Spring WebFlux** - WebClient reactivo
- **MySQL Connector** - Driver MySQL
- **Liquibase** - Gestión de versiones BD
- **Lombok** - Generación automática de getters/setters
- **Spring Cloud 2025.1.1** - Para integración con servicios

## 🚀 Flujo de Crear Envío

1. Cliente envía POST a `/envios` con clienteId y datos de envío
2. EnvioController.crearEnvio() recibe el DTO
3. EnvioService.guardar():
   - Valida que clienteId exista
   - Contacta a cliente-service para obtener nombre, dirección, teléfono
   - Copia datos del cliente al envío
   - Establece estado por defecto si no se proporciona
   - Guarda en BD
4. Respuesta JSON con envío completo

## 🆙 Flujo de Actualizar Envío

1. Cliente envía PUT a `/envios/{id}` con nuevos datos
2. EnvioService.actualizar():
   - Busca envío existente
   - Si cambió clienteId → sincroniza datos del cliente
   - Actualiza campos de dirección de entrega, estado, costo, fechas
   - Guarda cambios
3. Respuesta JSON con envío actualizado

## 📌 Notas Importantes

✅ **Compilación verificada**: El proyecto compila sin errores
✅ **Sincronización con Cliente-Service**: WebClient obtiene datos automáticamente
✅ **Datos de Prueba**: 10 envíos completos listos para usar
✅ **Timestamps Automáticos**: created_at y updated_at se generan automáticamente
✅ **Estados Predeterminados**: Si no se especifica, estado = "PENDIENTE"
✅ **Fallback Mode**: Si cliente-service no está disponible, funciona con datos locales
✅ **Nuevos Endpoints**: Filtros por estado y cliente

## 🧪 Validaciones

- `estado`: PENDIENTE, EN_TRANSITO, ENTREGADO
- `costo`: DECIMAL(10,2) - máximo $99,999,999.99
- `clienteId`: Requerido (aunque cliente-service puede no estar disponible)
- `fechaEntrega`: Puede ser NULL hasta que se entregue
- `nombre`, `direccion`, `telefono`: Se sincronizan automáticamente

## 🔄 Próximos Pasos

1. Levantar MySQL y crear base de datos `bd_envio`
2. Ejecutar `mvn spring-boot:run` desde la carpeta envio-service
3. Liquibase ejecutará automáticamente los changesets
4. Envíos de prueba se insertarán automáticamente
5. Validar endpoints con Postman o similar
6. Asegurar que cliente-service está en puerto 9091 para sincronización

## 📞 Integración con Otros Servicios

- **Cliente-Service (Puerto 9091)**: Proporciona nombre, dirección, teléfono
- **Pedido-Service (Puerto 9095)**: Puede consultar envíos para seguimiento
- **Gateway (Puerto 8080)**: Enruta solicitudes a este servicio
