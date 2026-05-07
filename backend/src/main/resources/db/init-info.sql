-- Tabla de candidatos
CREATE TABLE candidatos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    numero VARCHAR(50) NOT NULL UNIQUE,
    partido_id BIGINT,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de electores
CREATE TABLE electores (
    id BIGSERIAL PRIMARY KEY,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    apellido VARCHAR(200),
    mesa_id BIGINT,
    puede_votar BOOLEAN DEFAULT true,
    ya_voto BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de mesas
CREATE TABLE mesas (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    centro_votacion_id BIGINT,
    activa BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_electores_cedula ON electores(cedula);
CREATE INDEX idx_candidatos_numero ON candidatos(numero);
CREATE INDEX idx_mesas_numero ON mesas(numero);