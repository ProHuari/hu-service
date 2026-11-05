-- ============================================================================
-- V1__init.sql - Initial schema for Huari Platform (with auditing & soft-delete)
-- ============================================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(60) NOT NULL,
    tipo_perfil VARCHAR(20) NOT NULL,
    criado_em DATETIME(6) NOT NULL,
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS autores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_canal VARCHAR(100) NOT NULL UNIQUE,
    link_canal VARCHAR(254) NOT NULL UNIQUE,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS cursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(155) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    url_video VARCHAR(254) NOT NULL UNIQUE,
    categoria_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_curso_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_curso_autor FOREIGN KEY (autor_id) REFERENCES autores(id),
    INDEX idx_curso_categoria (categoria_id),
    INDEX idx_curso_autor (autor_id)
);

CREATE TABLE IF NOT EXISTS modulos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    ordem INT NOT NULL,
    curso_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_modulo_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT uk_modulo_curso_ordem UNIQUE (curso_id, ordem)
);

CREATE TABLE IF NOT EXISTS aulas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(155) NOT NULL,
    descricao TEXT NOT NULL,
    url_video VARCHAR(254) NOT NULL UNIQUE,
    ordem INT NOT NULL,
    duracao_em_minutos INT,
    modulo_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_aula_modulo FOREIGN KEY (modulo_id) REFERENCES modulos(id),
    CONSTRAINT uk_aula_modulo_ordem UNIQUE (modulo_id, ordem)
);

CREATE TABLE IF NOT EXISTS matriculas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_matricula DATE NOT NULL,
    status_matricula VARCHAR(30) NOT NULL,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_matricula_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_matricula_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT uk_matricula_usuario_curso UNIQUE (usuario_id, curso_id),
    INDEX idx_matricula_usuario (usuario_id),
    INDEX idx_matricula_curso (curso_id)
);

CREATE TABLE IF NOT EXISTS progresso_aula (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_aula VARCHAR(30) NOT NULL,
    data_conclusao DATETIME(6),
    usuario_id BIGINT NOT NULL,
    aula_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_progresso_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_progresso_aula FOREIGN KEY (aula_id) REFERENCES aulas(id),
    CONSTRAINT uk_progresso_usuario_aula UNIQUE (usuario_id, aula_id),
    INDEX idx_progresso_usuario (usuario_id),
    INDEX idx_progresso_aula (aula_id)
);

CREATE TABLE IF NOT EXISTS historico_ia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pergunta TEXT NOT NULL,
    resposta TEXT NOT NULL,
    data_consulta DATETIME(6) NOT NULL,
    usuario_id BIGINT NOT NULL,
    criado_em DATETIME(6),
    atualizado_em DATETIME(6),
    deleted_at DATETIME(6),
    CONSTRAINT fk_historico_ia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_historico_ia_usuario_data (usuario_id, data_consulta)
);

