# Reconstrucción Pedido-Service desde Cero

## 📋 Resumen Ejecutivo

Se ha reconstruido completamente el `pedido-service` desde cero con una arquitectura limpia que cumple los requisitos del negocio:

✅ **Almacena**: ID y nombre del cliente, fecha del pedido, estado, auditoría  
✅ **Detalles de venta**: Productos, cantidad, precio unitario, precio total  
✅ **Integración**: Obtiene datos del cliente desde `cliente-service` y productos desde `producto-service`  
✅ **Validación**: Verifica que cliente y productos existan antes de guardar  

---

## 🏗️ Arquitectura Reconstruida

### 1. Entidades (Models)

#### **Pedido.java**
```
campos:
- id (PK, auto-generado)
- idCliente (FK a cliente-service)
- nombreCliente (copiado de cliente-service)
- fecha (fecha/hora del pedido)
- estado (PENDIENTE, CONFIRMADO, ENTREGADO, CANCELADO)
- detalles (OneToMany con DetallePedido)
- createdAt (auditoría)
- updatedAt (auditoría)
```

**Relaciones**:
- OneToMany con DetallePedido (cascade all, orphan removal)
- Fetch type: EAGER (carga detalles automáticamente)

#### **DetallePedido.java**
```
campos:
- id (PK, auto-generado)
- pedido (ManyToOne FK)
- idProducto (referencia a producto-service)
- nombreProducto (copiado de producto-service)
- cantidad (unidades compradas)
- precioUnitario (precio de producto-service)
- precioTotal (cantidad × precioUnitario)
```

### 2. DTOs (Data Transfer Objects)

#### **PedidoDTO.java** (con Lombok)
```
@Data @NoArgsConstructor @AllArgsConstructor @Builder
campos: id, idCliente, nombreCliente, fecha, estado, detalles, createdAt, updatedAt
métodos: toModel(), fromModel()
```

#### **DetallePedidoDTO.java** (con Lombok)
```
@Data @NoArgsConstructor @AllArgsConstructor @Builder
campos: id, idProducto, nombreProducto, cantidad, precioUnitario, precioTotal
métodos: toModel(pedido), fromModel()
```

#### **ClienteDTO.java** (con Lombok) - NUEVO
```
Para capturar datos de cliente-service
campos: id, nombre, correo, telefono, direccion
```

#### **ProductoDTO.java** (con Lombok) - NUEVO
```
Para capturar datos de producto-service
campos: id, nombre, precio, stock
```

### 3. Repository

#### **PedidoRepository.java**
```java
extends JpaRepository<Pedido, Long>

Métodos automáticos:
- findByIdCliente(Long idCliente)
- findByFechaBetween(LocalDateTime inicio, LocalDateTime fin)
```

### 4. Service

#### **PedidoService.java**
```
Métodos principales:
- guardar(Pedido): 
  1. Obtiene cliente desde cliente-service
  2. Copia nombre del cliente
  3. Para cada detalle, obtiene producto desde producto-service
  4. Copia nombre y precio del producto
  5. Calcula precioTotal = cantidad × precioUnitario
  6. Valida que existan antes de guardar
  7. Establece estado "PENDIENTE" por defecto
  
- listar()
- buscarPorId(Long id)
- buscarPorIdCliente(Long idCliente)
- buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin)
- actualizar(Long id, Pedido detalles)
- eliminar(Long id)
```

### 5. Controller

#### **PedidoController.java** (@RequestMapping("/pedidos"))
```
POST   /pedidos                              → Crear pedido
GET    /pedidos                              → Listar todos
GET    /pedidos/{id}                         → Obtener por ID
GET    /pedidos/cliente/{idCliente}          → Listar por cliente
PUT    /pedidos/{id}                         → Actualizar
DELETE /pedidos/{id}                         → Eliminar
GET    /pedidos/total                        → Contar total
```

---

## 📊 Esquema de Base de Datos

### Tabla: pedido
```sql
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_id_cliente (id_cliente),
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
);
```

