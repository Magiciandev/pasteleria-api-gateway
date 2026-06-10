# Guía de Pruebas - Producto-Service

## 🧪 Pruebas con cURL

### 1. Crear un Producto

```bash
curl -X POST http://localhost:9093/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Brownies de Chocolate",
    "precio": 3500,
    "stock": 25,
    "detalleProducto": {
      "descripcion": "Brownies caseros de chocolate belga, suave y delicioso",
      "contieneLactosa": true,
      "contieneHuevos": true,
      "contieneFrutosSecos": false,
      "contieneGluten": true,
      "alergenos": "Lácteos, Huevos, Gluten, Soja",
      "instruccionesAlmacenamiento": "Mantener en lugar fresco",
      "presentacion": "Unidades"
    }
  }'
```

### 2. Listar Todos los Productos

```bash
curl -X GET http://localhost:9093/productos
```

### 3. Obtener un Producto por ID

```bash
curl -X GET http://localhost:9093/productos/1
```

### 4. Obtener solo los Detalles de un Producto

```bash
curl -X GET http://localhost:9093/productos/1/detalles
```

### 5. Verificar si un Producto Existe

```bash
curl -X GET http://localhost:9093/productos/5/exists
```

### 6. Actualizar un Producto

```bash
curl -X PUT http://localhost:9093/productos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Torta Tres Leches Premium",
    "precio": 18000,
    "stock": 15,
    "detalleProducto": {
      "descripcion": "Torta premium con tres leches importadas",
      "contieneLactosa": true,
      "contieneHuevos": true,
      "contieneFrutosSecos": false,
      "contieneGluten": true,
      "alergenos": "Lácteos, Huevos, Gluten",
      "instruccionesAlmacenamiento": "Refrigerar entre 2-8°C",
      "presentacion": "Porciones"
    }
  }'
```

### 7. Eliminar un Producto

```bash
curl -X DELETE http://localhost:9093/productos/11
```

## 📦 Colección de Postman

Importar esta colección en Postman:

```json
{
  "info": {
    "name": "Producto-Service",
    "description": "API Microservicio de Productos - Pastelería"
  },
  "item": [
    {
      "name": "Crear Producto",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nombre\": \"Brownies de Chocolate\",\n  \"precio\": 3500,\n  \"stock\": 25,\n  \"detalleProducto\": {\n    \"descripcion\": \"Brownies caseros de chocolate belga\",\n    \"contieneLactosa\": true,\n    \"contieneHuevos\": true,\n    \"contieneFrutosSecos\": false,\n    \"contieneGluten\": true,\n    \"alergenos\": \"Lácteos, Huevos, Gluten, Soja\",\n    \"instruccionesAlmacenamiento\": \"Mantener en lugar fresco\",\n    \"presentacion\": \"Unidades\"\n  }\n}"
        },
        "url": {
          "raw": "http://localhost:9093/productos",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos"]
        }
      }
    },
    {
      "name": "Listar Todos los Productos",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9093/productos",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos"]
        }
      }
    },
    {
      "name": "Obtener Producto por ID",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9093/productos/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos", "1"]
        }
      }
    },
    {
      "name": "Obtener Detalles de Producto",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9093/productos/1/detalles",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos", "1", "detalles"]
        }
      }
    },
    {
      "name": "Verificar Existencia de Producto",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9093/productos/5/exists",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos", "5", "exists"]
        }
      }
    },
    {
      "name": "Actualizar Producto",
      "request": {
        "method": "PUT",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nombre\": \"Torta Tres Leches Premium\",\n  \"precio\": 18000,\n  \"stock\": 15,\n  \"detalleProducto\": {\n    \"descripcion\": \"Torta premium con tres leches importadas\",\n    \"contieneLactosa\": true,\n    \"contieneHuevos\": true,\n    \"contieneFrutosSecos\": false,\n    \"contieneGluten\": true,\n    \"alergenos\": \"Lácteos, Huevos, Gluten\",\n    \"instruccionesAlmacenamiento\": \"Refrigerar entre 2-8°C\",\n    \"presentacion\": \"Porciones\"\n  }\n}"
        },
        "url": {
          "raw": "http://localhost:9093/productos/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos", "1"]
        }
      }
    },
    {
      "name": "Eliminar Producto",
      "request": {
        "method": "DELETE",
        "url": {
          "raw": "http://localhost:9093/productos/11",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9093",
          "path": ["productos", "11"]
        }
      }
    }
  ]
}
```

## ✅ Respuestas Esperadas

### Crear Producto - 200 OK
```json
{
  "id": 11,
  "nombre": "Brownies de Chocolate",
  "precio": 3500,
  "stock": 25,
  "detalleProducto": {
    "id": 11,
    "descripcion": "Brownies caseros de chocolate belga, suave y delicioso",
    "contieneLactosa": true,
    "contieneHuevos": true,
    "contieneFrutosSecos": false,
    "contieneGluten": true,
    "alergenos": "Lácteos, Huevos, Gluten, Soja",
    "instruccionesAlmacenamiento": "Mantener en lugar fresco",
    "presentacion": "Unidades"
  }
}
```

### Listar Productos - 200 OK
```json
[
  {
    "id": 1,
    "nombre": "Torta Tres Leches",
    "precio": 15000,
    "stock": 10,
    "detalleProducto": { ... }
  },
  {
    "id": 2,
    "nombre": "Torta de Chocolate",
    "precio": 18000,
    "stock": 8,
    "detalleProducto": { ... }
  }
]
```

### Obtener Detalles - 200 OK
```json
{
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
```

### Verificar Existencia - 200 OK
```json
true
```

### Producto No Encontrado - 404 Not Found
```
(sin cuerpo)
```

### Eliminar Exitoso - 204 No Content
```
(sin cuerpo)
```

## 🔍 Validaciones Importantes

1. **Crear producto sin detalles**: El detalleProducto es opcional
   ```json
   {
     "nombre": "Producto Simple",
     "precio": 5000,
     "stock": 20
   }
   ```

2. **Actualizar solo el precio**: No es necesario enviar detalles si solo actualizas datos base
   ```json
   {
     "nombre": "Nombre Actualizado",
     "precio": 6000,
     "stock": 25
   }
   ```

3. **Alérgenos múltiples**: Separar con comas en el campo alergenos
   ```json
   "alergenos": "Lácteos, Huevos, Gluten, Soja, Frutos Secos"
   ```

4. **Verificar alérgenos antes de compra**: El cliente puede usar el endpoint `/productos/{id}/detalles` para verificar alérgenos antes de confirmar un pedido

## 🚀 Checklist de Prueba

- [ ] Listar todos los productos iniciales (debe haber 10)
- [ ] Crear un nuevo producto con detalles
- [ ] Obtener un producto y verificar que los detalles están presentes
- [ ] Actualizar un producto existente
- [ ] Obtener solo los detalles de un producto
- [ ] Verificar que un producto existe
- [ ] Eliminar un producto
- [ ] Verificar que al eliminar un producto, el detalle también se elimina
- [ ] Crear producto sin detalles (opcional)
- [ ] Actualizar solo información base sin tocar detalles
