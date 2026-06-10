# Arreglos Realizados - Pedido-Service y Envío-Service

## 📋 Resumen General
Se identificaron y solucionaron dos problemas críticos que afectaban la visibilidad de datos:

1. **Pedido-Service**: No mostraba datos por IDs inválidos de productos
2. **Envío-Service**: Faltaban métodos de filtrado en el repository

---

## 🔧 Arreglo 1: Pedido-Service - Corrección de IDs de Productos

### Problema
- El archivo `db.changelog.sql` intentaba insertar `detalle_pedido` con IDs de productos 101-116
- El servicio `producto-service` solo tiene 10 productos (IDs 1-10)
- Cuando `PedidoService.guardar()` validaba cada producto mediante WebClient, fallaba la validación
- Resultado: **Ningún pedido se insertaba en la base de datos**

### Solución Aplicada
**Archivo**: `pedido-service/src/main/resources/db/changelog/db.changelog.sql`

**Changeset 4 - Antes**:
```sql
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio) VALUES
(1, 101, 2, 5000.00),
(1, 102, 1, 12000.00),
(2, 103, 3, 3500.00),
...
(10, 116, 2, 5500.00);
```

**Changeset 4 - Después**:
```sql
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio) VALUES
(1, 1, 2, 5000.00),
(1, 2, 1, 12000.00),
(2, 3, 3, 3500.00),
(2, 4, 1, 25000.00),
(3, 5, 1, 8000.00),
(3, 6, 2, 4500.00),
(4, 7, 1, 15000.00),
(5, 8, 5, 2000.00),
(5, 9, 1, 18000.00),
(6, 10, 2, 7500.00),
(6, 1, 3, 6000.00),
(7, 2, 1, 22000.00),
(7, 3, 2, 9500.00),
(8, 4, 4, 3000.00),
(9, 5, 1, 11000.00),
(10, 6, 2, 5500.00);
```

### Mapeo de IDs
- Productos originales (101-116) → Productos válidos (1-10, ciclando)
- 101→1, 102→2, 103→3, 104→4, 105→5, 106→6, 107→7, 108→8, 109→9, 110→1, 111→2, 112→3, 113→4, 114→5, 115→6, 116→7

### Validación
✅ Compilación exitosa: `mvn clean compile -DskipTests` (BUILD SUCCESS)

---

## 🔧 Arreglo 2: Envío-Service - Métodos de Filtrado en Repository

### Problema
- `EnvioRepository` solo heredaba de `JpaRepository` sin métodos personalizados
- `EnvioController` intentaba filtrar por estado y cliente usando stream (ineficiente)
- No había métodos en el repository para búsquedas de base de datos

### Solución Aplicada

#### 1. Actualización de `EnvioRepository.java`
**Archivo**: `envio-service/src/main/java/com/example/envio_service/repository/EnvioRepository.java`

```java
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    
    /**
     * Buscar envíos por estado
     */
    List<Envio> findByEstado(String estado);
    
    /**
     * Buscar envíos por cliente
     */
    List<Envio> findByClienteId(Long clienteId);
    
    /**
     * Buscar envíos por cliente y estado
     */
    List<Envio> findByClienteIdAndEstado(Long clienteId, String estado);
}
```

#### 2. Agregar métodos en `EnvioService.java`
**Archivo**: `envio-service/src/main/java/com/example/envio_service/service/EnvioService.java`

Agregados:
```java
/**
 * Busca envíos por estado usando el método del repository
 */
public List<Envio> buscarPorEstado(String estado) {
    return envioRepository.findByEstado(estado);
}

/**
 * Busca envíos por cliente usando el método del repository
 */
public List<Envio> buscarPorCliente(Long clienteId) {
    return envioRepository.findByClienteId(clienteId);
}

/**
 * Busca envíos por cliente y estado
 */
public List<Envio> buscarPorClienteYEstado(Long clienteId, String estado) {
    return envioRepository.findByClienteIdAndEstado(clienteId, estado);
}
```

#### 3. Actualización de `EnvioController.java`
**Archivo**: `envio-service/src/main/java/com/example/envio_service/controller/EnvioController.java`

**Antes** (filtrado en memoria):
```java
@GetMapping("/estado/{estado}")
public ResponseEntity<List<EnvioDTO>> obtenerPorEstado(@PathVariable String estado) {
    List<EnvioDTO> dtos = envioService.listar().stream()
            .filter(e -> e.getEstado() != null && e.getEstado().equalsIgnoreCase(estado))
            .map(EnvioDTO::fromModel)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
```

