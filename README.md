# Drone Tracking

Aplicación que muestra en un mapa los vuelos de drones en tiempo real. Un simulador envía detecciones por WebSocket al backend, que las valida, las guarda en MongoDB y las retransmite a los navegadores. Al recargar la página, el recorrido completo se recupera por API.

## Stack

- **Backend:** Java 25, Spring Boot 4 (Web, WebSocket, Data MongoDB, Validation)
- **Frontend:** Angular + Leaflet
- **Base de datos:** MongoDB
- **Simulador:** Java (un único archivo)

## Estructura

```
backend/      API REST + servidor WebSocket
frontend/     Angular + Leaflet
simulator/    Simulator.java
```

## Requisitos

- JDK 25
- Node.js y Angular CLI (`npm i -g @angular/cli`)
- MongoDB (o Docker)

## Cómo arrancarlo

Orden: MongoDB, backend, frontend y simulador, cada uno en una terminal.

```bash
# 1. MongoDB
docker run -d -p 27017:27017 --name mongo mongo:7

# 2. Backend (http://localhost:8080)
cd backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run

# 3. Frontend (http://localhost:4200)
cd frontend
npm install
ng serve

# 4. Simulador
cd simulator
java Simulator.java
```

Al abrir http://localhost:4200 se verán 3 drones moviéndose en el mapa.

## Endpoints

| Ruta | Descripción |
|---|---|
| `GET /api/flights/active` | Vuelos con detecciones en los últimos 2 minutos, con su recorrido |
| `WS /ws/ingest` | Entrada de detecciones (exige la cabecera `X-Ingest-Token`) |
| `WS /ws/live` | Detecciones en tiempo real para el frontend |
