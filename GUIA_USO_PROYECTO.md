# 📘 Guía de Uso – Proyecto Microservicios "Pastelería"

Guía paso a paso, en el orden correcto, para levantar el proyecto, probarlo con Swagger + ModHeader, y dejarlo listo para presentar. Guárdala y léela tal cual el día de la presentación: solo copia y pega los comandos en orden.

---

## 0. Mapa rápido del proyecto

8 módulos Maven independientes. El **api-gateway** es la única puerta de entrada "oficial" (con seguridad JWT). Los otros 7 son microservicios que también se pueden golpear directo en su propio puerto (sin token) para debug rápido.

| # | Servicio | Puerto | Base de datos | Ruta pública (vía Gateway) | ¿Necesita token? |
|---|----------|--------|----------------|------------------------------|-------------------|
| 1 | **api-gateway** | 9090 | — | `http://localhost:9090` | Es el que exige el token |
| 2 | **auth-service** | 9097 | `bd_users` | `/auth/**` | ❌ No (es público) |
| 3 | **cliente-service** | 9091 | `bd_cliente` | `/clientes/**` | ✅ Sí |
| 4 | **empleado-service** | 9092 | `bd_empleado` | `/empleados/**` | ✅ Sí |
| 5 | **producto-service** | 9093 | `bd_producto` | `/productos/**` | ✅ Sí |
| 6 | **pedido-service** | 9094 | `bd_pedidos` | `/pedidos/**` | ✅ Sí |
| 7 | **asistencia-service** | 9095 | `bd_asistencia` | `/asistencias/**` | ✅ Sí |
| 8 | **envio-service** | 9096 | `bd_envio` | `/envios/**` | ✅ Sí |

**Importante:** ninguna base de datos corre dentro de Docker. `docker-compose.yml` **no** trae un contenedor MySQL — todos los servicios apuntan a `host.docker.internal:3306` (o `localhost:3306` si los corres sin Docker). **Por eso MySQL debe estar corriendo en tu PC (Workbench/XAMPP/servicio de Windows) antes de levantar cualquier cosa.** Usuario `root`, sin contraseña.

Cada servicio crea su propia base de datos solo si no existe (`createDatabaseIfNotExist=true`), así que no hace falta crearlas a mano — solo borrarlas cuando quieras reiniciar todo de cero (ver paso 9).

---

## 1. Requisitos antes de empezar (una sola vez por PC)

- **Java 21** instalado (todos los `pom.xml` lo exigen).
- **MySQL** corriendo en `localhost:3306`, usuario `root`, sin password.
- **Docker Desktop** abierto (si van a usar `build-all`).
- **Postman** o el navegador para pegar URLs.
- Extensión **ModHeader** instalada en Chrome/Edge (para inyectar el token automáticamente).

---

## 2. Configurar ModHeader (se hace una sola vez, queda guardado)

ModHeader sirve para no tener que pegar el header `Authorization` a mano en cada request del navegador.

1. Abre la extensión ModHeader → crea un nuevo perfil.
2. En **Request headers** agrega:
   - **Name:** `Authorization`
   - **Value:** `Bearer ` *(luego, en el paso 6, pegas el token detrás de "Bearer ")*
3. En **Request URL filters** (o "Filters") pon esta regex para que el header solo se mande hacia el gateway y no se filtre a otras páginas:
   ```
   .*localhost:9090.*
   ```
4. Activa el switch del perfil (ON).

Con esto, **toda** petición que vaya a `localhost:9090` (Swagger, endpoints, lo que sea) llevará el token automáticamente.

> ⚠️ El token dura **1 hora**. Si pasa ese tiempo y te empieza a tirar 401/403, vuelve a loguearte (paso 6) y reemplaza el valor en ModHeader.

---

## 3. Levantar TODO el proyecto (opción recomendada para el día de la presentación)

Esto compila los 8 microservicios y levanta todo con Docker en un solo comando.

### Windows
```bat
build-all.bat
```

### Mac / Linux
```bash
./build-all.sh
```

