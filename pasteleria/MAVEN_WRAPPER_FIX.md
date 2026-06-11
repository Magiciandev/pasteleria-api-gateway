# Microservicios de Pastelería

## Descripción

Proyecto de microservicios para gestión de una pastelería, incluyendo:
- **empleado-service**: Gestión de empleados (puerto 9092)
- **asistencia-service**: Gestión de asistencia y horas de trabajo con cálculo de horas extra (puerto 9095)

## Configuración del Entorno

### Requisitos Previos
- Java 17 (Eclipse Adoptium JDK 17.0.18+8)
- Maven 3.9.6 (se descarga automáticamente vía Maven Wrapper)
- MySQL 5.7 o superior
- MySQL Client (para scripts de inicialización)

### Instalación de MySQL en Windows

```powershell
# Si no tienes MySQL instalado, puedes usar Docker o instalarlo manualmente
# Asegúrate de que MySQL esté corriendo en localhost:3306
mysql -u root -p < init_databases.sql
```

### Problemas Comunes Solucionados

#### Error: "MAVEN_HOME is not defined"
**Solución**: Se ha actualizado el Maven Wrapper para detectar Java automáticamente sin necesidad de MAVEN_HOME.

#### Error: "Java version 21 not supported"
**Solución**: Se han actualizado los archivos pom.xml para usar Java 17, que es la versión disponible.

## Ejecución de los Servicios

### Opción 1: Usando PowerShell Script (Recomendado)

```powershell
# Para empleado-service
cd empleado-service
.\mvnw.ps1 spring-boot:run

# En otra terminal, para asistencia-service
cd asistencia-service
.\mvnw.ps1 spring-boot:run
```

### Opción 2: Usando Java Directamente

```powershell
# Para empleado-service
cd empleado-service
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe" `
  "-Dmaven.multiModuleProjectDirectory=$(pwd)" `
  "-cp" ".mvn/wrapper/maven-wrapper.jar" `
  "org.apache.maven.wrapper.MavenWrapperMain" `
  "spring-boot:run"

# En otra terminal, para asistencia-service
cd asistencia-service
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe" `
  "-Dmaven.multiModuleProjectDirectory=$(pwd)" `
  "-cp" ".mvn/wrapper/maven-wrapper.jar" `
  "org.apache.maven.wrapper.MavenWrapperMain" `
  "spring-boot:run"
```

### Opción 3: Compilar e Ejecutar JAR

```powershell
# Para empleado-service
cd empleado-service
.\mvnw.ps1 clean package
java -jar target/empleado-service-0.0.1-SNAPSHOT.jar

# En otra terminal, para asistencia-service
cd asistencia-service
.\mvnw.ps1 clean package
java -jar target/asistencia-service-0.0.1-SNAPSHOT.jar
```

## Verificación de Servicios

### Empleado Service (Puerto 9092)

```bash
# Listar todos los empleados
curl http://localhost:9092/empleados

# Crear un nuevo empleado
curl -X POST http://localhost:9092/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "rutEmpleado": "12345678-9",
    "nombre": "Juan García",
    "correo": "juan@pasteleria.com",
    "telefono": "987654321",
    "direccion": "Calle Principal 123",
    "cargo": "Pastelero",
    "fechaIngreso": "2023-01-15",
    "sueldoBase": 1200000,
    "valorHoraExtra": 15000
  }'

# Buscar empleado por RUT
curl http://localhost:9092/empleados/rut/12345678-9
```

### Asistencia Service (Puerto 9095)

```bash
# Listar todas las asistencias
curl http://localhost:9095/asistencias

# Registrar asistencia
curl -X POST http://localhost:9095/asistencias \
  -H "Content-Type: application/json" \
  -d '{
    "idEmpleado": 1,
    "fecha": "2025-06-10",
    "horaEntrada": "08:00:00",
    "horaSalida": "17:00:00",
    "presente": true,
    "observacion": "Sin observaciones"
  }'

# Buscar asistencias de un empleado
curl http://localhost:9095/asistencias/empleado/1

# Buscar asistencias por fecha
curl http://localhost:9095/asistencias/fecha/2025-06-10
```

## Bases de Datos

### Esquema: bd_empleado

**Tabla: empleado**
- id (INT, PK, Auto-increment)
- rut_empleado (VARCHAR 12, UNIQUE)
- nombre (VARCHAR 255, NOT NULL)
- correo (VARCHAR 255)
- telefono (VARCHAR 20)
- direccion (VARCHAR 255)
- cargo (VARCHAR 100)
- fecha_ingreso (DATE)
- sueldo_base (DECIMAL 10,2)
- valor_hora_extra (DECIMAL 10,2)
- activo (BOOLEAN, DEFAULT TRUE)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

