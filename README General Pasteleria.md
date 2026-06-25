# 🎂 Sistema de Gestión de Pastelería - Arquitectura de Microservicios

Este repositorio contiene la documentación técnica y el código fuente del sistema integral de gestión para una pastelería automatizada. El proyecto está diseñado bajo un enfoque de **Arquitectura de Microservicios** altamente desacoplados, escalables y orientados al dominio, utilizando **Java 21 / 17**, **Spring Boot 4.0.5**, **Liquibase** para el control de versiones de bases de datos y **Docker** para la orquestación de contenedores.

---

## 🗺️ Arquitectura General del Sistema

El ecosistema de la aplicación está compuesto por un **API Gateway** unificado como único punto de entrada perimetral y un conjunto de **7 microservicios de negocio y soporte** que se comunican entre sí de forma síncrona mediante **Spring WebClient**, incluyendo políticas de tolerancia a fallos (*fallback*).

```
                      ┌────────────────────────────────────────┐
                      │       API GATEWAY (Puerto 9090)        │
                      └───────────────────┬────────────────────┘
                                          │
       ┌──────────────┬───────────────┼───────────────┬──────────────┬──────────────┐
       ▼              ▼               ▼               ▼              ▼              ▼
┌────────────┐ ┌────────────┐  ┌────────────┐  ┌────────────┐ ┌────────────┐ ┌────────────┐
│    Auth    │ │  Cliente   │  │  Producto  │  │   Pedido   │ │   Envío    │ │  Empleado  │
│  Service   │ │  Service   │  │  Service   │  │  Service   │ │  Service   │ │  Service   │
│   (9097)   │ │   (9091)   │  │   (9093)   │  │   (9094)   │ │   (9096)   │ │   (9092)   │
└─────┬──────┘ └─────┬──────┘  └─────┬──────┘  └─────┬──────┘ └─────┬──────┘ └─────┬──────┘
      │              │               │               │              │              │
┌─────┴──────┐ ┌─────┴──────┐  ┌─────┴──────┐  ┌─────┴──────┐ ┌─────┴──────┐ ┌─────┴──────┐
│  bd_auth   │ │ bd_cliente │  │ bd_producto│  │ bd_pedido  │ │  bd_envio  │ │ bd_empleado│
└────────────┘ └────────────┘  └────────────┘  └────────────┘ └────────────┘ └─────┬──────┘
                                                                                   │
                                                                             ┌─────▼──────┐
                                                                             │ Asistencia │
                                                                             │  Service   │
                                                                             │   (9095)   │
                                                                             └─────┬──────┘
                                                                             ┌─────▼──────┐
                                                                             │bd_asistencia│
                                                                             └────────────┘
```

---

## 🧰 Stack Tecnológico Completo

* **Lenguaje de Programación:** Java 21 / 17 (LTS)
* **Framework Principal:** Spring Boot 4.0.5 & Spring Cloud Gateway
* **Gestión de Dependencias:** Maven 3.x
* **Persistencia de Datos:** Spring Data JPA / Hibernate
* **Motores de Base de Datos:** MySQL 8.0 (Producción/Docker) & H2 Database (Entorno de Pruebas)
* **Evolución del Esquema:** Liquibase 4.29.2
* **Contenedorización:** Docker & Docker Compose
* **Pruebas Unitarias y de Integración:** JUnit 5 (Jupiter), AssertJ, Mockito

---

## 📁 Catálogo de Microservicios y Puertos

| Microservicio | Puerto | Base de Datos | Descripción Funcional |
| :--- | :---: | :--- | :--- |
| `api-gateway` | **9090** | *Ninguna* | Punto de acceso único. Enrutamiento dinámico y seguridad perimetral. |
| `auth-service` | **9097** | `bd_auth` | Proveedor de identidad. Autenticación y emisión/validación de tokens JWT. |
| `cliente-service` | **9091** | `bd_cliente` | Directorio maestro de clientes (RUT, datos de contacto y direcciones). |
| `producto-service` | **9093** | `bd_producto` | Catálogo de productos terminados, insumos y gestión estricta de alérgenos. |
| `pedido-service` | **9094** | `bd_pedido` | Motor transaccional de compras. Gestión de líneas de pedidos y cálculo de totales. |
| `asistencia-service`| **9095** | `bd_asistencia`| Marcaje de jornadas laborales y cálculo de liquidaciones de horas extras. |
| `envio-service` | **9096** | `bd_envio` | Gestión logística, asignación de despachos y tracking de entregas en tiempo real. |
| `empleado-service` | **9092** | `bd_empleado` | Maestro del personal interno, cargos, sueldos base y contratos. |

---

## 🗄️ Diseño y Relaciones de Modelos de Datos

### 1. Gestión de Alérgenos (`producto-service`)
Implementa una relación **OneToOne** bidireccional limpia entre el producto y su ficha técnica médica, garantizando la eliminación atómica en cascada:
* **Producto:** `id`, `nombre`, `precio`, `stock`.
* **DetalleProducto:** `descripcion`, `contiene_lactosa` (boolean), `contiene_huevos` (boolean), `contiene_frutos_secos` (boolean), `contiene_gluten` (boolean), `alergenos` (textual), `instrucciones_almacenamiento`, `presentacion`.
* *Configuración JPA:* `CascadeType.ALL` y `orphanRemoval = true`.

