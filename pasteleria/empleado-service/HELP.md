# Empleado Service

Microservicio de gestión de empleados para el sistema de pastelería con arquitectura de microservicios.

## Descripción

El Empleado Service es responsable de gestionar la información de empleados incluyendo:
- Registro y actualización de datos personales
- Gestión de cargos y asignaciones
- Cálculo de antigüedad
- Seguimiento de sueldos y valores de horas extra

## Configuración

### Base de Datos

- **Driver**: MySQL
- **URL**: jdbc:mysql://localhost:3306/bd_empleado
- **Usuario**: root
- **Contraseña**: (vacía)

### Puerto

El servicio corre en el puerto **9092**

## Endpoints Disponibles

### Crear Empleado
```
POST /empleados
Content-Type: application/json

{
  "rutEmpleado": "12345678-K",
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "telefono": "+56912345678",
  "direccion": "Calle Principal 123",
  "cargo": "Pastelero",
  "fechaIngreso": "2023-01-15",
  "sueldoBase": 1500000.00,
  "valorHoraExtra": 15000.00
}
```

### Listar Empleados
```
GET /empleados
```

### Obtener Empleado por ID
```
GET /empleados/{id}
```

### Buscar Empleado por RUT
```
GET /empleados/rut/{rut}
```

### Buscar Empleados por Cargo
```
GET /empleados/cargo/{cargo}
```

### Listar Empleados Activos
```
GET /empleados/activos
```

### Actualizar Empleado
```
PUT /empleados/{id}
Content-Type: application/json

{
  "cargo": "Pastelero Senior",
  "sueldoBase": 1800000.00,
  "valorHoraExtra": 18000.00,
  "activo": true,
  "telefono": "+56987654321",
  "direccion": "Nueva dirección"
}
```

### Eliminar Empleado
```
DELETE /empleados/{id}
```

## Compilar

```bash
./mvnw clean package
```

## Ejecutar

```bash
./mvnw spring-boot:run
```

## Testing

```bash
./mvnw test
```

## Cambios en Base de Datos

Los cambios en el esquema se gestionan a través de Liquibase:
- Script maestro: `src/main/resources/db/changelog-master.xml`
- Scripts de cambios: `src/main/resources/db/changelog/db.changelog.sql`

## Estructura de Datos

### Tabla: empleado

| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| rut_empleado | VARCHAR(255) | NOT NULL, UNIQUE |
| nombre | VARCHAR(255) | NOT NULL |
| correo | VARCHAR(255) | - |
| telefono | VARCHAR(255) | - |
| direccion | VARCHAR(255) | - |
| cargo | VARCHAR(255) | NOT NULL |
| fecha_ingreso | DATE | NOT NULL |
| sueldo_base | DECIMAL(10,2) | NOT NULL |
| valor_hora_extra | DECIMAL(10,2) | NOT NULL |
| activo | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