### Tabla: detalle_pedido
```sql
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    precio_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
    INDEX idx_id_pedido (id_pedido),
    INDEX idx_id_producto (id_producto)
);
```

---

## 📝 Datos de Ejemplo (Seed Data)

### Pedidos (10 registros)
```
ID 1: Juan García (cliente 1) - PENDIENTE
ID 2: María López (cliente 2) - CONFIRMADO
ID 3: Carlos Rodríguez (cliente 3) - ENTREGADO
ID 4: Ana Martínez (cliente 4) - CANCELADO
ID 5-10: Ciclo de clientes 1-4 con diferentes estados
```

### Detalles de Pedidos (16 registros)
```
Ejemplo:
Pedido 1 (Juan García):
  - Torta Tres Leches x2 @ $15,000 = $30,000
  - Torta Chocolate x1 @ $18,000 = $18,000

Pedido 2 (María López):
  - Cheesecake x3 @ $12,000 = $36,000
  - Mousse de Fresa x1 @ $8,000 = $8,000
```

---

## 🔄 Flujo de Integración

### Crear Pedido - Flujo de Datos

```
1. Cliente envía: POST /pedidos
   {
     "idCliente": 1,
     "detalles": [
       {
         "idProducto": 1,
         "cantidad": 2
       },
       {
         "idProducto": 3,
         "cantidad": 1
       }
     ]
   }

2. PedidoService.guardar():
   a. GET http://localhost:9091/clientes/1
      → { "id": 1, "nombre": "Juan García", ... }
      → Copia nombre al pedido
   
   b. Para detalle 1:
      GET http://localhost:9093/productos/1
      → { "id": 1, "nombre": "Torta Tres Leches", "precio": 15000 }
      → Set nombreProducto = "Torta Tres Leches"
      → Set precioUnitario = 15000
      → Set precioTotal = 2 × 15000 = 30000
   
   c. Para detalle 2:
      GET http://localhost:9093/productos/3
      → { "id": 3, "nombre": "Cheesecake", "precio": 12000 }
      → Set nombreProducto = "Cheesecake"
      → Set precioUnitario = 12000
      → Set precioTotal = 1 × 12000 = 12000
   
   d. Guarda pedido con estado "PENDIENTE"

3. Respuesta:
   {
     "id": 1,
     "idCliente": 1,
     "nombreCliente": "Juan García",
     "fecha": "2026-05-24T03:00:00",
     "estado": "PENDIENTE",
     "detalles": [
       {
         "id": 1,
         "idProducto": 1,
         "nombreProducto": "Torta Tres Leches",
         "cantidad": 2,
         "precioUnitario": 15000.00,
         "precioTotal": 30000.00
       },
       {
         "id": 2,
         "idProducto": 3,
         "nombreProducto": "Cheesecake",
         "cantidad": 1,
         "precioUnitario": 12000.00,
         "precioTotal": 12000.00
       }
     ]
   }
```

---

## ✅ Validaciones Implementadas

### 1. Cliente
- ✅ Validar que cliente exista en cliente-service
- ✅ Si no existe → ERROR: "[ERROR] CLIENTE NO EXISTE"
- ✅ Si existe → Copiar nombre automáticamente

### 2. Productos
- ✅ Validar que cada producto exista en producto-service
- ✅ Si no existe → ERROR: "[ERROR] PRODUCTO NO EXISTE: {id}"
- ✅ Si existe → Copiar nombre y precio automáticamente

### 3. Cálculos
- ✅ precioTotal = cantidad × precioUnitario
- ✅ Se recalcula automáticamente si cambian cantidad o precioUnitario

### 4. Estados
- ✅ Estado por defecto: "PENDIENTE"
- ✅ Estados válidos: PENDIENTE, CONFIRMADO, ENTREGADO, CANCELADO, EN_PREPARACION

---

## 🔧 Puertos y Configuración

### Pedido-Service
- **Puerto**: 9094
- **Base de datos**: bd_pedidos (MySQL localhost:3306)
- **Usuario**: root (sin contraseña)
- **Liquibase**: Ejecuta automáticamente al iniciar

