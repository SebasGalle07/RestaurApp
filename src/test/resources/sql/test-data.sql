SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE facturas;
TRUNCATE TABLE pagos;
TRUNCATE TABLE pedido_items;
TRUNCATE TABLE pedidos;
TRUNCATE TABLE menu;
TRUNCATE TABLE categorias;
TRUNCATE TABLE mesas;
TRUNCATE TABLE users;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO categorias (id, nombre, descripcion) VALUES
  (1, 'Bebidas', 'Frias y calientes'),
  (2, 'Entradas', 'Para compartir'),
  (3, 'Platos fuertes', 'Carnes y pastas'),
  (4, 'Postres', 'Dulces'),
  (5, 'Especiales', 'Recomendaciones del chef');

INSERT INTO mesas (id, numero) VALUES
  (1, 'M1'),
  (2, 'M2'),
  (3, 'M3'),
  (4, 'M4'),
  (5, 'M5');

INSERT INTO users (id, codigo, nombre, email, password_hash, rol, activo) VALUES
  ('11111111-1111-1111-1111-111111111111', 1001, 'Admin', 'admin@test.com', '$2a$10$QGJDOeP/0lWTLFf9tXoS0Obq8zQAH6Xb4lwvx6pRP70dNDO7xj1te', 'admin', TRUE),
  ('22222222-2222-2222-2222-222222222222', 1002, 'Maria Mesera', 'mesera@test.com', '$2a$10$QGJDOeP/0lWTLFf9tXoS0Obq8zQAH6Xb4lwvx6pRP70dNDO7xj1te', 'mesero', TRUE),
  ('33333333-3333-3333-3333-333333333333', 1003, 'Carlos Cocinero', 'cocinero@test.com', '$2a$10$QGJDOeP/0lWTLFf9tXoS0Obq8zQAH6Xb4lwvx6pRP70dNDO7xj1te', 'cocinero', TRUE),
  ('44444444-4444-4444-4444-444444444444', 1004, 'Lucia Cajera', 'cajera@test.com', '$2a$10$QGJDOeP/0lWTLFf9tXoS0Obq8zQAH6Xb4lwvx6pRP70dNDO7xj1te', 'cajero', TRUE),
  ('55555555-5555-5555-5555-555555555555', 1005, 'Oscar Mesero', 'mesero2@test.com', '$2a$10$QGJDOeP/0lWTLFf9tXoS0Obq8zQAH6Xb4lwvx6pRP70dNDO7xj1te', 'mesero', TRUE);

INSERT INTO menu (id, nombre, descripcion, precio, categoria_id, activo) VALUES
  (1, 'Agua', 'Botella 600 ml', 5000, 1, TRUE),
  (2, 'Cafe Americano', 'Taza caliente', 6000, 1, TRUE),
  (3, 'Ceviche Clasico', 'Pescado fresco', 25000, 2, TRUE),
  (4, 'Lomo Saltado', 'Salteado peruano', 32000, 3, TRUE),
  (5, 'Pasta Alfredo', 'Con salsa cremosa', 28000, 3, TRUE),
  (6, 'Brownie con Helado', 'Brownie tibio + bola vainilla', 15000, 4, TRUE);

INSERT INTO pedidos (id, mesa_id, mesero_id, estado, total, notas, created_at, updated_at) VALUES
  (1, 1, '22222222-2222-2222-2222-222222222222', 'CERRADO', 70000, 'Almuerzo empresarial', TIMESTAMP '2024-05-01 12:00:00', TIMESTAMP '2024-05-01 13:00:00'),
  (2, 2, '22222222-2222-2222-2222-222222222222', 'CERRADO', 45000, 'Mesa familiar', TIMESTAMP '2024-05-02 12:15:00', TIMESTAMP '2024-05-02 13:05:00'),
  (3, 3, '55555555-5555-5555-5555-555555555555', 'CERRADO', 56000, 'Clientes frecuentes', TIMESTAMP '2024-05-03 11:40:00', TIMESTAMP '2024-05-03 12:10:00'),
  (4, 4, '55555555-5555-5555-5555-555555555555', 'CERRADO', 47000, 'Mesa terraza', TIMESTAMP '2024-05-04 13:00:00', TIMESTAMP '2024-05-04 13:45:00'),
  (5, 5, '55555555-5555-5555-5555-555555555555', 'CERRADO', 31000, 'Mesa interior', TIMESTAMP '2024-05-05 18:30:00', TIMESTAMP '2024-05-05 19:05:00'),
  (6, 1, '22222222-2222-2222-2222-222222222222', 'EN_PREPARACION', 37000, 'Pedido en cocina', TIMESTAMP '2024-05-06 11:00:00', TIMESTAMP '2024-05-06 11:20:00'),
  (7, 2, '55555555-5555-5555-5555-555555555555', 'ABIERTO', 20000, 'Cliente nuevo', TIMESTAMP '2024-05-06 12:30:00', TIMESTAMP '2024-05-06 12:30:00'),
  (8, 3, '22222222-2222-2222-2222-222222222222', 'EN_PREPARACION', 30000, 'Mesa patio', TIMESTAMP '2024-05-06 13:15:00', TIMESTAMP '2024-05-06 13:15:00');