¿Qué hace exactamente, en orden?
1. Borra contenedores e imágenes Docker viejas.
2. Compila cada microservicio (genera el `.jar`) en este orden: `api-gateway → auth-service → cliente-service → empleado-service → producto-service → pedido-service → asistencia-service → envio-service`.
3. Ejecuta `docker compose up --build`, que construye las 8 imágenes y levanta los 8 contenedores, cada uno publicado en su puerto (tabla del punto 0).

Espera a ver en consola que los 8 servicios terminaron de iniciar (Spring Boot imprime "Started ___Application" para cada uno). Recién ahí están todos arriba.

---

## 4. Alternativa: levantar servicio por servicio sin Docker (para programar/debuggear)

Útil cuando estás modificando código de un solo servicio y no quieres reconstruir todo Docker cada vez. Necesitas una terminal por servicio.

**Windows:**
```bat
cd nombre-del-servicio
.\mvnw.cmd spring-boot:run
```

**Mac / Linux:**
```bash
cd nombre-del-servicio
./mvnw spring-boot:run
```

Orden recomendado de arranque (algunos servicios validan datos contra otros al iniciar, vía Liquibase + WebClient):

1. `cliente-service` (9091)
2. `empleado-service` (9092)
3. `producto-service` (9093)
4. `pedido-service` (9094) — necesita a cliente y producto arriba
5. `asistencia-service` (9095) — necesita a empleado arriba
6. `envio-service` (9096) — necesita a cliente y pedido arriba
7. `auth-service` (9097)
8. `api-gateway` (9090) — al final, porque solo enruta hacia los demás

---

## 5. Ejecutar los tests automáticos

Antes de presentar, no está de más correr los tests de cada módulo para confirmar que compila y pasa:

**Windows:**
```bat
cd nombre-del-servicio
.\mvnw.cmd test
```

**Mac / Linux:**
```bash
cd nombre-del-servicio
./mvnw test
```

(Repite cambiando de carpeta para cada uno de los 8 servicios, o usa `build-all`, que ya compila — `test` solo es necesario si quieres ver el detalle de los `@Test`.)

---

## 6. Autenticarse: registrar usuario y obtener el token

Todo pasa por el **gateway, puerto 9090**. `/auth/**` es la única ruta pública; todo lo demás exige el token.

### 6.1 Registrar usuario
`POST http://localhost:9090/auth/register`

Body (JSON):
```json
{
  "email": "hola@ejemplo.com",
  "password": "123123"
}
```

### 6.2 Iniciar sesión (obtener el token)
`POST http://localhost:9090/auth/login`

Mismo body que arriba. La respuesta es:
```json
{
  "status": "ok",
  "token": "eyJhbGciOiJIUzI1NiJ9....."
}
```

### 6.3 Activar el token en ModHeader
Copia el valor de `"token"` y pégalo en ModHeader (paso 2), quedando así:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.....
```

Desde este momento, cualquier endpoint protegido (`/clientes`, `/productos`, `/pedidos`, etc.) funcionará al navegarlo o probarlo a través del puerto **9090**.

> 💡 Tip para la demo en vivo: antes de loguearte, intenta abrir `http://localhost:9090/clientes` — te va a tirar 401/403. Eso demuestra que la seguridad del gateway funciona. Después de pegar el token en ModHeader, la misma URL responde con datos. Es una buena forma de mostrarle al profesor que el JWT realmente está protegiendo las rutas.

---

## 7. Explorar la API con Swagger

Hay dos formas. La dirección que tenías apuntada (`http://localhost:9090/doc/swagger-ui.html`) **no existe** así en el gateway — el gateway no tiene su propio Swagger, solo enruta hacia el de cada microservicio. Usa una de estas dos en su lugar:

### Opción A — Directo a cada servicio (la más simple, NO pide token)
Como la seguridad JWT solo vive en el api-gateway, si entras directo al puerto del microservicio no te pide login. Ideal para probar CRUD rápido.