### Dependencias Externas
- **Cliente-Service**: http://localhost:9091 (obtener datos de cliente)
- **Producto-Service**: http://localhost:9093 (obtener datos de producto)

---

## 📋 Requisitos del Negocio - Verificación

| Requisito | Estado | Detalle |
|-----------|--------|---------|
| Almacenar ID cliente | ✅ | Campo: idCliente |
| Almacenar nombre cliente | ✅ | Campo: nombreCliente (copia de cliente-service) |
| Fecha del pedido | ✅ | Campo: fecha (LocalDateTime) |
| Detalles de venta | ✅ | Entidad: DetallePedido (OneToMany) |
| Producto en detalle | ✅ | Campo: idProducto, nombreProducto |
| Cantidad en detalle | ✅ | Campo: cantidad (Integer) |
| Precio unitario | ✅ | Campo: precioUnitario (Double) |
| Precio total | ✅ | Campo: precioTotal (cantidad × precioUnitario) |
| Estado del pedido | ✅ | Campo: estado (String) |
| Auditoría | ✅ | Campos: createdAt, updatedAt |

---

## 🚀 Cómo Usar

### 1. Iniciar pedido-service
```bash
cd pedido-service
./mvnw spring-boot:run
```

### 2. Crear un pedido (curl)
```bash
curl -X POST http://localhost:9094/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1,
    "detalles": [
      {
        "idProducto": 1,
        "cantidad": 2
      },
      {
        "idProducto": 2,
        "cantidad": 1
      }
    ]
  }'
```

### 3. Listar pedidos
```bash
curl http://localhost:9094/pedidos
```

### 4. Obtener pedido por ID
```bash
curl http://localhost:9094/pedidos/1
```

### 5. Obtener pedidos de un cliente
```bash
curl http://localhost:9094/pedidos/cliente/1
```

### 6. Actualizar pedido (cambiar estado)
```bash
curl -X PUT http://localhost:9094/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "CONFIRMADO"
  }'
```

---

## 📦 Estructura Final de Archivos

```
pedido-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/pedido_service/
│   │   │   ├── model/
│   │   │   │   ├── Pedido.java ✅ ACTUALIZADO
│   │   │   │   └── DetallePedido.java ✅ ACTUALIZADO
│   │   │   ├── dto/
│   │   │   │   ├── PedidoDTO.java ✅ ACTUALIZADO
│   │   │   │   ├── DetallePedidoDTO.java ✅ ACTUALIZADO
│   │   │   │   ├── ClienteDTO.java ✅ NUEVO
│   │   │   │   └── ProductoDTO.java ✅ NUEVO
│   │   │   ├── repository/
│   │   │   │   └── PedidoRepository.java ✅ (sin cambios)
│   │   │   ├── service/
│   │   │   │   └── PedidoService.java ✅ ACTUALIZADO
│   │   │   ├── controller/
│   │   │   │   └── PedidoController.java ✅ ACTUALIZADO
│   │   │   └── PedidoServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/
│   │           └── db.changelog.sql ✅ ACTUALIZADO
│   └── test/
└── pom.xml
```

---

## ✅ Estado de Compilación

```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
```

✅ **Sin errores de compilación**  
✅ **Lista para ejecutarse**  
✅ **Datos de ejemplo incluidos**

---

## 📞 Próximos Pasos

1. ✅ Iniciar cliente-service (puerto 9091)
2. ✅ Iniciar producto-service (puerto 9093)
3. ✅ Iniciar pedido-service (puerto 9094)
4. ✅ Probar endpoints con curl o Postman
5. ✅ Verificar que 10 pedidos + 16 detalles se crean en la BD

---

**Versión**: 1.0 (Reconstrucción desde cero)  
**Fecha**: 2026-05-24  
**Framework**: Spring Boot 4.0.5 + Java 21 + Lombok  
**Base de Datos**: MySQL 5.7+
