-- Tabla de votos (append-only)
CREATE TABLE votos (
    id BIGSERIAL PRIMARY KEY,
    id_candidato BIGINT NOT NULL,
    id_elector VARCHAR(20) NOT NULL,
    id_mesa BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dispositivo_id VARCHAR(100),
    hash VARCHAR(256)
);

-- Índices
CREATE INDEX idx_votos_elector ON votos(id_elector);
CREATE INDEX idx_votos_candidato ON votos(id_candidato);
CREATE INDEX idx_votos_mesa ON votos(id_mesa);
CREATE INDEX idx_votos_timestamp ON votos(timestamp);

-- Ver (no permitir UPDATE/DELETE)
CREATE VIEW votos_audit AS
SELECT * FROM votos;