| Servicio | URL Swagger |
|---|---|
| auth-service | `http://localhost:9097/doc/swagger-ui.html` |
| cliente-service | `http://localhost:9091/doc/swagger-ui.html` |
| empleado-service | `http://localhost:9092/doc/swagger-ui.html` |
| producto-service | `http://localhost:9093/doc/swagger-ui.html` |
| pedido-service | `http://localhost:9094/doc/swagger-ui.html` |
| asistencia-service | `http://localhost:9095/doc/swagger-ui.html` |
| envio-service | `http://localhost:9096/doc/swagger-ui.html` |

### Opción B — A través del Gateway (puerto 9090, SÍ pide token vía ModHeader)
Sirve para demostrar la arquitectura completa con seguridad. Nota que la ruta usa `/doc/swagger-ui/index.html` (con `/index.html`, no `.html` solo) y el nombre del servicio como prefijo:

```
http://localhost:9090/cliente-service/doc/swagger-ui/index.html
http://localhost:9090/empleado-service/doc/swagger-ui/index.html
http://localhost:9090/producto-service/doc/swagger-ui/index.html
http://localhost:9090/pedido-service/doc/swagger-ui/index.html
http://localhost:9090/asistencia-service/doc/swagger-ui/index.html
http://localhost:9090/envio-service/doc/swagger-ui/index.html
http://localhost:9090/auth-service/doc/swagger-ui/index.html
```

Con ModHeader activado (paso 6.3), tanto la página como los botones "Try it out" funcionarán porque el header viaja con cada llamada al puerto 9090.

---

## 8. Referencia rápida de endpoints (para no andar adivinando en Postman)

> Todos se piden con el prefijo del Gateway `http://localhost:9090...` (o directo al puerto del servicio sin prefijo extra, ver tabla del punto 0). Los que dicen "v2" devuelven el mismo recurso pero con formato HATEOAS (con links).

**auth-service** (`/auth`, público)
```
POST   /auth/register
POST   /auth/login
GET    /auth/user/{email}/exists
```

**cliente-service** (`/clientes`)
```
POST   /clientes
GET    /clientes
GET    /clientes/{id}
PUT    /clientes/{id}
DELETE /clientes/{id}
GET    /clientes/total
GET    /clientes/correo/{correo}
GET    /clientes/nombre/{nombre}
GET    /clientes/{id}/exists
GET    /clientes/v2          (HATEOAS)
```

Ejemplo de body para crear cliente:
```json
{
  "rutCliente": "12345678-9",
  "nombre": "Juan Perez",
  "correo": "juan@correo.com",
  "telefono": "+56912345678",
  "direccion": "Calle Falsa 123"
}
```

**empleado-service** (`/empleados`)
```
POST   /empleados
GET    /empleados
GET    /empleados/{id}
GET    /empleados/rut/{rut}
GET    /empleados/cargo/{cargo}
GET    /empleados/activos
PUT    /empleados/{id}
DELETE /empleados/{id}
GET    /empleados/v2          (HATEOAS)
```

**producto-service** (`/productos`)
```
POST   /productos
GET    /productos
GET    /productos/{id}
GET    /productos/{id}/detalles
GET    /productos/{id}/exists
PUT    /productos/{id}
DELETE /productos/{id}
GET    /productos/v2          (HATEOAS)
```

**pedido-service** (`/pedidos`)
```
POST   /pedidos
GET    /pedidos
GET    /pedidos/{id}
GET    /pedidos/cliente/{idCliente}
GET    /pedidos/total
PUT    /pedidos/{id}
DELETE /pedidos/{id}
GET    /pedidos/v2          (HATEOAS)
```

**asistencia-service** (`/asistencias`)
```
POST   /asistencias
GET    /asistencias
GET    /asistencias/{id}
GET    /asistencias/empleado/{idEmpleado}
GET    /asistencias/fecha/{fecha}
GET    /asistencias/empleado/{idEmpleado}/fecha/{fecha}
PUT    /asistencias/{id}
DELETE /asistencias/{id}
GET    /asistencias/v2          (HATEOAS)
```

**envio-service** (`/envios`)
```
POST   /envios
GET    /envios
GET    /envios/{id}
GET    /envios/cliente/{idCliente}
GET    /envios/pedido/{idPedido}
PUT    /envios/{id}/estado
DELETE /envios/{id}
GET    /envios/v2          (HATEOAS)
```

