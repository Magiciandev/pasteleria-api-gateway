# Guía de Pruebas - Envío-Service

## 🧪 Pruebas con cURL

### 1. Listar todos los envíos

```bash
curl -X GET http://localhost:9096/envios
```

### 2. Obtener un envío específico

```bash
curl -X GET http://localhost:9096/envios/1
```

### 3. Crear un nuevo envío

```bash
curl -X POST http://localhost:9096/envios \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 5,
    "direccionEntrega": "Calle Nueva 999, Depto 10, Santiago",
    "fechaEnvio": "2026-05-26",
    "estado": "PENDIENTE",
    "costo": 18000.00
  }'
```

**Nota:** Si cliente-service está disponible en `http://localhost:9091`, se sincronizan automáticamente nombre, dirección y teléfono del cliente.

### 4. Obtener envíos por estado

```bash
# PENDIENTE
curl -X GET "http://localhost:9096/envios/estado/PENDIENTE"

# EN_TRANSITO
curl -X GET "http://localhost:9096/envios/estado/EN_TRANSITO"

# ENTREGADO
curl -X GET "http://localhost:9096/envios/estado/ENTREGADO"
```

### 5. Obtener envíos de un cliente específico

```bash
curl -X GET "http://localhost:9096/envios/cliente/1"
```

### 6. Actualizar un envío

```bash
curl -X PUT http://localhost:9096/envios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "direccionEntrega": "Avenida Nueva 123, Depto 5, Santiago",
    "fechaEnvio": "2026-05-25",
    "fechaEntrega": "2026-05-27",
    "estado": "EN_TRANSITO",
    "costo": 16000.00
  }'
```

### 7. Verificar existencia de envío

```bash
curl -X GET http://localhost:9096/envios/5/existe
```

### 8. Eliminar un envío

```bash
curl -X DELETE http://localhost:9096/envios/11
```

## 📦 Colección de Postman

```json
{
  "info": {
    "name": "Envío-Service",
    "description": "API Microservicio de Envíos - Pastelería"
  },
  "item": [
    {
      "name": "Listar todos los envíos",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9096/envios",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios"]
        }
      }
    },
    {
      "name": "Obtener envío por ID",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9096/envios/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "1"]
        }
      }
    },
    {
      "name": "Crear envío",
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
          "raw": "{\n  \"clienteId\": 5,\n  \"direccionEntrega\": \"Calle Nueva 999, Depto 10, Santiago\",\n  \"fechaEnvio\": \"2026-05-26\",\n  \"estado\": \"PENDIENTE\",\n  \"costo\": 18000.00\n}"
        },
        "url": {
          "raw": "http://localhost:9096/envios",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios"]
        }
      }
    },
    {
      "name": "Obtener envíos por estado",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9096/envios/estado/PENDIENTE",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "estado", "PENDIENTE"]
        }
      }
    },
    {
      "name": "Obtener envíos por cliente",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9096/envios/cliente/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "cliente", "1"]
        }
      }
    },
    {
      "name": "Actualizar envío",
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
          "raw": "{\n  \"clienteId\": 1,\n  \"direccionEntrega\": \"Avenida Nueva 123, Depto 5, Santiago\",\n  \"fechaEnvio\": \"2026-05-25\",\n  \"fechaEntrega\": \"2026-05-27\",\n  \"estado\": \"EN_TRANSITO\",\n  \"costo\": 16000.00\n}"
        },
        "url": {
          "raw": "http://localhost:9096/envios/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "1"]
        }
      }
    },
    {
      "name": "Verificar existencia",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9096/envios/5/existe",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "5", "existe"]
        }
      }
    },
    {
      "name": "Eliminar envío",
      "request": {
        "method": "DELETE",
        "url": {
          "raw": "http://localhost:9096/envios/11",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9096",
          "path": ["envios", "11"]
        }
      }
    }
  ]
}
```

## ✅ Respuestas Esperadas

### Listar Envíos - 200 OK
```json
[
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
  },
  { ... más envíos ... }
]
```

### Crear Envío - 200 OK
```json
{
  "id": 11,
  "clienteId": 5,
  "nombre": "Cliente Sincronizado",
  "direccion": "Dirección del Cliente",
  "telefono": "+56987654321",
  "direccionEntrega": "Calle Nueva 999, Depto 10, Santiago",
  "fechaEnvio": "2026-05-26",
  "fechaEntrega": null,
  "estado": "PENDIENTE",
  "costo": 18000.00,
  "createdAt": "2026-05-23T15:45:00",
  "updatedAt": "2026-05-23T15:45:00"
}
```

### Envíos por Estado - 200 OK
```json
[
  {
    "id": 1,
    "clienteId": 1,
    "nombre": "Juan García López",
    "estado": "PENDIENTE",
    ...
  },
  {
    "id": 4,
    "clienteId": 4,
    "nombre": "Ana Martínez Pérez",
    "estado": "PENDIENTE",
    ...
  }
]
```

### Envíos por Cliente - 200 OK
```json
[
  {
    "id": 1,
    "clienteId": 1,
    "nombre": "Juan García López",
    ...
  },
  {
    "id": 5,
    "clienteId": 1,
    "nombre": "Juan García López",
    ...
  },
  {
    "id": 9,
    "clienteId": 1,
    "nombre": "Juan García López",
    ...
  }
]
```

### Verificar Existencia - 200 OK
```json
true
```

### Envío No Encontrado - 404 Not Found
```
(sin cuerpo)
```

### Eliminar Exitoso - 204 No Content
```
(sin cuerpo)
```

## 🔍 Validaciones Importantes

1. **clienteId es requerido**: Siempre debe proporcionar el ID del cliente
2. **Sincronización automática**: Nombre, dirección y teléfono se llenan automáticamente
3. **Estado por defecto**: Si no se proporciona, será "PENDIENTE"
4. **Fechas**: fechaEntrega puede ser NULL hasta que se entregue el envío
5. **Costo**: Debe ser un decimal válido (máximo 99,999,999.99)

## 🚀 Checklist de Prueba

- [ ] Listar los 10 envíos iniciales
- [ ] Obtener un envío específico
- [ ] Crear un nuevo envío con sincronización de cliente
- [ ] Filtrar envíos por estado (PENDIENTE, EN_TRANSITO, ENTREGADO)
- [ ] Obtener envíos de un cliente específico
- [ ] Actualizar envío (cambiar estado a EN_TRANSITO)
- [ ] Marcar envío como ENTREGADO con fecha de entrega
- [ ] Verificar que un envío existe
- [ ] Eliminar un envío
- [ ] Verificar que el envío eliminado ya no existe

## 🔗 Pruebas de Integración

### Con Cliente-Service

1. Asegurar que cliente-service está en puerto 9091
2. Crear envío con clienteId que exista en cliente-service
3. Verificar que nombre, dirección y teléfono se sincronizan correctamente
4. Actualizar cliente en cliente-service y crear nuevo envío para ese cliente
5. Verificar que los datos nuevos se reflejan en envios

### Sin Cliente-Service

1. Detener cliente-service
2. Crear envío con clienteId (aunque no exista el cliente)
3. Usar nombre, dirección, teléfono proporcionados en fallback
4. Verificar que envío se crea correctamente

## 📊 Datos Esperados Después de Levantamiento

**10 envíos precargados:**
- 3 en estado PENDIENTE (IDs: 1, 4, 7, 9)
- 3 en estado EN_TRANSITO (IDs: 2, 6)
- 4 en estado ENTREGADO (IDs: 3, 5, 8, 10)

Distribuidos entre 4 clientes (IDs: 1, 2, 3, 4)