### Esquema: bd_asistencia

**Tabla: asistencia**
- id (INT, PK, Auto-increment)
- id_empleado (INT, NOT NULL)
- nombre_empleado (VARCHAR 255)
- fecha (DATE, NOT NULL)
- hora_entrada (TIME)
- hora_salida (TIME)
- horas_trabajadas (DECIMAL 4,2)
- horas_extra (DECIMAL 4,2, DEFAULT 0)
- costo_horas_extra (DECIMAL 10,2, DEFAULT 0)
- presente (BOOLEAN, DEFAULT TRUE)
- observacion (TEXT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

## Lógica de Cálculo de Horas Extra

En el `asistencia-service`, cuando se registra una asistencia:

1. Se valida que el empleado exista usando WebClient para llamar a `empleado-service`
2. Si no está presente, todas las horas se ponen en cero
3. Si está presente:
   - Se calcula el tiempo transcurrido entre hora_entrada y hora_salida
   - Si el resultado es > 9 horas, el excedente se calcula como horas extra
   - El costo de horas extra se calcula: horas_extra × valor_hora_extra del empleado

Ejemplo:
- Entrada: 08:00, Salida: 18:30 = 10.5 horas
- Horas extra = 10.5 - 9 = 1.5 horas
- Si valor_hora_extra = 15,000 CLP
- Costo = 1.5 × 15,000 = 22,500 CLP

## Datos de Prueba

Ambos servicios vienen preconfigurados con 10 registros de prueba cada uno:

- **empleado-service**: 10 empleados con diferentes roles (Pastelero, Repartidor, Cajero, etc.)
- **asistencia-service**: 10 registros de asistencia de empleados 1-5 con fechas variadas

Para verificar los datos inserados:

```bash
# MySQL
mysql -u root bd_empleado -e "SELECT * FROM empleado;"
mysql -u root bd_asistencia -e "SELECT * FROM asistencia;"

# REST API
curl http://localhost:9092/empleados | jq '.content[] | {id, nombre, cargo}'
curl http://localhost:9095/asistencias | jq '.content[] | {id, nombreEmpleado, fecha, horasTrabajadas, horasExtra}'
```

## Logs y Debugging

Los servicios registran información de DEBUG. Para ver los logs:

```powershell
# Los logs se mostrarán en la consola al ejecutar spring-boot:run
# Busca líneas que comiencen con "[INFO]" o "[ERROR]"

# Para ejecutar con más verbosidad
.\mvnw.ps1 spring-boot:run --debug
```

## Documentación Adicional

- **empleado-service/HELP.md**: Documentación específica del servicio de empleados
- **asistencia-service/HELP.md**: Documentación específica del servicio de asistencia
- **init_databases.sql**: Script de inicialización de bases de datos
- **.mvn/wrapper/maven-wrapper.properties**: Configuración del Maven Wrapper

## Solución de Problemas

### Error: Puerto ya en uso
```powershell
# Empleado Service en puerto 9092
# Cambia el puerto en: empleado-service/src/main/resources/application.properties
# server.port=9092

# Asistencia Service en puerto 9095
# Cambia el puerto en: asistencia-service/src/main/resources/application.properties
# server.port=9095
```

### Error: No se puede conectar a MySQL
```powershell
# Verifica que MySQL esté corriendo
mysql -u root -e "SELECT 1;"

# Verifica las credenciales en application.properties
# spring.datasource.username=root
# spring.datasource.password=
# spring.datasource.url=jdbc:mysql://localhost:3306/...

# Asegúrate de que las bases de datos existan
mysql -u root -e "CREATE DATABASE IF NOT EXISTS bd_empleado;"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS bd_asistencia;"
```

### Error: Asistencia Service no puede contactar Empleado Service
```powershell
# Asegúrate de que Empleado Service esté corriendo en puerto 9092
# Verifica la configuración en asistencia-service/src/main/java/com/example/config/WebClientConfig.java
# EMPLEADO_SERVICE_URL = "http://localhost:9092"

# Prueba la conectividad
curl http://localhost:9092/empleados
```

## Notas Importantes

1. **Maven Wrapper**: Los scripts mvnw/mvnw.cmd y mvnw.ps1 descargan automáticamente Maven 3.9.6 en `.m2/wrapper/dists/` la primera vez
2. **Liquibase**: Las migraciones se ejecutan automáticamente al iniciar los servicios
3. **WebClient**: El asistencia-service usa WebClient (reactive) pero con `.block()` para obtener respuestas sincrónicas
4. **Lombok**: Se usa solo en DTOs y objetos de solicitud/respuesta, NO en entidades JPA
5. **Base de Datos**: Usa `spring.jpa.hibernate.ddl-auto=none` para evitar cambios automáticos en el esquema