INSERT INTO pedido_items (id, pedido_id, item_id, cantidad, precio_unit, subtotal, estado_preparacion, notas) VALUES
  (1, 1, 3, 2, 25000, 50000, 'LISTO', 'Con camote'),
  (2, 1, 6, 1, 15000, 15000, 'LISTO', NULL),
  (3, 1, 1, 1, 5000, 5000, 'LISTO', 'Botella fria'),
  (4, 2, 4, 1, 32000, 32000, 'LISTO', 'Extra cebolla'),
  (5, 2, 2, 1, 6000, 6000, 'LISTO', NULL),
  (6, 2, 1, 1, 5000, 5000, 'LISTO', NULL),
  (7, 3, 5, 2, 28000, 56000, 'LISTO', 'Compartir'),
  (8, 4, 4, 1, 32000, 32000, 'LISTO', NULL),
  (9, 4, 1, 1, 5000, 5000, 'LISTO', 'Agua natural'),
  (10, 5, 3, 1, 25000, 25000, 'LISTO', NULL),
  (11, 5, 1, 1, 5000, 5000, 'LISTO', NULL),
  (12, 6, 4, 1, 32000, 32000, 'EN_PREPARACION', 'Sin ají'),
  (13, 6, 1, 1, 5000, 5000, 'PENDIENTE', NULL),
  (14, 7, 6, 1, 15000, 15000, 'PENDIENTE', 'Servir caliente'),
  (15, 7, 1, 1, 5000, 5000, 'PENDIENTE', NULL),
  (16, 8, 3, 1, 25000, 25000, 'EN_PREPARACION', 'Sin cilantro'),
  (17, 8, 1, 1, 5000, 5000, 'EN_PREPARACION', NULL);

INSERT INTO pagos (id, pedido_id, monto, metodo, estado, created_at) VALUES
  (1, 1, 70000, 'EFECTIVO', 'APLICADO', TIMESTAMP '2024-05-01 13:10:00'),
  (2, 2, 45000, 'TARJETA', 'APLICADO', TIMESTAMP '2024-05-02 13:10:00'),
  (3, 3, 56000, 'QR', 'APLICADO', TIMESTAMP '2024-05-03 12:15:00'),
  (4, 4, 47000, 'EFECTIVO', 'APLICADO', TIMESTAMP '2024-05-04 13:50:00'),
  (5, 5, 31000, 'TARJETA', 'APLICADO', TIMESTAMP '2024-05-05 19:10:00'),
  (6, 6, 10000, 'EFECTIVO', 'APLICADO', TIMESTAMP '2024-05-06 11:25:00'),
  (7, 8, 30000, 'EFECTIVO', 'APLICADO', TIMESTAMP '2024-05-06 13:30:00');

INSERT INTO facturas (id, pedido_id, numero, fecha_emision, total) VALUES
  (1, 1, 'F-00000001', TIMESTAMP '2024-05-01 13:12:00', 70000),
  (2, 2, 'F-00000002', TIMESTAMP '2024-05-02 13:12:00', 45000),
  (3, 3, 'F-00000003', TIMESTAMP '2024-05-03 12:20:00', 56000),
  (4, 4, 'F-00000004', TIMESTAMP '2024-05-04 13:55:00', 47000),
  (5, 5, 'F-00000005', TIMESTAMP '2024-05-05 19:15:00', 31000);

ALTER TABLE menu ALTER COLUMN id RESTART WITH 7;
ALTER TABLE pedidos ALTER COLUMN id RESTART WITH 9;
ALTER TABLE pedido_items ALTER COLUMN id RESTART WITH 18;
ALTER TABLE pagos ALTER COLUMN id RESTART WITH 8;
ALTER TABLE facturas ALTER COLUMN id RESTART WITH 6;
