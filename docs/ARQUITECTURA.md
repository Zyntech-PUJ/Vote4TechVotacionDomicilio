# Arquitectura - Vote4Tech Votación Domiciliaria

## Componentes

### Backend (Spring Boot)
- Puerto: 8080
- Framework: Spring Boot 3.2
- Base de datos: PostgreSQL (2 instancias)

### Frontend (Kotlin Android)
- API: Retrofit
- Estado: ViewModel + LiveData
- Configuración: Shared Preferences

### Infraestructura (Docker)
- BD Votos: PostgreSQL en puerto 5432
- BD Info: PostgreSQL en puerto 5433
- Backend: Spring Boot en puerto 8080

## Flujo

Celular Android (Kotlin)
↓ HTTP REST
Spring Boot API
├── (Write) → BD Votos
└── (Read) → BD Información

## Endpoints principales

- POST /api/votos - Registrar voto
- GET /api/candidatos - Obtener candidatos
- GET /api/electores/{cedula} - Validar elector
- GET /api/config - Obtener configuración

# Setup Local

## Requisitos
- Docker Desktop
- Java 17+
- Android Studio

## Levantar infraestructura

docker compose up -d

## Verifica

docker ps
docker compose logs backend

## Acceder a las BDs

# BD Votos
psql -h localhost -p 5432 -U admin -d votacion_votos

# BD Información
psql -h localhost -p 5433 -U admin -d votacion_info

## Desarrollar Backend

cd backend
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

Backend disponible en:
http://localhost:8080

## Desarrollar Frontend

1. Abre Android Studio
2. Open project → frontend/
3. Espera a que sincronice gradle
4. Run en emulador

# API Documentation

## Base URL

http://192.168.x.x:8080/api (local)
http://localhost:8080/api (desarrollo)

## Endpoints

### Votos

POST /votos

Registra un voto

{
  "id_candidato": 1,
  "id_elector": "123456789",
  "id_mesa": 1,
  "dispositivo_id": "device-001"
}

Response:
201 Created

### Candidatos

GET /candidatos

Obtiene lista de candidatos

Response:

[
  {
    "id": 1,
    "nombre": "Candidato 1",
    "numero": "1",
    "partido_id": 1,
    "activo": true
  }
]

### Electores

GET /electores/{cedula}

Valida un elector

Response:

{
  "id": 1,
  "cedula": "123456789",
  "nombre": "Juan",
  "puede_votar": true,
  "ya_voto": false
}