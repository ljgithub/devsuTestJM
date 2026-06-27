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

---

## 3. Ejecución en Modo Desarrollo (Local)

Si prefieres ejecutar cada componente de forma independiente en tu máquina local:

### Requisitos previos
* **Node.js**: v22.22.3 o v24+
* **Java Development Kit (JDK)**: versión 17
* **PostgreSQL**: Instalado y corriendo en el puerto 5432.

### A. Inicializar la Base de Datos
1. Crea una base de datos vacía llamada `banco_db` en tu instancia de PostgreSQL.
2. Ejecuta el script `BaseDatos.sql` sobre la base de datos para crear las tablas e insertar las semillas de prueba:
   ```bash
   psql -U postgres -d banco_db -f BaseDatos.sql
   ```

### B. Ejecutar el Backend (Spring Boot + Gradle)
1. Navega a la carpeta `backend/`.
2. Compila y ejecuta la aplicación utilizando el Gradle wrapper:
   ```bash
   ./gradlew bootRun
   ```
3. El servidor iniciará en `http://localhost:8080/api`.

### C. Ejecutar el Frontend (Angular)
1. Navega a la carpeta `frontend/`.
2. Instala las dependencias y corre el servidor de desarrollo de Angular:
   ```bash
   npm install
   npm run start
   ```
3. La aplicación estará accesible en `http://localhost:4200`.

---

## 4. Pruebas Unitarias e Integración

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

## 5. Pruebas de API con Postman
1. Abre Postman e importa el archivo **`Banco.postman_collection.json`** que se encuentra en la raíz del proyecto.
2. Encontrarás carpetas con peticiones listas para validar el CRUD de cada recurso.
3. Se incluyen escenarios de éxito y escenarios controlados de error que devuelven estados HTTP 400 Bad Request y mensajes legibles de negocio:
   * **Saldo no disponible**: Se genera cuando un retiro supera el saldo disponible.
   * **Cupo diario Excedido**: Se genera cuando el total acumulado de retiros de un cliente en el día excede los $1000.