---

## 9. Limpiar todo (antes de re-presentar o cuando algo se traba)

Hay dos niveles de limpieza: **código/Docker** y **bases de datos**. Normalmente con el primero te alcanza.

### 9.1 Limpieza de Docker y carpetas `target` (no toca las bases de datos)

**Windows:**
```bat
clean-all.bat
```
**Mac / Linux:**
```bash
./clean-all.sh
```

Hace, en orden: 1) `docker compose down --remove-orphans`, 2) borra imágenes `pasteleria-*`, 3) borra la carpeta `target` de cada uno de los 8 módulos, 4) `docker volume prune -f`. Queda todo listo para volver a correr `build-all`.

### 9.2 Reseteo total de datos (borra TODA la información, vuelve a foja cero)

Solo si quieres que los datos de prueba se vuelvan a cargar desde Liquibase tal como el primer día. Ejecuta esto en MySQL Workbench / consola `mysql -u root`:

```sql
DROP DATABASE bd_cliente;
DROP DATABASE bd_envio;
DROP DATABASE bd_pedidos;
DROP DATABASE bd_producto;
DROP DATABASE bd_users;
DROP DATABASE bd_empleado;
DROP DATABASE bd_asistencia;
```

Después de soltar las bases, vuelve a correr `build-all` (o `mvnw spring-boot:run` en cada servicio): cada Spring Boot las vuelve a crear vacías y Liquibase las llena de nuevo con los datos de prueba.

> ⚠️ Esto borra TODO: clientes, productos, pedidos, usuarios registrados, etc. Hazlo solo si quieres reiniciar la demo desde cero, no minutos antes de presentar salvo que sea intencional.

---

## 10. ✅ Checklist mínimo para el día de la presentación

Copy-paste en este orden exacto:

1. ☐ Abrir MySQL (Workbench o el servicio) y confirmar que está corriendo en el puerto 3306.
2. ☐ Abrir Docker Desktop.
3. ☐ En la raíz del proyecto: `build-all.bat` (Windows) o `./build-all.sh` (Mac/Linux).
4. ☐ Esperar a que los 8 servicios digan "Started" en consola.
5. ☐ `POST http://localhost:9090/auth/register` con el JSON de ejemplo (paso 6.1).
6. ☐ `POST http://localhost:9090/auth/login` → copiar el `token`.
7. ☐ Pegar `Bearer <token>` en ModHeader (filtro `.*localhost:9090.*`, perfil activado).
8. ☐ Probar `http://localhost:9090/clientes` (o cualquier otro) para confirmar que responde con datos.
9. ☐ Mostrar Swagger directo en algún puerto de servicio (Opción A, paso 7) para las pruebas en vivo de CRUD.
10. ☐ Si algo se traba a mitad de la demo: `clean-all.bat` → `build-all.bat` de nuevo.

---

## 11. Cómo incorporar el proyecto nuevo del profesor

Como todavía no sabemos qué trae ese proyecto, esta es la receta general para integrarlo siguiendo exactamente la misma estructura que ya usa este repo (así queda "consolidado" y no se nota el pegado):

1. **Decide qué es:** ¿es un microservicio nuevo (otra entidad/dominio) o son cambios sobre uno que ya existe (otro controller, otro campo)? Eso cambia el resto de los pasos.

2. **Si es un microservicio nuevo:**
   - Copia su carpeta a la raíz del proyecto, al mismo nivel que `cliente-service`, `producto-service`, etc.
   - Asígnale un puerto libre siguiente en la serie (el último usado es `9097`, así que el nuevo podría ser `9098`).
   - Crea su base de datos propia `bd_nombre` y configura `application.yml`/`application.properties` con el mismo patrón de los demás: perfil `dev` → `jdbc:mysql://localhost:3306/bd_nombre?createDatabaseIfNotExist=true`; perfil `docker` → `jdbc:mysql://host.docker.internal:3306/bd_nombre?createDatabaseIfNotExist=true`.
   - Reordena el código para que respete los mismos paquetes que usa el resto: `model`, `dto`, `repository`, `service`, `controller`, `exception`, `config`, `assemblers` (si quieres que tenga también versión v2 con HATEOAS).
   - Agrégale `springdoc-openapi-starter-webmvc-ui` al `pom.xml` y el bloque `springdoc.swagger-ui.path: /doc/swagger-ui.html` en el `application.yml`, igual que los demás, para que el Swagger directo le funcione igual.