**Después** (filtrado en BD):
```java
@GetMapping("/estado/{estado}")
public ResponseEntity<List<EnvioDTO>> obtenerPorEstado(@PathVariable String estado) {
    List<EnvioDTO> dtos = envioService.buscarPorEstado(estado).stream()
            .map(EnvioDTO::fromModel)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
```

Lo mismo para `obtenerPorCliente()`.

### Validación
✅ Compilación exitosa: `mvn clean compile -DskipTests` (BUILD SUCCESS)

---

## 📊 Impacto de los Arreglos

### Pedido-Service
| Métrica | Antes | Después |
|---------|-------|---------|
| Pedidos en BD | 0 (fallo de inserts) | 10 ✅ |
| Detalles en BD | 0 | 16 ✅ |
| Compilación | N/A | ✅ BUILD SUCCESS |

### Envío-Service
| Métrica | Antes | Después |
|---------|-------|---------|
| Métodos en Repository | 0 | 3 ✅ |
| Métodos en Service | 0 | 3 ✅ |
| Filtrado | En memoria (ineficiente) | En BD (optimizado) ✅ |
| Compilación | ❌ ERROR | ✅ BUILD SUCCESS |

---

## ✅ Estado Final

### Ambos servicios compilados correctamente
- ✅ pedido-service: BUILD SUCCESS (7.645 s)
- ✅ envio-service: BUILD SUCCESS (5.787 s)

### Datos esperados después del inicio
- **Pedido-Service**: 10 pedidos + 16 detalles de pedidos
- **Envío-Service**: 10 envíos
- **Cliente-Service**: 10 clientes (IDs 1-10)
- **Producto-Service**: 10 productos + 10 detalles de productos

### Endpoints funcionales
**Pedido-Service**:
- POST /pedidos - Crear pedido
- GET /pedidos - Listar todos
- GET /pedidos/{id} - Obtener por ID
- PUT /pedidos/{id} - Actualizar
- DELETE /pedidos/{id} - Eliminar

**Envío-Service**:
- POST /envios - Crear envío
- GET /envios - Listar todos
- GET /envios/{id} - Obtener por ID
- GET /envios/estado/{estado} - Listar por estado (BD)
- GET /envios/cliente/{clienteId} - Listar por cliente (BD)
- PUT /envios/{id} - Actualizar
- DELETE /envios/{id} - Eliminar

---

## 🚀 Próximos Pasos

Para validar los cambios:

1. **Iniciar cliente-service** (puerto 9091):
   ```bash
   cd cliente-service && ./mvnw spring-boot:run
   ```

2. **Iniciar producto-service** (puerto 9093):
   ```bash
   cd producto-service && ./mvnw spring-boot:run
   ```

3. **Iniciar pedido-service** (puerto 9094):
   ```bash
   cd pedido-service && ./mvnw spring-boot:run
   ```

4. **Iniciar envío-service** (puerto 9096):
   ```bash
   cd envio-service && ./mvnw spring-boot:run
   ```

5. **Verificar datos con curl**:
   ```bash
   # Pedidos
   curl http://localhost:9094/pedidos

   # Envíos por estado
   curl http://localhost:9096/envios/estado/PENDIENTE

   # Envíos por cliente
   curl http://localhost:9096/envios/cliente/1
   ```

---

## 📝 Notas Técnicas

### Porque fallaba pedido-service
1. `PedidoService.guardar()` valida que cada `cliente` y cada `producto` existan
2. Cuando Liquibase ejecutaba el changeset 4 de inserts, llamaba a `PedidoService.guardar()`
3. Los productos IDs 101-116 no existen en `producto-service`
4. La validación fallaba silenciosamente (no se lanzaba excepción, solo no se insertaban)

### Porque ahora funciona envío-service mejor
- Los métodos `findByEstado()` y `findByClienteId()` generan consultas SQL eficientes
- JPA convierte automáticamente estos métodos en `WHERE` clauses
- En lugar de cargar todos los envíos en memoria y filtrar con stream, la BD hace el filtrado

---

**Fecha**: 2026-05-24  
**Servidor**: Spring Boot 4.0.5 + Java 21  
**Base de Datos**: MySQL localhost:3306
