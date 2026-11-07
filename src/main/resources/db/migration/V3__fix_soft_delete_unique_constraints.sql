-- ============================================================================
-- V3__fix_soft_delete_unique_constraints.sql
-- Resolve conflict between soft-delete (deleted_at) and UNIQUE constraints in MySQL 8+
-- Uses a virtual generated column `is_active` (1 when deleted_at IS NULL, NULL when deleted).
-- In MySQL, multiple NULL values do not violate a UNIQUE index.
-- ============================================================================

-- 1. Usuarios: email
ALTER TABLE usuarios ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE usuarios DROP INDEX email;
CREATE UNIQUE INDEX uk_usuarios_email_active ON usuarios (email, is_active);

-- 2. Autores: nome_canal, link_canal
ALTER TABLE autores ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE autores DROP INDEX nome_canal;
ALTER TABLE autores DROP INDEX link_canal;
CREATE UNIQUE INDEX uk_autores_nome_canal_active ON autores (nome_canal, is_active);
CREATE UNIQUE INDEX uk_autores_link_canal_active ON autores (link_canal, is_active);

-- 3. Categorias: nome
ALTER TABLE categorias ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE categorias DROP INDEX nome;
CREATE UNIQUE INDEX uk_categorias_nome_active ON categorias (nome, is_active);

-- 4. Cursos: titulo, url_video
ALTER TABLE cursos ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE cursos DROP INDEX titulo;
ALTER TABLE cursos DROP INDEX url_video;
CREATE UNIQUE INDEX uk_cursos_titulo_active ON cursos (titulo, is_active);
CREATE UNIQUE INDEX uk_cursos_url_video_active ON cursos (url_video, is_active);

-- 5. Modulos: (curso_id, ordem)
ALTER TABLE modulos ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE modulos DROP INDEX uk_modulo_curso_ordem;
CREATE UNIQUE INDEX uk_modulos_curso_ordem_active ON modulos (curso_id, ordem, is_active);

-- 6. Aulas: url_video, (modulo_id, ordem)
ALTER TABLE aulas ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE aulas DROP INDEX url_video;
ALTER TABLE aulas DROP INDEX uk_aula_modulo_ordem;
CREATE UNIQUE INDEX uk_aulas_url_video_active ON aulas (url_video, is_active);
CREATE UNIQUE INDEX uk_aulas_modulo_ordem_active ON aulas (modulo_id, ordem, is_active);

-- 7. Matriculas: (usuario_id, curso_id)
ALTER TABLE matriculas ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE matriculas DROP INDEX uk_matricula_usuario_curso;
CREATE UNIQUE INDEX uk_matriculas_usuario_curso_active ON matriculas (usuario_id, curso_id, is_active);

-- 8. Progresso Aula: (usuario_id, aula_id)
ALTER TABLE progresso_aula ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE progresso_aula DROP INDEX uk_progresso_usuario_aula;
CREATE UNIQUE INDEX uk_progresso_usuario_aula_active ON progresso_aula (usuario_id, aula_id, is_active);

-- 9. Projetos de Lei: codigo
ALTER TABLE projetos_lei ADD COLUMN is_active TINYINT(1) GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) VIRTUAL;
ALTER TABLE projetos_lei DROP INDEX codigo;
CREATE UNIQUE INDEX uk_projetos_lei_codigo_active ON projetos_lei (codigo, is_active);
