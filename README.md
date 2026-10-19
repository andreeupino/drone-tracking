# Drone Tracking

Aplicación web para visualizar en un mapa las detecciones y trayectorias de drones en tiempo real. Un simulador envía detecciones mediante WebSocket al backend, que las valida, las almacena en MongoDB y las retransmite a los clientes conectados. Al cargar la aplicación, el recorrido disponible se recupera mediante la API REST.

## Stack tecnológico

- **Backend:** Java, Spring Boot (Web, WebSocket, Data MongoDB y Validation)
- **Frontend:** Angular y Leaflet
- **Base de datos:** MongoDB
- **Simulador:** Java

## Estructura del proyecto

```text
drone-tracking/
├── backend/      # API REST y servidor WebSocket
├── frontend/     # Aplicación Angular y mapa interactivo
└── simulator/    # Simulador de detecciones
```

## Requisitos

- JDK compatible con la versión configurada en el backend.
- Node.js y npm.
- Angular CLI.
- Docker para ejecutar MongoDB (o una instalación local de MongoDB).

## Puesta en marcha

Inicia cada componente en una terminal independiente, siguiendo este orden.

### 1. Base de datos

Si todavía no existe el contenedor:

```bash
docker run -d -p 27017:27017 --name mongo mongo:7
```

Si el contenedor ya existe y está detenido:

```bash
docker start mongo
```

### 2. Backend

Desde la raíz del proyecto:

```bash
cd backend
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

El backend estará disponible en `http://localhost:8080`.

**Configuración:** asegúrate de que el token de ingesta configurado en el backend coincide con el que utiliza el simulador.

### 3. Frontend

Desde la raíz del proyecto:

```bash
cd frontend
npm install
npx ng serve
```

La aplicación estará disponible en `http://localhost:4200`.

### 4. Simulador

Desde la raíz del proyecto:

```bash
cd simulator
java Simulator.java
```

El simulador enviará detecciones de tres drones al backend mediante WebSocket.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| HTTP | `GET /api/flights/active` | Recupera las detecciones de los drones con actividad reciente. |
| WebSocket | `/ws/ingest` | Recibe detecciones y requiere la cabecera `X-Ingest-Token`. |
| WebSocket | `/ws/live` | Transmite nuevas detecciones a los clientes conectados. |
