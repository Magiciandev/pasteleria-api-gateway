# Producto-Service Actualizado - Documentación Técnica

## 📋 Resumen de Cambios

El microservicio `producto-service` ha sido reconstruido con una arquitectura mejorada que incluye gestión de detalles de productos y información de alérgenos.

## 🏗️ Arquitectura Base (Mantenida)

La estructura del proyecto sigue el patrón estándar de Spring Boot con separación de responsabilidades:

```
producto-service/
├── src/main/java/com/example/producto_service/
│   ├── controller/
│   │   └── ProductoController.java        # Endpoints REST
│   ├── service/
│   │   └── ProductoService.java           # Lógica de negocio
│   ├── repository/
│   │   ├── ProductoRepository.java        # Acceso a Producto
│   │   └── DetalleProductoRepository.java # Acceso a DetalleProducto (NUEVO)
│   ├── model/
│   │   ├── Producto.java                  # Entidad Producto
│   │   └── DetalleProducto.java           # Entidad DetalleProducto (NUEVO)
│   ├── dto/
│   │   ├── ProductoDTO.java               # DTO Producto
│   │   └── DetalleProductoDTO.java        # DTO DetalleProducto (NUEVO)
│   └── ProductoServiceApplication.java   # Clase principal
├── src/main/resources/
│   ├── application.properties
│   └── db/
│       ├── changelog-master.xml
│       └── changelog/
│           └── db.changelog.sql          # Scripts de BD (ACTUALIZADO)
└── pom.xml
```

## 📊 Modelo de Datos

### Tabla: `producto`
```sql
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    precio DOUBLE,
    stock INT,
    detalle_id BIGINT UNIQUE,
    FOREIGN KEY (detalle_id) REFERENCES detalle_producto(id)
);
```

### Tabla: `detalle_producto` (NUEVA)
```sql
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
```

## 🔗 Relación entre Entidades

**Tipo:** OneToOne
- **Relación:** Un Producto tiene exactamente un DetalleProducto
- **Cascade:** CascadeType.ALL - Al eliminar un producto, se elimina automáticamente su detalle
- **Orphan Removal:** true - Los detalles sin producto asociado se eliminan

```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "detalle_id", referencedColumnName = "id")
private DetalleProducto detalleProducto;
```

## 📝 Entidades (Models)

### Producto.java
- `id`: Identificador único
- `nombre`: Nombre del producto
- `precio`: Precio en pesos
- `stock`: Cantidad disponible
- `detalleProducto`: Relación con DetalleProducto (NUEVO)

### DetalleProducto.java (NUEVA)
- `id`: Identificador único
- `descripcion`: Descripción detallada del producto
- `contieneLactosa`: Booleano para alergia a lácteos
- `contieneHuevos`: Booleano para alergia a huevos
- `contieneFrutosSecos`: Booleano para alergia a frutos secos
- `contieneGluten`: Booleano para alergia a gluten
- `alergenos`: String con lista de alérgenos
- `instruccionesAlmacenamiento`: Instrucciones de conservación
- `presentacion`: Formato de presentación (Porciones, Unidades, Docena, Bolsa)

## 🔌 Endpoints REST

### Productos Base
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/productos` | Crear nuevo producto con detalles |
| `GET` | `/productos` | Listar todos los productos |
| `GET` | `/productos/{id}` | Obtener producto por ID (incluye detalles) |
| `PUT` | `/productos/{id}` | Actualizar producto y sus detalles |
| `DELETE` | `/productos/{id}` | Eliminar producto (elimina detalles automáticamente) |

### Detalles de Productos (NUEVOS)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/productos/{id}/detalles` | Obtener solo los detalles de un producto |

### Validación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/productos/{id}/exists` | Validar si existe un producto |

## 📤 Ejemplo de Payload JSON

### POST /productos (Crear)
```json
{
  "nombre": "Torta Tres Leches",
  "precio": 15000.0,
  "stock": 10,
  "detalleProducto": {
    "descripcion": "Torta clásica elaborada con tres tipos de leche: leche condensada, leche evaporada y crema de leche.",
    "contieneLactosa": true,
    "contieneHuevos": true,
    "contieneFrutosSecos": false,
    "contieneGluten": true,
    "alergenos": "Lácteos, Huevos, Gluten",
    "instruccionesAlmacenamiento": "Refrigerar entre 2-8°C",
    "presentacion": "Porciones"
  }
}
```

