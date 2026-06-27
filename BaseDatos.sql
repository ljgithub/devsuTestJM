-- BaseDatos.sql
-- Drop tables if they exist to allow clean runs
DROP TABLE IF EXISTS movimiento CASCADE;
DROP TABLE IF EXISTS cuenta CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS persona CASCADE;

-- Table: persona
CREATE TABLE persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20) NOT NULL,
    edad INT NOT NULL,
    identificacion VARCHAR(50) UNIQUE NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(50) NOT NULL
);

-- Table: cliente (inherits from persona using JOINED strategy)
CREATE TABLE cliente (
    id INT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    clienteid VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table: cuenta
CREATE TABLE cuenta (
    numero_cuenta VARCHAR(50) PRIMARY KEY,
    tipo_cuenta VARCHAR(20) NOT NULL, -- 'Ahorros' or 'Corriente'
    saldo_inicial NUMERIC(15, 2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id INT NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT
);

-- Table: movimiento
CREATE TABLE movimiento (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(50) NOT NULL, -- 'Retiro' or 'Deposito'
    valor NUMERIC(15, 2) NOT NULL,
    saldo NUMERIC(15, 2) NOT NULL,
    cuenta_numero VARCHAR(50) NOT NULL REFERENCES cuenta(numero_cuenta) ON DELETE RESTRICT
);

-- Seed Data (Casos de Uso / Ejemplos)

-- 1. Insert Personas & Clientes
-- Jose Lema
INSERT INTO persona (id, nombre, genero, edad, identificacion, direccion, telefono)
VALUES (1, 'Jose Lema', 'Masculino', 30, '1712345678', 'Otavalo sn y principal', '098254785');
INSERT INTO cliente (id, clienteid, contrasena, estado)
VALUES (1, 'CLI1001', '1234', TRUE);

-- Marianela Montalvo
INSERT INTO persona (id, nombre, genero, edad, identificacion, direccion, telefono)
VALUES (2, 'Marianela Montalvo', 'Femenino', 28, '1723456789', 'Amazonas y NNUU', '097548965');
INSERT INTO cliente (id, clienteid, contrasena, estado)
VALUES (2, 'CLI1002', '5678', TRUE);

-- Juan Osorio
INSERT INTO persona (id, nombre, genero, edad, identificacion, direccion, telefono)
VALUES (3, 'Juan Osorio', 'Masculino', 35, '1734567890', '13 junio y Equinoccial', '098874587');
INSERT INTO cliente (id, clienteid, contrasena, estado)
VALUES (3, 'CLI1003', '1245', TRUE);

-- Adjust SERIAL sequence for persona id
SELECT setval('persona_id_seq', 3);

-- 2. Insert Cuentas
-- Jose Lema (id = 1)
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('478758', 'Ahorros', 2000.00, TRUE, 1);
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('585545', 'Corriente', 1000.00, TRUE, 1);

-- Marianela Montalvo (id = 2)
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('225487', 'Corriente', 100.00, TRUE, 2);
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('496825', 'Ahorros', 540.00, TRUE, 2);

-- Juan Osorio (id = 3)
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('495878', 'Ahorros', 0.00, TRUE, 3);

-- 3. Insert Movimientos (matching initial cases)
-- Retiro de 575 from 478758 (Initial: 2000.00 -> New balance: 1425.00)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_numero)
VALUES ('2022-02-10 14:30:00', 'Retiro', -575.00, 1425.00, '478758');

-- Depósito de 600 into 225487 (Initial: 100.00 -> New balance: 700.00)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_numero)
VALUES ('2022-02-10 15:00:00', 'Deposito', 600.00, 700.00, '225487');

-- Depósito de 150 into 495878 (Initial: 0.00 -> New balance: 150.00)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_numero)
VALUES ('2022-02-10 16:00:00', 'Deposito', 150.00, 150.00, '495878');

-- Retiro de 540 from 496825 (Initial: 540.00 -> New balance: 0.00)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_numero)
VALUES ('2022-02-08 09:00:00', 'Retiro', -540.00, 0.00, '496825');
