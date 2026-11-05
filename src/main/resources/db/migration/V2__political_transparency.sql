-- ============================================================================
-- V2__political_transparency.sql - Tables for Deputies, Senators, Bills & Expenses
-- ============================================================================

CREATE TABLE IF NOT EXISTS parlamentares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    casa_legislativa VARCHAR(30) NOT NULL,
    partido VARCHAR(30) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    id_externo VARCHAR(60),
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    INDEX idx_parlamentar_partido_uf (partido, uf)
);

CREATE TABLE IF NOT EXISTS projetos_lei (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    ementa TEXT NOT NULL,
    texto_integral TEXT,
    status_tramitacao VARCHAR(100) NOT NULL,
    parlamentar_autor_id BIGINT,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_pl_autor FOREIGN KEY (parlamentar_autor_id) REFERENCES parlamentares(id)
);

CREATE TABLE IF NOT EXISTS despesas_parlamentares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parlamentar_id BIGINT NOT NULL,
    categoria_despesa VARCHAR(150) NOT NULL,
    fornecedor VARCHAR(200) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    data_despesa DATE NOT NULL,
    url_documento VARCHAR(254),
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_despesa_parlamentar FOREIGN KEY (parlamentar_id) REFERENCES parlamentares(id),
    INDEX idx_despesa_parlamentar (parlamentar_id)
);