### GET /productos/{id} (Respuesta)
```json
{
  "id": 1,
  "nombre": "Torta Tres Leches",
  "precio": 15000.0,
  "stock": 10,
  "detalleProducto": {
    "id": 1,
    "descripcion": "Torta clásica elaborada con tres tipos de leche...",
    "contieneLactosa": true,
    "contieneHuevos": true,
    "contieneFrutosSecos": false,
    "contieneGluten": true,
    "alergenos": "Lácteos, Huevos, Gluten",
    "instruccionesAlmacenamiento": "Refrigerar entre 2-8°C",
    "presentacion": "Porciones"
  }
}
```

## 🗄️ Datos de Prueba

El archivo `db.changelog.sql` incluye 10 productos con sus detalles:

1. **Torta Tres Leches** - $15,000 | Contiene: Lactosa, Huevos, Gluten
2. **Torta de Chocolate** - $18,000 | Contiene: Lactosa, Huevos, Gluten, Soja
3. **Cheesecake de Frambuesa** - $12,000 | Contiene: Lactosa, Huevos, Gluten
4. **Pie de Limón** - $10,000 | Contiene: Lactosa, Huevos, Gluten
5. **Kuchen de Nuez** - $14,000 | Contiene: Lactosa, Huevos, Gluten, Frutos Secos
6. **Cupcake de Vainilla** - $2,000 | Contiene: Lactosa, Huevos, Gluten
7. **Cupcake Red Velvet** - $2,500 | Contiene: Lactosa, Huevos, Gluten
8. **Galletas con Chips de Chocolate** - $500 | Contiene: Lactosa, Gluten, Soja
9. **Pan Amasado (Docena)** - $3,000 | Contiene: Gluten
10. **Empanada de Pino** - $2,000 | Contiene: Gluten, Soja

## 🔧 Configuración (application.properties)

```properties
spring.application.name=producto-service
server.port=9093

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/bd_producto?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none

# Liquibase
spring.liquibase.change-log=classpath:db/changelog-master.xml
```

## ⚙️ Dependencias Clave

- **Spring Boot 4.0.5**
- **Spring Data JPA** - Acceso a datos
- **Spring Data REST** - Exponer repositorios como endpoints REST
- **MySQL Connector** - Driver MySQL
- **Liquibase** - Gestión de versiones de base de datos
- **Lombok** - Generación automática de getters/setters
- **Spring Cloud 2025.1.1** - Para integración con gateway

## 🚀 Flujo de Guardar Producto

1. Cliente envía POST a `/productos` con ProductoDTO
2. ProductoController.crearProducto() recibe el DTO
3. ProductoDTO.toModel() convierte a Producto
4. ProductoService.guardar() es llamado:
   - Si DetalleProducto existe → guardarlo primero
   - Luego guardar Producto con referencia al detalle
5. Producto guardado es convertido a ProductoDTO
6. Respuesta JSON se envía al cliente

## 🧹 Flujo de Eliminar Producto

1. Cliente envía DELETE a `/productos/{id}`
2. ProductoController.eliminar() recibe el ID
3. ProductoService.eliminar() es llamado
4. JPA elimina el Producto
5. Debido a `orphanRemoval=true`, DetalleProducto es eliminado automáticamente
6. Respuesta 204 No Content

## 📌 Notas Importantes

✅ **Compilación verificada**: El proyecto compila sin errores
✅ **Arquitectura consistente**: Mantiene patrón DTO/Model/Repository/Service
✅ **Datos persistentes**: Incluye 10 productos con información real de alérgenos
✅ **Validación de alérgenos**: Información detallada para que clientes identifiquen alergias
✅ **Escalabilidad**: Estructura permite agregar más detalles sin refactorizar

## 📞 Integración con Otros Servicios

El endpoint `/productos/{id}/exists` sigue disponible para que **Pedido-Service** y otros servicios validen si un producto existe antes de procesarlo.

## 🔄 Próximos Pasos

1. Levantar MySQL y crear base de datos `bd_producto`
2. Ejecutar `mvn spring-boot:run` desde la carpeta producto-service
3. Liquibase ejecutará automáticamente los changesets
4. Productos de prueba se insertarán automáticamente
5. Validar endpoints con Postman o similar
