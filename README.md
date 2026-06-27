# Sistema Bancario ("DEV SU BANCO")

Este es un sistema bancario que consta de una API REST Backend construida con **Java Spring Boot y Gradle**, una aplicación Web Frontend construida con **Angular**, y una base de datos relacional **PostgreSQL**. La solución está completamente dockerizada para facilitar su despliegue y pruebas.

---

## 1. Arquitectura del Sistema

### Backend (Java Spring Boot)
El backend utiliza una **Arquitectura en Capas (Layered Architecture)** para garantizar una separación limpia de responsabilidades y facilitar el cumplimiento de los principios SOLID:
* **Controladores (`/api/clientes`, `/api/cuentas`, `/api/movimientos`, `/api/reportes`)**: Reciben peticiones HTTP, validan los parámetros y mapean objetos a DTOs.
* **Servicios (Lógica de Negocio)**: Implementa los cálculos de saldo, validación de retiros contra saldo ("Saldo no disponible") y validación de límites diarios acumulados por cliente de $1000 ("Cupo diario Excedido"). Modela el patrón **Strategy** para la entrega de reportes (JSON directo y PDF codificado en Base64).
* **Repositorios**: Interfaces que heredan de `JpaRepository` para la comunicación con la base de datos PostgreSQL mediante Hibernate/JPA. Implementa el patrón **Repository**.
* **Entidades**: Modelos relacionales JPA. Se configuró herencia con estrategia `JOINED` para la relación `Persona` -> `Cliente`.

### Frontend (Angular)
El frontend implementa una **Arquitectura Basada en Componentes y Servicios (Component-Based & Service-Oriented)**:
* **Componentes**: Controlan el ciclo de vida de la vista, formulan búsquedas rápidas en tablas, abren popups de formularios CRUD y muestran alertas/mensajes de error provenientes de la validación del backend.
* **Servicios**: Clases Singleton inyectables que encapsulan las peticiones HTTP REST de forma reactiva utilizando **RxJS** (Observables).
* **Estilos (Vanilla CSS)**: Hojas de estilos en CSS nativo estructuradas con variables de color modernas, sombras suaves, transiciones y un diseño responsivo de primer nivel (sin frameworks como Bootstrap ni Tailwind).

---

## 2. Instrucciones de Despliegue con Docker (Recomendado)

Para levantar todo el entorno de forma automática (Base de Datos PostgreSQL, API Backend en puerto 8080 y Frontend Web en puerto 80):

1. Asegúrate de tener instalado **Docker** y **Docker Compose**.
2. Ubícate en el directorio raíz del proyecto (`E:\devs\apps\devsu`).
3. Ejecuta el comando:
   ```bash
   docker-compose up --build
   ```
4. El contenedor de base de datos se inicializará y ejecutará automáticamente el archivo `BaseDatos.sql`, creando el esquema de tablas e insertando los registros semilla de los casos de uso.
5. Abre tu navegador e ingresa a: **`http://localhost`** para interactuar con la aplicación.

> [!IMPORTANT]
> **Nota sobre la Zona Horaria (Timezone):**
> Por defecto, los contenedores del backend y de la base de datos están configurados en la zona horaria `America/Guayaquil` (Ecuador, UTC-5) en el archivo `docker-compose.yml`. Esto asegura que los timestamps de las transacciones y las búsquedas por rangos de fecha en el módulo de reportes coincidan exactamente con la hora local de ejecución. Si se requiere probar en otra zona horaria, se puede modificar el parámetro `TZ` en `docker-compose.yml`.

---

## 3. Pruebas Unitarias e Integración

### Pruebas del Backend
Para ejecutar las pruebas del endpoint REST (que comprueban mediante MockMvc las excepciones de cupo diario excedido y saldo no disponible):
```bash
cd backend
./gradlew test
```

### Pruebas del Frontend
Para correr las pruebas unitarias del cliente Angular (con Jasmine/Karma):
```bash
cd frontend
npm run test
```

---

## 4. Pruebas de API con Postman
1. Abre Postman e importa el archivo **`Banco.postman_collection.json`** que se encuentra en la raíz del proyecto.
2. Encontrarás carpetas con peticiones listas para validar el CRUD de cada recurso.
3. Se incluyen escenarios de éxito y escenarios controlados de error que devuelven estados HTTP 400 Bad Request y mensajes legibles de negocio:
   * **Saldo no disponible**: Se genera cuando un retiro supera el saldo disponible.
   * **Cupo diario Excedido**: Se genera cuando el total acumulado de retiros de un cliente en el día excede los $1000.

---

## 5. Endpoints de la API REST (Servicios Web)

Todos los endpoints del backend se encuentran expuestos bajo el puerto **`8080`** y llevan el prefijo de contexto **`/api`**.

| Método | Endpoint | Request Body | Descripción |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/clientes` | - | Obtiene todos los clientes registrados. |
| **GET** | `/api/clientes/{id}` | - | Obtiene un cliente según su ID numérico. |
| **POST** | `/api/clientes` | `ClienteDTO` | Crea un nuevo cliente. Valida duplicidad de ID. |
| **PUT** | `/api/clientes/{id}` | `ClienteDTO` | Modifica datos de un cliente. |
| **DELETE** | `/api/clientes/{id}` | - | Elimina un cliente (restringido si posee cuentas). |
| **GET** | `/api/cuentas` | - | Obtiene todas las cuentas bancarias registradas. |
| **GET** | `/api/cuentas/{numero}` | - | Obtiene una cuenta mediante su número. |
| **POST** | `/api/cuentas` | `CuentaDTO` | Registra una cuenta. Valida unicidad de número. |
| **PUT** | `/api/cuentas/{numero}` | `CuentaDTO` | Actualiza la información de una cuenta. |
| **DELETE** | `/api/cuentas/{numero}` | - | Elimina una cuenta (restringido si tiene movimientos). |
| **GET** | `/api/movimientos` | - | Obtiene el historial total de movimientos. |
| **GET** | `/api/movimientos/{id}` | - | Consulta un movimiento según su ID. |
| **POST** | `/api/movimientos` | `MovimientoDTO` | Crea un depósito (+) o retiro (-). Controla saldo y cupo diario. |
| **PUT** | `/api/movimientos/{id}` | `MovimientoDTO` | Modifica un movimiento y recalcala en cascada. |
| **DELETE** | `/api/movimientos/{id}` | - | Elimina un movimiento y recalcala en cascada. |
| **GET** | `/api/reportes` | - | Genera el estado de cuenta plano para Devsu (`?fecha=...&cliente=...`). |
| **GET** | `/api/reportes/pdf` | - | Genera el estado de cuenta en JSON y PDF Base64 (`?fecha=...&cliente=...`). |