3. **Registra el nuevo servicio en `docker-compose.yml`** (raíz del proyecto), copiando el bloque de cualquier servicio existente y cambiando `build`, `ports` y `depends_on` según corresponda.

4. **Agrégalo a `build-all.bat` / `build-all.sh` y a `clean-all.bat` / `clean-all.sh`**, sumando su nombre de carpeta a la lista de servicios que ya están ahí, para que se compile/limpie junto con los demás.

5. **Dale ruta en el api-gateway** (`api-gateway/src/main/resources/application.yml`): copia uno de los bloques de ruta existentes (por ejemplo el de `producto-service`) y reemplaza el `id`, `uri` y `Path` por los del servicio nuevo, agregando también sus dos rutas extra de `v3/api-docs` y `doc/swagger-ui`.

6. **Si el nuevo servicio necesita llamar a otro ya existente** (por ejemplo, valida que un cliente exista), copia el patrón `WebClientConfig.java` + DTO espejo que ya usan `pedido-service` o `envio-service` para llamar a `cliente-service`.

7. **Si tiene datos de prueba con IDs que referencian otro servicio** (como pasó con `pedido-service` referenciando productos), revisa que esos IDs realmente existan en el otro servicio — si no, el `changelog.sql` de Liquibase falla en silencio y simplemente no inserta nada (ese bug ya se dio en este proyecto, revisa `ARREGLOS_REALIZADOS.md` si quieres ver cómo se solucionó la última vez).

8. **Compila y corre solo ese servicio primero** (`mvnw spring-boot:run`) antes de meterlo al `build-all`, para depurarlo más rápido sin reconstruir Docker entero cada vez.

9. **Una vez que funciona suelto, corre `build-all` completo** para confirmar que conviven todos juntos, y prueba su Swagger y sus rutas a través del gateway con ModHeader, igual que el resto.

---

## 12. Solución de problemas comunes

| Problema | Causa probable | Solución |
|---|---|---|
| 401/403 al llamar `/clientes` (u otro) por el puerto 9090 | Token vencido o ModHeader apagado | Vuelve a loguearte (paso 6) y actualiza el valor en ModHeader |
| "No se puede conectar a MySQL" al iniciar un servicio | MySQL no está corriendo, o corre en otro puerto | Verifica `mysql -u root -e "SELECT 1;"` y que escuche en 3306 |
| Un servicio dice "Address already in use" en su puerto | Quedó un proceso anterior corriendo (o un contenedor viejo) | Cierra esa terminal/proceso, o `docker compose down`, y reintenta |
| `pedido-service` o `envio-service` no traen datos de prueba | Liquibase insertó registros con IDs que no existen en otro servicio (ya pasó una vez) | Revisa que ese servicio dependiente esté arriba antes de levantar este, o mira `ARREGLOS_REALIZADOS.md` |
| Swagger vía gateway da 404 | Falta el `/index.html` al final, o falta el nombre del servicio como prefijo | Usa exactamente `http://localhost:9090/<servicio>/doc/swagger-ui/index.html` |
| `.\mvnw.cmd` falla con "MAVEN_HOME is not defined" o error de versión de Java | Java mal configurado en el PATH | Confirma `java -version` → debe decir 21; si no, instala/selecciona el JDK 21 |
| Quiero ver qué hay en una base de datos sin entrar a Workbench | — | `mysql -u root bd_producto -e "SELECT * FROM producto;"` (cambia el nombre de la BD/tabla) |

---

**Resumen en una frase:** MySQL local arriba → `build-all` → registrarse y loguearse en `/auth` por el puerto 9090 → pegar el token en ModHeader → navegar `/clientes`, `/productos`, etc. por el 9090, o usar Swagger directo en cada puerto de servicio para probar CRUD sin token → `clean-all` cuando quieras reiniciar.
