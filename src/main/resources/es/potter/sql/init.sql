--Base de datos de Griffindor
CREATE DATABASE IF NOT EXISTS gryffindor;
USE gryffindor;

CREATE TABLE alumnos (
    id VARCHAR(8) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    curso INT CHECK (curso BETWEEN 1 AND 7),
    casa VARCHAR(20) NOT NULL,
    patronus VARCHAR(50)
);

INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES
('GRY00001', 'Harry', 'Potter', 5, 'Gryffindor', 'Ciervo'),
('GRY00002', 'Hermione', 'Granger', 5, 'Gryffindor', 'Nutria'),
('GRY00003', 'Ron', 'Weasley', 5, 'Gryffindor', 'Jack Russell Terrier'),
('GRY00004', 'Neville', 'Longbottom', 5, 'Gryffindor', null),
('GRY00005', 'Ginny', 'Weasley', 4, 'Gryffindor', 'Caballo');

--Base de datos de slytherin
CREATE DATABASE IF NOT EXISTS slytherin;
USE slytherin;

CREATE TABLE alumnos (
    id VARCHAR(8) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    curso INT CHECK (curso BETWEEN 1 AND 7),
    casa VARCHAR(20) NOT NULL,
    patronus VARCHAR(50)
);

INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES
('SLY00001', 'Draco', 'Malfoy', 5, 'Slytherin', null),
('SLY00002', 'Pansy', 'Parkinson', 5, 'Slytherin', null),
('SLY00003', 'Blaise', 'Zabini', 5, 'Slytherin', null),
('SLY00004', 'Millicent', 'Bulstrode', 5, 'Slytherin', null),
('SLY00005', 'Theodore', 'Nott', 5, 'Slytherin', null);

--Base de datos Ravenclaw
CREATE DATABASE IF NOT EXISTS ravenclaw;
 USE ravenclaw;

 CREATE TABLE alumnos (
     id VARCHAR(8) PRIMARY KEY,
     nombre VARCHAR(50) NOT NULL,
     apellidos VARCHAR(100) NOT NULL,
     curso INT CHECK (curso BETWEEN 1 AND 7),
     casa VARCHAR(20) NOT NULL,
     patronus VARCHAR(50)
 );

 INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES
 ('RAV00001', 'Luna', 'Lovegood', 4, 'Ravenclaw', 'Liebre'),
 ('RAV00002', 'Cho', 'Chang', 5, 'Ravenclaw', 'Cisne'),
 ('RAV00003', 'Padma', 'Patil', 5, 'Ravenclaw', null),
 ('RAV00004', 'Michael', 'Corner', 5, 'Ravenclaw', null),
 ('RAV00005', 'Terry', 'Boot', 5, 'Ravenclaw', null);

--Base de datos de Hufflepuff
CREATE DATABASE IF NOT EXISTS hufflepuff;
USE hufflepuff;

CREATE TABLE alumnos (
    id VARCHAR(8) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    curso INT CHECK (curso BETWEEN 1 AND 7),
    casa VARCHAR(20) NOT NULL,
    patronus VARCHAR(50)
);

INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES
('HUF00001', 'Cedric', 'Diggory', 6, 'Hufflepuff', null),
('HUF00002', 'Hannah', 'Abbott', 5, 'Hufflepuff', null),
('HUF00003', 'Ernie', 'Macmillan', 5, 'Hufflepuff', 'Jabalí'),
('HUF00004', 'Susan', 'Bones', 5, 'Hufflepuff', null),
('HUF00005', 'Justin', 'Finch-Fletchley', 5, 'Hufflepuff', 'Hurón');

--Base de datos Hogwarts
CREATE DATABASE IF NOT EXISTS hogwarts;
USE hogwarts;

CREATE TABLE alumnos (
    id VARCHAR(8) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    curso INT CHECK (curso BETWEEN 1 AND 7),
    casa VARCHAR(20) NOT NULL,
    patronus VARCHAR(50)
);

-- Datos consolidados
INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES
('HUF00001', 'Cedric', 'Diggory', 6, 'Hufflepuff', null),
('HUF00002', 'Hannah', 'Abbott', 5, 'Hufflepuff', null),
('HUF00003', 'Ernie', 'Macmillan', 5, 'Hufflepuff', 'Jabalí'),
('HUF00004', 'Susan', 'Bones', 5, 'Hufflepuff', null),
('HUF00005', 'Justin', 'Finch-Fletchley', 5, 'Hufflepuff', 'Hurón'),
 ('RAV00001', 'Luna', 'Lovegood', 4, 'Ravenclaw', 'Liebre'),
 ('RAV00002', 'Cho', 'Chang', 5, 'Ravenclaw', 'Cisne'),
 ('RAV00003', 'Padma', 'Patil', 5, 'Ravenclaw', null),
 ('RAV00004', 'Michael', 'Corner', 5, 'Ravenclaw', null),
 ('RAV00005', 'Terry', 'Boot', 5, 'Ravenclaw', null),
 ('SLY00001', 'Draco', 'Malfoy', 5, 'Slytherin', null),
 ('SLY00002', 'Pansy', 'Parkinson', 5, 'Slytherin', null),
 ('SLY00003', 'Blaise', 'Zabini', 5, 'Slytherin', null),
 ('SLY00004', 'Millicent', 'Bulstrode', 5, 'Slytherin', null),
 ('SLY00005', 'Theodore', 'Nott', 5, 'Slytherin', null),
('GRY00001', 'Harry', 'Potter', 5, 'Gryffindor', 'Ciervo'),
('GRY00002', 'Hermione', 'Granger', 5, 'Gryffindor', 'Nutria'),
('GRY00003', 'Ron', 'Weasley', 5, 'Gryffindor', 'Jack Russell Terrier'),
('GRY00004', 'Neville', 'Longbottom', 5, 'Gryffindor', null),
('GRY00005', 'Ginny', 'Weasley', 4, 'Gryffindor', 'Caballo');