### 2. Estructura Transaccional (`pedido-service`)
Soporta el flujo de ventas mediante una composición **OneToMany** hacia los ítems del pedido:
* **Pedido:** `id`, `id_cliente`, `nombre_cliente` (recuperado reactivamente), `fecha`, `total`, `estado` (*PENDIENTE, CONFIRMADO, ENTREGADO, CANCELADO*).
* **DetallePedido:** `id`, `id_producto`, `nombre_producto`, `cantidad`, `precio_unitario`, `precio_total`.

### 3. Logística Dinámica (`envio-service`)
* **Envio:** `id`, `id_pedido`, `id_cliente`, `nombre_cliente`, `direccion_entrega`, `telefono`, `estado` (*PENDIENTE, EN_TRANSITO, ENTREGADO*), `costo_envio`.

---

## 🎯 Endpoints de la API (Acceso vía Gateway Puerto 9090)

### 🧁 Catálogo de Productos
* `POST /productos` - Registra un nuevo producto junto con su ficha de alérgenos.
* `GET /productos` - Obtiene la lista completa de pastelería (retorna por defecto 10 productos base precargados).
* `GET /productos/{id}` - Obtiene un producto por su clave primaria.
* `GET /productos/{id}/detalles` - Endpoint especializado para extraer de forma aislada las restricciones de salud y alérgenos del producto.
* `PUT /productos/{id}` - Modificación integral de datos comerciales y médicos.
* `DELETE /productos/{id}` - Remoción física/lógica del producto y su detalle estructurado.

### 🛒 Flujo de Pedidos
* `POST /pedidos` - Generación de órdenes de compra. Consume de forma interna `cliente-service` y `producto-service` para validar existencias, calcular precios y consolidar los totales.
* `GET /pedidos` - Recupera el histórico global de transacciones.
* `GET /pedidos/cliente/{clienteId}` - Filtra dinámicamente las compras asociadas a un cliente específico.

### 🚚 Logística y Despacho
* `POST /envios` - Instancia una orden de despacho vinculada a un pedido confirmado.
* `GET /envios/estado/{estado}` - Consulta optimizada para la indexación de despachos según su estado logístico (*PENDIENTE, EN_TRANSITO, ENTREGADO*).
* `GET /envios/cliente/{clienteId}` - Listado de entregas destinadas a un cliente en particular.

---

## 🔧 Refactorizaciones y Mejoras Críticas del Sistema

El código base fue sometido a una auditoría técnica profunda, resolviendo los siguientes incidentes:

1.  **Sincronización en Liquibase (`pedido-service`):** Se corrigió un error crítico en el archivo de migración `db.changelog.sql` donde los registros semilla de la tabla `detalle_pedido` intentaban apuntar a llaves foráneas de productos inexistentes (IDs del `101` al `116`). Se remapearon los inserts para consumir de forma cíclica los IDs válidos del `1` al `10` generados por `producto-service`, logrando una inicialización limpia de la base de datos.
2.  **Rendimiento en Base de Datos (`envio-service`):** Se eliminó una mala práctica arquitectónica en el controlador de envíos, la cual realizaba filtrados en memoria utilizando *Streams de Java* sobre colecciones globales (`.stream().filter(...)`). Esta lógica se delegó al motor de base de datos MySQL mediante la declaración explícitas de métodos de consulta derivados en `EnvioRepository` (`findByEstado` y `findByClienteId`), reduciendo drásticamente el consumo de CPU y memoria del microservicio.
3.  **Tolerancia a Fallos e Integridad de Datos:** Se implementaron bloques de contingencia `try-catch` alrededor de las llamadas externas HTTP efectuadas por `WebClient` encapsulando excepciones del tipo `ExternalServiceException`. En caso de indisponibilidad temporal de un microservicio, el sistema activa un comportamiento degradado controlado en lugar de propagar un error de servidor interno (500).

---

## ⚡ Automatización y Scripts de Control

Para facilitar la orquestación y el despliegue expedito de los 8 componentes, el proyecto dispone de scripts automatizados de consola para múltiples plataformas:

### Compilación y Construcción Integral
Compila los proyectos de forma secuencial omitiendo la suite de pruebas para acelerar el despliegue local, construye las imágenes Docker correspondientes y levanta el entorno de red unificado:
* **En entornos Linux / macOS:** `./build-all.sh`
* **En entornos Windows:** `.\build-all.bat`

### Depuración y Limpieza Absoluta
Detiene todos los contenedores en ejecución, remueve volúmenes persistidos, purga las imágenes exclusivas del negocio (`pasteleria-*`) y elimina de forma recursiva los directorios `/target` generados por Maven:
* **En entornos Linux / macOS:** `./clean-all.sh`
* **En entornos Windows:** `.\clean-all.bat`

---

## 🚀 Guía de Despliegue Rápido con Docker

### Requisitos Mínimos
* Docker Desktop con soporte para Docker Compose v2+.
* Kit de Desarrollo de Java (JDK) 17 o 21 configurado en la variable de entorno `JAVA_HOME`.

### Pasos para Inicializar el Cluster

1.  Clone el repositorio y posiciónese en la raíz del espacio de trabajo.
2.  Ejecute el script automatizado de construcción según su sistema operativo:
    ```bash
    ./build-all.sh
    ```
3.  Docker Compose levantará de forma ordenada las bases de datos, aplicará las migraciones de Liquibase y desplegará las instancias Java.
4.  **Verificación del Catálogo Base:** Puede comprobar el correcto funcionamiento del Gateway y la ingesta de datos iniciales realizando una consulta HTTP:
    ```bash
    curl http://localhost:9090/productos
    ```
    *(Debería obtener un arreglo JSON con los 10 productos iniciales de la pastelería, tales como tortas, kuchenes y postres con sus respectivos detalles de alérgenos)*.
