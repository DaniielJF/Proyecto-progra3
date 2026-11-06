-- ============================================================
-- Sistema de Reserva de Recursos - Proyecto #2 (EIF206, 2026-II)
-- Script de creacion de la base de datos (MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS reservas_db CHARACTER SET utf8mb4;
USE reservas_db;

-- Usuarios: Administrador y Funcionario en una sola tabla, discriminados
-- por "rol". Para un Administrador, nombre y telefono quedan en NULL.
CREATE TABLE usuarios (
    id        VARCHAR(20)  NOT NULL,
    clave     VARCHAR(60)  NOT NULL,
    rol       VARCHAR(20)  NOT NULL,
    nombre    VARCHAR(100),
    telefono  VARCHAR(30),
    PRIMARY KEY (id)
);

CREATE TABLE categorias (
    id          VARCHAR(20)  NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE recursos (
    id           VARCHAR(20)  NOT NULL,
    categoria_id VARCHAR(20)  NOT NULL,
    descripcion  VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE reservas (
    id             VARCHAR(20)  NOT NULL,
    actividad      VARCHAR(150) NOT NULL,
    fecha          DATE         NOT NULL,
    hora_inicio    TIME         NOT NULL,
    hora_fin       TIME         NOT NULL,
    funcionario_id VARCHAR(20)  NOT NULL,
    estado         VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (funcionario_id) REFERENCES usuarios(id)
);

-- Relacion muchos-a-muchos: categorias solicitadas originalmente en la reserva
CREATE TABLE reserva_categoria (
    reserva_id   VARCHAR(20) NOT NULL,
    categoria_id VARCHAR(20) NOT NULL,
    PRIMARY KEY (reserva_id, categoria_id),
    FOREIGN KEY (reserva_id) REFERENCES reservas(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Relacion muchos-a-muchos: recursos especificos asignados a la reserva
CREATE TABLE reserva_recurso (
    reserva_id VARCHAR(20) NOT NULL,
    recurso_id VARCHAR(20) NOT NULL,
    PRIMARY KEY (reserva_id, recurso_id),
    FOREIGN KEY (reserva_id) REFERENCES reservas(id),
    FOREIGN KEY (recurso_id) REFERENCES recursos(id)
);

-- Usuario semilla para poder ingresar la primera vez
INSERT INTO usuarios (id, clave, rol, nombre, telefono)
VALUES ('admin', 'admin', 'ADMIN', NULL, NULL);
