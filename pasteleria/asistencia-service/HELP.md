# Asistencia Service

Microservicio de gestión de asistencia de empleados para el sistema de pastelería con arquitectura de microservicios.

## Descripción

El Asistencia Service es responsable de gestionar el registro de asistencia de empleados incluyendo:
- Registro de entrada y salida
- Cálculo automático de horas trabajadas
- Cálculo de horas extra
- Cálculo del costo de horas extra
- Integración con Empleado Service para obtener datos de empleados
- Gestión de asistencias por empleado, fecha, etc.

## Configuración

### Base de Datos

- **Driver**: MySQL
- **URL**: jdbc:mysql://localhost:3306/bd_asistencia
- **Usuario**: root
- **Contraseña**: (vacía)

### Puerto

El servicio corre en el puerto **9095**

### Integración con Empleado Service

- **URL Base**: http://localhost:9092
- **Endpoint**: GET /empleados/{id}
- **Campos Utilizados**: id, nombre, sueldoBase, valorHoraExtra

## Endpoints Disponibles

### Registrar Asistencia
```
POST /asistencias
Content-Type: application/json

{
  "idEmpleado": 1,
  "fecha": "2025-06-10",
  "horaEntrada": "08:00:00",
  "horaSalida": "17:30:00",
  "presente": true,
  "observacion": "Trabajó normalmente"
}
```

### Listar Asistencias
```
GET /asistencias
```

### Obtener Asistencia por ID
```
GET /asistencias/{id}
```

### Buscar por Empleado
```
GET /asistencias/empleado/{idEmpleado}
```

### Buscar por Fecha
```
GET /asistencias/fecha/{fecha}
```

### Buscar por Empleado y Fecha
```
GET /asistencias/empleado/{idEmpleado}/fecha/{fecha}
```

### Actualizar Asistencia
```
PUT /asistencias/{id}
Content-Type: application/json

{
  "horaEntrada": "08:30:00",
  "horaSalida": "17:30:00",
  "presente": true,
  "observacion": "Entrada retrasada"
}
```

### Eliminar Asistencia
```
DELETE /asistencias/{id}
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

## Lógica de Negocio

### Al Registrar Asistencia

1. Valida que el empleado existe en empleado-service
2. Obtiene nombre y valorHoraExtra del empleado
3. Calcula horas_trabajadas (diferencia entre hora_salida y hora_entrada)
4. Calcula horas_extra (si horas_trabajadas > 9, entonces horas_trabajadas - 9, si no 0)
5. Calcula costo_horas_extra (horas_extra * valorHoraExtra)
6. Si presente=false, todas las horas y costos se establecen en 0

### Cálculo de Horas Extra

- Jornada completa: 9 horas
- Si trabajó más de 9 horas, el excedente es hora extra
- Ejemplo: Entrada 08:00, Salida 18:00 = 10 horas
  - Horas trabajadas: 10
  - Horas extra: 10 - 9 = 1 hora
  - Costo: 1 * valorHoraExtra

## Cambios en Base de Datos

Los cambios en el esquema se gestionan a través de Liquibase:
- Script maestro: `src/main/resources/db/changelog-master.xml`
- Scripts de cambios: `src/main/resources/db/changelog/db.changelog.sql`

## Estructura de Datos

### Tabla: asistencia

| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| id_empleado | BIGINT | NOT NULL |
| nombre_empleado | VARCHAR(255) | NOT NULL |
| fecha | DATE | NOT NULL |
| hora_entrada | TIME | NOT NULL |
| hora_salida | TIME | NOT NULL |
| horas_trabajadas | DECIMAL(4,2) | NOT NULL |
| horas_extra | DECIMAL(4,2) | NOT NULL, DEFAULT 0 |
| costo_horas_extra | DECIMAL(10,2) | NOT NULL, DEFAULT 0 |
| presente | BOOLEAN | NOT NULL, DEFAULT TRUE |
| observacion | VARCHAR(500) | - |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
